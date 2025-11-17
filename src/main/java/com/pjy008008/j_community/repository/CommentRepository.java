package com.pjy008008.j_community.repository;

import com.pjy008008.j_community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);
}