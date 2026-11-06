package in.malabartrails;
import com.fasterxml.jackson.annotation.JsonIgnore;import jakarta.persistence.*;import java.util.UUID;
@Entity @Table(name="itinerary_days") public class ItineraryDay {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="package_id",nullable=false) @JsonIgnore public PackageEntity travelPackage;
 @Column(name="day_number",nullable=false) public int dayNumber;
 @Column(nullable=false) public String title;
 @Column(nullable=false,columnDefinition="text") public String description;
}
