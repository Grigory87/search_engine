package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Optional<Page> findPageByPathAndSite(String url, Site site);
    Page findPageById(long id);
    Page findByPath(String path);
    @Transactional
    void deleteByPath(String path);
    long countPageBySite(Site site);
    @Query(value = "SELECT COUNT(*) FROM page ", nativeQuery = true)
    long countAllPage();
}