package searchengine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SearchResponse {
    private boolean result;
    private long count;
    private List<SearchData> data;
    private String error;

    public SearchResponse(boolean result, long count, List<SearchData> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }

    public SearchResponse(boolean result, String error) {
        this.result = result;
        this.error = error;
    }
}
