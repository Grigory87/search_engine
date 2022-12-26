package searchengine.dto.search;

import lombok.Data;

@Data
public class NegativeSearchingResponse implements ResponseSearch {
    private boolean result;
    private String error;

    public NegativeSearchingResponse(String error) {
        this.result = false;
        this.error = error;
    }
}
