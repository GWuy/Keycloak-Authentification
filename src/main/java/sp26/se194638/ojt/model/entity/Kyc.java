package sp26.se194638.ojt.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kyc")
public class Kyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "front_image", length = Integer.MAX_VALUE)
    private String frontImage;

    @Column(name = "back_image", length = Integer.MAX_VALUE)
    private String backImage;

    @Column(name = "status", length = 15)
    private String status;

    @Column(name = "reject_reason")
    private String rejectReason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "no")
    private String no;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "sex", length = 20)
    private String sex;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "place_of_origin", length = Integer.MAX_VALUE)
    private String placeOfOrigin;

    @Column(name = "date_of_expiry")
    private LocalDate dateOfExpiry;

    @Column(name = "date_issue")
    private LocalDate dateIssue;

    @Column(name = "place_of_issue", length = Integer.MAX_VALUE)
    private String placeOfIssue;

}
