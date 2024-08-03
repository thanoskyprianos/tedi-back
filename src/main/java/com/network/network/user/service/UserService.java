package com.network.network.user.service;

import com.network.network.media.service.MediaService;
import com.network.network.role.Role;
import com.network.network.role.exception.RoleNotFoundException;
import com.network.network.role.service.RoleService;
import com.network.network.security.jwt.JwtUtil;
import com.network.network.user.User;
import com.network.network.user.exception.LoginException;
import com.network.network.user.exception.UserNotFoundException;
import com.network.network.user.repr.LoginRequest;
import com.network.network.user.repr.LoginResponse;
import com.network.network.user.resource.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Resource UserRepository userRepository;

    @Resource MediaService mediaService;

    @Resource RoleService roleService;

    @Resource JwtUtil jwtUtil;

    @Resource AuthenticationManager authenticationManager;

    @Resource PasswordEncoder passwordEncoder;

    @Value("${roles.names.admin}")
    String adminName;

    @Value("${roles.names.professional}")
    String professionalName;

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new LoginException(e.getMessage());
        }

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()
                -> new UserNotFoundException(loginRequest.getEmail()));

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(user, token);
    }

    @Transactional
    public User saveUser(User user) {
        Role role = roleService.getRole(professionalName);
        if (role == null) { throw new RoleNotFoundException(user.getRole().getName()); }

        user.setRole(role);
        role.addUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword())); // BCrypt encoding

        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
