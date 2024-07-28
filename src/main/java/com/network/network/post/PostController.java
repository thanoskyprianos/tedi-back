package com.network.network.post;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostController {
    @Resource
    private PostRepository postRepository;

    @GetMapping("/posts")
    public List<Post> posts() {
        return postRepository.findAll();
    }
}
