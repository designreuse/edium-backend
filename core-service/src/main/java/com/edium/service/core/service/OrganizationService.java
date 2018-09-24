package com.edium.service.core.service;

import com.edium.library.payload.PagedResponse;
import com.edium.service.core.model.Organization;

public interface OrganizationService {

    PagedResponse<Organization> getAllOrganization(int page, int size);

    Organization findById(Long organizationId);

    Organization save(Organization organization);

    void deleteById(Long organizationId);

}
