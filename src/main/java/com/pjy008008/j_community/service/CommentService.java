package com.pjy008008.j_community.service;

import com.pjy008008.j_community.controller.dto.CommentCreateRequest;
import com.pjy008008.j_community.controller.dto.CommentResponse;
import com.pjy008008.j_community.controller.dto.CommentUpdateRequest;
import com.pjy008008.j_community.entity.Comment;
import com.pjy008008.j_community.entity.Post;
import com.pjy008008.j_community.entity.User;
import com.pjy008008.j_community.exception.ResourceNotFoundException;
import com.pjy008008.j_community.repository.CommentRepository;
import com.pjy008008.j_community.repository.PostRepository;
import com.pjy008008.j_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<CommentResponse> getCommentsByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        List<Comment> topLevelComments = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);

        return topLevelComments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse createTopLevelComment(Long postId, CommentCreateRequest request, String username) {
        User author = findUserByUsername(username);
        Post post = findPostById(postId);

        Comment newComment = Comment.builder()
                .content(request.content())
                .author(author)
                .post(post)
                .parent(null)
                .build();

        Comment savedComment = commentRepository.save(newComment);
        return CommentResponse.from(savedComment);
    }

    @Transactional
    public CommentResponse createReply(Long parentCommentId, CommentCreateRequest request, String username) {
        User author = findUserByUsername(username);

        Comment parentComment = findCommentById(parentCommentId);

        Post post = parentComment.getPost();

        Comment reply = Comment.builder()
                .content(request.content())
                .author(author)
                .post(post)
                .parent(parentComment)
                .build();

        Comment savedReply = commentRepository.save(reply);
        return CommentResponse.from(savedReply);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, String username) {
        Comment comment = findCommentById(commentId);

        validateAuthor(comment, username);

        comment.update(request.content());

        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = findCommentById(commentId);

        validateAuthor(comment, username);

        commentRepository.delete(comment);
    }

    private void validateAuthor(Comment comment, String username) {
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not the author of this comment.");
        }
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
    }
}