package searchengine.services.search;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomizedSearch<Search> {
    List<Search> findPagesByQuery(List<Long> lemmaId, List<Long> pageId,
                                                     int limit, int offset);
}
