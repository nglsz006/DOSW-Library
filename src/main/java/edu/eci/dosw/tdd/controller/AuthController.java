package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoginRequestDTO;
import edu.eci.dosw.tdd.controller.dto.LoginResponseDTO;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.dto.UserRegistrationDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            String token = jwtService.generateToken(loginRequest.getUsername(), role);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO registrationDTO) {
        User user = userService.registerUser(
                registrationDTO.getName(), registrationDTO.getUsername(), registrationDTO.getPassword(), "USER");
        return new ResponseEntity<>(UserMapper.toDTO(user), HttpStatus.CREATED);
    }
}
