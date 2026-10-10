package com.estateelite.service;

import com.estateelite.dto.request.InquiryRequest;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.Agency;
import com.estateelite.model.NotificationType;
import com.estateelite.model.Role;
import com.estateelite.model.User;
import com.estateelite.repository.AgencyRepository;
import com.estateelite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final AgencyRepository agencyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void submitInquiry(InquiryRequest request) {
        String target = "Admin";
        if (request.getAgencyId() != null) {
            Agency agency = agencyRepository.findById(request.getAgencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
            target = agency.getName();
        }

        List<User> admins = userRepository.findByRole(Role.ADMIN);
        String message = "New inquiry from " + request.getName() + " (" + request.getEmail() + ") for " +
                target + ": " + request.getMessage();

        for (User admin : admins) {
            notificationService.notify(admin, message, NotificationType.GENERAL);
        }
    }
}
