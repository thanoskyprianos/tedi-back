package com.network.network.user;

import jakarta.annotation.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(userResourceAssembler.toCollectionModel(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        User user = userService.getUser(id);

        if (user == null) {
            throw new UserNotFoundException(id);
        }

        return ResponseEntity.ok(userResourceAssembler.toModel(user));
    }

    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.userExistsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }

        User newUser = userService.saveUser(user);
        EntityModel<User> userModel = userResourceAssembler.toModel(newUser);

        return ResponseEntity
                .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(userModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(Map.of("message", "User " + id + " has been deleted"));
    }
}
