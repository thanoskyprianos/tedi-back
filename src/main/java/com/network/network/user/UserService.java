package com.network.network.user;

import com.network.network.media.Media;
import com.network.network.media.MediaNotFoundException;
import com.network.network.media.MediaService;
import com.network.network.role.Role;
import com.network.network.role.RoleNotFoundException;
import com.network.network.role.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private MediaService mediaService;

    @Resource
    RoleService roleService;

    public User getUser(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public User saveUser(User user) {
        Role role = roleService.getRole(user.getRole().getName());
        if (role == null) {
            throw new RoleNotFoundException(user.getRole().getName());
        }

        user.setRole(role);
        role.addUser(user);

        Media avatar = mediaService.getMedia(user.getAvatar().getId());
        if (avatar == null) {
            throw new MediaNotFoundException(user.getAvatar().getId());
        }

        user.setAvatar(avatar);

        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
