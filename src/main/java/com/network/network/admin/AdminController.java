package com.network.network.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import com.network.network.user.resource.UserRepository;
import com.network.network.user.resource.UserResourceAssembler;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin", produces = "application/hal+json")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Resource
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    // get users with ids in extended form
    @PostMapping("/users")
    @JsonView(View.AsAdmin.class)
    public ResponseEntity<?> getUsers(@RequestBody List<Integer> ids) {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(
                userResourceAssembler
                        .toCollectionModel(users
                        .stream().filter(user -> ids.contains(user.getId()))
                                 .toList()));
    }

    // get all users in general form
    @GetMapping("/users/all")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getAllUsers() {
        // admins do not have name so they're hidden
        List<User> users = userRepository.findByNameLike("");
        return ResponseEntity.ok(userResourceAssembler.toCollectionModel(users));
    }
}
