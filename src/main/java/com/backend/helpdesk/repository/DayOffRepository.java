package com.backend.helpdesk.repository;

import com.backend.helpdesk.entity.DayOff;
import com.backend.helpdesk.entity.Status;
import com.backend.helpdesk.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DayOffRepository extends JpaRepository<DayOff,Integer> {

    Optional<DayOff> findById(int id);

    List<DayOff> findByStatus(Status status);

    List<DayOff> findByUserEntityAndStatus(UserEntity userEntity,Status status);

    List<DayOff> findByUserEntity(UserEntity userEntity);

    @Query(value = "SELECT * FROM day_offs where EXTRACT(YEAR FROM day_start_off) = ?1 and id_user=?2",nativeQuery = true)
    List<DayOff> getDayOffByYear(int year,int idUser);

    @Query(value = "SELECT * FROM day_offs where description like CONCAT('%', :content, '%') or day_start_off::::text like CONCAT('%', :content, '%') or day_end_off::::text like CONCAT('%', :content, '%')",nativeQuery = true)
    List<DayOff> searchDayOff(@Param("content") String content,Pageable pageable);

}
