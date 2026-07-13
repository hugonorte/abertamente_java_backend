package com_abertamente_cms.service;

import com_abertamente_cms.domain.Category;
import com_abertamente_cms.domain.Post;
import com_abertamente_cms.domain.PostStatus;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.post.PostRequest;
import com_abertamente_cms.dto.post.PostResponse;
import com_abertamente_cms.repository.CategoryRepository;
import com_abertamente_cms.repository.PostRepository;
import com_abertamente_cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostService postService;

    private User author;
    private Category category;
    private Post post;
    private UUID postId;

    @BeforeEach
    void setUp() {
        author = new User("Jane", "jane@example.com", "pass");
        ReflectionTestUtils.setField(author, "id", UUID.randomUUID());

        category = new Category("Tech", "tech", "desc");
        ReflectionTestUtils.setField(category, "id", UUID.randomUUID());

        postId = UUID.randomUUID();
        post = new Post("Título", "titulo", "conteudo", author, category);
        ReflectionTestUtils.setField(post, "id", postId);
    }

    @Test
    void shouldCreatePost() {
        PostRequest request = new PostRequest("Título Novo", "titulo-novo", "conteudo", category.getId());

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("jane@example.com");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(author));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(postRepository.findBySlug(request.slug())).thenReturn(Optional.empty());
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostResponse response = postService.create(request);

        assertNotNull(response);
        assertEquals("Título Novo", response.title());
        assertEquals("titulo-novo", response.slug());
    }

    @Test
    void shouldUpdatePost() {
        PostRequest request = new PostRequest("Título Edit", "titulo-edit", "conteudo edit", category.getId());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.findBySlug(request.slug())).thenReturn(Optional.empty());
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostResponse response = postService.update(postId, request);

        assertEquals("Título Edit", response.title());
    }

    @Test
    void shouldDeletePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.delete(postId);
        verify(postRepository).delete(post);
    }

    @Test
    void shouldChangeStatus() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostResponse response = postService.changeStatus(postId, PostStatus.PUBLISHED);

        assertEquals(PostStatus.PUBLISHED, response.status());
    }
}
