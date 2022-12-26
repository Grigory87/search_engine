package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import searchengine.model.IndexTable;
import searchengine.services.search.CustomizedSearch;
import searchengine.model.Search;

import java.util.Collection;
import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexTable, Long>, CustomizedSearch<Search> {
    List<IndexTable> findByLemma_IdIn(Collection<Long> id);
}