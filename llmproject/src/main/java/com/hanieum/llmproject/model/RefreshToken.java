package com.hanieum.llmproject.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public boolean isSame(String otherToken) {
		return token.equals(otherToken);
	}
}
