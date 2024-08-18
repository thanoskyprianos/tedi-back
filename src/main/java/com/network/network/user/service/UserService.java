package com.network.network.user.service;

import com.network.network.role.Role;
import com.network.network.role.exception.RoleNotFoundException;
import com.network.network.role.service.RoleService;
import com.network.network.security.jwt.JwtToken;
import com.network.network.security.jwt.JwtTokenRepository;
import com.network.network.security.jwt.JwtUtil;
import com.network.network.user.User;
import com.network.network.user.exception.LoginException;
import com.network.network.user.exception.UserNotFoundException;
import com.network.network.user.info.Info;
import com.network.network.user.info.exception.InfoNotSetException;
import com.network.network.user.repr.AuthResponse;
import com.network.network.user.repr.LoginRequest;
import com.network.network.user.resource.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Resource UserRepository userRepository;

    @Resource RoleService roleService;

    @Resource JwtUtil jwtUtil;

    @Resource AuthenticationManager authenticationManager;

    @Resource PasswordEncoder passwordEncoder;

    @Resource JwtTokenRepository jwtTokenRepository;

    @Value("${roles.names.admin}")
    String adminName;

    @Value("${roles.names.professional}")
    String professionalName;

    public User getPrincipal() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return getUserByEmail(userDetails.getUsername());
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public AuthResponse loginUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new LoginException(e.getMessage());
        }

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()
                -> new UserNotFoundException(loginRequest.getEmail()));

        jwtUtil.invalidateAllTokens(user);
        String token = jwtUtil.generateToken(user.getEmail());
        JwtToken jwtToken = new JwtToken();

        jwtToken.setToken(token);
        jwtToken.setUser(user);
        user.addToken(jwtToken);

        jwtTokenRepository.save(jwtToken);

        return new AuthResponse(user, token);
    }

    @Transactional
    public User saveUser(User user) {
        Role role = roleService.getRole(professionalName);
        if (role == null) { throw new RoleNotFoundException(professionalName); }

        user.setRole(role);
        role.addUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword())); // BCrypt encoding

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public Info getUserInfoOrThrow(int id) {
        User user = getUserById(id);
        Info info = user.getInfo();

        if  (info == null) {
            throw new InfoNotSetException();
        }

        return info;
    }
}
