package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;

public class UserPersistenceMapper {

    private UserPersistenceMapper() {}

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .username(entity.getUsername())
                .role(entity.getRole())
                .email(entity.getEmail())
                .membershipType(entity.getMembershipType())
                .registrationDate(entity.getRegistrationDate())
                .build();
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole())
                .email(user.getEmail())
                .membershipType(user.getMembershipType())
                .registrationDate(user.getRegistrationDate())
                .build();
    }
}
