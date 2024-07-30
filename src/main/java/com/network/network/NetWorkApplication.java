package com.network.network;

import com.network.network.role.Role;
import com.network.network.role.RoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NetWorkApplication {
    @Resource
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(NetWorkApplication.class, args);
    }

    @PostConstruct
    public void init() {
        Role admin = new Role();
        admin.setName("ADMIN");

        Role professional = new Role();
        professional.setName("PROFESSIONAL");

        roleRepository.save(admin);
        roleRepository.save(professional);
    }
}
