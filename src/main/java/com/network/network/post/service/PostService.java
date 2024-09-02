package com.network.network.post.service;

import com.network.network.post.Post;
import com.network.network.post.exception.PostNotFoundException;
import com.network.network.post.resource.PostRepository;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Resource
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPost(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // Job Offers section
    /* public List<Post> getJobOfferPosts() {
        return postRepository.getAllByIsJobOfferIsTrue();
    } */

    public List<Post> getJobOffersUserBased(int userId) {
        List<Post> allJobOfferPosts = postRepository.getAllByIsJobOfferIsTrue();

        User user = userService.getUserById(userId);
        System.out.println(allJobOfferPosts);
        List<String> userSkills = skillsSepUser(user);
        System.out.println(userSkills);

        // for every job offer find matching score
        return allJobOfferPosts.stream()
        .sorted(Comparator.comparingInt(jobOff -> matchPoints(userSkills, skillsSepPost((Post) jobOff))).reversed())
        .collect(Collectors.toList());
    }

    public List<String> skillsSepUser(User user) {
        String skills = user.getInfo().getSkills();
        if (skills != null && !skills.isEmpty()) {
            return Arrays.asList(skills.split(","));
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
