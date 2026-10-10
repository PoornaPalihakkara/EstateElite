package com.estateelite.service;

import com.estateelite.dto.request.FlagRequest;
import com.estateelite.dto.request.ReviewRequest;
import com.estateelite.dto.response.ReviewResponse;
import com.estateelite.exception.AccessDeniedCustomException;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.*;
import com.estateelite.repository.PropertyRepository;
import com.estateelite.repository.ReviewRepository;
import com.estateelite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    @Transactional
    public ReviewResponse createReview(ReviewRequest request, User buyer) {
        if (request.getPropertyId() == null && request.getAgentId() == null) {
            throw new BadRequestException("A review must target either a property or an agent");
        }

        Property property = null;
        User agent = null;
        User targetOwner = null;

        if (request.getPropertyId() != null) {
            property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            targetOwner = property.getSeller();
        }
        if (request.getAgentId() != null) {
            agent = userRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
            if (agent.getRole() != Role.AGENT) {
                throw new BadRequestException("Reviews targeting a person must target an AGENT role user");
            }
            targetOwner = agent;
        }

        Review review = Review.builder()
                .property(property)
                .agent(agent)
                .buyer(buyer)
                .rating(request.getRating())
                .comment(request.getComment())
                .flagged(false)
                .build();
        reviewRepository.save(review);

        if (targetOwner != null) {
            notificationService.notify(targetOwner,
                    buyer.getFullName() + " left a " + request.getRating() + "-star review",
                    NotificationType.NEW_REVIEW);
        }

        return ReviewResponse.from(review);
    }


    @Transactional(readOnly = true)
    public List<ReviewResponse> getByProperty(Long propertyId) {
        return reviewRepository.findByPropertyId(propertyId).stream().map(ReviewResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getByAgent(Long agentId) {
        return reviewRepository.findByAgentId(agentId).stream().map(ReviewResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getByBuyer(Long buyerId) {
        return reviewRepository.findByBuyerId(buyerId).stream().map(ReviewResponse::from).collect(Collectors.toList());
    }


    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request, User buyer) {
        Review review = findOrThrow(id);
        if (!review.getBuyer().getId().equals(buyer.getId())) {
            throw new AccessDeniedCustomException("You can only edit your own reviews");
        }
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);
        return ReviewResponse.from(review);
    }


    @Transactional
    public void deleteReview(Long id, User actingUser) {
        Review review = findOrThrow(id);
        boolean isOwner = review.getBuyer().getId().equals(actingUser.getId());
        boolean isAdmin = actingUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedCustomException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }


    @Transactional
    public ReviewResponse flagReview(Long id, FlagRequest request) {
        Review review = findOrThrow(id);
        review.setFlagged(true);
        review.setFlagNote(request.getNote().trim());
        reviewRepository.save(review);

        notificationService.notify(review.getBuyer(),
                "Your review was flagged for moderation and is under Admin review",
                NotificationType.REVIEW_FLAGGED);

        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getFlagged() {
        return reviewRepository.findByFlaggedTrue().stream().map(ReviewResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse unflagReview(Long id) {
        Review review = findOrThrow(id);
        review.setFlagged(false);
        review.setFlagNote(null);
        reviewRepository.save(review);
        return ReviewResponse.from(review);
    }

    private Review findOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }
}
