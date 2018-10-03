package com.edium.library.repository;

import com.edium.library.config.UnitTestCoreConfig;
import com.edium.library.model.core.Authority;
import com.edium.library.repository.core.AuthorityRepository;
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
public class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test(expected = Exception.class)
    public void whenSaveRole_withCodeBlank_thenException() {
        try {
            authorityRepository.saveAndFlush(new Authority(""));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("must not be blank", "propertyPath=code")));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveRole_withDuplicateCode_thenException() {
        try {
            long timestamp = System.currentTimeMillis();

            authorityRepository.saveAndFlush(new Authority(String.valueOf(timestamp)));
            authorityRepository.saveAndFlush(new Authority(String.valueOf(timestamp)));
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.stringContainsInOrder(Arrays.asList("Duplicate entry", "for key 'uk_authority_code'")));
            throw ex;
        }
    }

}
