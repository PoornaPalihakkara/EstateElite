package com.estateelite.dto.response;

import com.estateelite.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long agentId;
    private String agentName;
    private Long buyerId;
    private String buyerName;
    private Integer rating;
    private String comment;
    private Boolean flagged;
    private String flagNote;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .propertyId(r.getProperty() != null ? r.getProperty().getId() : null)
                .propertyTitle(r.getProperty() != null ? r.getProperty().getTitle() : null)
                .agentId(r.getAgent() != null ? r.getAgent().getId() : null)
                .agentName(r.getAgent() != null ? r.getAgent().getFullName() : null)
                .buyerId(r.getBuyer().getId())
                .buyerName(r.getBuyer().getFullName())
                .rating(r.getRating())
                .comment(r.getComment())
                .flagged(r.getFlagged())
                .flagNote(r.getFlagNote())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
