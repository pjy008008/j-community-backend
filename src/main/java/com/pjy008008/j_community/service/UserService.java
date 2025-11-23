package com.pjy008008.j_community.service;

import com.pjy008008.j_community.controller.dto.CommunityResponse;
import com.pjy008008.j_community.controller.dto.PostResponse;
import com.pjy008008.j_community.entity.Community;
import com.pjy008008.j_community.entity.Post;
import com.pjy008008.j_community.entity.User;
import com.pjy008008.j_community.entity.UserCommunity;
import com.pjy008008.j_community.exception.DuplicateResourceException;
import com.pjy008008.j_community.exception.ResourceNotFoundException;
import com.pjy008008.j_community.repository.CommunityRepository;
import com.pjy008008.j_community.repository.PostRepository;
import com.pjy008008.j_community.repository.UserCommunityRepository;
import com.pjy008008.j_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final UserCommunityRepository userCommunityRepository;

    public Page<PostResponse> getMyPosts(String username, Pageable pageable) {
        User user = getUserByUsername(username);
        Page<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(user.getId(), pageable);
        return posts.map(PostResponse::from);
    }

    @Transactional
    public void joinCommunity(String username, String communityName) {
        User user = getUserByUsername(username);
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found: " + communityName));

        if (userCommunityRepository.existsByUserIdAndCommunityId(user.getId(), community.getId())) {
            throw new DuplicateResourceException("Already joined this community");
        }

        UserCommunity userCommunity = UserCommunity.builder()
                .user(user)
                .community(community)
                .build();

        userCommunityRepository.save(userCommunity);
    }

    @Transactional
    public void leaveCommunity(String username, String communityName) {
        User user = getUserByUsername(username);
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found: " + communityName));

        UserCommunity userCommunity = userCommunityRepository.findByUserIdAndCommunityId(user.getId(), community.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not joined this community"));

        userCommunityRepository.delete(userCommunity);
    }

    public List<CommunityResponse> getMyCommunities(String username) {
        User user = getUserByUsername(username);
        List<UserCommunity> list = userCommunityRepository.findAllByUserId(user.getId());

        return list.stream()
                .map(uc -> CommunityResponse.from(uc.getCommunity()))
                .collect(Collectors.toList());
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}