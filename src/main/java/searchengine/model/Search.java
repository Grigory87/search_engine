package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Search {

    @Id
    @Column(name = "page_id")
    private long pageId;
    @Column(name = "relative_relevance")
    private float relativeRelevance;
}
