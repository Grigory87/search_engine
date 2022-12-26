package searchengine.services.parsing;

import lombok.RequiredArgsConstructor;

import searchengine.model.Site;
import searchengine.model.StatusType;
import searchengine.tools.Tools;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class ThreadForSite extends Thread {
    private final Site site;
    private final PageParser pageParser;

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        System.out.println("Start parsing " + site.getUrl() + " " + new Date());

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.execute(new WalkSiteLink(site, pageParser));

        while(isAlive()) {
            if (isInterrupted()) {
                forkJoinPool.shutdownNow();
                pageParser.updateStatus(site, StatusType.FAILED, "Индексация прервана");
            }
            if (forkJoinPool.getActiveThreadCount() == 0) {
                finishParsing(start);
                break;
            }
        }
    }

    private void finishParsing(long start) {
        System.out.println("Duration of site parsing "
                + site.getUrl() + " "
                + Tools.getTime((System.currentTimeMillis() - start) / 1000));

        pageParser.updateStatus(site, StatusType.INDEXED);
    }
}
