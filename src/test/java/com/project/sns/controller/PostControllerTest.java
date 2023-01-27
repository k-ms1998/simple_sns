package com.project.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sns.dto.request.PostCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("[Controller][Post] Creating Post - Success")
    @Test
    @WithMockUser
    void givenParameters_whenSavingPost_thenSuccess() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        PostCreateRequest request = createPostCreateRequest(title, body);

        // When & Then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[Controller][Post] Creating Post - Not Logged In - Fail")
    @Test
    @WithAnonymousUser
    void givenParameters_whenSavingPostAndNotLoggedIn_thenFail() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        PostCreateRequest request = createPostCreateRequest(title, body);

        // When & Then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized());


    }

    private static PostCreateRequest createPostCreateRequest(String title, String body) {
        return PostCreateRequest.of(title, body);
    }
}