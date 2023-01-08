package org.fatmansoft.teach.controllers;


import org.fatmansoft.teach.models.Selections;
import org.fatmansoft.teach.payload.request.ChooseRequest;
import org.fatmansoft.teach.payload.request.IdentifyRoleRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/home")
public class ChooseController {
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SelectionsRepository selectionsRepository;
    @Autowired
    private CourseInfoRepository courseInfoRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/choose")
    public String choose(@Valid @RequestBody ChooseRequest chooseRequest) {
        Integer personId = userRepository.getPersonIdByUsername(chooseRequest.getUsername());
        String perNum = personRepository.getPerNumByPerId(personId);
        Integer studentId = studentRepository.getStudentIdByPerNum(perNum);
        Integer courseId = Integer.parseInt(chooseRequest.getCourseid());
        //查看选课在时间上是否冲突
        List<Integer> ChosenCouIdList = selectionsRepository.getChosenCouIdList();
        Integer ChosenCouId;
        for(int i = 0;i<ChosenCouIdList.size();i++){
            ChosenCouId = ChosenCouIdList.get(i);
            Integer ChosenWeek = courseRepository.getWeekByCourseId(ChosenCouId);
            Integer ChosenIndex = courseRepository.getTimeByCourseId(ChosenCouId);
            Integer theWeek = courseRepository.getWeekByCourseId(courseId);
            Integer theIndex = courseRepository.getIndexByCourseId(courseId);
            if( (ChosenIndex == theIndex) && (ChosenWeek == theWeek) ){
                return "时间冲突";
            }
        }

        Selections selections = new Selections(selectionsRepository.getNextId(),studentId,courseId,1);
        selectionsRepository.save(selections);
        return "选课成功";
    }

    @PostMapping("/cancel")
    public String cancel(@Valid @RequestBody ChooseRequest chooseRequest){
        Integer courseId = Integer.parseInt(chooseRequest.getCourseid());
        Integer personId = userRepository.getPersonIdByUsername(chooseRequest.getUsername());
        String perNum = personRepository.getPerNumByPerId(personId);
        Integer studentId = studentRepository.getStudentIdByPerNum(perNum);
        Selections s = null;
        Integer id = selectionsRepository.getIdBySIdAndCouId(studentId,courseId);
        Optional<Selections> op;
        if(id != null) {
            op= selectionsRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            selectionsRepository.delete(s);    //数据库永久删除
        }
        return "刪除成功";
    }

    @PostMapping("/getCourse")//获得该用户所选的课程并返回相关信息
    public DataResponse getCourse(@Valid @RequestBody IdentifyRoleRequest identifyRoleRequest) {
        String username = identifyRoleRequest.getUsername();
        Integer personId = userRepository.getPersonIdByUsername(username);
        String perNum = personRepository.getPerNumByPerId(personId);
        Integer studentId = studentRepository.getStudentIdByPerNum(perNum);
        List dataList = new ArrayList();
        List<Integer> courseIdList = selectionsRepository.getChosenCourseId(studentId);
        Integer courseId;
        Map m;
        for(int i = 0; i< courseIdList.size();i++){
            courseId = courseIdList.get(i);
            m = new HashMap();
            String courseName = courseRepository.getCourseNameByCourseId(courseId);
            m.put("courseName",courseName);
            m.put("week",courseRepository.getWeekByCourseId(courseId));
            m.put("index",courseRepository.getIndexByCourseId(courseId));

            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getChosenList")
    public DataResponse getChosenList(@Valid @RequestBody IdentifyRoleRequest identifyRoleRequest){
        String username = identifyRoleRequest.getUsername();
        Integer personId = userRepository.getPersonIdByUsername(username);
        String perNum = personRepository.getPerNumByPerId(personId);
        Integer studentId = studentRepository.getStudentIdByPerNum(perNum);
        List dataList = new ArrayList();
        List<Integer> courseIdList = selectionsRepository.getChosenCourseId(studentId);
        Integer courseId;
        Map m;
        for(int i = 0; i< courseIdList.size();i++){
            courseId = courseIdList.get(i);
            m = new HashMap();
            Integer courseNum = courseRepository.getCourseNumByCourseId(courseId);
            m.put("courseNum",courseNum);
            m.put("selected",selectionsRepository.getSelectedByCourseId(courseId));

            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

}
