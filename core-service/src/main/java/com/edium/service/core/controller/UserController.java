package com.edium.service.core.controller;

import com.edium.library.model.UserPrincipal;
import com.edium.library.repository.core.UserRepository;
import com.edium.service.core.model.Group;
import com.edium.service.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserPrincipal currUser) {
        return currUser;
    }

    @GetMapping("/checkUsernameAvailability")
    public boolean checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userService.existsByUsername(username);
        return isAvailable;
    }

    @GetMapping("/checkEmailAvailability")
    public boolean checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userService.existsByEmail(email);
        return isAvailable;
    }

    @GetMapping("/{id}/group")
    public Group getGroupOfUser(@PathVariable(name = "id") Long id) {
        return userService.getGroupOfUser(id);
    }

}
