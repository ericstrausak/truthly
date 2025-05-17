package ch.zhaw.truthly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.truthly.model.User;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByUsernameContainingIgnoreCase(String username);
}