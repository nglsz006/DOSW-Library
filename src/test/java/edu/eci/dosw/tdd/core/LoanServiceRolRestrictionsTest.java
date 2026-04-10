package edu.eci.dosw.tdd.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class LoanServiceRolRestrictionsTest {

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

    private String userToken;
    private Long userId;
    private Long otherUserId;
    private String librarianToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User user = userService.registerUser("User One", "user_rol_1", "pass123", "USER");
        userId = user.getId();
        userService.registerUser("User Two", "user_rol_2", "pass123", "USER");
        otherUserId = userRepository.findByUsername("user_rol_2").orElseThrow().getId();
        userService.registerUser("Librarian Rol", "librarian_rol", "libpass1", "LIBRARIAN");

        userToken = loginAndGetToken("user_rol_1", "pass123");
        librarianToken = loginAndGetToken("librarian_rol", "libpass1");
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
    void userAccedeSusPropiosPrestamos_debeRetornar200() throws Exception {
        mockMvc.perform(get("/api/loans/user/" + userId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void userAccedePrestamoDeOtro_debeRetornar403() throws Exception {
        mockMvc.perform(get("/api/loans/user/" + otherUserId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void librarianAccedeCualquierPrestamo_debeRetornar200() throws Exception {
        mockMvc.perform(get("/api/loans")
                        .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isOk());
    }

    @Test
    void userIntentaActualizarStock_debeRetornar403() throws Exception {
        mockMvc.perform(put("/api/books/1/stock")
                        .param("totalCopies", "5")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
