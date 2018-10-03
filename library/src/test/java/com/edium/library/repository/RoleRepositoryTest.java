package com.edium.library.repository;

import com.edium.library.config.UnitTestCoreConfig;
import com.edium.library.model.core.Role;
import com.edium.library.repository.core.RoleRepository;
import com.google.common.base.Throwables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
@ContextConfiguration(classes = UnitTestCoreConfig.class)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test(expected = Exception.class)
    public void whenSaveRole_withCodeBlank_thenException() {
        try {
            Role role = new Role("", String.valueOf(System.currentTimeMillis()));
            roleRepository.saveAndFlush(role);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=code")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveRole_withLabelBlank_thenException() {
        try {
            Role role = new Role(String.valueOf(System.currentTimeMillis()), "");
            roleRepository.saveAndFlush(role);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=label")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveRole_withDuplicateCode_thenException() {
        try {
            long timestamp = System.currentTimeMillis();

            roleRepository.saveAndFlush(new Role(String.valueOf(timestamp), "test"));
            roleRepository.saveAndFlush(new Role(String.valueOf(timestamp), "test1"));
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("Duplicate entry", "for key 'uk_role_code'")));
            throw ex;
        }
    }

}
