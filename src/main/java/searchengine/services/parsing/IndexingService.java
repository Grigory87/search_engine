package searchengine.services.parsing;

import searchengine.dto.indexing.ResponseIndexing;
import searchengine.model.IndexTable;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

public interface IndexingService {
    List<Site> getSitesFromConfig();
    void addSitesInDBFromConfig(List<Site> siteList);
    boolean isIndexing();
    ResponseIndexing startIndexing();
    ResponseIndexing stopIndexing();
    ResponseIndexing indexPage(String url);
    Optional<Site> getSiteByUrl(String url);
    Page getPageById(long id);
    long countPageBySite(Site site);
    long countAllPage();
    List<IndexTable> getIndexWithLemma(List<Long> lemmaId);
}
