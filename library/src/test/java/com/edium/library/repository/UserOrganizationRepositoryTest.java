package com.edium.library.repository;

import com.edium.library.config.AuditingConfig;
import com.edium.library.config.UnitTestCoreConfig;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.repository.core.UserOrganizationRepository;
import com.google.common.base.Throwables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
@ContextConfiguration(classes = {UnitTestCoreConfig.class, AuditingConfig.class})
public class UserOrganizationRepositoryTest {

    @Autowired
    private UserOrganizationRepository userOrganizationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test(expected = Exception.class)
    public void whenSaveObject_withUserNull_thenException() {
        try {
            userOrganizationRepository.saveAndFlush(new UserOrganization(null, 1L, 1L));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be null", "propertyPath=user")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveObject_withGroupIdNull_thenException() {
        try {
            User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "12345678");
            entityManager.persist(user);

            userOrganizationRepository.saveAndFlush(new UserOrganization(user, 1L, null));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be null", "propertyPath=groupId")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveObject_withOrganizationIdNull_thenException() {
        try {
            User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "12345678");
            entityManager.persist(user);

            userOrganizationRepository.saveAndFlush(new UserOrganization(user, null, 1L));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be null", "propertyPath=organizationId")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveObject_withDuplicateUserOrg_thenException() {
        try {
            User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "12345678");
            entityManager.persist(user);

            userOrganizationRepository.saveAndFlush(new UserOrganization(user, 1L, 1L));
            userOrganizationRepository.saveAndFlush(new UserOrganization(user, 1L, 2L));
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("Duplicate entry", "for key 'uk_user_organization'")));
            throw ex;
        }
    }

    @Test
    public void whenSaveObject_thenReturn() {
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "12345678");
        entityManager.persist(user);

        UserOrganization userOrganization = userOrganizationRepository.saveAndFlush(new UserOrganization(user, 1L, 1L));

        Assert.assertNotNull(userOrganization.getId());
    }
}
