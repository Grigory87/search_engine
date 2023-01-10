package searchengine.services.parsing;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.config.ConnectConfig;
import searchengine.config.Cookie;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.StatusType;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.ruMorphology.MorphologyService;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PageParserImpl implements PageParser {
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final MorphologyService morphologyService;
    private final ConnectConfig connectConfig;

    @Override
    public Document pageParse(String url, Site site) throws IOException {
        updateStatus(site, StatusType.INDEXING);
        Document document = getConnection(url).get();
        String relativeLink = url.substring(site.getUrl().length());
        Page page = savePage(new Page(site, relativeLink), document);
        morphologyService.saveParsing(site, page, document);

        return document;
    }

    private Connection getConnection(String url) {
        Cookie cookie = connectConfig.getCookie();
        return Jsoup.connect(url)
                .userAgent(connectConfig.getUserAgent())
                .referrer(connectConfig.getReferrer())
                .cookie(cookie.getAuth(), cookie.getToken())
                .timeout(connectConfig.getTimeout())
                .ignoreHttpErrors(connectConfig.isIgnoreHttpErrors())
                .ignoreContentType(connectConfig.isIgnoreContentType())
                .maxBodySize(connectConfig.getMaxBodySize());
    }

    private synchronized Page savePage(Page page, Document document) {
        int responseCode = document.connection().response().statusCode();
        page.setCode(responseCode);
        if(responseCode != 200) {
            page.setContent("");
        } else {
            String content = document.html();
            page.setContent(content);
        }
        pageRepository.save(page);
        return page;
    }

    public void updateStatus(Site site, StatusType statusType) {
        site.setStatus(statusType);
        site.setStatusTime(new Date());
        siteRepository.save(site);
    }

    public void updateStatus(Site site, StatusType statusType, String error) {
        site.setStatus(statusType);
        site.setStatusTime(new Date());
        site.setLastError(error);
        siteRepository.save(site);
    }

    public String getBody(String text) {
        Document doc = Jsoup.parse(text);
        return doc.select("body").text();
    }

    public String getTitle(String text) {
        Document doc = Jsoup.parse(text);
        return doc.select("title").text();
    }
}
