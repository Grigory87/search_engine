package searchengine.dto.statistics;

import lombok.Data;

import java.util.Date;

@Data
public class DetailedStatisticsItem {
    private String url;
    private String name;
    private String status;
    private Date statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
