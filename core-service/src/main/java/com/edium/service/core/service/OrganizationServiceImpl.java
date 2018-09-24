package com.edium.service.core.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.payload.PagedResponse;
import com.edium.library.util.Utils;
import com.edium.service.core.model.Organization;
import com.edium.service.core.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;

    @Override
    public PagedResponse<Organization> getAllOrganization(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "name");
        Page<Organization> organizations = organizationRepository.findAll(pageable);

        return new PagedResponse<>(organizations.getContent(), organizations.getNumber(),
                organizations.getSize(), organizations.getTotalElements(), organizations.getTotalPages(), organizations.isLast());
    }

    @Override
    public Organization findById(Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));
    }

    @Override
    public Organization save(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public void deleteById(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        organizationRepository.delete(organization);
    }
}