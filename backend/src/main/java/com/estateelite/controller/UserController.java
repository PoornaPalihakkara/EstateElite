package com.estateelite.controller;

import com.estateelite.dto.request.ProfileUpdateRequest;
import com.estateelite.dto.response.UserResponse;
import com.estateelite.model.Role;
import com.estateelite.model.User;
import com.estateelite.service.CurrentUserService;
import com.estateelite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        User me = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.getProfile(me.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@RequestBody ProfileUpdateRequest request) {
        User me = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(me.getId(), request));
    }

    @PostMapping(value = "/me/photo", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateMyPhoto(@RequestParam("file") MultipartFile file) {
        User me = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.updateProfileImage(me.getId(), file));
    }

    @GetMapping("/agents")
    public ResponseEntity<List<UserResponse>> getAllAgents(
            @RequestParam(required = false) String branch) {
        if (branch != null && !branch.isBlank()) {
            return ResponseEntity.ok(userService.getAgentsByBranch(branch.trim()));
        }
        return ResponseEntity.ok(userService.getByRole(Role.AGENT));
    }

    @GetMapping("/buyers")
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllBuyers() {
        return ResponseEntity.ok(userService.getByRole(Role.BUYER));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
}
