package com.dot.repository;

import com.dot.entity.Retrospect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {

    Optional<Retrospect> findByUserIdAndDate(Long userId, LocalDate date);

    // 월별 캘린더용 (점수 + 날짜만)
    @Query("SELECT r FROM Retrospect r WHERE r.user.id = :userId " +
           "AND r.date >= :startDate AND r.date <= :endDate " +
           "ORDER BY r.date ASC")
    List<Retrospect> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Retrospect> findByUserIdOrderByDateDesc(Long userId);
}
