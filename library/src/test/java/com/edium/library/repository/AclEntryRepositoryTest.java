package com.edium.library.repository;

import com.edium.library.config.UnitTestConfig;
import com.edium.library.model.share.AclEntry;
import com.edium.library.repository.share.AclEntryRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
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
@ContextConfiguration(classes = UnitTestConfig.class)
public class AclEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AclEntryRepository aclEntryRepository;

    private AclEntry entry1, entry2, entry3, entry4, entry5, entry6;

    @Before
    public void setup() {
        entry1 = new AclEntry(1l, 1l, 1l,
                2l, true, false, false, null, false);
        entry2 = new AclEntry(1l, 2l, 1l,
                2l, false, true, false, null, false);
        entry3 = new AclEntry(1l, 3l, 1l,
                2l, false, false, true, null, false);

        entry4 = new AclEntry(1l, 4l, 1l,
                3l, true, false, false, null, true);
        entry5 = new AclEntry(1l, 5l, 1l,
                3l, false, true, false, null, true);
        entry6 = new AclEntry(1l, 6l, 1l,
                3l, false, false, true, null, true);

        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);
        entityManager.persist(entry5);
        entityManager.persist(entry6);
        entityManager.flush();
    }

    @Test
    public void whenFindEntryPermissionRead_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionRead(1l, 2l,
                1l, 1l, 2l, Arrays.asList(new Long[] {1l, 2l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 3);
    }

    @Test
    public void whenFindEntryPermissionReadInherit_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionRead(1l, 2l,
                1l, 1l, 4l, Arrays.asList(new Long[] {1l, 3l, 4l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 3);
    }

    @Test
    public void whenFindEntryPermissionReadResource_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionReadResource(1l, entry1.getResourceId(), 2l,
                1l, 1l, 2l, Arrays.asList(new Long[] {1l, 2l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry1.getResourceId())));
    }

    @Test
    public void whenFindEntryPermissionWrite_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionWrite(1l, 2l,
                1l, 1l, 2l, Arrays.asList(new Long[] {1l, 2l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry2.getResourceId())));
    }

    @Test
    public void whenFindEntryPermissionWriteInherit_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionWrite(1l, 2l,
                1l, 1l, 4l, Arrays.asList(new Long[] {1l, 3l, 4l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry5.getResourceId())));
    }

    @Test
    public void whenFindEntryPermissionWriteResource_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionWriteResource(1l, entry2.getResourceId(), 2l,
                1l, 1l, 2l, Arrays.asList(new Long[] {1l, 2l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry2.getResourceId())));
    }

    @Test
    public void whenFindEntryPermissionDelete_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionDelete(1l, 2l,
                1l, 1l, 2l, Arrays.asList(new Long[] {1l, 2l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry3.getResourceId())));
    }

    @Test
    public void whenFindEntryPermissionDeleteInherit_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionDelete(1l, 2l,
                1l, 1l, 4l, Arrays.asList(new Long[] {1l, 3l, 4l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry6.getResourceId())));
    }

    @Test
    public void whenFindEntryPermissionDeleteResource_thenReturnEntries() {
        // when
        List<AclEntry> entries = aclEntryRepository.findEntryPermissionDeleteResource(1l, entry3.getResourceId(), 2l,
                1l, 1l, 2l, Arrays.asList(new Long[] {1l, 2l}));

        // then
        Assert.assertNotNull(entries);
        Assert.assertEquals(entries.size(), 1);
        Assert.assertTrue(entries.stream().anyMatch(aclEntry -> aclEntry.getResourceId().equals(entry3.getResourceId())));
    }

    @Test
    public void whenSave_withResourceTypeNull_thenException() {
        try {
            AclEntry entry = new AclEntry(null, 1l, 2l,
                    2l, true, false, false, null, false);

            aclEntryRepository.save(entry);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be null"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=resourceTypeId"));
        }
    }

    @Test
    public void whenSave_withResourceIdNull_thenException() {
        try {
            AclEntry entry = new AclEntry(1l, null, 2l,
                    2l, true, false, false, null, false);

            aclEntryRepository.save(entry);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be null"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=resourceId"));
        }
    }

    @Test
    public void whenSave_withSubjectTypeNull_thenException() {
        try {
            AclEntry entry = new AclEntry(1l, 1l, null,
                    2l, true, false, false, null, false);

            aclEntryRepository.save(entry);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be null"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=subjectTypeId"));
        }
    }

    @Test(expected = Exception.class)
    public void whenSave_withSubjectIdNull_thenException() {
        try {
            AclEntry entry = new AclEntry(1l, 1l, 2l,
                    null, true, false, false, null, false);

            aclEntryRepository.save(entry);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be null"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=subjectId"));
            throw ex;
        }
    }

}
