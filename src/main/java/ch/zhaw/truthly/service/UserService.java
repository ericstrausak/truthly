package ch.zhaw.truthly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.zhaw.truthly.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    
    public boolean userExists(String userId) {
        return userRepository.existsById(userId);
    }
}