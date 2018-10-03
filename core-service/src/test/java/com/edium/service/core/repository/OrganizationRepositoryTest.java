package com.edium.service.core.repository;

import com.edium.service.core.model.Organization;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
public class OrganizationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test(expected = Exception.class)
    public void whenSaveOrg_withNameBlank_thenException() {
        try {
            Organization organization = new Organization();
            entityManager.persist(organization);
            entityManager.flush();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=name"));
            throw ex;
        }
    }

    @Test
    public void whenGetById_thenReturnOrganization() {
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        entityManager.persist(organization);
        entityManager.flush();

        Optional<Organization> org = organizationRepository.findById(organization.getId());
        Assert.assertTrue(org.isPresent());
        Assert.assertEquals(org.get().getName(), organization.getName());
    }

    @Test
    public void whenSaveOrganization_thenReturn() {
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        organizationRepository.saveAndFlush(organization);

        Organization org = entityManager.find(Organization.class, organization.getId());

        Assert.assertEquals(org.getName(), organization.getName());
    }

    @Test
    public void whenFindAll_thenReturn() {
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        entityManager.persist(organization);
        entityManager.flush();

        Organization organization1 = new Organization(String.valueOf(System.currentTimeMillis()));
        entityManager.persist(organization1);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "name");

        Page<Organization> organizations = organizationRepository.findAll(pageable);

        Assert.assertEquals(organizations.getTotalElements(), 2);
        Assert.assertTrue(organizations.getContent().stream().anyMatch(organization2 -> organization2.getName().equals(organization.getName())));
    }

}
