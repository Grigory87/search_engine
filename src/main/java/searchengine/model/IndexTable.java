package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "`'index'`", schema = "search_engine2")
@NoArgsConstructor
public class IndexTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false)
    private Page page;

    @ManyToOne()
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false)
    private Lemma lemma;

    @Column(name = "`rank`", columnDefinition = "FLOAT NOT NULL")
    private float rank;

    @Override
    public String toString() {
        return "IndexTable{" +
                "id=" + id +
                ", page=" + page.getId() +
                ", lemma=" + lemma.getId() +
                ", rank=" + rank +
                '}';
    }
}
