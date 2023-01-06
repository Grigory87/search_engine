package searchengine.services.parsing;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.Site;
import searchengine.model.StatusType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class WalkSiteLink extends RecursiveAction {
    private final String url;
    private final Site site;
    private final PageParser pageParser;
    private final ConcurrentSkipListSet<String> allLinks;

    public WalkSiteLink(String url, Site site,
                        PageParser pageParser,
                        ConcurrentSkipListSet<String> allLinks) {
        this.url = url;
        this.site = site;
        this.pageParser = pageParser;
        this.allLinks = allLinks;
    }

    public WalkSiteLink(Site site, PageParser pageParser) {
        this.url = site.getUrl().concat("/");
        this.site = site;
        this.pageParser = pageParser;
        this.allLinks = new ConcurrentSkipListSet<>();
    }

    @Override
    protected void compute() {
        if (StopIndicator.getStop()) {
            return;
        }

        List<WalkSiteLink> subList = new ArrayList<>(30);
        try {
            Thread.sleep(300);
            Document document = pageParser.pageParse(url, site);
            Elements elements = document.select("a");

            for (Element elem : elements) {
                String link = elem.attr("abs:href").replace("www.", "");
                if (!link.isEmpty()
                        && (link.startsWith(site.getUrl()) && !link.equals(site.getUrl() + "/"))
                        && !allLinks.contains(link)
                        && !link.matches(".+#$|.+.css$|.+.css?|.+.png$|.+.ico$|.+.jpe?g?$|.+.JPE?G?$|.+.jfif$"
                        + "|.+.bmp$|.+.dib$|.+.gif$|.+.json$|.+.docx?$|.+.pdf$|.+.xls?x$|.+.pptx$|.+.mp3$"
                        + "|.+.mp4$|.+.csv$|.+.xml$|.+.exe(.)?|.+.apk$|.+.rar$|.+.zip$|.+.jar$|.+.js|.+.svg")) {
                    WalkSiteLink walkSiteLink = new WalkSiteLink(link, site, pageParser, allLinks);
                    walkSiteLink.fork();
                    subList.add(walkSiteLink);
                    allLinks.add(link);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            pageParser.updateStatus(site, StatusType.FAILED, e.getMessage());
            e.printStackTrace();
        }

        subList.forEach(ForkJoinTask::join);
    }
}
