package com.estateelite.service;

import com.estateelite.dto.request.ProfileUpdateRequest;
import com.estateelite.dto.response.UserResponse;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.Role;
import com.estateelite.model.User;
import com.estateelite.repository.UserRepository;
import com.estateelite.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserResponse.from(findOrThrow(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = findOrThrow(userId);
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBranch() != null) {
            // Agents can update their branch district; clear only if blank intentionally
            String b = request.getBranch().isBlank() ? null : request.getBranch().trim();
            if (user.getRole() == Role.AGENT && b == null) {
                throw new BadRequestException("Agents must have a branch district");
            }
            user.setBranch(b);
        }
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfileImage(Long userId, MultipartFile file) {
        User user = findOrThrow(userId);
        String url = fileStorageService.store(file);
        user.setProfileImage(url);
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getByRole(Role role) {
        return userRepository.findByRole(role).stream().map(UserResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAgentsByBranch(String branch) {
        return userRepository.findByRoleAndBranchIgnoreCase(Role.AGENT, branch)
                .stream().map(UserResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserResponse::from).collect(Collectors.toList());
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
