package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.IndexResponse;
import searchengine.services.parsing.IndexingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ManagementController {
    private final IndexingService indexingService;

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexResponse> startIndexing() {
        return ResponseEntity.ok(indexingService.startIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexResponse> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexResponse> indexPage(@RequestParam String url) {
        return ResponseEntity.ok(indexingService.indexPage(url));
    }
}
