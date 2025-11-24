package com.pjy008008.j_community.controller;

import com.pjy008008.j_community.controller.dto.ErrorResponse;
import com.pjy008008.j_community.controller.dto.PostCreateRequest;
import com.pjy008008.j_community.controller.dto.PostResponse;
import com.pjy008008.j_community.controller.dto.PostUpdateRequest;
import com.pjy008008.j_community.model.VoteType;
import com.pjy008008.j_community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "게시글 생성, 조회 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다. (인증 필요)")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        PostResponse response = postService.createPost(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails // 추가
    ) {
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        Page<PostResponse> posts = postService.getAllPosts(pageable, username);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/c/{communityName}")
    public ResponseEntity<Page<PostResponse>> getPostsByCommunity(
            @PathVariable("communityName") String communityName,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails // 추가
    ) {
        String username = (userDetails != null) ? userDetails.getUsername() : null;
        Page<PostResponse> posts = postService.getPostsByCommunity(communityName, pageable, username);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 수정", description = "자신이 작성한 게시글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (작성자 아님)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        PostResponse response = postService.updatePost(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "자신이 작성한 게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (작성자 아님)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 추천", description = "게시글을 추천(+1)하거나 취소합니다.")
    @PostMapping("/{id}/upvote")
    public ResponseEntity<Integer> upvotePost(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        int votes = postService.votePost(id, VoteType.UP, userDetails.getUsername());
        return ResponseEntity.ok(votes);
    }

    @Operation(summary = "게시글 비추천", description = "게시글을 비추천(-1)하거나 취소합니다.")
    @PostMapping("/{id}/downvote")
    public ResponseEntity<Integer> downvotePost(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        int votes = postService.votePost(id, VoteType.DOWN, userDetails.getUsername());
        return ResponseEntity.ok(votes);
    }
}