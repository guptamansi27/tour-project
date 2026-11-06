package in.malabartrails;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.*;import java.util.UUID;
@Entity @Table(name="enquiries")
public class Enquiry {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @Column(name="idempotency_key",unique=true) public String idempotencyKey;
 @Column(name="package_id") public UUID packageId;
 @Column(name="full_name",nullable=false) public String fullName;
 @Column(nullable=false) public String email;
 @Column(nullable=false) public String phone;
 @Column(nullable=false) public String destination;
 @Column(name="travel_date",nullable=false) public LocalDate travelDate;
 @Column(nullable=false) public int adults;
 @Column(nullable=false) public int children;
 public BigDecimal budget;
 @Column(length=3000) public String message;
 @Column(name="whatsapp_consent",nullable=false) public boolean whatsappConsent;
 @Column(nullable=false) public String status="NEW";
 @Column(name="ai_status",nullable=false) public String aiStatus="UNAVAILABLE";
 @Column(name="ai_summary",columnDefinition="text") public String aiSummary;
 @Transient public String aiError;
 @Column(nullable=false) public String channel="WEBSITE";
 @Column(name="assigned_staff_id") public UUID assignedStaffId;
 @Column(name="created_at",insertable=false,updatable=false) public OffsetDateTime createdAt;
 @Column(name="updated_at",insertable=false,updatable=false) public OffsetDateTime updatedAt;
}
