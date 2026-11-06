package in.malabartrails;
import jakarta.persistence.*;import java.util.UUID;
@Entity @Table(name="staff_users")
public class StaffUser {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @Column(nullable=false,unique=true) public String email;
 @Column(name="password_hash",nullable=false) public String passwordHash;
 @Column(nullable=false) public String role;
}
