package sp26.se194638.ojt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp26.se194638.ojt.model.entity.Blacklist;
import sp26.se194638.ojt.model.entity.User;


@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Integer> {
    boolean existsByUser(User userLogin);
}
