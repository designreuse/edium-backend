package com.edium.library.repository;

import com.edium.library.config.AuditingConfig;
import com.edium.library.config.UnitTestCoreConfig;
import com.edium.library.model.core.Role;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.model.core.UserRole;
import com.edium.library.repository.core.UserRoleRepository;
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
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
@ContextConfiguration(classes = {UnitTestCoreConfig.class, AuditingConfig.class})
public class UserRoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test(expected = Exception.class)
    public void whenSaveUserRole_withRoleNull_thenException() {
        try {
            User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
            entityManager.persist(user);

            UserOrganization userOrganization = new UserOrganization(user, 1L, 2L);
            entityManager.persist(userOrganization);

            UserRole userRole = new UserRole(userOrganization, null);
            entityManager.persistAndFlush(userRole);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be null", "propertyPath=role")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUserRole_withUserOrganizationNull_thenException() {
        try {
            Role role = new Role("test", "test");
            entityManager.persist(role);

            UserRole userRole = new UserRole(null, role);
            entityManager.persistAndFlush(userRole);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be null", "propertyPath=userOrganization")));
            throw ex;
        }
    }

    @Test
    public void whenSaveUserRole_thenReturn() {
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persist(user);

        UserOrganization userOrganization = new UserOrganization(user, 1L, 2L);
        entityManager.persist(userOrganization);

        Role role = new Role("test", "test");
        entityManager.persist(role);

        UserRole userRole = new UserRole(userOrganization, role);
        entityManager.persistAndFlush(userRole);

        Assert.assertNotNull(userRole.getId());
    }

    @Test
    public void whenGetByUserOrganization_Id_thenReturn() {
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persist(user);

        UserOrganization userOrganization = new UserOrganization(user, 1L, 2L);
        entityManager.persist(userOrganization);

        Role role = new Role("test", "test");
        entityManager.persist(role);

        UserRole userRole = new UserRole(userOrganization, role);
        entityManager.persistAndFlush(userRole);

        List<UserRole> userRoles = userRoleRepository.getByUserOrganization_Id(userOrganization.getId());

        Assert.assertEquals(userRoles.get(0).getId(), userRole.getId());
    }

    @Test
    public void whenGetByUserId_thenReturn() {
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persist(user);

        UserOrganization userOrganization = new UserOrganization(user, 1L, 2L);
        entityManager.persist(userOrganization);

        Role role = new Role("test", "test");
        entityManager.persist(role);

        UserRole userRole = new UserRole(userOrganization, role);
        entityManager.persistAndFlush(userRole);

        List<UserRole> userRoles = userRoleRepository.getByUserId(user.getId());

        Assert.assertEquals(userRoles.get(0).getId(), userRole.getId());
    }

}
