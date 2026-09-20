package com.apitester.api_tester_backend.entity;

import com.apitester.api_tester_backend.entity.enums.AuthType;
import com.apitester.api_tester_backend.entity.enums.HttpMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "collection_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod method;

    @Column(length = 2000, nullable = false)
    private String url;

    @Lob
    private String requestBody;

    @Lob
    private String requestHeaders;

    @Lob
    private String queryParams;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Lob
    private String authData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;


    // authData
    // 1 {"bearerToken": "{{token}}"}
    // 2 {
    //     "username": "{{username}}",
    //     "password": "{{password}}"
    //   }
    // 3 {
    //     "apiKeyName": "X-API-Key",
    //     "apiKey": "{{apiKey}}"
    //   }    
}