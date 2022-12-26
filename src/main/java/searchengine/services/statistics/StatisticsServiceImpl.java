package searchengine.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesConfig;
import searchengine.dto.statistics.*;
import searchengine.model.Site;
import searchengine.model.StatusType;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SitesConfig sites;

    @Override
     public StatisticsResponse getStatistics() {
        List<Site> list = sites.getSites();
        boolean isEmpty = true;
        for(Site site : list) {
            String siteUrl = site.getUrl().replace("www.", "");
            if (siteRepository.findByUrl(siteUrl).isPresent()) {
                isEmpty = false;
                break;
            }
        }

        if(!isEmpty) {
//            List<Site> sitesList = indexingService.getSitesFromDB();
            List<Site> sitesList = siteRepository.findAll();
            List<DetailedStatisticsItem> detailed = new ArrayList<>();
            long countAllPage = 0;
            long countAllLemma = 0;

            for (Site site : sitesList) {
                DetailedStatisticsItem item = new DetailedStatisticsItem();
                item.setName(site.getName());
                item.setUrl(site.getUrl());

                long countPage = pageRepository.countPageBySite(site);
                long countLemma = lemmaRepository.countLemmaBySite(site);

                item.setPages(countPage);
                item.setLemmas(countLemma);
                String status = site.getStatus().toString();
                item.setStatus(status);
                if (status.equals("FAILED")) {
                    item.setError(site.getLastError());
                }
                item.setStatusTime(site.getStatusTime());
                detailed.add(item);
                countAllPage += countPage;
                countAllLemma += countLemma;
            }
            TotalStatistics total = new TotalStatistics();
            total.setSites(sitesList.size());
            total.setPages(countAllPage);
            total.setLemmas(countAllLemma);
            total.setIndexing(!siteRepository.findByStatus(StatusType.INDEXING).isEmpty());

            StatisticsResponse response = new StatisticsResponse();
            StatisticsData statisticsData = new StatisticsData();
            statisticsData.setTotal(total);
            statisticsData.setDetailed(detailed);
            response.setResult(true);
            response.setStatistics(statisticsData);
            return response;
        } else {
            return new EmptyStatistics().getStatistics();
        }
    }
}