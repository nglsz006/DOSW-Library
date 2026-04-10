package edu.eci.dosw.tdd.config;

import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void adminNoExiste_debeCrearUsuarioLibrarian() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-password");

        dataInitializer.run();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals("LIBRARIAN", captor.getValue().getRole());
        assertEquals("admin", captor.getValue().getUsername());
    }

    @Test
    void adminYaExiste_noDebeCrearUsuario() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        dataInitializer.run();

        verify(userRepository, never()).save(any());
    }

    @Test
    void adminNoExiste_passwordDebeEstarEncodeado() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("bcrypt-hash");

        dataInitializer.run();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals("bcrypt-hash", captor.getValue().getPassword());
    }
}
