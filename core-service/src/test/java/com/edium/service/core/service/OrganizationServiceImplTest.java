package com.edium.service.core.service;

import com.edium.library.exception.BadRequestException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.service.core.model.Organization;
import com.edium.service.core.repository.OrganizationRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class OrganizationServiceImplTest {

    private OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);

    private OrganizationService organizationService = new OrganizationServiceImpl(organizationRepository);

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindById_withIdNotFound_thenException() {
        try {
            long orgId = 1L;

            Mockito.when(organizationRepository.findById(orgId)).thenReturn(Optional.empty());

            organizationService.findById(orgId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Organization");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test
    public void whenFindById_thenReturn() {
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        organization.setId(1L);

        Mockito.when(organizationRepository.findById(organization.getId())).thenReturn(Optional.of(organization));

        Assert.assertEquals(organizationService.findById(organization.getId()), organization);
    }

    @Test
    public void whenDelete_thenOk() {
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        organization.setId(1L);

        organizationService.delete(organization);
    }

    @Test
    public void whenSaveOrganization_thenReturn() {
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        organization.setId(1L);

        Mockito.when(organizationRepository.save(organization)).thenReturn(organization);

        Assert.assertEquals(organizationService.save(organization), organization);
    }

    @Test(expected = BadRequestException.class)
    public void whenGetAllOrganization_withPageNegative_thenException() {
        try {
            int page = -1, size = 10;

            organizationService.getAllOrganization(page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page number cannot be less than zero.");
            throw ex;
        }
    }

    @Test(expected = BadRequestException.class)
    public void whenGetAllOrganization_withSizeNegative_thenException() {
        try {
            int page = 0, size = -1;

            organizationService.getAllOrganization(page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page size cannot be less than zero.");
            throw ex;
        }
    }

    @Test
    public void whenGetAllOrganization_thenReturn() {
        int page = 0, size = 10;

        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        organization.setId(1L);

        List<Organization> organizations = Collections.singletonList(organization);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "name");
        Mockito.when(organizationRepository.findAll(pageable)).thenReturn(new PageImpl<>(organizations));

        Assert.assertEquals(organizationService.getAllOrganization(page, size).getContent(), organizations);
    }

}
