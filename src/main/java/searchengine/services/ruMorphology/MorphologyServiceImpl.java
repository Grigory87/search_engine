package searchengine.services.ruMorphology;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.FieldsConfig;
import searchengine.config.Field;
import searchengine.model.IndexTable;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MorphologyServiceImpl implements MorphologyService {
    private final LuceneMorphology luceneMorphology;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final FieldsConfig fieldsConfig;

    public long countLemmaBySite(Site site) {
        return lemmaRepository.countLemmaBySite(site);
    }

    @Override
    public List<Field> getFieldList() {
        return fieldsConfig.getFields();
    }

    @Override
    public List<Lemma> collectLemmasFromRequest(String text, Site site) {
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorphology);
        List<String> lemmaList = lemmaFinder.collectLemmasFromRequest(text);

        List<Lemma> lemmaListFromDB;
        if(site == null) {
            lemmaListFromDB = lemmaRepository.findByLemmaInOrderByFrequency(lemmaList);
        } else {
            lemmaListFromDB = lemmaRepository.findBySiteAndLemmaInOrderByFrequency(
                    site, lemmaList);
        }
        return lemmaListFromDB;
    }

    @Override
    public List<Map<String, Float>> collectLemmasForAllTags(Document document) {
        List<Field> fieldList = getFieldList();
        return LemmaFinder.getInstance().collectLemmas(document, fieldList);
    }

    @Transactional
    @Override
    public void saveParsing(Site site, Page page, Document document) {
        List<Map<String, Float>> lemmas = collectLemmasForAllTags(document);
        Map<String, Float> lemmasWithRank = uniqueLemmasForPage(lemmas);

        List<Lemma> lemmaList = saveLemmas(site, lemmasWithRank);
        lemmaRepository.flush();

        List<IndexTable> indexList = new ArrayList<>(500);
        for(Lemma lemma : lemmaList) {
            IndexTable index = new IndexTable();
            float rank = lemmasWithRank.get(lemma.getLemma());
            index.setPage(page);
            index.setLemma(lemma);
            index.setRank(rank);
            indexList.add(index);
        }
        indexRepository.saveAllAndFlush(indexList);
    }

    @Override
    public synchronized List<Lemma> saveLemmas(Site site, Map<String, Float> lemmasWithRank) {
        HashMap<String, Lemma> lemmasForPage = new HashMap<>();

        List<String> lemmasName = lemmasWithRank.keySet().stream().toList();
        for(String nameLemma : lemmasName) {
            lemmasForPage.put(nameLemma, new Lemma(site, nameLemma, 1));
        }

        List<Lemma> findLemmas = lemmaRepository.findBySiteAndLemmaIn(site, lemmasName);
        for(Lemma lemma : findLemmas) {
            if(lemmasForPage.containsKey(lemma.getLemma())) {
                lemma.setFrequency(lemma.getFrequency() + 1);
                lemmasForPage.replace(lemma.getLemma(), lemma);
            }
        }
        return lemmaRepository.saveAllAndFlush(lemmasForPage.values());
    }

    @Override
    public Map<String, Float> uniqueLemmasForPage(List<Map<String, Float>> lemmasAllTags) {
        Map<String, Float> uniqueLemmasWithRank = new HashMap<>();
        for (Map<String, Float> map : lemmasAllTags) {
            for (Map.Entry<String, Float> entry : map.entrySet()) {
                String lemma = entry.getKey();
                float rank = entry.getValue();
                if (uniqueLemmasWithRank.containsKey(lemma)) {
                    uniqueLemmasWithRank.put(lemma, uniqueLemmasWithRank.get(lemma) + rank);
                } else {
                    uniqueLemmasWithRank.put(lemma, rank);
                }
            }
        }
        return uniqueLemmasWithRank;
    }

    public List<Lemma> getLemmasByPageId(long id) {
        return lemmaRepository.findByIndexList_Page_Id(id);
    }

    public void changeFrequencyLemmasForOnePage(List<Lemma> lemmaList) {
        lemmaRepository.saveAll(lemmaList);
        lemmaRepository.flush();
    }
}