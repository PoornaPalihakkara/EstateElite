package com.estateelite.repository;

import com.estateelite.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findTop5ByBuyerIdOrderBySearchedAtDesc(Long buyerId);
}
