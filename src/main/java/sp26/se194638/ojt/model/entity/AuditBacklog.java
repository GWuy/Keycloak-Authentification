package sp26.se194638.ojt.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_backlog")
public class AuditBacklog {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "status", nullable = false)
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "actor")
    private User actor;

    @Column(name = "error_reason")
    private String errorReason;



}
