package com.edium.service.core.controller;

import com.edium.library.model.core.Role;
import com.edium.library.payload.ApiResponse;
import com.edium.library.payload.PagedResponse;
import com.edium.library.service.core.RoleService;
import com.edium.library.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("")
    public PagedResponse<Role> getAllRole(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                          @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return roleService.findAll(page, size);
    }

    @PostMapping("")
    public Role createRole(@Valid @RequestBody Role role) throws Exception {
        return roleService.save(role);
    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable(value = "id") Long id) {
        return roleService.findById(id);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable(value = "id") Long id,
                           @Valid @RequestBody Role role) {
        role.setId(id);

        return roleService.save(role);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(@PathVariable(value = "id") Long id) {
        roleService.deleteById(id);

        return new ApiResponse(true, "success");
    }

    @GetMapping("/getByCode/{code}")
    public Role getRoleById(@PathVariable(value = "code") String code) {
        return roleService.findByCode(code);
    }

    @DeleteMapping("/deleteByCode/{code}")
    public ApiResponse deleteByCode(@PathVariable(value = "code") String code) {
        roleService.deleteByCode(code);

        return new ApiResponse(true, "success");
    }
}
