package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Selections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionsRepository extends JpaRepository<Selections, Integer> {

    @Query(value = "select max(id) from Person ")
    Integer getMaxId();

    @Query(value = "select COALESCE(max(id), 1) + 1 as next_id from selections", nativeQuery = true )
    Integer getNextId();
    @Query(value = "SELECT course_id FROM selections WHERE student_id = ?1", nativeQuery = true )
    List<Integer> getChosenCourseId(Integer studentId);

    @Query(value = "SELECT selected from selections where student_id =?1", nativeQuery = true )
    List<Integer> getSelectedByStudentId(Integer StudentId);

    @Query(value = "SELECT selected from selections where course_id =?1", nativeQuery = true )
    Integer getSelectedByCourseId(Integer CourseId);
    @Query(value = "select id from selections s where s.student_id=:sid and s.course_id=:cid",nativeQuery = true)
    Integer getIdBySIdAndCouId(@Param("sid") Integer sid, @Param("cid") Integer cid);

    @Query(value = "SELECT course_id from selections", nativeQuery = true)
    List<Integer> getChosenCouIdList();
}
