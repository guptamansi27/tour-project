package in.malabartrails;
import com.fasterxml.jackson.annotation.JsonIgnore;import jakarta.persistence.*;import java.math.BigDecimal;import java.time.LocalDate;import java.util.UUID;
@Entity @Table(name="seasonal_prices") public class SeasonalPrice {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="package_id",nullable=false) @JsonIgnore public PackageEntity travelPackage;
 @Column(nullable=false) public String label;
 @Column(name="starts_on",nullable=false) public LocalDate startsOn;
 @Column(name="ends_on",nullable=false) public LocalDate endsOn;
 @Column(nullable=false) public BigDecimal price;
}
