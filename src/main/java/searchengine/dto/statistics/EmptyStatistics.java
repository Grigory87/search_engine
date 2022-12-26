package searchengine.dto.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmptyStatistics {

    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(0);
        total.setIndexing(false);
        total.setPages(0);
        total.setLemmas(0);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();

        DetailedStatisticsItem item = new DetailedStatisticsItem();
        item.setName("UNKNOWN");
        item.setUrl("UNKNOWN");

        item.setPages(0);
        item.setLemmas(0);
        item.setStatus("UNKNOWN");
        item.setError("Пустая база данных");
        item.setStatusTime(new Date());

        detailed.add(item);

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}