package com.project.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    private String title;
    private String body;

    public static PostUpdateRequest of(String title, String body) {
        return new PostUpdateRequest(title, body);
    }
}
