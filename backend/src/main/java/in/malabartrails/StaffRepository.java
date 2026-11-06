package in.malabartrails;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface StaffRepository extends JpaRepository<StaffUser,UUID>{Optional<StaffUser> findByEmailIgnoreCase(String email);}
