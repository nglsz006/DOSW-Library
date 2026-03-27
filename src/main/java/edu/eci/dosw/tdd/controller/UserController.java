package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.controller.dto.UserRegistrationDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        User user = userService.registerUser(
                registrationDTO.getName(), registrationDTO.getUsername(), registrationDTO.getPassword(), "USER");
        return new ResponseEntity<>(UserMapper.toDTO(user), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) throws UserNotFoundException {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }
}
