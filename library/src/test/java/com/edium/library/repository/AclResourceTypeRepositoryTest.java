package com.edium.library.repository;

import com.edium.library.config.UnitTestConfig;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.repository.share.AclResourceTypeRepository;
import org.hamcrest.CoreMatchers;
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

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
@ContextConfiguration(classes = UnitTestConfig.class)
public class AclResourceTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AclResourceTypeRepository aclResourceTypeRepository;

    @Test
    public void whenSave_withIdNull_thenException() {
        try {
            aclResourceTypeRepository.save(new AclResourceType(null, "test"));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("ids for this class must be manually assigned before calling save"));
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withTypeNull_thenException() {
        try {
            aclResourceTypeRepository.saveAndFlush(new AclResourceType(1L, null));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=type"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withTypeBlank_thenException() {
        try {
            aclResourceTypeRepository.saveAndFlush(new AclResourceType(1L, ""));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=type"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withTypeDuplicate_thenException() {
        try {
            aclResourceTypeRepository.saveAndFlush(new AclResourceType(1L, "test"));
            aclResourceTypeRepository.saveAndFlush(new AclResourceType(2L, "test"));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("ConstraintViolationException"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("could not execute statement"));
            throw ex;
        }
    }

}
