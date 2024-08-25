package com.network.network;

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

@SpringBootApplication
public class NetWorkApplication {
    @Resource
    private RoleRepository roleRepository;

    @Resource
    private JwtTokenRepository tokenRepository;

    @Resource
    private UserService userService;

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
            System.out.println("CREATING ADMIN");

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword);

            userService.saveAdmin(admin);
        }
    }
}
