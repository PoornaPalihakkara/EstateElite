package com.estateelite.controller;

import com.estateelite.dto.request.AppointmentRequest;
import com.estateelite.dto.request.AppointmentStatusUpdateRequest;
import com.estateelite.dto.request.SlotRequest;
import com.estateelite.dto.response.AppointmentResponse;
import com.estateelite.dto.response.SlotResponse;
import com.estateelite.model.User;
import com.estateelite.service.AppointmentService;
import com.estateelite.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CurrentUserService currentUserService;

    @PostMapping("/slots")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<SlotResponse> createSlot(@Valid @RequestBody SlotRequest request) {
        User agent = currentUserService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.createSlot(request, agent));
    }

    @PutMapping("/slots/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<SlotResponse> updateSlot(@PathVariable Long id, @Valid @RequestBody SlotRequest request) {
        User agent = currentUserService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.updateSlot(id, request, agent));
    }

    @DeleteMapping("/slots/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        User agent = currentUserService.getCurrentUser();
        appointmentService.deleteSlot(id, agent);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slots/{agentId}")
    public ResponseEntity<List<SlotResponse>> getOpenSlots(@PathVariable Long agentId) {
        return ResponseEntity.ok(appointmentService.getOpenSlots(agentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<AppointmentResponse> request(@Valid @RequestBody AppointmentRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.requestAppointment(request, buyer));
    }

    @GetMapping("/agent")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<AppointmentResponse>> getMineAsAgent() {
        User agent = currentUserService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.getByAgent(agent.getId()));
    }

    @GetMapping("/buyer")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<AppointmentResponse>> getMineAsBuyer() {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.getByBuyer(buyer.getId()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('AGENT') or hasRole('BUYER') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(appointmentService.updateStatus(id, request, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT') or hasRole('BUYER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = currentUserService.getCurrentUser();
        appointmentService.deleteAppointment(id, user);
        return ResponseEntity.noContent().build();
    }
}
