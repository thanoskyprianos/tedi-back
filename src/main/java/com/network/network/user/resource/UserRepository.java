package com.network.network.user.resource;

import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, CustomUserRepository {
    Optional<User> findByEmail(String email);
}
