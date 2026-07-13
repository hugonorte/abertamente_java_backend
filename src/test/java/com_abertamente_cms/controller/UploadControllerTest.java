package com_abertamente_cms.controller;

import com_abertamente_cms.security.JwtAuthenticationFilter;
import com_abertamente_cms.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldUploadAvatarSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "dummy-content".getBytes());
        when(fileStorageService.storeFile(any(), eq("avatars"))).thenReturn("/uploads/avatars/random-uuid.png");

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .param("type", "avatar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/uploads/avatars/random-uuid.png"));
    }

    @Test
    void shouldUploadPostImageSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "post.jpg", "image/jpeg", "dummy-content".getBytes());
        when(fileStorageService.storeFile(any(), eq("posts"))).thenReturn("/uploads/posts/random-uuid.jpg");

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .param("type", "post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/uploads/posts/random-uuid.jpg"));
    }

    @Test
    void shouldRejectUnsupportedExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "script.js", "application/javascript", "alert(1);".getBytes());

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .param("type", "post"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Formato de arquivo não suportado. Use PNG, JPG, WEBP ou AVIF."));
    }

    @Test
    void shouldRejectInvalidType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummy-content".getBytes());

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .param("type", "invalidType"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tipo de upload inválido. Use 'avatar' ou 'post'."));
    }

    @Test
    void shouldRejectOversizedAvatar() throws Exception {
        byte[] largeContent = new byte[1024 * 1024 + 1]; // 1MB + 1 byte
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", largeContent);

        mockMvc.perform(multipart("/api/upload")
                        .file(file)
                        .param("type", "avatar"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.message").value("O arquivo excede o limite máximo permitido de 1MB para avatar."));
    }
}
