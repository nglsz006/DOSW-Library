package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;

public class UserPersistenceMapper {

    private UserPersistenceMapper() {}

    public static User toDomain(UserEntity entity) {
        return new User(entity.getName(), entity.getId().intValue());
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id((long) user.getId())
                .name(user.getName())
                .build();
    }
}
