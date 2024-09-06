package com.network.network.post.service;

import com.network.network.post.Post;
import com.network.network.post.exception.PostNotFoundException;
import com.network.network.post.resource.PostRepository;
import com.network.network.recommendation.JobType;
import com.network.network.recommendation.Recommendation;
import com.network.network.recommendation.resource.RecommendationRepository;
import com.network.network.user.User;
import com.network.network.user.info.Info;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PostService {
    @Resource
    private PostRepository postRepository;

    @Resource
    private UserService userService;

    @Value("${post.page.size}")
    private int pageSize;

    @Resource
    private RecommendationRepository recommendationRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll()
                             .stream().filter(Post::isPost)
                             .sorted(Comparator.comparing(Post::getCreated))
                             .toList();
    }

    public List<Post> getAllPostsFor(User user, int page) throws IllegalArgumentException {
        if (page <= 0) {
            throw new IllegalArgumentException("Page should be positive");
        }

        List<Post> recPosts =
                recommendationRepository.findAllByUserAndType(user, JobType.POST)
                                        .stream()
                                        .sorted(Comparator.<Recommendation, Boolean>
                                                 comparing(rec -> !rec.getPost().getUser().isConnected(user)) // ! makes connected go first
                                                .thenComparingDouble(Recommendation::getRating)
                                        )
                                        .map(Recommendation::getPost)
                                        .toList();
        if (recPosts.isEmpty()) {
            List<Post> connPosts =
                    new ArrayList<>(postRepository.findAll().stream().filter(post ->
                        post.isPost() && post.getUser() != user && (
                                post.getUser().isConnected(user) ||
                                user.getConnected().stream().anyMatch(connection ->
                                        connection.isConnected(post.getUser()))))
                            .sorted(Comparator.comparing(Post::getCreated).reversed())
                            .toList());

            // get maxPage latest posts (used for new users)
            if (connPosts.isEmpty()) {
                if (page == 1) {
                    List<Post> posts = getAllPosts();
                    return posts.subList(0, Math.min(pageSize, posts.size()));
                } else {
                    throw new IllegalArgumentException("Should follow users or wait for recommendations");
                }
            }

            return connPosts.subList((page - 1) * pageSize, Math.min(page * pageSize, connPosts.size()));
        }

        return recPosts.subList((page - 1) * pageSize, Math.min(page * pageSize, recPosts.size()));
    }

    public Post getPost(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // Job Offers section
    public List<Post> getAllJobOffers() {
        return postRepository.findAll().stream()
                .filter(Post::isJobOffer).toList();
    }

    public List<Post> getJobOffersUserBased(int userId, int page) {
        if (page <= 0) {
            throw new IllegalArgumentException("Page should be positive");
        }

        List<Post> allJobOfferPosts = postRepository.getAllByIsJobOfferIsTrue();

        User user = userService.getUserById(userId);
        List<String> userSkills = skillsSepUser(user);

        // for every job offer find matching score
        List<Post> selected = allJobOfferPosts.stream()
        .peek(post -> post.setMatchLvl(matchPoints(userSkills, skillsSepPost(post))))
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
