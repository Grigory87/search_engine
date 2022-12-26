package searchengine.services.search;

import searchengine.dto.search.NegativeSearchingResponse;
import searchengine.dto.search.PositiveSearchingResponse;
import searchengine.dto.search.ResponseSearch;
import searchengine.dto.search.SearchData;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.services.parsing.IndexingService;
import searchengine.services.parsing.PageParser;
import searchengine.services.ruMorphology.MorphologyService;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SearchByRequest {
    private final IndexRepository indexRepository;
    private final MorphologyService morphologyService;
    private final IndexingService indexingService;
    private final PageParser pageParser;

    public ResponseSearch getResponseSearchQuery(
            String query, String siteUrl, int offset, int limit) {
        if(query.equals("")){
            return new NegativeSearchingResponse("Задан пустой поисковый запрос");
        }
        Site site = indexingService.getSiteByUrl(siteUrl).orElse(null);

        List<Lemma> lemmaListFromDB =
                morphologyService.collectLemmasFromRequest(query, site);
        if(lemmaListFromDB.isEmpty()) {
            return new NegativeSearchingResponse(
                    "По вашему запросу ничего не найдено");
        }

        long totalPage;
        if(site != null) {
            totalPage = indexingService.countPageBySite(site);
        } else {
            totalPage = indexingService.countAllPage();
        }

        List<Lemma> selectionOfLemmas = lemmaListFromDB.stream()
                .filter(lemma -> ((lemma.getFrequency() * 100L) / totalPage) < 90).toList();

        List<Long> listLemmaId = selectionOfLemmas.stream()
                .mapToLong(Lemma::getId)
                .boxed()
                .toList();

        List<IndexTable> indexList = indexingService.getIndexWithLemma(listLemmaId);

        List<Long> listPageId = indexList.stream()
                .map(indexTable -> indexTable.getPage().getId())
                .distinct()
                .collect(Collectors.toList());

        List<String> listLemmaName = selectionOfLemmas.stream()
                .map(Lemma::getLemma)
                .distinct()
                .toList();

        List<Search> searchList = indexRepository.findPagesByQuery(
                listLemmaId, listPageId, limit, offset);

        List<SearchData> searchDataList = getStatisticsForEachPage(searchList, listLemmaName, site);
        return new PositiveSearchingResponse(searchList.size(), searchDataList);
    }

    private List<SearchData> getStatisticsForEachPage(
            List<Search> searchList, List<String> listLemmaName, Site site) {

        List<SearchData> searchDataList = new ArrayList<>();
        for(Search search : searchList) {
            SearchData searchData = new SearchData();
            Page page = indexingService.getPageById(search.getPageId());

            String uri = page.getPath();
            String content = page.getContent();
            String title = pageParser.getTitle(content);
            String body = pageParser.getBody(content);
            String snippet = getSnippet(body, listLemmaName);

            if(site != null) {
                searchData.setSite(String.valueOf(site));
                searchData.setSiteName(site.getName());
            } else {
                searchData.setSite("Поиск по всем сайтам");
                searchData.setSiteName("");
            }
            searchData.setUri(uri);
            searchData.setTitle(title);
            searchData.setSnippet(snippet);
            searchData.setRelevance(search.getRelativeRelevance());
            searchDataList.add(searchData);
        }
        return searchDataList;
    }

    private String getSnippet(String body, List<String> listLemmaName) {
        StringBuilder snippet = new StringBuilder(300);

        Pattern pattern = Pattern.compile("[A-ZА-ЯЁ]+[^А-ЯЁ]+\\.?\\s?");
        Matcher matcher = pattern.matcher(body);

        List<String> sentences = new ArrayList<>();
        while (matcher.find()) {
            sentences.add(matcher.group());
        }

        Map<String, Integer> mapOccurrencesLemmas = new HashMap<>();

        for(String lemma : listLemmaName) {
            for(int i = 0; i < sentences.size(); i++) {
                if(sentences.get(i).toLowerCase().contains(lemma.substring(0, lemma.length() - 1))) {
                    mapOccurrencesLemmas.put(sentences.get(i), i);
                }
            }
        }

        if(mapOccurrencesLemmas.size() < 3) {
            int index = mapOccurrencesLemmas.entrySet().stream()
                    .map(key -> key.getValue()).findFirst().get();
            while (snippet.length() < 280) {
                writeSnippet(sentences.get(index), listLemmaName, snippet);
                index++;
            }
        } else {
            for(Map.Entry<String, Integer> entry : mapOccurrencesLemmas.entrySet()) {
                writeSnippet(entry.getKey(), listLemmaName, snippet);
                if(snippet.length() > 280) {
                    break;
                }
            }
        }
        return snippet.toString().trim();
    }

    private void writeSnippet(String sentence, List<String> listLemmaName, StringBuilder snippet) {
        sentence = changeLemmaToBold(sentence, listLemmaName);
        snippet.append(sentence);
    }

    private String changeLemmaToBold(String sentence, List<String> listLemmaName) {
        String[] words = sentence.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for(String word : words) {
            for(String lemma : listLemmaName) {
                if(word.toLowerCase().contains(lemma.substring(0, lemma.length() - 1))) {
                    word = "<b>" + word + "</b>";
                    break;
                }
            }
            builder.append(word).append(" ");
        }
        return builder.toString();
    }
}
