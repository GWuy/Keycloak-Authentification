package sp26.se194638.ojt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sp26.se194638.ojt.model.entity.User;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    @Query("SELECT email FROM User")
    List<String> emails();

    boolean existsByUsername(String username);

    boolean existsByPassword(String password);

  User findUserById(Integer userBanId);

  User findUserByEmail(String email);

  boolean existsByEmail(String email);
}
