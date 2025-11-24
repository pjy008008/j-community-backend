package com.pjy008008.j_community.service;

import com.pjy008008.j_community.controller.dto.CommunityResponse;
import com.pjy008008.j_community.controller.dto.PostResponse;
import com.pjy008008.j_community.entity.*;
import com.pjy008008.j_community.exception.DuplicateResourceException;
import com.pjy008008.j_community.exception.ResourceNotFoundException;
import com.pjy008008.j_community.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SavedPostRepository savedPostRepository;
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

    @Transactional
    public boolean toggleSavedPost(String username, Long postId) {
        User user = getUserByUsername(username);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        Optional<SavedPost> savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), postId);

        if (savedPost.isPresent()) {
            savedPostRepository.delete(savedPost.get());
            return false;
        } else {
            savedPostRepository.save(SavedPost.builder().user(user).post(post).build());
            return true;
        }
    }

    public List<CommunityResponse> getMyCommunities(String username) {
        User user = getUserByUsername(username);
        List<UserCommunity> list = userCommunityRepository.findAllByUserId(user.getId());

        return list.stream()
                .map(uc -> CommunityResponse.from(uc.getCommunity()))
                .collect(Collectors.toList());
    }

    public Page<PostResponse> getMySavedPosts(String username, Pageable pageable) {
        User user = getUserByUsername(username);

        Page<SavedPost> savedPosts = savedPostRepository.findAllByUserId(user.getId(), pageable);

        return savedPosts.map(sp -> PostResponse.from(sp.getPost()));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}