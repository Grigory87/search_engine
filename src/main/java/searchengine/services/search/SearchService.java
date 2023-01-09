package searchengine.services.search;

import searchengine.dto.SearchResponse;

public interface SearchService {
    SearchResponse getResponseSearchQuery(
            String query, String siteUrl, int offset, int limit);
}
