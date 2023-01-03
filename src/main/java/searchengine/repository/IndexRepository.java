package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import searchengine.model.IndexTable;
import searchengine.model.Search;

import java.util.Collection;
import java.util.List;

@Repository
//public interface IndexRepository extends JpaRepository<IndexTable, Long>, CustomizedSearch<Search> {
public interface IndexRepository extends JpaRepository<IndexTable, Long> {
    List<IndexTable> findByLemma_IdIn(Collection<Long> id);

    @Query(value = "select page_id as pageId, (SUM(`rank`) / " +
            "( with relevance as (SELECT page_id, SUM(`rank`) as abs_relev FROM `'index'` " +
            "WHERE lemma_id IN (:listLemma) AND page_id IN (:listPage) " +
            "GROUP BY page_id) " +
            "select max(abs_relev) from relevance" +
            ")) as relative_relevance from `'index'` " +
            "WHERE lemma_id IN (:listLemma) AND page_id IN (:listPage) " +
            "GROUP BY page_id ORDER BY relative_relevance DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Search> findPagesBySearch(@Param("listLemma") List<Long> lemmaId,
                                      @Param("listPage") List<Long> pageId,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);



}