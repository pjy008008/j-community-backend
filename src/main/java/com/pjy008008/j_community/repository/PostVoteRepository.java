package com.pjy008008.j_community.repository;

import com.pjy008008.j_community.entity.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    Optional<PostVote> findByUserIdAndPostId(Long userId, Long postId);
    List<PostVote> findByUserIdAndPostIdIn(Long userId, List<Long> postIds);
}