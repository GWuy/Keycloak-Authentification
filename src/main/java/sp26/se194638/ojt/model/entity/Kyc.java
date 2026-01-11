package sp26.se194638.ojt.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "kyc")
public class Kyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "id_number", nullable = false, length = 30)
    private String idNumber;

    @Column(name = "front_image", nullable = false, length = Integer.MAX_VALUE)
    private String frontImage;

    @Column(name = "back_image", length = Integer.MAX_VALUE)
    private String backImage;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    @Column(name = "reject_reason")
    private String rejectReason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;


}
