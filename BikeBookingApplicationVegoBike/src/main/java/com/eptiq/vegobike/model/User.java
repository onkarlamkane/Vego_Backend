package com.eptiq.vegobike.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements Serializable, UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
//	private int id;

	private String address;

	@Column(name = "alternate_number")
	private String alternateNumber;

	@Column(name = "contact_number")
	private String phoneNumber;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "deleted_at")
	private Timestamp deletedAt;

	@Lob
	@Column(name = "device_id")
	private String deviceId;

	@Lob
	@Column(name = "document_image")
	private String documentImage;

	private String email;

	@Column(name = "email_verified_at")
	private Timestamp emailVerifiedAt;

	@Lob
	@Column(name = "firebase_token" , columnDefinition = "TEXT")
	private String firebaseToken;

	@Builder.Default
	@Column(name = "is_active")
	private Integer isActive = 1;

//	private int isActive = 1;

	@Column(name = "is_document_verified")
	private Integer isDocumentVerified;

//	private int isDocumentVerified;

	@Lob
	private String latitude;

	@Lob
	private String longitude;

	private String name;

	private String otp;

	//New 27/9/25
	@JsonIgnore
	private String password;

	private String profile;

	@Column(name = "remember_token")
	private String rememberToken;

	@Column(name = "role_id")
	private Integer roleId;

//	private int roleId;

	@Column(name = "store_id")
	private Long storeId;

//	private int storeId;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "account_number", nullable = true)
	private String accountNumber;

	@Column(name = "ifsc", nullable = true)
	private String ifsc;

	@Column(name = "upi_id", nullable = true)
	private String upiId;

	// UserDetails methods

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String roleName;
		switch (roleId) {
			case 1:
				roleName = "ADMIN";
				break;
			case 2:
				roleName = "STORE_MANAGER";
				break;
			case 3:
			default:
				roleName = "USER";
				break;
		}
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
	}


	@Override
	public String getUsername() {
		return this.email != null ? this.email : this.phoneNumber;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.isActive == 1;
	}
}