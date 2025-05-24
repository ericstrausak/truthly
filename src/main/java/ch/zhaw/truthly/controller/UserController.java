package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.zhaw.truthly.model.User;
import ch.zhaw.truthly.model.UserCreateDTO;
import ch.zhaw.truthly.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;
    
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody UserCreateDTO userDTO) {
        try {
            User user = new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getRole()
            );
            User savedUser = userRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            Optional<User> userData = userRepository.findById(id);
            
            if (userData.isPresent()) {
                return new ResponseEntity<>(userData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/user/search")
public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String username) {
    try {
        List<User> users;
        
        if (username != null && !username.isEmpty()) {
            // Assuming you have a method like this in UserRepository
            users = userRepository.findByUsernameContainingIgnoreCase(username);
        } else {
            users = userRepository.findAll();
        }
        
        return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}