package edu.eci.dosw.tdd.config;

import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            UserEntity adminEntity = UserEntity.builder()
                    .name("Admin")
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("LIBRARIAN")
                    .build();
            userRepository.save(adminEntity);
            log.info("Admin LIBRARIAN creado exitosamente");
        } else {
            log.info("Admin ya existe, se omite creación");
        }
    }
}
