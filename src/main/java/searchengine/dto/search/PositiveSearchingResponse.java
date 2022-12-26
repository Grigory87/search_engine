package searchengine.dto.search;

import lombok.Data;

import java.util.List;

@Data
public class PositiveSearchingResponse implements ResponseSearch {
    private boolean result;
    private long count;
    private List<SearchData> data;

    public PositiveSearchingResponse(long count, List<SearchData> data) {
        this.result = true;
        this.count = count;
        this.data = data;
    }
}
