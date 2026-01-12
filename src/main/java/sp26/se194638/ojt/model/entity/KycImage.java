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
@Table(name = "kyc_image")
public class KycImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "kyc_id", nullable = false)
    private Kyc kyc;

    @Column(name = "image_type", nullable = false, length = 10)
    private String imageType;

    @Column(name = "image_data", nullable = false, length = Integer.MAX_VALUE)
    private String imageData;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "uploaded_at")
    private OffsetDateTime uploadedAt;

}
