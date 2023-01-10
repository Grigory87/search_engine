package searchengine.services.ruMorphology;

import org.jsoup.nodes.Document;
import searchengine.config.Field;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;
import java.util.Map;

public interface MorphologyService {
    List<Field> getFieldList();
    List<Map<String, Float>> collectLemmasForAllTags(Document document);
    List<Lemma> collectLemmasFromRequest(String text, Site site);
    Map<String, Float> uniqueLemmasForPage(List<Map<String, Float>> lemmasAllTags);
    List<Lemma> saveLemmas(Site site, Map<String, Float> lemmasWithRank);
    void saveParsing(Site site, Page page, Document document);
}
