package com.project.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    private String comment;

    public static CommentCreateRequest of(String comment) {
        return new CommentCreateRequest(comment);
    }
}
