package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.exception.DuplicateEmailException;
import com.network.network.user.repr.LoginRequest;
import com.network.network.user.repr.RegisterRequest;
import com.network.network.user.resource.UserRepository;
import com.network.network.user.resource.UserResourceAssembler;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users", produces = "application/hal+json")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    @Resource
    private UserRepository userRepository;

    @GetMapping("")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(userResourceAssembler.toCollectionModel(users));
    }

    @GetMapping("/self")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getSelf() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        return ResponseEntity.ok(userResourceAssembler.toModel(user));
    }

    @GetMapping("/like")
    @JsonView(View.AsProfessional.class)
    public List<User> likeUser(@RequestParam String fullName) {
        return userRepository.findByNameLike(fullName);
    }

    @PostMapping("/login")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(userResourceAssembler.toModel(userService.loginUser(loginRequest, request)));
    }

    @PostMapping("/register")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request

    ) {
        if (userService.userExistsByEmail(registerRequest.getEmail())) {
            throw new DuplicateEmailException(registerRequest.getEmail());
        }

        User user = new User(registerRequest);
        user = userService.saveUser(user);

        // auto login
        EntityModel<User> userModel = userResourceAssembler
                .toModel(userService.loginUser( // use the request password since it has been encoded on user
                        new LoginRequest(user.getEmail(), registerRequest.getPassword()), request));

        return ResponseEntity
                .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(userModel);
    }

    @GetMapping("/logout/success")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getUser(@PathVariable int id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(userResourceAssembler.toModel(user));
    }

    @DeleteMapping("/{id}")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId() || hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(Map.of("message", "User " + id + " has been deleted"));
    }

    @PutMapping("/{id}/email")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> updateEmail(@PathVariable int id, @RequestBody String newEmail) {
        User user = userService.getUserById(id);

        userService.updateEmail(user, newEmail);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> updatePassword(@PathVariable int id, @RequestBody String newPassword) {
        User user = userService.getUserById(id);

        userService.updatePassword(user, newPassword);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/credentials")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> updateCredentials(@PathVariable int id, @RequestBody LoginRequest loginRequest) {
        User user = userService.getUserById(id);

        if (loginRequest.getPassword() != null) {
            userService.updatePassword(user, loginRequest.getPassword());
        }

        if (loginRequest.getEmail() != null) {
            userService.updateEmail(user, loginRequest.getEmail());
        }

        return ResponseEntity.noContent().build();
    }

    // todo: move
    @GetMapping("/{id}/friends")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getFriends(@PathVariable int id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(userResourceAssembler.toCollectionModel(user.getConnected()));
    }

    // todo: move
    @JsonView(View.AsProfessional.class)
    @PutMapping("/{id}/befriend/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable int id, @PathVariable int friendId) {
        User user = userService.getUserById(id);
        User friend = userService.getUserById(friendId);

        user.addConnected(friend);
        friend.addConnected(user);

        userService.updateUser(user);
        userService.updateUser(friend);

        return ResponseEntity.noContent().build();
    }
}
