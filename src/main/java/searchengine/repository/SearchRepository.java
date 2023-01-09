package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Search;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    @Query(value = "SELECT page_id, SUM(`rank`) / :maxRelev as relative_relevance " +
            "FROM `'index'` WHERE lemma_id IN (:listLemma) AND page_id IN (:listPage) " +
            "GROUP BY page_id ORDER BY relative_relevance DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Search> findPageBySearch(@Param("listLemma") List<Long> lemmaId,
                                  @Param("listPage") List<Long> pageId,
                                  @Param("maxRelev") float maxRelevance,
                                  @Param("limit") int limit,
                                  @Param("offset") int offset);
}
