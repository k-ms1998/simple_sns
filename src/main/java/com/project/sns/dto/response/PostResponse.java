package com.project.sns.dto.response;

import com.project.sns.dto.entity.PostDto;
import com.project.sns.dto.entity.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String body;
    private String username;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static PostResponse fromPostDto(PostDto postDto) {
        return new PostResponse(
                postDto.getId(),
                postDto.getTitle(),
                postDto.getBody(),
                postDto.getUserDto().getUsername(),
                postDto.getRegisteredAt(),
                postDto.getUpdatedAt(),
                postDto.getDeletedAt()
        );
    }
}
