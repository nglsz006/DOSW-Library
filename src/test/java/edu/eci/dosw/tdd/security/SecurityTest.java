package edu.eci.dosw.tdd.security;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityTest {

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        userService.registerUser("Librarian", "librarian", "libpass", "LIBRARIAN");
        userService.registerUser("Regular User", "user1", "userpass", "USER");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    // ===================== ACCESO SIN TOKEN =====================

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401ForProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    // ===================== TOKEN INVÁLIDO =====================

    @Test
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer este.token.esinvalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenTokenIsMalformed() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer malformed"))
                .andExpect(status().isUnauthorized());
    }

    // ===================== USER vs LIBRARIAN =====================

    @Test
    void shouldReturn403WhenUserTriesLibrarianOperation() throws Exception {
        String userToken = loginAndGetToken("user1", "userpass");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Libro\",\"author\":\"Autor\",\"totalCopies\":1,\"availableCopies\":0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenUserTriesToListAllUsers() throws Exception {
        String userToken = loginAndGetToken("user1", "userpass");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn201WhenLibrarianCreatesBook() throws Exception {
        String librarianToken = loginAndGetToken("librarian", "libpass");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + librarianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"totalCopies\":3,\"availableCopies\":0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.totalCopies").value(3));
    }

    @Test
    void shouldReturn200WhenLibrarianListsUsers() throws Exception {
        String librarianToken = loginAndGetToken("librarian", "libpass");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200WhenAuthenticatedUserListsBooks() throws Exception {
        String userToken = loginAndGetToken("user1", "userpass");

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    // ===================== LOGIN =====================

    @Test
    void shouldReturnTokenOnSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1\",\"password\":\"userpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturn401OnLoginWithWrongPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401OnLoginWithNonExistentUser() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"noexiste\",\"password\":\"pass\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ===================== ENDPOINTS PÚBLICOS =====================

    @Test
    void shouldAllowRegisterWithoutToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nuevo\",\"username\":\"nuevo\",\"password\":\"pass123\"}"))
                .andExpect(status().isCreated());
    }
}
