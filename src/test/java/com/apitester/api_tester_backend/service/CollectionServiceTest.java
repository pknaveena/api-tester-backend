package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.apitester.api_tester_backend.dto.request.CollectionRequest;
import com.apitester.api_tester_backend.dto.response.CollectionResponse;
import com.apitester.api_tester_backend.entity.Collection;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.CollectionRepository;
import com.apitester.api_tester_backend.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private CollectionService collectionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L) 
            .build();
    }

    // CREATE
    @Test
    void shouldCreateCollection() {

         // Arrange

        CollectionRequest request = CollectionRequest.builder()
                .name("Users API")
                .description("Collection for user APIs")
                .build();

        Collection savedCollection = Collection.builder()
                .id(10L)
                .name("Users API")
                .description("Collection for user APIs")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.save(any(Collection.class)))
                .thenReturn(savedCollection);

        // Act

        CollectionResponse response = collectionService.create(request);

        // Assert

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Users API", response.getName());
        assertEquals(
                "Collection for user APIs",
                response.getDescription());

        verify(securityUtil).getCurrentUser();
        verify(collectionRepository).save(any(Collection.class));
    }

    // --------------------------------------------------
    // GET ALL
    // --------------------------------------------------

    @Test
    void shouldGetAllCollectionsForCurrentUser() {

        Collection collection1 = Collection.builder()
                .id(1L)
                .name("Users API")
                .description("User endpoints")
                .user(user)
                .build();

        Collection collection2 = Collection.builder()
                .id(2L)
                .name("Orders API")
                .description("Order endpoints")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByUser(user))
                .thenReturn(List.of(collection1, collection2));

        List<CollectionResponse> response = collectionService.getAll();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals(
                "Users API",
                response.get(0).getName());

        assertEquals(2L, response.get(1).getId());
        assertEquals(
                "Orders API",
                response.get(1).getName());

        verify(securityUtil).getCurrentUser();
        verify(collectionRepository).findByUser(user);
    }

    // --------------------------------------------------
    // GET ALL - EMPTY
    // --------------------------------------------------

    @Test
    void shouldReturnEmptyListWhenNoCollectionsExist() {

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByUser(user))
                .thenReturn(List.of());

        List<CollectionResponse> response = collectionService.getAll();

        assertNotNull(response);
        assertEquals(0, response.size());

        verify(collectionRepository)
                .findByUser(user);
    }

    // --------------------------------------------------
    // GET BY ID
    // --------------------------------------------------

    @Test
    void shouldGetCollectionById() {

        Collection collection = Collection.builder()
                .id(10L)
                .name("Users API")
                .description("User endpoints")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository
                .findByIdAndUser(10L, user))
                .thenReturn(Optional.of(collection));

        CollectionResponse response = collectionService.getById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(
                "Users API",
                response.getName());
        assertEquals(
                "User endpoints",
                response.getDescription());

        verify(collectionRepository)
                .findByIdAndUser(10L, user);
    }

    // --------------------------------------------------
    // GET BY ID - NOT FOUND
    // --------------------------------------------------

    @Test
    void shouldThrowExceptionWhenCollectionNotFound() {

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository
                .findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> collectionService.getById(99L));

        assertEquals(
                "Collection not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(collectionRepository)
                .findByIdAndUser(99L, user);
    }

    // --------------------------------------------------
    // UPDATE
    // --------------------------------------------------

    @Test
    void shouldUpdateCollection() {

        Collection collection = Collection.builder()
                .id(10L)
                .name("Old Name")
                .description("Old description")
                .user(user)
                .build();

        CollectionRequest request = CollectionRequest.builder()
                .name("Updated Name")
                .description("Updated description")
                .build();

        Collection savedCollection = Collection.builder()
                .id(10L)
                .name("Updated Name")
                .description("Updated description")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository
                .findByIdAndUser(10L, user))
                .thenReturn(Optional.of(collection));

        when(collectionRepository.save(collection))
                .thenReturn(savedCollection);

        CollectionResponse response = collectionService.update(10L, request);

        assertNotNull(response);
        assertEquals(
                10L,
                response.getId());

        assertEquals(
                "Updated Name",
                response.getName());

        assertEquals(
                "Updated description",
                response.getDescription());

        verify(collectionRepository)
                .findByIdAndUser(10L, user);

        verify(collectionRepository)
                .save(collection);
    }

    // --------------------------------------------------
    // UPDATE - NOT FOUND
    // --------------------------------------------------

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCollection() {

        CollectionRequest request = CollectionRequest.builder()
                .name("Updated Name")
                .description("Updated description")
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository
                .findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> collectionService.update(99L, request));

        assertEquals(
                "Collection not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(collectionRepository, never())
                .save(any(Collection.class));
    }

    // --------------------------------------------------
    // DELETE
    // --------------------------------------------------

    @Test
    void shouldDeleteCollection() {

        Collection collection = Collection.builder()
                .id(10L)
                .name("Users API")
                .description("User endpoints")
                .user(user)
                .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository
                .findByIdAndUser(10L, user))
                .thenReturn(Optional.of(collection));

        collectionService.delete(10L);

        verify(collectionRepository)
                .findByIdAndUser(10L, user);

        verify(collectionRepository)
                .delete(collection);
    }

    // --------------------------------------------------
    // DELETE - NOT FOUND
    // --------------------------------------------------

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCollection() {

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository
                .findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> collectionService.delete(99L));

        assertEquals(
                "Collection not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(collectionRepository, never())
                .delete(any(Collection.class));
    }
}