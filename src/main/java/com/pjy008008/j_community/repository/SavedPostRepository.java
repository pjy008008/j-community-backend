package com.pjy008008.j_community.repository;

import com.pjy008008.j_community.entity.SavedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Optional<SavedPost> findByUserIdAndPostId(Long userId, Long postId);
    @Query("SELECT sp FROM SavedPost sp JOIN FETCH sp.post WHERE sp.user.id = :userId ORDER BY sp.createdAt DESC")
    Page<SavedPost> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}