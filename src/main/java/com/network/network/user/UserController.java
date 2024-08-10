package com.network.network.user;

import com.network.network.user.exception.DuplicateEmailException;
import com.network.network.user.repr.LoginRequest;
import com.network.network.user.repr.RegisterRequest;
import com.network.network.user.repr.UserRepr;
import com.network.network.user.resource.UserRepository;
import com.network.network.user.resource.UserResourceAssembler;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
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
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    @Resource
    private UserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(userResourceAssembler.toUserCollectionModel(users));
    }

    @GetMapping("/self")
    public ResponseEntity<?> getSelf() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        return ResponseEntity.ok(userResourceAssembler.toModel(user));
    }

    @GetMapping("/like")
    public List<User> likeUser(@RequestParam String fullName) {
        return userRepository.findByNameLike(fullName);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userResourceAssembler.toModel(userService.loginUser(loginRequest)));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        if (userService.userExistsByEmail(registerRequest.getEmail())) {
            throw new DuplicateEmailException(registerRequest.getEmail());
        }

        User user = new User(registerRequest);
        user = userService.saveUser(user);

        // auto login
        EntityModel<UserRepr> userModel = userResourceAssembler
                .toModel(userService.loginUser( // use the request password since it has been encoded on user
                        new LoginRequest(user.getEmail(), registerRequest.getPassword())));

        return ResponseEntity
                .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(userModel);
    }

    @GetMapping("/logout/success")
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(userResourceAssembler.toModel(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == principal.getId() || hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(Map.of("message", "User " + id + " has been deleted"));
    }
}
