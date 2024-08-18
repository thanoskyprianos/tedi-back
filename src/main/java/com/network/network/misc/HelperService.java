package com.network.network.misc;

import com.network.network.comment.Comment;
import com.network.network.comment.service.CommentService;
import com.network.network.post.Post;
import com.network.network.post.service.PostService;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class HelperService {
    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private CommentService commentService;

    public Post getPostByPair(int userId, int postId) {
        User user = userService.getUserById(userId);

        return postService.getPostByIdAndUser(postId, user);
    }

    public Comment getCommentByTriplet(int userId, int postId, int commentId) {
        User user = userService.getUserById(userId);
        Post post = postService.getPostByIdAndUser(postId, user);

        return commentService.getCommentByPost(commentId, post);
    }
}
