package com.network.network.user.service;

import com.network.network.role.Role;
import com.network.network.role.exception.RoleNotFoundException;
import com.network.network.role.service.RoleService;
import com.network.network.security.jwt.*;
import com.network.network.user.User;
import com.network.network.user.exception.DuplicateEmailException;
import com.network.network.user.exception.LoginException;
import com.network.network.user.exception.UserNotFoundException;
import com.network.network.user.info.Info;
import com.network.network.user.info.service.InfoService;
import com.network.network.user.repr.AuthResponse;
import com.network.network.user.repr.LoginRequest;
import com.network.network.user.resource.UserRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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

    @Resource InfoService infoService;

    @Resource JwtUtil jwtUtil;

    @Resource JwtAuthFilter jwtAuthFilter;

    @Resource JwtTokenRepository jwtTokenRepository;

    @Resource AuthenticationManager authenticationManager;

    @Resource PasswordEncoder passwordEncoder;

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
        return userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole().getName().equals(professionalName))
                .toList();
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public AuthResponse loginUser(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new LoginException(e.getMessage());
        }

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()
                -> new UserNotFoundException(loginRequest.getEmail()));

        // access token
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        JwtToken jwtAccess = new JwtToken();

        jwtAccess.setToken(accessToken);
        jwtAccess.setUser(user);
        jwtAccess.setType(TokenType.ACCESS);
        user.addToken(jwtAccess);

        // refresh token
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        JwtToken jwtRefresh = new JwtToken();

        jwtRefresh.setToken(refreshToken);
        jwtRefresh.setUser(user);
        jwtRefresh.setType(TokenType.REFRESH);
        user.addToken(jwtRefresh);

        jwtTokenRepository.save(jwtAccess);
        jwtTokenRepository.save(jwtRefresh);
        updateUser(user);

        jwtAuthFilter.setContext(request, accessToken);

        return new AuthResponse(user, accessToken, refreshToken);
    }

    @Transactional
    public User saveUser(User user) {
        Role role = roleService.getRole(professionalName);
        if (role == null) { throw new RoleNotFoundException(professionalName); }

        user.setRole(role);
        role.addUser(user);

        Info info = Info.allPrivate();
        info.setUser(user);
        user.setInfo(info);

        infoService.saveInfo(info);

        user.setPassword(passwordEncoder.encode(user.getPassword())); // BCrypt encoding

        return userRepository.save(user);
    }

    @Transactional
    public TokenRepr refreshToken(TokenRepr tokenRepr) throws Exception {
        User principal = getPrincipal();

        invalidateTokens(tokenRepr);

        String accessToken = jwtUtil.generateAccessToken(principal.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(principal.getEmail());

        JwtToken newAccess = new JwtToken();
        newAccess.setToken(accessToken);
        newAccess.setType(TokenType.ACCESS);
        newAccess.setUser(principal);

        JwtToken newRefresh = new JwtToken();
        newRefresh.setToken(refreshToken);
        newRefresh.setType(TokenType.REFRESH);
        newRefresh.setUser(principal);

        principal.addToken(newAccess);
        principal.addToken(newRefresh);

        jwtTokenRepository.save(newAccess);
        jwtTokenRepository.save(newRefresh);
        updateUser(principal);

        return new TokenRepr(accessToken, refreshToken);
    }

    @Transactional
    public void invalidateTokens(TokenRepr tokenRepr) throws Exception {
        User principal = getPrincipal();

        String accessToken = tokenRepr.getAccessToken();
        String refreshToken = tokenRepr.getRefreshToken();

        JwtToken access  = jwtTokenRepository.findById(accessToken).orElseThrow(() -> new Exception("Token not found"));
        JwtToken refresh = jwtTokenRepository.findById(refreshToken).orElseThrow(() -> new Exception("Token not found"));

        if (!principal.getJwtTokens().contains(access) || !principal.getJwtTokens().contains(refresh)) {
            throw new Exception("Invalid tokens contains");
        }

        if (accessToken.equals(refreshToken)) {
            throw new Exception("Tokens should be different");
        }

        if (access.getType() != TokenType.ACCESS) {
            throw new Exception("Not access token");
        }

        if (refresh.getType() != TokenType.REFRESH) {
            throw new Exception("Not refresh token");
        }

        access.setInvalid(true);
        refresh.setInvalid(true);

        jwtTokenRepository.save(access);
        jwtTokenRepository.save(refresh);
    }

    @Transactional
    public User saveAdmin(User user) {
        Role role = roleService.getRole(adminName);
        if (role == null) { throw new RoleNotFoundException(adminName); }

        user.setRole(role);
        role.addUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public void updateEmail(User user, String email) {
        // check if email exists
        if(userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }

        user.setEmail(email);
        updateUser(user);

        // principal username changed
        jwtUtil.invalidateAllTokens(user);
    }

    public void updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        updateUser(user);
    }
}
