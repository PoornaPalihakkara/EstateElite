package com.estateelite.controller;

import com.estateelite.dto.request.InquiryRequest;
import com.estateelite.dto.response.ApiMessageResponse;
import com.estateelite.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<ApiMessageResponse> submit(@Valid @RequestBody InquiryRequest request) {
        inquiryService.submitInquiry(request);
        return ResponseEntity.ok(new ApiMessageResponse("Your inquiry has been sent. We'll get back to you soon."));
    }
}
