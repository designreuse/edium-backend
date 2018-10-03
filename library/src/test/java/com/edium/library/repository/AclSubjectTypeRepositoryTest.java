package com.edium.library.repository;

import com.edium.library.config.UnitTestShareConfig;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.repository.share.AclSubjectTypeRepository;
import org.hamcrest.CoreMatchers;
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

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
@ContextConfiguration(classes = UnitTestShareConfig.class)
public class AclSubjectTypeRepositoryTest {

    @Autowired
    private AclSubjectTypeRepository aclSubjectTypeRepository;

    @Test
    public void whenSave_withIdNull_thenException() {
        try {
            aclSubjectTypeRepository.save(new AclSubjectType(null, "test"));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("ids for this class must be manually assigned before calling save"));
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withTypeNull_thenException() {
        try {
            aclSubjectTypeRepository.saveAndFlush(new AclSubjectType(1L, null));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=type"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withTypeBlank_thenException() {
        try {
            aclSubjectTypeRepository.saveAndFlush(new AclSubjectType(1L, ""));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=type"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withTypeDuplicate_thenException() {
        try {
            aclSubjectTypeRepository.saveAndFlush(new AclSubjectType(1L, "test"));
            aclSubjectTypeRepository.saveAndFlush(new AclSubjectType(2L, "test"));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("ConstraintViolationException"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("could not execute statement"));
            throw ex;
        }
    }

}
