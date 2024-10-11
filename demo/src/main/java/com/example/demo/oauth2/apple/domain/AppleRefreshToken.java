package com.example.demo.oauth2.apple.domain;

import com.example.demo.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AppleRefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apple_refresh_token_id")
    private Long id;

    private Long userId;

    private String memberRole;

    private String refreshToken;

}
