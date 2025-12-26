package sp26.se194638.ojt.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blacklist")
public class Blacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reason", length = 100)
    private String reason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "blacklisted_at")
    private Instant blacklistedAt;

    @Column(name = "token", length = Integer.MAX_VALUE)
    private String token;


}