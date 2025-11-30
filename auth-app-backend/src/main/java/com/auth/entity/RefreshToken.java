package com.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 512)
	private String token;

	@Column(nullable = false)
	private Instant expiry;

	@Column(nullable = false)
	private String userEmail;

	@Builder.Default
	@Column(nullable = false)
	private boolean revoked = false;

	@Builder.Default
	@Column(nullable = false)
	private boolean expired = false;

	private Instant createdAt;
	private Instant updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = Instant.now();
		updatedAt = Instant.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = Instant.now();
	}
}
