package com.pjy008008.j_community.controller.dto;

import com.pjy008008.j_community.entity.Comment;
import com.pjy008008.j_community.model.VoteType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record CommentResponse(
        Long id,
        String author,
        String content,
        int votes,
        VoteType myVote,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {
    public static CommentResponse from(Comment comment) {
        return from(comment, Collections.emptyMap());
    }

    public static CommentResponse from(Comment comment, Map<Long, VoteType> userVotes) {
        VoteType myVote = userVotes.getOrDefault(comment.getId(), null);

        List<CommentResponse> replyDtos = comment.getReplies().stream()
                .map(reply -> from(reply, userVotes))
                .collect(Collectors.toList());

        return new CommentResponse(
                comment.getId(),
                comment.getAuthor().getUsername(),
                comment.getContent(),
                comment.getVotes(),
                myVote,
                comment.getCreatedAt(),
                replyDtos
        );
    }
}