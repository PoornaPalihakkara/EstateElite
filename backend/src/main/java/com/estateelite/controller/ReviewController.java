package com.estateelite.controller;

import com.estateelite.dto.request.FlagRequest;
import com.estateelite.dto.request.ReviewRequest;
import com.estateelite.dto.response.ReviewResponse;
import com.estateelite.model.User;
import com.estateelite.service.CurrentUserService;
import com.estateelite.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(reviewService.createReview(request, buyer));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ReviewResponse>> getByProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getByProperty(propertyId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<ReviewResponse>> getByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(reviewService.getByAgent(agentId));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<ReviewResponse>> getMine() {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(reviewService.getByBuyer(buyer.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(reviewService.updateReview(id, request, buyer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = currentUserService.getCurrentUser();
        reviewService.deleteReview(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/flag")
    public ResponseEntity<ReviewResponse> flag(@PathVariable Long id,
                                               @Valid @RequestBody FlagRequest request) {
        return ResponseEntity.ok(reviewService.flagReview(id, request));
    }

    @GetMapping("/flagged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getFlagged() {
        return ResponseEntity.ok(reviewService.getFlagged());
    }

    @PostMapping("/{id}/unflag")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> unflag(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.unflagReview(id));
    }
}
