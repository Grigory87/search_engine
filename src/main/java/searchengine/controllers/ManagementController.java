package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.ResponseIndexing;
import searchengine.services.parsing.IndexingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ManagementController {
    private final IndexingService indexingService;

    @GetMapping("/startIndexing")
    public ResponseEntity<ResponseIndexing> startIndexing() {
        return ResponseEntity.ok(indexingService.startIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<ResponseIndexing> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<ResponseIndexing> indexPage(@RequestParam String url) {
        return ResponseEntity.ok(indexingService.indexPage(url));
    }
}
