package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaLoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private UserService userService;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private JpaLoanRepository loanRepository;

    private String librarianToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        userService.registerUser("Librarian", "librarian_uc", "libpass2", "LIBRARIAN");
        librarianToken = loginAndGetToken("librarian_uc", "libpass2");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void shouldReturn400WhenRegisterUserWithShortPassword() throws Exception {
        String body = "{\"name\":\"Test\",\"username\":\"testuser\",\"password\":\"abc\",\"role\":\"USER\"}";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + librarianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
