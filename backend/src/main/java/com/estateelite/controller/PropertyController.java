package com.estateelite.controller;

import com.estateelite.dto.request.PropertyRequest;
import com.estateelite.dto.request.PropertyStatusUpdateRequest;
import com.estateelite.dto.response.PropertyResponse;
import com.estateelite.model.User;
import com.estateelite.service.CurrentUserService;
import com.estateelite.service.PropertySearchService;
import com.estateelite.service.PropertyService;
import com.estateelite.service.SearchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertySearchService propertySearchService;
    private final SearchHistoryService searchHistoryService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest request) {
        User seller = currentUserService.getCurrentUser();
        return ResponseEntity.ok(propertyService.createProperty(request, seller));
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAll() {
        return ResponseEntity.ok(propertyService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> search(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minBedrooms) {

        try {
            User buyer = currentUserService.getCurrentUser();
            String queryDescription = "location=" + location + ", price=" + minPrice + "-" + maxPrice + ", bedrooms>=" + minBedrooms;
            searchHistoryService.recordSearch(buyer, queryDescription);
        } catch (Exception ignored) {
            // Unauthenticated visitors can still search; history is only kept for logged-in buyers.
        }

        return ResponseEntity.ok(propertySearchService.search(location, minPrice, maxPrice, minBedrooms));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<PropertyResponse>> getMine() {
        User seller = currentUserService.getCurrentUser();
        return ResponseEntity.ok(propertyService.getBySeller(seller.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getByIdAndIncrementViews(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PropertyResponse> update(@PathVariable Long id, @Valid @RequestBody PropertyRequest request) {
        User seller = currentUserService.getCurrentUser();
        return ResponseEntity.ok(propertyService.updateProperty(id, request, seller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PropertyResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody PropertyStatusUpdateRequest request) {
        User seller = currentUserService.getCurrentUser();
        return ResponseEntity.ok(propertyService.updateStatus(id, request, seller));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User seller = currentUserService.getCurrentUser();
        propertyService.deleteProperty(id, seller);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PropertyResponse> addImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        User seller = currentUserService.getCurrentUser();
        return ResponseEntity.ok(propertyService.addImage(id, file, seller));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        User seller = currentUserService.getCurrentUser();
        propertyService.deleteImage(id, imageId, seller);
        return ResponseEntity.noContent().build();
    }
}
