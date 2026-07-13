package com_abertamente_cms.dto.post;

import com_abertamente_cms.domain.Post;
import com_abertamente_cms.domain.PostStatus;
import com_abertamente_cms.dto.category.CategoryResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String title,
        String slug,
        String content,
        PostStatus status,
        UUID authorId,
        String authorName,
        CategoryResponse category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse fromEntity(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getContent(),
                post.getStatus(),
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                CategoryResponse.fromEntity(post.getCategory()),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
