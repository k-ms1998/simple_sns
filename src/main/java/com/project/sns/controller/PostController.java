package com.project.sns.controller;

import com.project.sns.dto.entity.PostDto;
import com.project.sns.dto.request.PostCreateRequest;
import com.project.sns.dto.request.PostUpdateRequest;
import com.project.sns.dto.response.PostResponse;
import com.project.sns.dto.response.ResponseBody;
import com.project.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseBody<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());

        return ResponseBody.success("Success", null);
    }

    @PostMapping("/update/{postId}")
    public ResponseBody<PostResponse> update(@RequestBody PostUpdateRequest request, @PathVariable Long postId, Authentication authentication) {
        PostDto postDto = postService.update(request.getTitle(), request.getBody(), authentication.getName(), postId);

        return ResponseBody.success("Success", PostResponse.fromPostDto(postDto));
    }

}
