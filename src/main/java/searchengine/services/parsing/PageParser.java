package searchengine.services.parsing;

import org.jsoup.nodes.Document;
import searchengine.model.Site;
import searchengine.model.StatusType;

import java.io.IOException;

public interface PageParser {
    Document pageParse(String url, Site site) throws IOException;
    void updateStatus(Site site, StatusType statusType);
    void updateStatus(Site site, StatusType statusType, String error);
    String getBody(String text);
    String getTitle(String text);
}
