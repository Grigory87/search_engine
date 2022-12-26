package searchengine.dto.indexing;

import lombok.Data;

@Data
public class PositiveIndexingResponse implements ResponseIndexing {
    private boolean result;

    public PositiveIndexingResponse(boolean result) {
        this.result = result;
    }
}
