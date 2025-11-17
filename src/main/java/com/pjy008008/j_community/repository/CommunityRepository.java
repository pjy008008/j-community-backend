package com.pjy008008.j_community.repository;
import com.pjy008008.j_community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Optional<Community> findByName(String name);
}