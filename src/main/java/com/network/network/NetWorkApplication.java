package com.network.network;

import com.network.network.role.Role;
import com.network.network.role.resource.RoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NetWorkApplication {
    @Resource
    private RoleRepository roleRepository;

    @Value("${roles.names.admin}")
    private String adminName;

    @Value("${roles.names.professional}")
    private String professionalName;

    public static void main(String[] args) {
        SpringApplication.run(NetWorkApplication.class, args);
    }

//    @PostConstruct
//    public void init() {
//        Role admin = new Role();
//        admin.setName(adminName);
//
//        Role professional = new Role();
//        professional.setName(professionalName);
//
//        roleRepository.save(admin);
//        roleRepository.save(professional);
//    }
}
