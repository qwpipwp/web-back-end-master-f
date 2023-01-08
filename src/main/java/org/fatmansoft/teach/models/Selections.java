package org.fatmansoft.teach.models;

import org.springframework.data.relational.core.sql.In;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "selections",
        uniqueConstraints = {
        })
public class Selections {
        @Id
        private Integer id;

//        @ManyToOne(targetEntity = Student.class)
//        @JoinColumn(name ="student_id")
//        private Student student;//与学生类建立多对一关系
//
//        @ManyToOne(targetEntity = Course.class)
//        @JoinColumn(name ="course_id")
//        private Course course;//与课程类建立多对一关系

        @Column(name = "student_id")
        private Integer studentId;

        public Selections(Integer id, Integer studentId, Integer courseId, Integer selected) {
                this.id = id;
                this.studentId = studentId;
                this.courseId = courseId;
        }
        public Selections() {}


        @Column(name = "course_id")
        private Integer courseId;
        private Integer selected;

        public Integer getSelected() {
                return selected;
        }

        public void setSelected(Integer selected) {
                this.selected = selected;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Integer getStudentId() {
                return studentId;
        }

        public void setStudentId(Integer studentId) {
                this.studentId = studentId;
        }

        public Integer getCourseId() {
                return courseId;
        }

        public void setCourseId(Integer courseId) {
                this.courseId = courseId;
        }





}
