package com.network.network.post.resource;

import com.network.network.post.Post;
import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByIdAndUser(Integer id, User user);
    List<Post> getAllByIsJobOfferIsTrue();
}
