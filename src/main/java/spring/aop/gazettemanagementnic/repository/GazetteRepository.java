package spring.aop.gazettemanagementnic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.aop.gazettemanagementnic.entity.Gazette;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GazetteRepository extends JpaRepository<Gazette, Long> {

    Gazette findByFileName(String fileName);

    Gazette findByFileNameAndDate(String fileName, LocalDate date);

    Gazette findByDateAndPart(LocalDate currentDate, String part);

    List<Gazette> findAllByGcUser_Username(String username);

    List<Gazette> findAllByGcUser_UsernameAndStatus_State(String username, String state);

    List<Gazette> findAllByStatus_State(String state);

    Optional<Gazette> findById(Long id);

    // @Query("SELECT DISTINCT YEAR(g.date) FROM Gazette g ORDER BY YEAR(g.date)
    // DESC")
    // List<Integer> findDistinctYears();

    @Query("SELECT DISTINCT YEAR(g.date) FROM Gazette g WHERE g.status.state = 'Published' ORDER BY YEAR(g.date) DESC")
    List<Integer> findDistinctYears();

    // @Query("SELECT DISTINCT MONTH(g.date) FROM Gazette g WHERE YEAR(g.date) =
    // :year ORDER BY MONTH(g.date)")
    // List<Integer> findDistinctMonthsByYear(@Param("year") int year);

    @Query("SELECT DISTINCT MONTH(g.date) FROM Gazette g WHERE YEAR(g.date) = :year AND g.status.state = 'Published' ORDER BY MONTH(g.date)")
    List<Integer> findDistinctMonthsByYear(@Param("year") int year);

    @Query("SELECT DISTINCT DAY(g.date) FROM Gazette g WHERE YEAR(g.date) = :year AND MONTH(g.date) = :month AND g.status.state = 'Published' ORDER BY DAY(g.date)")
    List<Integer> findDistinctDaysByYearAndMonth(@Param("year") int year, @Param("month") int month);

    List<Gazette> findByDateAndStatus_State(LocalDate date, String string);
}
