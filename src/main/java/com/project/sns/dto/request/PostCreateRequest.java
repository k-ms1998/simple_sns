package com.project.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    private String title;
    private String body;

    public static PostCreateRequest of(String title, String body) {
        return new PostCreateRequest(title, body);
    }
}
