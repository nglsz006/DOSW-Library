package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validators.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();


    public User registerUser(String name) {
        User user = new User(name, IdGeneratorUtil.getInstance().generateUserId());
        UserValidator.validate(user);
        users.add(user);
        return user;
    }

    public List<User> getAllUsers() {
        return users.stream()
                .collect(Collectors.toList());
    }

    public Optional<User> getUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    public User findUserById(int id) throws UserNotFoundException {
        return getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con ID: " + id));
    }

    public void clear() {
        users.clear();
    }
}
