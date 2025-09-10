package lk.ijse.gdse72.backend.repository;

//import lk.ijse.gdse72.o13_spring_security_with_jwt.entity.User;
import lk.ijse.gdse72.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}