package com.network.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network.network.post.Post;
import com.network.network.post.service.PostService;
import com.network.network.role.Role;
import com.network.network.role.resource.RoleRepository;
import com.network.network.security.jwt.JwtTokenRepository;
import com.network.network.user.User;
import com.network.network.user.exception.UserNotFoundException;
import com.network.network.user.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class NetWorkApplication {
    @Resource
    private RoleRepository roleRepository;

    @Resource
    private JwtTokenRepository tokenRepository;

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Value("${roles.names.admin}")
    private String adminName;

    @Value("${roles.names.professional}")
    private String professionalName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public static void main(String[] args) {
        SpringApplication.run(NetWorkApplication.class, args);
    }

    @Transactional
    @PostConstruct
    public void init() {
        tokenRepository.deleteAll();
        roleRepository.truncateRoleTable();

        Role adminRole = new Role(adminName);
        Role professionalRole = new Role(professionalName);

        roleRepository.save(adminRole);
        roleRepository.save(professionalRole);


        try {
            userService.getUserByEmail(adminEmail);
        } catch (UserNotFoundException e) {
            System.err.println(ANSI_RED + "CREATING ADMIN" + ANSI_RESET);

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword);

            userService.saveAdmin(admin);
        }
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
}
