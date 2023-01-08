package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Selections;
import org.fatmansoft.teach.payload.request.ChooseRequest;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class CourseController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SelectionsRepository selectionsRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    UserRepository userRepository;


    public String transformWeek(Integer week) {
        switch (week) {
            case 0:
                return "星期日";
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            default:
                return "错误";
        }
    }


    public String transformIndex(Integer index) {
        switch (index) {
            case 0:
                return "第一节";
            case 1:
                return "第二节";
            case 2:
                return "第三节";
            case 3:
                return "第四节";
            case 4:
                return "第五节";
            case 5:
                return "第六节";
            default:
                return "错误";
        }
    }

    public List getCourseMapList(String numName) {
        List dataList = new ArrayList();
        List<Course> sList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        Course s;
        Map m;
        String courseNameParas, attendenceParas, courseInfoParas;
        for (int i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("courseNum", s.getCourseNum());
            m.put("courseName", s.getCourseName());
            courseNameParas = "model=homework&courseId=" + s.getId() + "&courseName=" + s.getCourseName();
            m.put("courseNameParas", courseNameParas);
            m.put("credit", s.getCredit());
            m.put("precourse", s.getPreCourse());
            attendenceParas = "model=attendence&courseId=" + s.getId() + "&courseName=" + s.getCourseName();
            m.put("attendence", "出勤情况");
            m.put("attendenceParas", attendenceParas);
            courseInfoParas = "model=courseInfo&courseId=" + s.getId() + "&courseName=" + s.getCourseName();
            m.put("courseInfo", "课程详情");
            m.put("courseInfoParas", courseInfoParas);
            m.put("week", transformWeek(s.getWeek()));
            m.put("index", transformIndex(s.getTime()));
            dataList.add(m);
        }
        return dataList;
    }

    //course页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/courseInit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public DataResponse courseInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String courseName = dataRequest.getString("courseName");
        List dataList = getCourseMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //course页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/courseQuery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public DataResponse courseQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getCourseMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    //courseEdit初始化方法
    //courseEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/courseEditInit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public DataResponse courseEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course s = null;
        Optional<Course> op;
        if (id != null) {
            op = courseRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if (s != null) {
            form.put("id", s.getId());
            form.put("courseNum", s.getCourseNum());
            form.put("courseName", s.getCourseName());
            form.put("credit", s.getCredit());
            form.put("precourse", s.getPreCourse());
            form.put("index",s.getTime());
            form.put("week",s.getWeek());
        }
        Map data = new HashMap();
        data.put("form", form);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }

    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewCourseId() {
        Integer
                id = courseRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    @PostMapping("/courseEditSubmit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') ")
    public DataResponse courseEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        String courseNum = CommonMethod.getString(form, "courseNum");  //Map 获取属性的值
        String courseName = CommonMethod.getString(form, "courseName");
        Integer credit = CommonMethod.getInteger(form, "credit");
        String preCourse = CommonMethod.getString(form, "preCourse");
        Integer index = CommonMethod.getInteger(form, "index");
        Integer week = CommonMethod.getInteger(form, "week");
        Course s = null;
        Optional<Course> op;
        if (id != null) {
            op = courseRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s == null) {
            s = new Course();   //不存在 创建实体对象
            id = getNewCourseId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setCourseNum(courseNum);  //设置属性
        s.setCourseName(courseName);
        s.setCredit(credit);
        s.setPreCourse(preCourse);
        s.setTime(index);
        s.setWeek(week);
        courseRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  学生信息删除方法
    //course页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/courseDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') ")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Course s = null;
        Optional<Course> op;
        if (id != null) {
            op = courseRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            courseRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


}
