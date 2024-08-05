package com.network.network.user.resource;

import com.network.network.user.User;

import java.util.List;

public interface CustomUserRepository {
    List<User> findByNameLike(String name);
}
