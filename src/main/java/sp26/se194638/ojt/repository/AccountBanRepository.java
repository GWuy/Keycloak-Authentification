package sp26.se194638.ojt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp26.se194638.ojt.model.entity.AccountBan;

@Repository
public interface AccountBanRepository extends JpaRepository<AccountBan, Integer> {
}
