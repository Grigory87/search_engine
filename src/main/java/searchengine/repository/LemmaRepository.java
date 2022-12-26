package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.Collection;
import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Long> {
    long countLemmaBySite(Site site);
    List<Lemma> findByIndexList_Page_Id(long id);
    List<Lemma> findBySiteAndLemmaIn(Site site, Collection<String> lemmas);
    List<Lemma> findByLemmaInOrderByFrequency(List<String> lemmas);
    List<Lemma> findBySiteAndLemmaInOrderByFrequency(Site site, List<String> lemmas);
}