package com.project.sns.controller;

import com.project.sns.dto.entity.PostDto;
import com.project.sns.dto.request.PostCreateRequest;
import com.project.sns.dto.request.PostUpdateRequest;
import com.project.sns.dto.response.PostResponse;
import com.project.sns.dto.response.ResponseBody;
import com.project.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseBody<Page<PostResponse>> fetchAll(Pageable pageable) {
        Page<PostDto> result = postService.fetchAllPosts(pageable);
        Page<PostResponse> responsePage = result.map(PostResponse::fromPostDto);

        return ResponseBody.success("Success", responsePage);
    }

    @GetMapping("/my")
    public ResponseBody<Page<PostResponse>> fetchMy(Pageable pageable, Authentication authentication) {
        Page<PostDto> result = postService.fetchMyPosts(pageable, authentication.getName());
        Page<PostResponse> responsePage = result.map(PostResponse::fromPostDto);

        return ResponseBody.success("Success", responsePage);
    }

    @PostMapping("/create")
    public ResponseBody<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());

        return ResponseBody.success("Success", null);
    }

    @PostMapping("/update/{id}")
    public ResponseBody<PostResponse> update(@RequestBody PostUpdateRequest request, @PathVariable Long id, Authentication authentication) {
        PostDto postDto = postService.update(request.getTitle(), request.getBody(), authentication.getName(), Long.valueOf(id));

        return ResponseBody.success("Success", PostResponse.fromPostDto(postDto));
    }

    @PostMapping("/delete/{id}")
    public ResponseBody delete(@PathVariable Long id, Authentication authentication) {
        postService.delete(authentication.getName(), Long.valueOf(id));

        return ResponseBody.success("Success");
    }

    @PostMapping("/upvote/{id}")
    public ResponseBody upvote(@PathVariable Long id, Authentication authentication) {
        postService.upvote(authentication.getName(), id);

        return ResponseBody.success("Success");
    }

    @GetMapping("/upvote/{id}/count")
    public ResponseBody fetchUpVotesCount(@PathVariable Long id) {
        Long upVotesCount = postService.fetchUpVotesCount(id);

        return ResponseBody.success("Success", upVotesCount);
    }

}
