package com.pjy008008.j_community.controller;

import com.pjy008008.j_community.controller.dto.CommunityResponse;
import com.pjy008008.j_community.controller.dto.PostResponse;
import com.pjy008008.j_community.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API (내 활동 조회, 커뮤니티 가입 등)")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내가 쓴 글 조회", description = "현재 로그인한 사용자가 작성한 게시글을 페이징하여 조회합니다.")
    @GetMapping("/me/posts")
    public ResponseEntity<Page<PostResponse>> getMyPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PostResponse> posts = userService.getMyPosts(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 저장/취소 (토글)", description = "게시글을 보관함에 저장하거나 취소합니다. (저장됨: true, 취소됨: false 반환)")
    @PostMapping("/me/saved-posts/{postId}")
    public ResponseEntity<Boolean> toggleSavedPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("postId") Long postId
    ) {
        boolean isSaved = userService.toggleSavedPost(userDetails.getUsername(), postId);
        return ResponseEntity.ok(isSaved);
    }

    @Operation(summary = "내가 저장한 글 조회", description = "내가 저장한 게시글 목록을 페이징하여 조회합니다.")
    @GetMapping("/me/saved-posts")
    public ResponseEntity<Page<PostResponse>> getMySavedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PostResponse> posts = userService.getMySavedPosts(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "내가 가입한 커뮤니티 조회", description = "현재 로그인한 사용자가 가입한 커뮤니티 목록을 조회합니다.")
    @GetMapping("/me/communities")
    public ResponseEntity<List<CommunityResponse>> getMyCommunities(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CommunityResponse> communities = userService.getMyCommunities(userDetails.getUsername());
        return ResponseEntity.ok(communities);
    }

    @Operation(summary = "커뮤니티 가입", description = "특정 커뮤니티에 가입합니다.")
    @PostMapping("/me/communities/{communityName}")
    public ResponseEntity<Void> joinCommunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("communityName") String communityName
    ) {
        userService.joinCommunity(userDetails.getUsername(), communityName);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "커뮤니티 탈퇴", description = "가입한 커뮤니티에서 탈퇴합니다.")
    @DeleteMapping("/me/communities/{communityName}")
    public ResponseEntity<Void> leaveCommunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("communityName") String communityName
    ) {
        userService.leaveCommunity(userDetails.getUsername(), communityName);
        return ResponseEntity.noContent().build();
    }
}