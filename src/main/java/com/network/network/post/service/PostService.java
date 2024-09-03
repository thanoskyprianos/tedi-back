package com.network.network.post.service;

import com.network.network.post.Post;
import com.network.network.post.exception.PostNotFoundException;
import com.network.network.post.resource.PostRepository;
import com.network.network.user.User;
import com.network.network.user.info.Info;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Resource
    private PostRepository postRepository;

    @Resource
    private UserService userService;

    @Value("${post.page.size}")
    private int pageSize;

    public List<Post> getAllPostsFor(User user, int page) throws IllegalArgumentException {
        if (page <= 0) {
            throw new IllegalArgumentException("Page should be positive");
        }

        List<Post> posts = postRepository.findAll().stream().filter(post ->
                post.isPost() &&
                post.getUser() != user &&
                (post.getUser().isConnected(user) ||
                user.getConnected().stream().anyMatch(connection -> connection.isConnected(post.getUser()))))
           .collect(Collectors.toList());

        return posts.subList((page - 1) * pageSize, Math.min(page * pageSize, posts.size()));
    }

    public Post getPost(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // Job Offers section
    /* public List<Post> getJobOfferPosts() {
        return postRepository.getAllByIsJobOfferIsTrue();
    } */

    public List<Post> getJobOffersUserBased(int userId, int page) {
        if (page <= 0) {
            throw new IllegalArgumentException("Page should be positive");
        }

        List<Post> allJobOfferPosts = postRepository.getAllByIsJobOfferIsTrue();

        User user = userService.getUserById(userId);
        List<String> userSkills = skillsSepUser(user);

        // for every job offer find matching score
        List<Post> selected = allJobOfferPosts.stream()
        .sorted(Comparator.comparingInt(jobOff -> matchPoints(userSkills, skillsSepPost((Post) jobOff))).reversed())
        .toList();

        return selected.subList((page - 1) * pageSize, Math.min(page * pageSize, selected.size()));
    }

    public List<String> skillsSepUser(User user) {
        Info info = user.getInfo();
        if (info != null) {
            String skills = user.getInfo().getSkills();
            if (skills != null && !skills.isEmpty()) {
                return Arrays.asList(skills.split(","));
            }
        }

        return List.of();
    }

    public List<String> skillsSepPost(Post post) {
        String skills = post.getSkills();
        if (skills != null && !skills.isEmpty()) {
            return Arrays.asList(skills.split(","));
        }
        return List.of(); 
    }

    public int matchPoints(List<String> userSkills, List<String> postSkills) {
        int matchLvl = 0;
        for (String skill : userSkills) {
            if (postSkills.contains(skill)) {
                matchLvl++;
            }
        }

        return matchLvl;
    }
    // end of job offers section

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
