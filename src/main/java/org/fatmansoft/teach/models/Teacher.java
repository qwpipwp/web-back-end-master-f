package org.fatmansoft.teach.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "teacher",
        uniqueConstraints = {
        })
public class Teacher {

    public Teacher(Integer id, String teacherNum, String teacherName) {
        this.id = id;
        this.teacherNum = teacherNum;
        this.teacherName = teacherName;
    }

    public Teacher(){}

    @Id
    private Integer id;

    @NotBlank
    @Size(max = 20)
    private String teacherNum;
    @Size(max = 50)
    private String teacherName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeacherNum() {
        return teacherNum;
    }

    public void setTeacherNum(String teacherNum) {
        this.teacherNum = teacherNum;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
