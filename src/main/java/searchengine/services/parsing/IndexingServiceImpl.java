package searchengine.services.parsing;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import searchengine.config.SitesConfig;
import searchengine.dto.indexing.PositiveIndexingResponse;
import searchengine.dto.indexing.ResponseIndexing;
import searchengine.dto.indexing.NegativeIndexingResponse;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SitesConfig sitesConfig;
    private final PageParser pageParser;
    private List<ThreadForSite> threads;

    @Override
    public List<Site> getSitesFromConfig() {
        return sitesConfig.getSites();
    }

    @Override
    public void addSitesInDBFromConfig(List<Site> siteList) {
        for (Site site : siteList) {
            site.setUrl(site.getUrl().replace("www.", ""));
            site.setName((site.getName()));
            site.setStatus(StatusType.INDEXING);
            site.setStatusTime(new Date());
            siteRepository.save(site);
        }
    }

    public Optional<Site> getSiteByUrl(String url) {
        return siteRepository.findByUrl(url.replace("www.", ""));
    }

    @Override
    public boolean isIndexing() {
        List<Site> list = siteRepository.findByStatus(StatusType.INDEXING);
        return !list.isEmpty();
    }

    @Override
    public ResponseIndexing startIndexing() {
        if (isIndexing()) {
            return new NegativeIndexingResponse("Индексация уже запущена");
        }
        threads = new ArrayList<>();
        List<Site> siteList = getSitesFromConfig();
        if (siteList.isEmpty()) {
            return new NegativeIndexingResponse(
                    "Пустой список сайтов в файле application.yaml");
        }

        for(Site site : siteList) {
            Optional<Site> siteOptional = getSiteByUrl(site.getUrl());
            siteOptional.ifPresent(siteRepository::delete);
        }
        addSitesInDBFromConfig(siteList);

        siteList.forEach(site -> threads.add(new ThreadForSite(site, pageParser)));
        threads.forEach(Thread::start);

        return new PositiveIndexingResponse(true);
    }

    @Override
    public ResponseIndexing stopIndexing() {
        if (!isIndexing()) {
            return new NegativeIndexingResponse("Индексация не запущена");
        }
        threads.forEach(Thread::interrupt);
        this.threads.removeAll(threads);
        return new PositiveIndexingResponse(true);
    }

    public ResponseIndexing indexPage(String url) {
        String[] arrayString = url.split("/+");
        String enteredSite = arrayString[0] + "//" + arrayString[1];

        Site site = getSitesFromConfig().stream()
                .filter(s -> s.getUrl().equals(enteredSite)).findFirst().orElse(null);
        if (site == null) {
            return new NegativeIndexingResponse("Данная страница находится за пределами"
                    + " сайтов, указанных в конфигурационном файле");
        }
        String shortUrl = url.replace(enteredSite, "");
        site.setUrl(site.getUrl().replace("www.", ""));

        Optional<Site> siteOptional = getSiteByUrl(site.getUrl());
        if (siteOptional.isEmpty()) {
            pageParser.updateStatus(site, StatusType.INDEXING);
        } else {
            site = siteOptional.get();
        }

        if (pageRepository.findPageByPathAndSite(shortUrl, site).isPresent()) {
            Page page = pageRepository.findByPath(shortUrl);
            List<Lemma> lemmaList = lemmaRepository.findByIndexList_Page_Id(page.getId());
            pageRepository.deleteByPath(shortUrl);

            lemmaList.stream().peek(lemma -> lemma.setFrequency(lemma.getFrequency() - 1)).toList();
            lemmaRepository.saveAllAndFlush(lemmaList);
        }

        try {
            String link = enteredSite.concat(shortUrl).replace("www.", "");
            Document doc = pageParser.pageParse(link, site);
        } catch (IOException e) {
            pageParser.updateStatus(site, StatusType.FAILED, "Ошибка индексации");
            e.printStackTrace();
        }
        pageParser.updateStatus(site, StatusType.INDEXED);

        return new PositiveIndexingResponse(true);
    }

    @Override
    public Page getPageById(long id) {
        return pageRepository.findPageById(id);
    }

    @Override
    public long countPageBySite(Site site) {
        return pageRepository.countPageBySite(site);
    }

    @Override
    public long countAllPage() {
        return pageRepository.countAllPage();
    }

    @Override
    public List<IndexTable> getIndexWithLemma(List<Long> lemmaId) {
        return indexRepository.findByLemma_IdIn(lemmaId);
    }
}