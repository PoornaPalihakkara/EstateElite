package com.estateelite.service;

import com.estateelite.dto.request.WishlistRequest;
import com.estateelite.dto.response.WishlistResponse;
import com.estateelite.exception.AccessDeniedCustomException;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.Property;
import com.estateelite.model.User;
import com.estateelite.model.WishlistItem;
import com.estateelite.repository.PropertyRepository;
import com.estateelite.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public WishlistResponse addToWishlist(WishlistRequest request, User buyer) {
        if (wishlistItemRepository.findByBuyerIdAndPropertyId(buyer.getId(), request.getPropertyId()).isPresent()) {
            throw new BadRequestException("This property is already in your wishlist");
        }
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        WishlistItem item = WishlistItem.builder()
                .buyer(buyer)
                .property(property)
                .note(request.getNote())
                .priority(request.getPriority())
                .targetPrice(request.getTargetPrice())
                .build();
        wishlistItemRepository.save(item);
        return WishlistResponse.from(item);
    }

    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(Long buyerId) {
        return wishlistItemRepository.findByBuyerId(buyerId)
                .stream().map(WishlistResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public WishlistResponse updateWishlistItem(Long id, WishlistRequest request, User buyer) {
        WishlistItem item = findOrThrow(id);
        assertOwnership(item, buyer);

        item.setNote(request.getNote());
        if (request.getPriority() != null) item.setPriority(request.getPriority());
        item.setTargetPrice(request.getTargetPrice());

        wishlistItemRepository.save(item);
        return WishlistResponse.from(item);
    }

    @Transactional
    public void removeFromWishlist(Long id, User buyer) {
        WishlistItem item = findOrThrow(id);
        assertOwnership(item, buyer);
        wishlistItemRepository.delete(item);
    }

    private WishlistItem findOrThrow(Long id) {
        return wishlistItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
    }

    private void assertOwnership(WishlistItem item, User buyer) {
        if (!item.getBuyer().getId().equals(buyer.getId())) {
            throw new AccessDeniedCustomException("This wishlist item does not belong to you");
        }
    }
}
