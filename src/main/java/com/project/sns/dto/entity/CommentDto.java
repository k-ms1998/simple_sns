package com.project.sns.dto.entity;

import com.project.sns.domain.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String comment;
    private String username;
    private Long postId;

    public static CommentDto fromEntity(CommentEntity commentEntity) {
        return new CommentDto(
                commentEntity.getId(),
                commentEntity.getComment(),
                commentEntity.getUserEntity().getUsername(),
                commentEntity.getPostEntity().getId()
        );
    }

}
