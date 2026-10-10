package com.estateelite.controller;

import com.estateelite.dto.request.AgencyRequest;
import com.estateelite.dto.response.AgencyResponse;
import com.estateelite.model.AgencyStatus;
import com.estateelite.service.AgencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgencyResponse> create(@Valid @RequestBody AgencyRequest request) {
        return ResponseEntity.ok(agencyService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AgencyResponse>> getAll() {
        return ResponseEntity.ok(agencyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(agencyService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgencyResponse> update(@PathVariable Long id, @Valid @RequestBody AgencyRequest request) {
        return ResponseEntity.ok(agencyService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgencyResponse> setStatus(@PathVariable Long id, @RequestParam AgencyStatus status) {
        return ResponseEntity.ok(agencyService.setStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agencyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
