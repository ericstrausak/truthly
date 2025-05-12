package ch.zhaw.truthly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.truthly.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}