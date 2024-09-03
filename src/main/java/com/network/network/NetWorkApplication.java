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

    @Resource
    private ObjectMapper objectMapper;


    @Value("${config.add.mock_users}")
    private boolean shouldAddMockUsers;
    @Value("classpath:mock_users.json")
    private File mockUsers;

    @Value("${config.add.mock_posts}")
    private boolean shouldAddMockPosts;
    @Value("classpath:mock_posts.json")
    private File mockPosts;
    @Value("classpath:mock_jobOffers.json")
    private File mockJobOffers;

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

        if (shouldAddMockUsers) {
            System.out.println(ANSI_RED + "ADDING MOCK USERS" + ANSI_RESET);
            addMockUsers();
        }

        if (shouldAddMockPosts) {
            System.out.println(ANSI_RED + "ADDING MOCK POSTS" + ANSI_RESET);
            addMockPosts();
            System.out.println(ANSI_RED + "ADDING MOCK POSTS" + ANSI_RESET);
            addMockJobOffers();
        }
    }

    @Transactional
    public void addMockUsers() {
        try {
            Arrays.stream(objectMapper.readValue(mockUsers, User[].class)).toList().forEach(user -> {
                System.out.print(ANSI_RED + "ADDING USER: " + ANSI_RESET + user);
                userService.saveUser(user);
            });
        } catch (Exception e) {
            System.out.println(ANSI_RED + "ERROR WHILE SAVING USER: " + ANSI_RESET + e.getMessage());
        }
    }

    @Transactional
    public void addMockPosts() {
        List<User> users = userService.getAllUsers();

        try {
            Arrays.stream(objectMapper.readValue(mockPosts, Post[].class)).toList().forEach(post -> {
                post.setUser(users.get((int) (Math.random() * users.size())));

                System.out.println(ANSI_RED + "ADDING POST: " + ANSI_RESET + post);
                postService.savePost(post);
            });
        } catch (Exception e) {
            System.out.println(ANSI_RED + "ERROR WHILE SAVING POST: " + ANSI_RESET + e.getMessage());
        }
    }

    @Transactional
    public void addMockJobOffers() {
        List<User> users = userService.getAllUsers();

        try {
            Arrays.stream(objectMapper.readValue(mockJobOffers, Post[].class)).toList().forEach(jobOffer -> {
                jobOffer.setUser(users.get((int) (Math.random() * users.size())));

                System.out.println(ANSI_RED + "ADDING JOB OFFER: " + ANSI_RESET + jobOffer);
                postService.savePost(jobOffer);
            });
        } catch (Exception e) {
            System.out.println(ANSI_RED + "ERROR WHILE SAVING JOB OFFER: " + ANSI_RESET + e.getMessage());
        }
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
}
