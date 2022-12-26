package searchengine.dto.indexing;

import lombok.Data;

@Data
public class NegativeIndexingResponse implements ResponseIndexing {
    private boolean result;
    private String error;

    public NegativeIndexingResponse(String error) {
        this.result = false;
        this.error = error;
    }
}
