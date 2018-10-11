package com.edium.library.service;

import com.edium.library.exception.BadRequestException;
import com.edium.library.exception.ResourceExistException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.Role;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.RoleRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class RoleServiceImplTest {

    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

    private RoleService roleService = new RoleServiceImpl(roleRepository);

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindByCode_withCodeNotFound_thenException() {
        try {
            String roleCode = String.valueOf(System.currentTimeMillis());

            Mockito.when(roleRepository.findByCode(roleCode)).thenReturn(Optional.empty());

            roleService.findByCode(roleCode);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Role");
            Assert.assertEquals(ex.getFieldName(), "code");
            throw ex;
        }
    }

    @Test
    public void whenFindByCode_withCodeFound_thenOk() {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));

        Mockito.when(roleRepository.findByCode(role.getCode())).thenReturn(Optional.of(role));

        Assert.assertEquals(roleService.findByCode(role.getCode()), role);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindById_withIdNotFound_thenException() {
        try {
            Long roleId = System.currentTimeMillis();

            Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

            roleService.findById(roleId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Role");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test
    public void whenFindById_withIdFound_thenOk() {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));
        role.setId(System.currentTimeMillis());

        Mockito.when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        Assert.assertEquals(roleService.findById(role.getId()), role);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteByCode_withCodeNotFound_thenException() {
        try {
            String roleCode = String.valueOf(System.currentTimeMillis());

            Mockito.when(roleRepository.findByCode(roleCode)).thenReturn(Optional.empty());

            roleService.deleteByCode(roleCode);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Role");
            Assert.assertEquals(ex.getFieldName(), "code");
            throw ex;
        }
    }

    @Test
    public void whenDeleteByCode_withCodeFound_thenOk() {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));

        Mockito.when(roleRepository.findByCode(role.getCode())).thenReturn(Optional.of(role));

        roleService.deleteByCode(role.getCode());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteById_withIdNotFound_thenException() {
        try {
            Long roleId = System.currentTimeMillis();

            Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

            roleService.deleteById(roleId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Role");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test
    public void whenDeleteById_withIdFound_thenOk() {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));
        role.setId(System.currentTimeMillis());

        Mockito.when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        roleService.deleteById(role.getId());
    }

    @Test(expected = BadRequestException.class)
    public void whenFindAll_withPageNegative_thenException() {
        try {
            int page = -1, size = 10;

            roleService.findAll(page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page number cannot be less than zero.");
            throw ex;
        }
    }

    @Test(expected = BadRequestException.class)
    public void whenFindAll_withSizeNegative_thenException() {
        try {
            int page = 0, size = -1;

            roleService.findAll(page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page size cannot be less than zero.");
            throw ex;
        }
    }

    @Test
    public void whenFindAll_thenReturn() {
        int page = 0, size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "code");
        Mockito.when(roleRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(new Role())));

        PagedResponse response = roleService.findAll(page, size);

        Assert.assertEquals(response.getTotalElements(), 1);
    }

    @Test
    public void whenSave_thenReturn() {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));

        Mockito.when(roleRepository.save(role)).thenReturn(role);

        Assert.assertEquals(roleService.save(role), role);
    }

    @Test(expected = Exception.class)
    public void whenSave_withException_thenException() {
        try {
            Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));

            Mockito.when(roleRepository.save(role)).thenAnswer(invocationOnMock -> {
                throw new Exception("test123");
            });

            roleService.save(role);
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.containsString("test123"));
            throw ex;
        }
    }

    @Test(expected = ResourceExistException.class)
    public void whenSave_withCodeDuplicate_thenException() {
        try {
            Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));

            Mockito.when(roleRepository.findByCode(role.getCode())).thenReturn(Optional.of(new Role()));

            roleService.save(role);
        } catch (ResourceExistException ex) {
            Assert.assertEquals(ex.getFieldName(), "code");
            Assert.assertEquals(ex.getResourceName(), "Role");
            throw ex;
        }
    }

    @Test(expected = ResourceExistException.class)
    public void whenUpdate_withCodeDuplicate_thenException() {
        try {
            Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));
            role.setId(1L);

            Role role1 = new Role(role.getCode(), String.valueOf(System.currentTimeMillis()));
            role1.setId(2L);

            Mockito.when(roleRepository.findByCode(role.getCode())).thenReturn(Optional.of(role1));

            roleService.save(role);
        } catch (ResourceExistException ex) {
            Assert.assertEquals(ex.getFieldName(), "code");
            Assert.assertEquals(ex.getResourceName(), "Role");
            throw ex;
        }
    }

    @Test
    public void whenUpdate_thenReturn() {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()));
        role.setId(1L);

        Mockito.when(roleRepository.findByCode(role.getCode())).thenReturn(Optional.of(role));

        Mockito.when(roleRepository.save(role)).thenReturn(role);

        Assert.assertEquals(roleService.save(role), role);
    }

}
