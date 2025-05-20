package spring.aop.gazettemanagementnic.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.Tender;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {


    List<Tender> findAllByGcUser_UsernameAndStatus_State(String username, String state);

    Optional<Tender> findById(Long id);


    List<Tender> findAllByStatus_State(String state);


    List<Tender> findAllByGcUser_Username(String username);

        boolean existsByTitleOrRefNo(String title, String ref_No);



    // @Query("SELECT t FROM Tender t WHERE t.opening_Date >= :cutoffDate")
    // List<Tender> findTendersOpenedAfter(@Param("cutoffDate") LocalDate cutoffDate);  


    @Query("SELECT DISTINCT YEAR(t.announcement_Date) FROM Tender t WHERE t.status.state = 'Published' ORDER BY YEAR(t.announcement_Date) DESC")
        List<Integer> findDistinctYears();


    @Query("SELECT DISTINCT MONTH(t.announcement_Date) FROM Tender t WHERE YEAR(t.announcement_Date) = :year AND t.status.state = 'Published' ORDER BY MONTH(t.announcement_Date)")
    List<Integer> findDistinctMonthsByYear(@Param("year") int year);

    @Query(value = "SELECT * FROM tender WHERE EXTRACT(YEAR FROM announcement_Date) = :year AND EXTRACT(MONTH FROM announcement_Date) = :month", nativeQuery = true)
    List<Tender> findByYearAndMonthAndStatus_State(@Param("year") int year, @Param("month") int month, String string);


    @Query("SELECT t FROM Tender t WHERE t.announcement_Date <= :today AND (t.opening_Date >= :today OR t.opening_Date >= :oneMonthAgo)")
    List<Tender> findActiveTenders(@Param("today") LocalDate today, @Param("oneMonthAgo") LocalDate oneMonthAgo);


}