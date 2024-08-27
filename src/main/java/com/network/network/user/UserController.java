package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.HelperService;
import com.network.network.misc.View;
import com.network.network.security.jwt.TokenRepr;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
    private HelperService helperService;

    @Resource
    private UserService userService;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    @Resource
    private UserRepository userRepository;

    @GetMapping("")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/{id}/connections")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getConnections(@PathVariable int id) {
        if (helperService.notAccessible(id)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        User user = userService.getUserById(id);
        return ResponseEntity.ok(userResourceAssembler.toCollectionModel(user.getConnected()));
    }

    @PostMapping("/login")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return ResponseEntity.ok(userResourceAssembler.toModel(userService.loginUser(loginRequest, request)));
    }

    @PostMapping("/register")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
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
    public ResponseEntity<?> logout(){
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> refresh(@RequestBody TokenRepr repr) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.refreshToken(repr));
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

    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public User addUser(@Payload User user) {
        userService.saveUser(user);
        return user;
    }

    @GetMapping("/search")
    public List<User> searchBar(@RequestParam String name) {
        return userRepository.findByNameLike(name);
    }

}
