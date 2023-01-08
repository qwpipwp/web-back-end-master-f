package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Integer> {
    Optional<Student> findByStudentNum(String studentNum);
    List<Student> findByStudentName(String studentName);

    @Query(value = "select max(id) from Student  ")
    Integer getMaxId();

    @Query(value = "select COALESCE(max(person_id), 1) + 1 as next_id from person", nativeQuery = true )
    Integer getNextId();

    @Query(value = "from Student where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Student> findStudentListByNumName(String numName);

    @Query(value = "select * from student  where ?1='' or student_num like %?1% or student_name like %?1% ", nativeQuery = true)
    List<Student> findStudentListByNumNameNative(String numName);

    @Query(value = "select id from student where student_num =?1", nativeQuery = true)
    Integer getStudentIdByPerNum(String PerNum);

    @Query(value = "select student_name from student where id =?1", nativeQuery = true)
    String  getStudentNameByStudentId(Integer studentId);

}
