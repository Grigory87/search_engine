package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import searchengine.dto.SearchResponse;
import searchengine.repository.IndexRepository;
import searchengine.services.parsing.IndexingService;
import searchengine.services.parsing.PageParser;
import searchengine.services.ruMorphology.MorphologyService;
import searchengine.services.search.SearchByRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {
    private final IndexRepository indexRepository;
    private final IndexingService indexingService;
    private final MorphologyService morphologyService;
    private final PageParser pageParser;


    @GetMapping(value = "/search")
    public ResponseEntity<SearchResponse> search(@RequestParam("query") String query,
                                                 @RequestParam(value = "site", defaultValue = "") String siteUrl,
                                                 @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                 @RequestParam(value = "limit", defaultValue = "0") int limit) {
        SearchByRequest searchByRequest = new SearchByRequest(indexRepository,
                morphologyService, indexingService, pageParser);
        return ResponseEntity.ok(searchByRequest.getResponseSearchQuery(
                query, siteUrl, offset, limit));
    }
}
