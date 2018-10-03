package com.edium.service.core.service;

import com.edium.library.exception.BadRequestException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.UserOrganizationRepository;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.repository.GroupRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class GroupServiceImplTest {

    @TestConfiguration
    static class GroupServiceImplTestContextConfiguration {

        @Bean
        public GroupService groupService() {
            return new GroupServiceImpl();
        }
    }

    @Autowired
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private UserOrganizationRepository userOrganizationRepository;

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindById_WithIdNotFound_thenException() {
        try {
            long id = 1L;
            Mockito.when(groupRepository.findById(id)).thenReturn(Optional.empty());

            groupService.findById(id);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getFieldName(), "id");
            Assert.assertEquals(ex.getResourceName(), "Group");
            throw ex;
        }
    }

    @Test
    public void whenFindById_WithIdFound_thenException() {
        // setup
        Group group = new Group();
        group.setId(1L);

        Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        // when
        Group result = groupService.findById(group.getId());

        // then
        Assert.assertEquals(group, result);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindAll_withPageNegative_thenException() {
        try {
            int page = -1, size = 10;

            groupService.findAll(page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page number cannot be less than zero.");
            throw ex;
        }
    }

    @Test(expected = BadRequestException.class)
    public void whenFindAll_withSizeNegative_thenException() {
        try {
            int page = 0, size = -1;

            groupService.findAll(page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page size cannot be less than zero.");
            throw ex;
        }
    }

    @Test
    public void whenFindAll_thenReturn() {
        int page = 0, size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Mockito.when(groupRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(new Group())));

        PagedResponse response = groupService.findAll(page, size);

        Assert.assertEquals(response.getTotalElements(), 1);
    }

    @Test
    public void whenSaveGroup_withParent_thenReturn() throws Exception {
        Group group = new Group(String.valueOf(System.currentTimeMillis()), 1L, new Organization(), 1L);
        group.setId(2L);
        group.setParentPath("/1");
        group.setParentEncodedPath("11");

        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        Group response = groupService.save(group);

        Assert.assertEquals(response.getEncodedId(), "12");
        Assert.assertEquals(response.getRootPath(), "/1/2");
        Assert.assertEquals(response.getEncodedRootPath(), "11-12");
    }

    @Test
    public void whenSaveGroup_withNoParent_thenReturn() throws Exception {
        Group group = new Group(String.valueOf(System.currentTimeMillis()), null, new Organization(), 0L);
        group.setId(2L);

        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        Group response = groupService.save(group);

        Assert.assertEquals(response.getEncodedId(), "12");
        Assert.assertEquals(response.getRootPath(), "/2");
        Assert.assertEquals(response.getEncodedRootPath(), "12");
    }

    @Test
    public void whenUpdateGroup_withParent_thenReturn(){
        Group group = new Group(String.valueOf(System.currentTimeMillis()), 1L, new Organization(), 1L);
        group.setId(2L);
        group.setEncodedId("12");
        group.setParentPath("/1");
        group.setParentEncodedPath("11");

        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        Group response = groupService.update(group);

        Assert.assertEquals(response.getRootPath(), "/1/2");
        Assert.assertEquals(response.getEncodedRootPath(), "11-12");
    }

    @Test
    public void whenUpdateGroup_withNoParent_thenReturn() {
        Group group = new Group(String.valueOf(System.currentTimeMillis()), null, new Organization(), 0L);
        group.setId(2L);
        group.setEncodedId("12");

        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        Group response = groupService.update(group);

        Assert.assertEquals(response.getRootPath(), "/2");
        Assert.assertEquals(response.getEncodedRootPath(), "12");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteById_WithIdNotFound_thenException() {
        try {
            long id = 1L;
            Mockito.when(groupRepository.findById(id)).thenReturn(Optional.empty());

            groupService.deleteById(id);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getFieldName(), "id");
            Assert.assertEquals(ex.getResourceName(), "Group");
            throw ex;
        }
    }

    @Test
    public void whenDeleteById_WithIdFound_thenOk() {
        // setup
        Group group = new Group();
        group.setId(1L);

        Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

        // when
        groupService.deleteById(group.getId());
    }

    @Test
    public void whenGetAllChildrenGroups_thenReturn() {
        long groupId = 1L;
        List<Group> returnList = Collections.singletonList(new Group());

        Mockito.when(groupRepository.getAllChildrenGroups(groupId)).thenReturn(returnList);

        Assert.assertEquals(groupService.getAllChildrenGroups(groupId), returnList);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenGetTreeGroupByGroupId_withGroupIdNotFound_thenException() {
        try {
            long groupId = 1L;

            Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            groupService.getTreeGroupByGroupId(groupId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getFieldName(), "id");
            Assert.assertEquals(ex.getResourceName(), "Group");
            throw ex;
        }
    }

    @Test
    public void whenGetTreeGroupByGroupId_withGroupIdFound_thenReturn() {
        Group group = new Group();
        group.setId(1L);
        group.setEncodedRootPath("11-12");

        Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        Mockito.when(groupRepository.getTreeGroupByGroupPath(group.getEncodedRootPath())).thenReturn(Collections.singletonList(group));

        List<Group> response = groupService.getTreeGroupByGroupId(group.getId());

        Assert.assertEquals(response.size(), 1);
        Assert.assertEquals(response.get(0).getId(), group.getId());
    }

    @Test
    public void whenGetTreeGroupOfOrganization_thenReturn() {
        long organizationId = 1L;
        List<Group> returnList = Collections.singletonList(new Group());

        Mockito.when(groupRepository.getTreeGroupOfOrganization(organizationId)).thenReturn(returnList);

        Assert.assertEquals(groupService.getTreeGroupOfOrganization(organizationId), returnList);
    }

    @Test
    public void whenGetRootGroupOfOrganization_thenReturn() {
        long organizationId = 1L;
        List<Group> returnList = Collections.singletonList(new Group());

        Mockito.when(groupRepository.getRootGroupOfOrganization(organizationId)).thenReturn(returnList);

        Assert.assertEquals(groupService.getRootGroupOfOrganization(organizationId), returnList);
    }

    @Test
    public void whenGetParentOfGroup_thenReturn() {
        long groupId = 1L;
        Group returnGroup = new Group();

        Mockito.when(groupRepository.getParentOfGroup(groupId)).thenReturn(returnGroup);

        Assert.assertEquals(groupService.getParentOfGroup(groupId), returnGroup);
    }

    @Test(expected = NullPointerException.class)
    public void whenGetGroupOfUserInOrganization_withNullGroup_thenException() {
        try {
            long userId = 1L, organizationId = 2L;

            Mockito.when(userOrganizationRepository.getByUser_IdAndOrganizationId(userId, organizationId)).thenReturn(null);

            groupService.getGroupOfUserInOrganization(userId, organizationId);
        } catch (NullPointerException ex) {
            throw ex;
        }
    }

    @Test
    public void whenGetGroupOfUserInOrganization_thenReturn() {
        long organizationId = 2L;
        User user = new User();
        user.setId(1L);
        UserOrganization userOrganization = new UserOrganization(user, organizationId, 3L);

        Mockito.when(userOrganizationRepository.getByUser_IdAndOrganizationId(user.getId(), organizationId)).thenReturn(userOrganization);

        Long response = groupService.getGroupOfUserInOrganization(user.getId(), organizationId);

        Assert.assertEquals(response, userOrganization.getGroupId());
    }

    @Test(expected = BadRequestException.class)
    public void whenGetGroupsOfUser_withPageNegative_thenException() {
        try {
            int page = -1, size = 10;

            groupService.getGroupsOfUser(1L, page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page number cannot be less than zero.");
            throw ex;
        }
    }

    @Test(expected = BadRequestException.class)
    public void whenGetGroupsOfUser_withSizeNegative_thenException() {
        try {
            int page = 0, size = -1;

            groupService.getGroupsOfUser(1L, page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page size cannot be less than zero.");
            throw ex;
        }
    }

    @Test
    public void whenGetGroupsOfUser_thenReturn() {
        int page = 0, size = 10;
        long userId = 1L;

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Mockito.when(groupRepository.getGroupsOfUser(userId, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(new Group())));

        PagedResponse response = groupService.getGroupsOfUser(userId, page, size);

        Assert.assertEquals(response.getTotalElements(), 1);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenGetCurrentGroupOfUser_withUserNotFound_thenException() {
        try {
            long userId = 1L;

            Mockito.when(userService.getById(userId)).thenReturn(Optional.empty());

            groupService.getCurrentGroupOfUser(userId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "User");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenGetCurrentGroupOfUser_withGroupNotFound_thenException() {
        try {
            User user = new User();
            user.setId(1L);
            user.setGroupId(2L);

            Mockito.when(userService.getById(user.getId())).thenReturn(Optional.of(user));
            Mockito.when(groupRepository.findById(user.getGroupId())).thenReturn(Optional.empty());

            groupService.getCurrentGroupOfUser(user.getId());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Group");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test
    public void whenGetCurrentGroupOfUser_thenReturn() {
        User user = new User();
        user.setId(1L);
        user.setGroupId(2L);

        Group group = new Group();

        Mockito.when(userService.getById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(groupRepository.findById(user.getGroupId())).thenReturn(Optional.of(group));

        Assert.assertEquals(groupService.getCurrentGroupOfUser(user.getId()), group);
    }
}
