package com.pjy008008.j_community.repository;
import com.pjy008008.j_community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}