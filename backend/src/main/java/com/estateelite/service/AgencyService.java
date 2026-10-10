package com.estateelite.service;

import com.estateelite.dto.request.AgencyRequest;
import com.estateelite.dto.response.AgencyResponse;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.Agency;
import com.estateelite.model.AgencyStatus;
import com.estateelite.repository.AgencyRepository;
import com.estateelite.pattern.factory.AgencyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;

    @Transactional
    public AgencyResponse create(AgencyRequest request) {
        if (agencyRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new BadRequestException("An agency with this license number already exists");
        }
        // FACTORY PATTERN (Member 4): centralises construction rules & defaults
        Agency agency = AgencyFactory.createFromRequest(request);
        agencyRepository.save(agency);
        return AgencyResponse.from(agency);
    }

    @Transactional(readOnly = true)
    public List<AgencyResponse> getAll() {
        return agencyRepository.findAll().stream().map(AgencyResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgencyResponse getById(Long id) {
        return AgencyResponse.from(findOrThrow(id));
    }

    @Transactional
    public AgencyResponse update(Long id, AgencyRequest request) {
        Agency agency = findOrThrow(id);
        agency.setName(request.getName());
        agency.setContactEmail(request.getContactEmail());
        agency.setContactPhone(request.getContactPhone());
        agency.setAddress(request.getAddress());
        agencyRepository.save(agency);
        return AgencyResponse.from(agency);
    }

    @Transactional
    public AgencyResponse setStatus(Long id, AgencyStatus status) {
        Agency agency = findOrThrow(id);
        // FACTORY PATTERN helper keeps status mutation consistent
        AgencyFactory.withStatus(agency, status);
        agencyRepository.save(agency);
        return AgencyResponse.from(agency);
    }

    @Transactional
    public void delete(Long id) {
        Agency agency = findOrThrow(id);
        agencyRepository.delete(agency);
    }

    private Agency findOrThrow(Long id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found with id " + id));
    }
}
