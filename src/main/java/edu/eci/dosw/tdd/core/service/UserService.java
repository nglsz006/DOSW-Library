package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validators.UserValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String name, String username, String password, String role) {
        User user = new User();
        user.setName(name);
        UserValidator.validate(user);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El username '" + username + "' ya está en uso");
        }

        UserEntity entity = UserEntity.builder()
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        UserEntity saved = userRepository.save(entity);
        return UserPersistenceMapper.toDomain(saved);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    public User findUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .map(UserPersistenceMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con ID: " + id));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserPersistenceMapper::toDomain);
    }
}
