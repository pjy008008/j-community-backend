package com.pjy008008.j_community.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
        @NotBlank(message = "수정할 내용은 필수입니다.")
        String content
) {}