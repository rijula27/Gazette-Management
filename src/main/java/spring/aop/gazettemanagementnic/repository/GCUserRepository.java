package spring.aop.gazettemanagementnic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.aop.gazettemanagementnic.entity.GCUser;
import java.util.List;
import java.util.Optional;


@Repository
public interface GCUserRepository extends JpaRepository<GCUser, Long> {
    


    Optional<GCUser> findByUsername(String username);




    List<GCUser> findByRole(String role);

}
