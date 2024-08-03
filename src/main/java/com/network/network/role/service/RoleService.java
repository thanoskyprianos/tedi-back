package com.network.network.role.service;

import com.network.network.role.Role;
import com.network.network.role.resource.RoleRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Resource
    private RoleRepository roleRepository;

    public Role getRole(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
}
