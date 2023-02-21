package com.project.sns.dto.response;

import com.project.sns.domain.CommentEntity;
import com.project.sns.dto.entity.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String comment;
    private String username;
    private Long postId;

    public static CommentResponse fromDto(CommentDto commentDto) {
        return new CommentResponse(
                commentDto.getId(),
                commentDto.getComment(),
                commentDto.getUsername(),
                commentDto.getPostId()
        );
    }
}
