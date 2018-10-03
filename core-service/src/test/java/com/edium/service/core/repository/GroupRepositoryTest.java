package com.edium.service.core.repository;

import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.google.common.base.Throwables;
import org.hamcrest.CoreMatchers;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.PersistenceException;
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
public class GroupRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void whenGetAllChildrenGroups_thenReturnGroup() {
        // setup
        Organization organization = new Organization("org_test");
        entityManager.persist(organization);

        Group group = new Group("group_test", 1L, organization, 1L);
        group.setEncodedRootPath("11-12");
        entityManager.persist(group);
        entityManager.flush();

        // when
        List<Group> groups = groupRepository.getAllChildrenGroups(1L);
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.get(0).getId(), group.getId());
    }

    @Test
    public void whenGetTreeGroupByGroupPath_thenReturnGroup() {
        // setup
        Organization organization = new Organization("org_test");
        entityManager.persist(organization);

        Group group = new Group("group_test", 1L, organization, 1L);
        group.setEncodedRootPath("11-12");
        entityManager.persist(group);
        entityManager.flush();

        // when
        List<Group> groups = groupRepository.getTreeGroupByGroupPath("11");
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.get(0).getId(), group.getId());
    }

    @Test
    public void whenGetTreeGroupOfOrganization_thenReturnGroup() {
        // setup
        Organization organization = new Organization("org_test");
        entityManager.persist(organization);

        Group group = new Group("group_test", 1L, organization, 1L);
        entityManager.persist(group);
        entityManager.flush();

        // when
        List<Group> groups = groupRepository.getTreeGroupOfOrganization(organization.getId());
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.get(0).getId(), group.getId());
    }

    @Test
    public void whenGetRootGroupOfOrganization_thenReturnGroup() {
        // setup
        Organization organization = new Organization("org_test");
        entityManager.persist(organization);

        Group group = new Group("group_test", null, organization, 0L);
        entityManager.persist(group);
        entityManager.flush();

        // when
        List<Group> groups = groupRepository.getRootGroupOfOrganization(organization.getId());
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.get(0).getId(), group.getId());
    }

    @Test
    public void whenGetParentOfGroup_thenReturnGroup() {
        // setup
        Organization organization = new Organization("org_test");
        entityManager.persist(organization);

        Group group = new Group("group_test", null, organization, 0L);
        entityManager.persist(group);

        Group group1 = new Group("group_test_children", group.getId(), organization, 1L);
        entityManager.persist(group1);
        entityManager.flush();

        // when
        Group parentOfGroup = groupRepository.getParentOfGroup(group1.getId());
        Assert.assertEquals(parentOfGroup.getId(), group.getId());
    }

    @Test
    public void whenGetGroupsOfUser_thenReturnGroup() {
        // setup
        Organization organization = new Organization("org_test");
        entityManager.persist(organization);

        Group group = new Group("group_test", null, organization, 0L);
        entityManager.persist(group);

        User user = new User("test", "test12345", "test@gmail.com", "123456789");
        entityManager.persist(user);

        UserOrganization userOrganization = new UserOrganization(user, organization.getId(), group.getId());
        entityManager.persist(userOrganization);
        entityManager.flush();

        // when
        Pageable pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "name"));
        Page<Group> groups = groupRepository.getGroupsOfUser(user.getId(), pageable);

        // then
        Assert.assertEquals(groups.getTotalElements(), 1);
        Assert.assertEquals(groups.getContent().get(0).getId(), group.getId());
    }

    @Test(expected = Exception.class)
    public void whenSaveGroup_withNameBlank_thenException() {
        try {
            Organization organization = new Organization("org_test");
            entityManager.persist(organization);

            Group group = new Group("", null, organization, 0L);
            entityManager.persist(group);
            entityManager.flush();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be blank"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=name"));
            throw ex;
        }
    }

    @Test(expected = PersistenceException.class)
    public void whenSaveGroup_withOrganizationNull_thenException() {
        try {
            Group group = new Group("test", null, null, 0L);
            entityManager.persist(group);
            entityManager.flush();
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Column 'organization_id' cannot be null"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveGroup_withGroupLevelNull_thenException() {
        try {
            Organization organization = new Organization("org_test");
            entityManager.persist(organization);

            Group group = new Group("test", null, organization, null);
            entityManager.persist(group);
            entityManager.flush();
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("must not be null"));
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("propertyPath=groupLevel"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveGroup_withDuplicateName_thenException() {
        long timestamp = System.currentTimeMillis();
        try {
            Organization organization = new Organization("org_test");
            entityManager.persist(organization);

            Group group = new Group(String.valueOf(timestamp), null, organization, 0L);
            entityManager.persist(group);

            Group group1 = new Group(String.valueOf(timestamp), group.getId(), organization, 1L);
            entityManager.persist(group1);
            entityManager.flush();
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Duplicate entry '" + timestamp + "' for key 'uk_group_name'"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveGroup_withDuplicateEncodedId_thenException() {
        long timestamp = System.currentTimeMillis();
        try {
            Organization organization = new Organization("org_test");
            entityManager.persist(organization);

            Group group = new Group("test_" + timestamp, null, organization, 0L);
            group.setEncodedId(String.valueOf(timestamp));
            entityManager.persist(group);

            Group group1 = new Group("test1_" + timestamp, group.getId(), organization, 1L);
            group1.setEncodedId(String.valueOf(timestamp));
            entityManager.persist(group1);
            entityManager.flush();
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Duplicate entry '" + timestamp + "' for key 'uk_group_encode_id'"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveGroup_withDuplicateEncodedRootPath_thenException() {
        long timestamp = System.currentTimeMillis();
        try {
            Organization organization = new Organization("org_test");
            entityManager.persist(organization);

            Group group = new Group("test_" + timestamp, null, organization, 0L);
            group.setEncodedRootPath(String.valueOf(timestamp));
            entityManager.persist(group);

            Group group1 = new Group("test1_" + timestamp, group.getId(), organization, 1L);
            group1.setEncodedRootPath(String.valueOf(timestamp));
            entityManager.persist(group1);
            entityManager.flush();
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Duplicate entry '" + timestamp + "' for key 'uk_group_encode_path'"));
            throw ex;
        }
    }

    @Test(expected = Exception.class)
    public void whenSaveGroup_withDuplicateRootPath_thenException() {
        long timestamp = System.currentTimeMillis();
        try {
            Organization organization = new Organization("org_test");
            entityManager.persist(organization);

            Group group = new Group("test_" + timestamp, null, organization, 0L);
            group.setRootPath(String.valueOf(timestamp));
            entityManager.persist(group);

            Group group1 = new Group("test1_" + timestamp, group.getId(), organization, 1L);
            group1.setRootPath(String.valueOf(timestamp));
            entityManager.persist(group1);
            entityManager.flush();
        } catch (Exception ex) {
            Throwable throwable = Throwables.getRootCause(ex);
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Duplicate entry '" + timestamp + "' for key 'uk_group_root_path'"));
            throw ex;
        }
    }

}
