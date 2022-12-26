package searchengine.response;

import lombok.Data;
import org.jsoup.Connection;

@Data
public class ResponseFalse implements Response {
    private boolean result;
    private String error;
}
