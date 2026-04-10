package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaLoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private JpaLoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn400WhenRegisterWithShortPassword() throws Exception {
        String body = "{\"name\":\"Test\",\"username\":\"testuser\",\"password\":\"abc\"}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void shouldReturn400WhenLoginWithBlankUsername() throws Exception {
        String body = "{\"username\":\"\",\"password\":\"somepass\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
    }
}
