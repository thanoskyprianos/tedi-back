package com.network.network.post.service;

import com.network.network.post.Post;
import com.network.network.post.exception.PostNotFoundException;
import com.network.network.post.resource.PostRepository;
import com.network.network.user.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PostService {
    @Resource
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPost(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public List<Post> getJobOfferPosts() {
        return postRepository.getAllByIsJobOfferIsTrue();
    }

    public List<String> getSeparatedSkills(String skills) {
        if (skills != null) {
            return Arrays.asList(skills.split(","));
        }
        return List.of();
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostByIdAndUser(int id, User user) {
        return postRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public void removePost(Post post) {
        postRepository.delete(post);
    }


}
