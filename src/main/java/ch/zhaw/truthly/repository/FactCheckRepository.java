package ch.zhaw.truthly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.truthly.model.FactCheck;

public interface FactCheckRepository extends MongoRepository<FactCheck, String> {
}