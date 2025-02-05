package com.mandamong.api.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "profile_image", nullable = false)
    private String profileImage;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "oauth_provider_uid", nullable = false)
    private String oauthProviderUid;
}
