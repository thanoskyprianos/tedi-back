package com.network.network.role;

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
