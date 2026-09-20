package com.apitester.api_tester_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.apitester.api_tester_backend.dto.request.ApiExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemExecuteRequest;
import com.apitester.api_tester_backend.dto.request.CollectionItemRequest;
import com.apitester.api_tester_backend.dto.response.ApiExecuteResponse;
import com.apitester.api_tester_backend.dto.response.CollectionItemResponse;
import com.apitester.api_tester_backend.entity.Collection;
import com.apitester.api_tester_backend.entity.CollectionItem;
import com.apitester.api_tester_backend.entity.Environment;
import com.apitester.api_tester_backend.entity.User;
import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;
import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.repository.CollectionItemRepository;
import com.apitester.api_tester_backend.repository.CollectionRepository;
import com.apitester.api_tester_backend.util.JsonUtil;
import com.apitester.api_tester_backend.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class CollectionItemServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionItemRepository collectionItemRepository;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ApiExecutionService apiExecutionService;

    @Mock
    private EnvironmentService environmentService;

    @InjectMocks
    private CollectionItemService collectionItemService;

    private User user;
    private Collection collection;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .build();

        collection = Collection.builder()
                .id(10L)
                .name("Users API")
                .description("User APIs")
                .user(user)
                .build();
    }

    // ==================================================
    // CREATE
    // ==================================================

    @Test
    void shouldCreateCollectionItem() {

        CollectionItemRequest request =
                CollectionItemRequest.builder()
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("/users")
                        .body(null)
                        .headers(Map.of(
                                "Accept",
                                "application/json"))
                        .queryParams(Map.of(
                                "page",
                                "1"))
                        .authType(AuthType.NONE)
                        .build();

        CollectionItem savedItem =
                CollectionItem.builder()
                        .id(100L)
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("/users")
                        .requestBody(null)
                        .requestHeaders(
                                "{\"Accept\":\"application/json\"}")
                        .queryParams(
                                "{\"page\":\"1\"}")
                        .authType(AuthType.NONE)
                        .authData("{}")
                        .collection(collection)
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(jsonUtil.toJson(any()))
                .thenReturn("{}");

        when(collectionItemRepository.save(
                any(CollectionItem.class)))
                .thenReturn(savedItem);

        when(jsonUtil.fromJson("{}"))
                .thenReturn(Map.of());

        CollectionItemResponse response =
                collectionItemService.create(
                        10L,
                        request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(
                "Get Users",
                response.getName());
        assertEquals(
                HttpMethod.GET,
                response.getMethod());
        assertEquals(
                "/users",
                response.getUrl());

        verify(collectionRepository)
                .findByIdAndUser(10L, user);

        verify(collectionItemRepository)
                .save(any(CollectionItem.class));
    }

    // ==================================================
    // CREATE - COLLECTION NOT FOUND
    // ==================================================

    @Test
    void shouldThrowExceptionWhenCreatingItemForNonExistingCollection() {

        CollectionItemRequest request =
                CollectionItemRequest.builder()
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("/users")
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                99L,
                user))
                .thenReturn(Optional.empty());

        ApiException exception =
                assertThrows(
                        ApiException.class,
                        () -> collectionItemService.create(
                                99L,
                                request));

        assertEquals(
                "Collection not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(collectionItemRepository, never())
                .save(any(CollectionItem.class));
    }

    // ==================================================
    // GET ALL
    // ==================================================

    @Test
    void shouldGetAllCollectionItems() {

        CollectionItem item1 =
                CollectionItem.builder()
                        .id(100L)
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("/users")
                        .requestHeaders("{}")
                        .queryParams("{}")
                        .authData("{}")
                        .authType(AuthType.NONE)
                        .collection(collection)
                        .build();

        CollectionItem item2 =
                CollectionItem.builder()
                        .id(101L)
                        .name("Create User")
                        .method(HttpMethod.POST)
                        .url("/users")
                        .requestHeaders("{}")
                        .queryParams("{}")
                        .authData("{}")
                        .authType(AuthType.NONE)
                        .collection(collection)
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByCollection(collection))
                .thenReturn(List.of(item1, item2));

        when(jsonUtil.fromJson("{}"))
                .thenReturn(Map.of());

        List<CollectionItemResponse> response =
                collectionItemService.getAll(10L);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(
                100L,
                response.get(0).getId());

        assertEquals(
                "Get Users",
                response.get(0).getName());

        assertEquals(
                101L,
                response.get(1).getId());

        assertEquals(
                "Create User",
                response.get(1).getName());

        verify(collectionItemRepository)
                .findByCollection(collection);
    }

    // ==================================================
    // GET BY ID
    // ==================================================

    @Test
    void shouldGetCollectionItemById() {

        CollectionItem item =
                CollectionItem.builder()
                        .id(100L)
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("/users")
                        .requestHeaders("{}")
                        .queryParams("{}")
                        .authData("{}")
                        .authType(AuthType.NONE)
                        .collection(collection)
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByIdAndCollection(
                        100L,
                        collection))
                .thenReturn(Optional.of(item));

        when(jsonUtil.fromJson("{}"))
                .thenReturn(Map.of());

        CollectionItemResponse response =
                collectionItemService.getById(
                        10L,
                        100L);

        assertNotNull(response);
        assertEquals(
                100L,
                response.getId());

        assertEquals(
                "Get Users",
                response.getName());

        assertEquals(
                HttpMethod.GET,
                response.getMethod());

        assertEquals(
                "/users",
                response.getUrl());

        verify(collectionItemRepository)
                .findByIdAndCollection(
                        100L,
                        collection);
    }

    // ==================================================
    // GET BY ID - ITEM NOT FOUND
    // ==================================================

    @Test
    void shouldThrowExceptionWhenItemNotFound() {

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByIdAndCollection(
                        999L,
                        collection))
                .thenReturn(Optional.empty());

        ApiException exception =
                assertThrows(
                        ApiException.class,
                        () -> collectionItemService.getById(
                                10L,
                                999L));

        assertEquals(
                "Collection item not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());
    }

    // ==================================================
    // UPDATE
    // ==================================================

    @Test
    void shouldUpdateCollectionItem() {

        CollectionItem item =
                CollectionItem.builder()
                        .id(100L)
                        .name("Old Name")
                        .method(HttpMethod.GET)
                        .url("/old-url")
                        .requestHeaders("{}")
                        .queryParams("{}")
                        .authData("{}")
                        .authType(AuthType.NONE)
                        .collection(collection)
                        .build();

        CollectionItemRequest request =
                CollectionItemRequest.builder()
                        .name("Updated Name")
                        .method(HttpMethod.POST)
                        .url("/users")
                        .body("{\"name\":\"John\"}")
                        .headers(Map.of(
                                "Content-Type",
                                "application/json"))
                        .queryParams(Map.of())
                        .authType(AuthType.NONE)
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByIdAndCollection(
                        100L,
                        collection))
                .thenReturn(Optional.of(item));

        when(jsonUtil.toJson(any()))
                .thenReturn("{}");

        when(collectionItemRepository.save(item))
                .thenReturn(item);

        when(jsonUtil.fromJson("{}"))
                .thenReturn(Map.of());

        CollectionItemResponse response =
                collectionItemService.update(
                        10L,
                        100L,
                        request);

        assertNotNull(response);

        assertEquals(
                "Updated Name",
                response.getName());

        assertEquals(
                HttpMethod.POST,
                response.getMethod());

        assertEquals(
                "/users",
                response.getUrl());

        assertEquals(
                "{\"name\":\"John\"}",
                response.getBody());

        verify(collectionItemRepository)
                .save(item);
    }

    // ==================================================
    // DELETE
    // ==================================================

    @Test
    void shouldDeleteCollectionItem() {

        CollectionItem item =
                CollectionItem.builder()
                        .id(100L)
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("/users")
                        .collection(collection)
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByIdAndCollection(
                        100L,
                        collection))
                .thenReturn(Optional.of(item));

        collectionItemService.delete(
                10L,
                100L);

        verify(collectionItemRepository)
                .delete(item);
    }

    // ==================================================
    // DELETE - ITEM NOT FOUND
    // ==================================================

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingItem() {

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByIdAndCollection(
                        999L,
                        collection))
                .thenReturn(Optional.empty());

        ApiException exception =
                assertThrows(
                        ApiException.class,
                        () -> collectionItemService.delete(
                                10L,
                                999L));

        assertEquals(
                "Collection item not found",
                exception.getMessage());

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatus());

        verify(collectionItemRepository, never())
                .delete(any(CollectionItem.class));
    }

    // ==================================================
    // EXECUTE
    // ==================================================

    @Test
    void shouldExecuteCollectionItem() {

        CollectionItem item =
                CollectionItem.builder()
                        .id(100L)
                        .name("Get Users")
                        .method(HttpMethod.GET)
                        .url("https://example.com/users")
                        .requestHeaders("{}")
                        .queryParams("{}")
                        .requestBody(null)
                        .authType(AuthType.NONE)
                        .authData("{}")
                        .collection(collection)
                        .build();

        Environment environment =
                Environment.builder()
                        .id(50L)
                        .build();

        CollectionItemExecuteRequest executeRequest =
                CollectionItemExecuteRequest.builder()
                        .environmentId(50L)
                        .build();

        ApiExecuteResponse expectedResponse =
                ApiExecuteResponse.builder()
                        .statusCode(200)
                        .build();

        when(securityUtil.getCurrentUser())
                .thenReturn(user);

        when(collectionRepository.findByIdAndUser(
                10L,
                user))
                .thenReturn(Optional.of(collection));

        when(collectionItemRepository
                .findByIdAndCollection(
                        100L,
                        collection))
                .thenReturn(Optional.of(item));

        when(jsonUtil.fromJson("{}"))
                .thenReturn(Map.of());

        when(environmentService
                .getOwnedEnvironment(50L))
                .thenReturn(environment);

        when(apiExecutionService.execute(
                any(ApiExecuteRequest.class)))
                .thenReturn(expectedResponse);

        ApiExecuteResponse response =
                collectionItemService.execute(
                        10L,
                        100L,
                        executeRequest);

        assertNotNull(response);
        assertEquals(
                200,
                response.getStatusCode());

        verify(environmentService)
                .getOwnedEnvironment(50L);

        verify(apiExecutionService)
                .execute(any(ApiExecuteRequest.class));
    }
}