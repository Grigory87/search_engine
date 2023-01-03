package searchengine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexResponse {
    private boolean result;
    private String error;

    public IndexResponse(boolean result) {
        this.result = result;
    }

    public IndexResponse(boolean result, String error) {
        this.result = result;
        this.error = error;
    }
}
