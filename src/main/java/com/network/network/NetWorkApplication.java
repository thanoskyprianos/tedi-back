package com.network.network;

import com.network.network.media.Media;
import com.network.network.media.MediaRepository;
import com.network.network.post.Post;
import com.network.network.post.PostRepository;
import com.network.network.user.Role;
import com.network.network.user.User;
import com.network.network.user.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class NetWorkApplication {
    @Resource
    private UserRepository userRepository;

    @Resource
    private PostRepository postRepository;

    @Resource
    private MediaRepository mediaRepository;

    public static void main(String[] args) {
        SpringApplication.run(NetWorkApplication.class, args);
    }

    @PostConstruct
    public void init() {
        Media media1 = new Media("path1");
        Media media2 = new Media("path2");
        Media media3 = new Media("path3");

        User user1 = new User("abc", "abc", "abc", "abc", "abc", media1, Role.ADMIN);
        User user2 = new User("abc", "abc", "def", "abc", "abc", media2, Role.ADMIN);

        Post post = new Post("This is a post");
        post.addMedia(media1);
        post.addMedia(media3);

        mediaRepository.saveAll(List.of(media1, media2, media3));

        userRepository.save(user1);
        userRepository.save(user2);

        postRepository.save(post);
    }
}
