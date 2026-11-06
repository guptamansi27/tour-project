package in.malabartrails;
import jakarta.persistence.*;import java.time.OffsetDateTime;import java.util.UUID;
@Entity @Table(name="enquiry_activity") public class EnquiryActivity { @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id; @Column(name="enquiry_id",nullable=false) public UUID enquiryId; @Column(name="actor_email") public String actorEmail; @Column(nullable=false) public String action; @Column(columnDefinition="text") public String detail; @Column(name="created_at",insertable=false,updatable=false) public OffsetDateTime createdAt; }
