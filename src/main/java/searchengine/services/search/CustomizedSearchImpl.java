package searchengine.services.search;

import org.springframework.stereotype.Service;
import searchengine.model.Search;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class CustomizedSearchImpl implements CustomizedSearch<Search> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List findPagesByQuery(List<Long> lemmaId,
                                 List<Long> pageId,
                                 int limit,
                                 int offset) {
        StringBuilder lemmasId = new StringBuilder();
        for(Long id : lemmaId) {
            if (lemmasId.length() > 0) {
                lemmasId.append(",");
            }
            lemmasId.append(id);
        }

        StringBuilder pagesId = new StringBuilder();
        for(Long id : pageId) {
            if(pagesId.length() > 0) {
                pagesId.append(",");
            }
            pagesId.append(id);
        }

        return entityManager.createNativeQuery(
            "select page_id, (SUM(`rank`) / " +
                    "( with relevance as (SELECT page_id, SUM(`rank`) as abs_relev FROM `'index'` " +
                    "WHERE lemma_id IN (" + lemmasId + ") AND page_id IN (" + pagesId + ") " +
                    "GROUP BY page_id) " +
                    "select max(abs_relev) from relevance" +
                    ")) as relative_relevance from `'index'` " +
                    "WHERE lemma_id IN (" + lemmasId + ") AND page_id IN (" + pagesId + ") " +
                    "GROUP BY page_id ORDER BY relative_relevance DESC " +
                    "LIMIT " + limit + " OFFSET " + offset, Search.class)
            .getResultList();
    }
}
