package in.malabartrails;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.OffsetDateTime;import java.util.UUID;
@Entity @Table(name="travel_packages")
public class PackageEntity {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @Column(nullable=false,unique=true) public String slug;
 @Column(nullable=false) public String title;
 @Column(nullable=false) public String destination;
 public String region;
 @Column(length=800) public String summary;
 @Column(columnDefinition="text") public String description;
 @Column(name="cover_image_url",columnDefinition="text") public String coverImageUrl;
 @Column(name="duration_nights",nullable=false) public int durationNights;
 @Column(name="hotel_category") public String hotelCategory;
 @Column(name="starting_price",nullable=false,precision=12,scale=2) public BigDecimal startingPrice;
 @Column(columnDefinition="text") public String inclusions;
 @Column(columnDefinition="text") public String exclusions;
 @Column(nullable=false) public String status="DRAFT";
 @Column(nullable=false) public int version=1;
 @Column(name="publish_at") public OffsetDateTime publishAt;
 @Column(name="published_at") public OffsetDateTime publishedAt;
 @Column(name="latitude") public Double latitude;
 @Column(name="longitude") public Double longitude;
 @Column(name="map_embed_url",columnDefinition="text") public String mapEmbedUrl;
 @Column(name="hotel_details",columnDefinition="text") public String hotelDetails;
 @Column(name="availability_message",columnDefinition="text") public String availabilityMessage;
 @Column(name="created_at",insertable=false,updatable=false) public OffsetDateTime createdAt;
 @Column(name="updated_at",insertable=false,updatable=false) public OffsetDateTime updatedAt;
}
