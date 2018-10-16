package com.edium.library.service;

import com.edium.library.exception.ResourceExistException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.SubjectTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.payload.GroupDTO;
import com.edium.library.repository.share.AclEntryRepository;
import com.edium.library.repository.share.AclResourceTypeRepository;
import com.edium.library.repository.share.AclSubjectTypeRepository;
import com.edium.library.service.share.AclService;
import com.edium.library.service.share.BaseAclServiceImpl;
import com.edium.library.util.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class BaseAclServiceTest {

    private static GroupDTO groupDTO = new GroupDTO(2L, "test", null, 0L, "/1/2");

    @TestConfiguration
    static class BaseAclServiceTestContextConfiguration {

        @Bean
        public BaseAclServiceImpl baseAclService() {
            return new BaseAclServiceImpl() {
                @Override
                public GroupDTO getGroupOfUser(Long userId) {
                    return groupDTO;
                }
            };
        }
    }

    @Autowired
    private AclService aclService;

    @MockBean
    private AclEntryRepository aclEntryRepository;

    @MockBean
    private AclResourceTypeRepository aclResourceTypeRepository;

    @MockBean
    private AclSubjectTypeRepository aclSubjectTypeRepository;

    @Test
    public void whenGetAllResourceId_withResourceTypeNotFound_thenException() {
        try {
            // setup
            String resourceType = "NO_EXIST";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType))
                    .thenThrow(new ResourceNotFoundException("AclResourceType", "type", resourceType));

            // when
            aclService.getAllResourceId(1L, resourceType, AclEntryPermission.READ.toString());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclResourceType");
        }
    }

    @Test
    public void whenGetAllResourceId_withPermissionNotFound_thenReturnEmpty() {
        // setup
        String resourceType = "EXIST";
        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType(1L, "test")));

        // when
        List<Long> entries = aclService.getAllResourceId(1L, resourceType, "NO");

        // then
        Assert.assertTrue(entries.isEmpty());
    }

    @Test
    public void whenGetAllResourceId_withSubjectTypeUserNotFound_thenException() {
        try {
            // setup
            String resourceType = "EXIST";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType(1L, "test")));

            String subjectType = SubjectTypeCode.USER.toString();
            Mockito.when(aclSubjectTypeRepository.findByType(subjectType))
                    .thenThrow(new ResourceNotFoundException("AclSubjectType", "type", subjectType));

            // when
            aclService.getAllResourceId(1L, resourceType, AclEntryPermission.READ.toString());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
        }
    }

    @Test
    public void whenGetAllResourceId_withSubjectTypeGroupNotFound_thenException() {
        try {
            // setup
            String resourceType = "EXIST";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType(1L, "test")));

            Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(new AclSubjectType(1L, "test")));

            Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString()))
                    .thenThrow(new ResourceNotFoundException("AclSubjectType", "type", SubjectTypeCode.GROUP.toString()));

            // when
            aclService.getAllResourceId(1L, resourceType, AclEntryPermission.READ.toString());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
        }
    }

    @Test
    public void whenGetAllResourceId_withPermissionRead_thenReturn() {
        // setup
        AclEntry entry = new AclEntry();
        entry.setResourceId(1L);

        String resourceType = "COURSE";
        AclResourceType aclResourceType = new AclResourceType(1L, "test");
        AclSubjectType userType = new AclSubjectType(1L, "user");
        AclSubjectType groupType = new AclSubjectType(2L, "group");

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(aclResourceType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(userType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString())).thenReturn(Optional.of(groupType));


        Mockito.when(aclEntryRepository.findEntryPermissionRead(aclResourceType.getId(), userType.getId(), 1L,
                groupType.getId(), groupDTO.getId(), groupIdOfUser)).thenReturn(Collections.singletonList(entry));

        // when
        List<Long> ids = aclService.getAllResourceId(1L, resourceType, AclEntryPermission.READ.toString());

        // then
        Assert.assertEquals(ids.size(), 1);
        Assert.assertTrue(ids.stream().anyMatch(aLong -> aLong.equals(entry.getResourceId())));
    }

    @Test
    public void whenGetAllResourceId_withPermissionWrite_thenReturn() {
        // setup
        AclEntry entry = new AclEntry();
        entry.setResourceId(1L);

        String resourceType = "COURSE";
        AclResourceType aclResourceType = new AclResourceType(1L, "test");
        AclSubjectType userType = new AclSubjectType(1L, "user");
        AclSubjectType groupType = new AclSubjectType(2L, "group");

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(aclResourceType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(userType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString())).thenReturn(Optional.of(groupType));


        Mockito.when(aclEntryRepository.findEntryPermissionWrite(aclResourceType.getId(), userType.getId(), 1L,
                groupType.getId(), groupDTO.getId(), groupIdOfUser)).thenReturn(Collections.singletonList(entry));

        // when
        List<Long> ids = aclService.getAllResourceId(1L, resourceType, AclEntryPermission.WRITE.toString());

        // then
        Assert.assertEquals(ids.size(), 1);
        Assert.assertTrue(ids.stream().anyMatch(aLong -> aLong.equals(entry.getResourceId())));
    }

    @Test
    public void whenGetAllResourceId_withPermissionDelete_thenReturn() {
        // setup
        AclEntry entry = new AclEntry();
        entry.setResourceId(1L);

        String resourceType = "COURSE";
        AclResourceType aclResourceType = new AclResourceType(1L, "test");
        AclSubjectType userType = new AclSubjectType(1L, "user");
        AclSubjectType groupType = new AclSubjectType(2L, "group");

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(aclResourceType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(userType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString())).thenReturn(Optional.of(groupType));


        Mockito.when(aclEntryRepository.findEntryPermissionDelete(aclResourceType.getId(), userType.getId(), 1L,
                groupType.getId(), groupDTO.getId(), groupIdOfUser)).thenReturn(Collections.singletonList(entry));

        // when
        List<Long> ids = aclService.getAllResourceId(1L, resourceType, AclEntryPermission.DELETE.toString());

        // then
        Assert.assertEquals(ids.size(), 1);
        Assert.assertTrue(ids.stream().anyMatch(aLong -> aLong.equals(entry.getResourceId())));
    }

    @Test
    public void whenGetResourceIdByResourceId_withResourceTypeNotFound_thenException() {
        try {
            // setup
            String resourceType = "NO_EXIST";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType))
                    .thenThrow(new ResourceNotFoundException("AclResourceType", "type", resourceType));

            // when
            aclService.getResourceIdByResourceId(1L, 1L, resourceType, AclEntryPermission.READ.toString());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclResourceType");
        }
    }

    @Test
    public void whenGetResourceIdByResourceId_withPermissionNotFound_thenReturnEmpty() {
        // setup
        String resourceType = "EXIST";
        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType(1L, "test")));

        // when
        List<Long> entries = aclService.getResourceIdByResourceId(1L, 1L, resourceType, "NO");

        // then
        Assert.assertTrue(entries.isEmpty());
    }

    @Test
    public void whenGetResourceIdByResourceId_withSubjectTypeUserNotFound_thenException() {
        try {
            // setup
            String resourceType = "EXIST";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType(1L, "test")));

            String subjectType = SubjectTypeCode.USER.toString();
            Mockito.when(aclSubjectTypeRepository.findByType(subjectType))
                    .thenThrow(new ResourceNotFoundException("AclSubjectType", "type", subjectType));

            // when
            aclService.getResourceIdByResourceId(1L, 1L, resourceType, AclEntryPermission.READ.toString());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
        }
    }

    @Test
    public void whenGetResourceIdByResourceId_withSubjectTypeGroupNotFound_thenException() {
        try {
            // setup
            String resourceType = "EXIST";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType(1L, "test")));

            Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(new AclSubjectType(1L, "test")));

            Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString()))
                    .thenThrow(new ResourceNotFoundException("AclSubjectType", "type", SubjectTypeCode.GROUP.toString()));

            // when
            aclService.getResourceIdByResourceId(1L, 1L, resourceType, AclEntryPermission.READ.toString());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
        }
    }

    @Test
    public void whenGetResourceIdByResourceId_withPermissionRead_thenReturn() {
        // setup
        AclEntry entry = new AclEntry();
        entry.setResourceId(1L);

        String resourceType = "COURSE";
        AclResourceType aclResourceType = new AclResourceType(1L, "test");
        AclSubjectType userType = new AclSubjectType(1L, "user");
        AclSubjectType groupType = new AclSubjectType(2L, "group");

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(aclResourceType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(userType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString())).thenReturn(Optional.of(groupType));


        Mockito.when(aclEntryRepository.findEntryPermissionReadResource(aclResourceType.getId(), 1L, userType.getId(), 1L,
                groupType.getId(), groupDTO.getId(), groupIdOfUser)).thenReturn(Collections.singletonList(entry));

        // when
        List<Long> ids = aclService.getResourceIdByResourceId(1L, 1L, resourceType,
                AclEntryPermission.READ.toString());

        // then
        Assert.assertEquals(ids.size(), 1);
        Assert.assertTrue(ids.stream().anyMatch(aLong -> aLong.equals(entry.getResourceId())));
    }

    @Test
    public void whenGetResourceIdByResourceId_withPermissionWrite_thenReturn() {
        // setup
        AclEntry entry = new AclEntry();
        entry.setResourceId(1L);

        String resourceType = "COURSE";
        AclResourceType aclResourceType = new AclResourceType(1L, "test");
        AclSubjectType userType = new AclSubjectType(1L, "user");
        AclSubjectType groupType = new AclSubjectType(2L, "group");

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(aclResourceType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(userType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString())).thenReturn(Optional.of(groupType));


        Mockito.when(aclEntryRepository.findEntryPermissionWriteResource(aclResourceType.getId(), 1L, userType.getId(), 1L,
                groupType.getId(), groupDTO.getId(), groupIdOfUser)).thenReturn(Collections.singletonList(entry));

        // when
        List<Long> ids = aclService.getResourceIdByResourceId(1L, 1L, resourceType,
                AclEntryPermission.WRITE.toString());

        // then
        Assert.assertEquals(ids.size(), 1);
        Assert.assertTrue(ids.stream().anyMatch(aLong -> aLong.equals(entry.getResourceId())));
    }

    @Test
    public void whenGetResourceIdByResourceId_withPermissionDelete_thenReturn() {
        // setup
        AclEntry entry = new AclEntry();
        entry.setResourceId(1L);

        String resourceType = "COURSE";
        AclResourceType aclResourceType = new AclResourceType(1L, "test");
        AclSubjectType userType = new AclSubjectType(1L, "user");
        AclSubjectType groupType = new AclSubjectType(2L, "group");

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(aclResourceType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.USER.toString())).thenReturn(Optional.of(userType));
        Mockito.when(aclSubjectTypeRepository.findByType(SubjectTypeCode.GROUP.toString())).thenReturn(Optional.of(groupType));


        Mockito.when(aclEntryRepository.findEntryPermissionDeleteResource(aclResourceType.getId(), 1L, userType.getId(), 1L,
                groupType.getId(), groupDTO.getId(), groupIdOfUser)).thenReturn(Collections.singletonList(entry));

        // when
        List<Long> ids = aclService.getResourceIdByResourceId(1L, 1L, resourceType,
                AclEntryPermission.DELETE.toString());

        // then
        Assert.assertEquals(ids.size(), 1);
        Assert.assertTrue(ids.stream().anyMatch(aLong -> aLong.equals(entry.getResourceId())));
    }

    @Test
    public void whenFindById_withIdNotFound_thenException() {
        try {
            long entryId = 1L;
            Mockito.when(aclEntryRepository.findById(entryId)).thenReturn(Optional.empty());

            aclService.findById(entryId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Entry");
            Assert.assertEquals(ex.getFieldName(), "id");
        }
    }

    @Test
    public void whenFindById_withIdFound_thenReturnEntry() {
        AclEntry entry = new AclEntry();
        entry.setId(1L);

        Mockito.when(aclEntryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        AclEntry response = aclService.findById(entry.getId());

        Assert.assertEquals(response.getId(), entry.getId());
    }

    @Test
    public void whenSave_withNoGrant_thenReturnEmptyEntry() {
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, false, false, false, null, false);

        AclEntry result = aclService.save(entry);

        Assert.assertNull(result.getId());
    }

    @Test
    public void whenSave_withGrant_thenReturnEntry() {
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, true, false, false, null, false);
        entry.setId(1L);

        Mockito.when(aclEntryRepository.save(entry)).thenReturn(entry);

        AclEntry result = aclService.save(entry);

        Assert.assertNotNull(result.getId());
    }

    @Test
    public void whenUpdate_withGrant_thenReturnEntry() {
        // setup
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, true, false, false, null, false);
        entry.setId(1L);
        Mockito.when(aclEntryRepository.save(entry)).thenReturn(entry);

        // when
        AclEntry result = aclService.update(entry);

        // then
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void whenUpdate_withNoGrant_thenReturnEntry() {
        // setup
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, false, false, false, null, false);
        entry.setId(1L);

        // when
        AclEntry result = aclService.update(entry);

        // then
        Assert.assertNull(result.getId());
    }

    @Test
    public void whenFindOne_withNotFound_thenReturnNull() {
        // setup
        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(1L, 1L,
                1L, 1L)).thenReturn(null);

        // when and then
        Assert.assertNull(aclService.findOne(1L, 1L, 1L, 1L));
    }

    @Test
    public void whenFindOne_withEmpty_thenReturnNull() {
        // setup
        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(1L, 1L,
                1L, 1L)).thenReturn(Collections.emptyList());

        // when and then
        Assert.assertNull(aclService.findOne(1L, 1L, 1L, 1L));
    }

    @Test
    public void whenFindOne_withResult_thenReturnEntry() {
        // setup
        AclEntry entry1 = new AclEntry();
        entry1.setId(1L);

        AclEntry entry2 = new AclEntry();
        entry2.setId(2L);

        List<AclEntry> entries = new ArrayList<>();
        entries.add(entry1);
        entries.add(entry2);

        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(1L, 1L,
                1L, 1L)).thenReturn(entries);

        // when and then
        Assert.assertEquals(aclService.findOne(1L, 1L, 1L, 1L).getId(), entry1.getId());
    }

    @Test
    public void whenSaveOrUpdate_withNoGrantAndNotExistEntry_thenReturnEmptyEntry() {
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, false, false, false, null, false);
        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(entry.getResourceTypeId(), entry.getResourceId(),
                entry.getSubjectTypeId(), entry.getSubjectId())).thenReturn(null);

        Assert.assertNull(aclService.saveOrUpdate(entry).getId());
    }

    @Test
    public void whenSaveOrUpdate_withNoGrantAndExistEntry_thenReturnEmptyEntry() {
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, false, false, false, null, false);

        AclEntry entry1 = new AclEntry();
        entry1.setId(100L);

        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(entry.getResourceTypeId(), entry.getResourceId(),
                entry.getSubjectTypeId(), entry.getSubjectId())).thenReturn(Collections.singletonList(entry1));

        Assert.assertEquals(aclService.saveOrUpdate(entry).getId(), entry1.getId());
    }

    @Test
    public void whenSaveOrUpdate_withGrantAndNotExistEntry_thenReturnEmptyEntry() {
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, true, false, false, null, false);

        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(entry.getResourceTypeId(), entry.getResourceId(),
                entry.getSubjectTypeId(), entry.getSubjectId())).thenReturn(null);

        Mockito.when(aclEntryRepository.save(entry)).thenReturn(entry);

        Assert.assertEquals(aclService.saveOrUpdate(entry), entry);
    }

    @Test
    public void whenSaveOrUpdate_withGrantAndExistEntry_thenReturnEmptyEntry() {
        AclEntry entry = new AclEntry(1L, 1L, 2L,
                2L, true, false, false, null, false);

        AclEntry aclEntry = new AclEntry();
        aclEntry.setId(1L);
        aclEntry.setWriteGrant(entry.isWriteGrant());
        aclEntry.setReadGrant(entry.isReadGrant());
        aclEntry.setPositionId(entry.getPositionId());
        aclEntry.setInherit(entry.isInherit());
        aclEntry.setDeleteGrant(entry.isDeleteGrant());

        Mockito.when(aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(entry.getResourceTypeId(), entry.getResourceId(),
                entry.getSubjectTypeId(), entry.getSubjectId())).thenReturn(Collections.singletonList(aclEntry));

        Mockito.when(aclEntryRepository.save(aclEntry)).thenReturn(aclEntry);

        Assert.assertEquals(aclService.saveOrUpdate(entry), aclEntry);
    }

    @Test
    public void whenGetResourceTypeByType_withTypeNotFound_thenException() {
        try {
            String resourceType = "no_exist";
            Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.empty());

            aclService.getResourceTypeByType(resourceType);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclResourceType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenGetResourceTypeByType_withTypeFound_thenReturnResource() {
        AclResourceType resourceType = new AclResourceType(1L, "exist");

        Mockito.when(aclResourceTypeRepository.findByType(resourceType.getType())).thenReturn(Optional.of(resourceType));

        Assert.assertEquals(aclService.getResourceTypeByType(resourceType.getType()), resourceType);
    }

    @Test
    public void whenSaveResourceType_withTypeExist_thenException() {
        try {
            AclResourceType resourceType = new AclResourceType(1L, "exist");

            Mockito.when(aclResourceTypeRepository.findByType(resourceType.getType())).thenReturn(Optional.of(new AclResourceType()));

            aclService.saveResourceType(resourceType);
        } catch (ResourceExistException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclResourceType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenSaveResourceType_withTypeNotExist_thenReturn() {
        AclResourceType resourceType = new AclResourceType(1L, "exist");

        Mockito.when(aclResourceTypeRepository.findByType(resourceType.getType())).thenReturn(Optional.empty());
        Mockito.when(aclResourceTypeRepository.save(resourceType)).thenReturn(resourceType);

        Assert.assertEquals(aclService.saveResourceType(resourceType), resourceType);
    }

    @Test
    public void whenUpdateResourceType_withTypeNotFound_thenException() {
        try {
            AclResourceType resourceType = new AclResourceType(1L, "exist");

            Mockito.when(aclResourceTypeRepository.findByType(resourceType.getType())).thenReturn(Optional.empty());

            aclService.saveResourceType(resourceType);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclResourceType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenUpdateResourceType_withTypeFound_thenReturn() {
        AclResourceType resourceType = new AclResourceType(1L, "exist");

        Mockito.when(aclResourceTypeRepository.findByType(resourceType.getType())).thenReturn(Optional.of(resourceType));
        Mockito.when(aclResourceTypeRepository.save(resourceType)).thenReturn(resourceType);

        Assert.assertEquals(aclService.updateResourceType(resourceType), resourceType);
    }

    @Test
    public void whenDeleteResourceType_withTypeNotFound_thenException() {
        try {
            String resourceType = "not_exist";

            Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.empty());

            aclService.deleteResourceTypeByType(resourceType);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclResourceType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenDeleteResourceType_withTypeFound_thenReturn() {
        String resourceType = "exist";

        Mockito.when(aclResourceTypeRepository.findByType(resourceType)).thenReturn(Optional.of(new AclResourceType()));

        aclService.deleteResourceTypeByType(resourceType);
    }

    @Test
    public void whenGetSubjectTypeByType_withTypeNotFound_thenException() {
        try {
            String subjectType = "no_exist";
            Mockito.when(aclSubjectTypeRepository.findByType(subjectType)).thenReturn(Optional.empty());

            aclService.getSubjectTypeByType(subjectType);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenGetSubjectTypeByType_withTypeFound_thenReturn() {
        AclSubjectType subjectType = new AclSubjectType(1L, "exist");

        Mockito.when(aclSubjectTypeRepository.findByType(subjectType.getType())).thenReturn(Optional.of(subjectType));

        Assert.assertEquals(aclService.getSubjectTypeByType(subjectType.getType()), subjectType);
    }

    @Test
    public void whenSaveSubjectType_withTypeExist_thenException() {
        try {
            AclSubjectType subjectType = new AclSubjectType(1L, "exist");

            Mockito.when(aclSubjectTypeRepository.findByType(subjectType.getType())).thenReturn(Optional.of(new AclSubjectType()));

            aclService.saveSubjectType(subjectType);
        } catch (ResourceExistException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenSaveSubjectType_withTypeNotExist_thenReturn() {
        AclSubjectType subjectType = new AclSubjectType(1L, "exist");

        Mockito.when(aclSubjectTypeRepository.findByType(subjectType.getType())).thenReturn(Optional.empty());
        Mockito.when(aclSubjectTypeRepository.save(subjectType)).thenReturn(subjectType);

        Assert.assertEquals(aclService.saveSubjectType(subjectType), subjectType);
    }

    @Test
    public void whenUpdateSubjectType_withTypeNotFound_thenException() {
        try {
            AclSubjectType subjectType = new AclSubjectType(1L, "exist");

            Mockito.when(aclSubjectTypeRepository.findByType(subjectType.getType())).thenReturn(Optional.empty());

            aclService.saveSubjectType(subjectType);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenUpdateSubjectType_withTypeFound_thenReturn() {
        AclSubjectType subjectType = new AclSubjectType(1L, "exist");

        Mockito.when(aclSubjectTypeRepository.findByType(subjectType.getType())).thenReturn(Optional.of(subjectType));
        Mockito.when(aclSubjectTypeRepository.save(subjectType)).thenReturn(subjectType);

        Assert.assertEquals(aclService.updateSubjectType(subjectType), subjectType);
    }

    @Test
    public void whenDeleteSubjectType_withTypeNotFound_thenException() {
        try {
            String subjectType = "not_exist";

            Mockito.when(aclSubjectTypeRepository.findByType(subjectType)).thenReturn(Optional.empty());

            aclService.deleteSubjectTypeByType(subjectType);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "AclSubjectType");
            Assert.assertEquals(ex.getFieldName(), "type");
        }
    }

    @Test
    public void whenDeleteSubjectType_withTypeFound_thenReturn() {
        String subjectType = "exist";

        Mockito.when(aclSubjectTypeRepository.findByType(subjectType)).thenReturn(Optional.of(new AclSubjectType()));

        aclService.deleteSubjectTypeByType(subjectType);
    }

}
