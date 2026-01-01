package sp26.se194638.ojt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sp26.se194638.ojt.model.entity.RedisToken;


@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
