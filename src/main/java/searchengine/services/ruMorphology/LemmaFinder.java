package searchengine.services.ruMorphology;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.Field;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class LemmaFinder {
    private static LemmaFinder instance;
    private final LuceneMorphology luceneMorphology;
    private static final String[] PART_OF_SPEECH =
            new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ"};

    public static LemmaFinder getInstance() {
        if(instance == null) {
            LuceneMorphology luceneMorphology = null;
            try {
                luceneMorphology = new RussianLuceneMorphology();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            instance = new LemmaFinder(luceneMorphology);
        }
        return instance;
    }

    public List<Map<String, Float>> collectLemmas(
            Document document, List<Field> fieldList) {

        List<Map<String, Float>> lemmasForTag = new ArrayList<>();
        for(Field field : fieldList) {
            String tag = field.getSelector();
            float ratio = field.getWeight();
            lemmasForTag.add(getLemmasByTag(document, tag, ratio));
        }
        return lemmasForTag;
    }

    private Map<String, Float> getLemmasByTag(
                    Document doc, String tag, float ratio) {
        StringBuilder builder = new StringBuilder();
        Map<String, Float> lemmas = new HashMap<>();

        Elements elements = doc.getElementsByTag(tag);
        for (Element elem : elements) {
            builder.append(elem.text() + " ");
        }
        String text = builder.toString();
        String[] words = arrayContainsRussianWords(text);

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            List<String> wordBaseForm = luceneMorphology.getMorphInfo(word);
            if (anyWordBaseBelongToParticle(wordBaseForm)) {
                continue;
            }
            List<String> normalForms = luceneMorphology.getNormalForms(word);
            String normalWord = normalForms.get(0);

            if (lemmas.containsKey(normalWord)) {
                lemmas.put(normalWord, lemmas.get(normalWord) + ratio);
            } else {
                lemmas.put(normalWord, ratio);
            }
        }
        return lemmas;
    }

    private String[] arrayContainsRussianWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-я\\s])", " ")
                .trim()
                .split("\\s+");
    }

    private boolean anyWordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::isPartOfSpeech);
    }

    private boolean isPartOfSpeech (String word) {
        for (String currentWord : PART_OF_SPEECH) {
            if (word.toUpperCase().contains(currentWord)) {
                return true;
            }
        }
        return false;
    }

    public List<String> collectLemmasFromRequest(String text) {
        List<String> lemmasFromQuery = new ArrayList<>();
        String[] words = arrayContainsRussianWords(text);

        for(String word : words) {
            if(word.isBlank()) {
                continue;
            }
            List<String> wordBaseForm = luceneMorphology.getMorphInfo(word);
            if (anyWordBaseBelongToParticle(wordBaseForm)) {
                continue;
            }
            List<String> normalForms = luceneMorphology.getNormalForms(word);
            String normalWord = normalForms.get(0);

            if(!lemmasFromQuery.contains(word)) {
                lemmasFromQuery.add(word);
            }
        }
        return lemmasFromQuery;
    }
}
