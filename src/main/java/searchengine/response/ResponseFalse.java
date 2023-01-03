package searchengine.response;

public class ResponseFalse implements Response {
    private boolean result;
    private String error;

    public ResponseFalse(boolean result, String error) {
        this.result = result;
        this.error = error;
    }
}
