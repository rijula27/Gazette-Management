package spring.aop.gazettemanagementnic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.aop.gazettemanagementnic.entity.LoginLog;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {


}
