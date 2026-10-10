package com.estateelite.controller;

import com.estateelite.dto.request.WishlistRequest;
import com.estateelite.dto.response.WishlistResponse;
import com.estateelite.model.User;
import com.estateelite.service.CurrentUserService;
import com.estateelite.service.SearchHistoryService;
import com.estateelite.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final SearchHistoryService searchHistoryService;
    private final CurrentUserService currentUserService;

    @PostMapping("/wishlist")
    public ResponseEntity<WishlistResponse> add(@Valid @RequestBody WishlistRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(wishlistService.addToWishlist(request, buyer));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<WishlistResponse>> getMine() {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(wishlistService.getWishlist(buyer.getId()));
    }

    @PutMapping("/wishlist/{id}")
    public ResponseEntity<WishlistResponse> update(@PathVariable Long id, @Valid @RequestBody WishlistRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(wishlistService.updateWishlistItem(id, request, buyer));
    }

    @DeleteMapping("/wishlist/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        User buyer = currentUserService.getCurrentUser();
        wishlistService.removeFromWishlist(id, buyer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-history")
    public ResponseEntity<List<String>> getRecentSearches() {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(searchHistoryService.getRecent(buyer.getId()));
    }
}
