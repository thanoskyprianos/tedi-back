package com.network.network.user;

import jakarta.annotation.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    public EntityModel<User> getUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return userResourceAssembler.toModel(user);
    }

    public CollectionModel<EntityModel<User>> getAllUsers() {
        List<User> users = userRepository.findAll();

        return userResourceAssembler.toCollectionModel(users);
    }
}
