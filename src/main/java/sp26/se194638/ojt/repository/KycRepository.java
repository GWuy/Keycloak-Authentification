package sp26.se194638.ojt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp26.se194638.ojt.model.dto.response.ListKycResponse;
import sp26.se194638.ojt.model.entity.Kyc;
import sp26.se194638.ojt.model.entity.User;

@Repository
public interface KycRepository extends JpaRepository<Kyc, Integer> {

  Kyc findKycById(Integer id);

  ListKycResponse findKycByUser(User user);

  boolean existsByNo(String no);
}
