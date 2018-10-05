package com.edium.library.repository;

import com.edium.library.config.AuditingConfig;
import com.edium.library.config.UnitTestCoreConfig;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.repository.core.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
@ContextConfiguration(classes = {UnitTestCoreConfig.class, AuditingConfig.class})
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test(expected = Exception.class)
    public void whenSaveUser_withNameNull_thenException() {
        try {
            User user = new User(null, "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=name")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withNameGreaterThan40_thenException() {
        try {
            User user = new User("12345678912345678912345678912345678912345", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("size must be between 0 and 40", "propertyPath=name")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withUsernameNull_thenException() {
        try {
            User user = new User("test" + System.currentTimeMillis(), null, "test@gmail.com", "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=username")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withUsernameLessThan8_thenException() {
        try {
            User user = new User("test" + System.currentTimeMillis(), "1234567", "test@gmail.com", "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("size must be between 8 and 255", "propertyPath=username")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withUsernameGreaterThan255_thenException() {
        try {
            String username = "";
            while (username.length() <= 255) {
                username += new Random().nextInt(10);
            }

            User user = new User("test" + System.currentTimeMillis(), username, "test@gmail.com", "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("size must be between 8 and 255", "propertyPath=username")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withUsernameDuplicate_thenException() {
        long timestamp = System.currentTimeMillis();
        try {
            User user = new User("test" + timestamp, "test" + timestamp, "test@gmail.com", "123456789");
            entityManager.persist(user);

            User user1 = new User("test1" + timestamp, "test" + timestamp, "test1@gmail.com", "123456789");
            entityManager.persist(user1);
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Duplicate entry 'test" + timestamp + "' for key 'uk_user_username'"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withEmailNull_thenException() {
        try {
            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), null, "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=email")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withEmailGreaterThan40_thenException() {
        try {
            String email = "";
            while (email.length() <= 40) {
                email += new Random().nextInt(10);
            }

            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), email, "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("size must be between 0 and 40", "propertyPath=email")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withEmailInvalid_thenException() {
        try {
            String email = "test";

            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), email, "123456789");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must be a well-formed email address", "propertyPath=email")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withEmailDuplicate_thenException() {
        long timestamp = System.currentTimeMillis();
        String email = "test" + timestamp + "@gmail.com";
        try {
            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), email, "123456789");
            entityManager.persist(user);

            User user1 = new User("test1" + System.currentTimeMillis(), "test1" + System.currentTimeMillis(), email, "123456789");
            entityManager.persist(user1);
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Duplicate entry '" + email + "' for key 'uk_user_email'"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withPasswordNull_thenException() {
        try {
            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", "");
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=password")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withPasswordLessThan8_thenException() {
        try {
            String password = "1234567";

            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", password);
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("size must be between 8 and 255", "propertyPath=password")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveUser_withPasswordGreaterThan255_thenException() {
        try {
            String password = "";
            while (password.length() <= 255) {
                password += new Random().nextInt(10);
            }

            User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", password);
            entityManager.persist(user);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("size must be between 8 and 255", "propertyPath=password")));
            throw ex;
        }
    }

    @Test
    public void whenFindByUsernameOrEmail_byEmail_thenReturn() {
        User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persistAndFlush(user);

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(user.getEmail(), user.getEmail());

        Assert.assertTrue(optionalUser.isPresent());
        Assert.assertEquals(optionalUser.get().getId(), user.getId());
    }

    @Test
    public void whenFindByUsernameOrEmail_byUsername_thenReturn() {
        User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persistAndFlush(user);

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername());

        Assert.assertTrue(optionalUser.isPresent());
        Assert.assertEquals(optionalUser.get().getId(), user.getId());
    }

    @Test
    public void whenExistsByEmail_thenReturn() {
        User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persistAndFlush(user);

        Assert.assertTrue(userRepository.existsByEmail(user.getEmail()));
    }

    @Test
    public void whenExistsByUsername_thenReturn() {
        User user = new User("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        entityManager.persistAndFlush(user);

        Assert.assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    public void whenFindByOrganizationId_thenReturn() {
        Pageable pageable = PageRequest.of(0, 10);
        long timestamp = System.currentTimeMillis();

        User user = new User("test" + timestamp, "test" + timestamp, "test@gmail.com", "123456789");
        entityManager.persist(user);

        UserOrganization userOrganization = new UserOrganization(user, 1L, 2L);
        entityManager.persist(userOrganization);

        Page<User> users = userRepository.findByOrganizationId(userOrganization.getOrganizationId(), pageable);

        Assert.assertEquals(users.getContent().get(0).getId(), user.getId());
    }

}
