package com.project.sns.dto.entity;

import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private UserRole userRole;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static UserDto of(Long id, String username, String password, UserRole userRole, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        return new UserDto(id, username, password, userRole, registeredAt, updatedAt, deletedAt);
    }

    public static UserDto from(UserEntity userEntity) {
        return UserDto.of(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getUserRole(),
                userEntity.getRegisteredAt(),
                userEntity.getUpdatedAt(),
                userEntity.getDeletedAt()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.deletedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.deletedAt == null;
    }

    @Override
    public boolean isEnabled() {
        return this.deletedAt == null;
    }
}
