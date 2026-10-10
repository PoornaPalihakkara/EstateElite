package com.estateelite.service;

import com.estateelite.model.SearchHistory;
import com.estateelite.model.User;
import com.estateelite.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private static final int MAX_HISTORY = 3;

    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional
    public void recordSearch(User buyer, String query) {
        if (query == null || query.isBlank()) return;
        SearchHistory entry = SearchHistory.builder().buyer(buyer).query(query).build();
        searchHistoryRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<String> getRecent(Long buyerId) {
        return searchHistoryRepository.findTop5ByBuyerIdOrderBySearchedAtDesc(buyerId)
                .stream().limit(MAX_HISTORY).map(SearchHistory::getQuery).collect(Collectors.toList());
    }
}
