package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.UserPrincipal;
import com.edium.library.model.core.User;
import com.edium.library.payload.ApiResponse;
import com.edium.library.payload.PagedResponse;
import com.edium.library.util.AppConstants;
import com.edium.service.core.model.Group;
import com.edium.service.core.payload.SetGroupRequest;
import com.edium.service.core.service.GroupService;
import com.edium.service.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public UserController(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(name = "id") Long id) {
        return userService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @GetMapping("/{id}/currentGroup")
    public Group getCurrentGroupOfUser(@PathVariable(name = "id") Long id) {
        return groupService.getCurrentGroupOfUser(id);
    }

    @GetMapping("/{id}/groups")
    public PagedResponse<Group> getGroupsOfUser(@PathVariable(name = "id") Long id,
                                                      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return groupService.getGroupsOfUser(id, page, size);
    }

    @PutMapping("/{id}/setCurrentOrganization/{organizationId}")
    public ApiResponse setCurrentOrganization(@PathVariable(name = "id") Long userId,
                                                    @PathVariable(name = "organizationId") Long organizationId) {
        userService.setCurrentOrganization(userId, organizationId);

        return new ApiResponse(true, "SUCCESS");
    }

    @PostMapping("/{id}/setGroup")
    public ApiResponse changeGroup(@PathVariable(name = "id") Long userId,
                                   @Valid @RequestBody SetGroupRequest setGroupRequest) {
        userService.setGroup(userId, setGroupRequest.getGroupId(), setGroupRequest.getRoles());

        return new ApiResponse(true, "SUCCESS");
    }

    @GetMapping("/getByUsernameOrEmail/{usernameOrEmail}")
    public User getUserByUsername(@PathVariable(name = "usernameOrEmail") String usernameOrEmail) {
        return userService.getByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username or email", usernameOrEmail));
    }

    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserPrincipal currUser) {
        return currUser;
    }

    @GetMapping("/checkUsernameAvailability")
    public boolean checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return !userService.existsByUsername(username);
    }

    @GetMapping("/checkEmailAvailability")
    public boolean checkEmailAvailability(@RequestParam(value = "email") String email) {
        return !userService.existsByEmail(email);
    }

}
