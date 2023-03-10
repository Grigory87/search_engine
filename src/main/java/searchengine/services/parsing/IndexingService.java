package searchengine.services.parsing;

import searchengine.dto.IndexResponse;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

public interface IndexingService {
    List<Site> getSitesFromConfig();
    void addSitesInDBFromConfig(List<Site> siteList);
    boolean isIndexing();
    IndexResponse startIndexing();
    IndexResponse stopIndexing();
    IndexResponse indexPage(String url);
    Optional<Site> getSiteByUrl(String url);
    Page getPageById(long id);
    long countPageBySite(Site site);
    long countAllPage();
}
