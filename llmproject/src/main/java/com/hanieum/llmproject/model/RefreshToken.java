package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESHTOKEN_ID")
    private Long id;
    private String token;
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    private Date expirationDate;

    public static RefreshToken buildRefreshToken(String token, User user, Date expirationDate) {
        return RefreshToken.builder()
                .token(token)
                .user(user)
                .expirationDate(expirationDate)
                .build();
    }

    public boolean isExpiredToken() {
        if (expirationDate.before(new Date())) {
            return true;
        }

        return false;
    }
}
