package com.estateelite.dto.response;

import com.estateelite.model.PropertyImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImageResponse {
    private Long id;
    private String imageUrl;

    public static PropertyImageResponse from(PropertyImage img) {
        return PropertyImageResponse.builder().id(img.getId()).imageUrl(img.getImageUrl()).build();
    }
}
