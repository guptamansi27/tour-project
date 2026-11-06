package in.malabartrails;
import jakarta.persistence.*;import java.time.OffsetDateTime;import java.util.UUID;
@Entity @Table(name="enquiry_notes") public class EnquiryNote { @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id; @Column(name="enquiry_id",nullable=false) public UUID enquiryId; @Column(name="author_email",nullable=false) public String authorEmail; @Column(nullable=false,columnDefinition="text") public String body; @Column(name="created_at",insertable=false,updatable=false) public OffsetDateTime createdAt; }
