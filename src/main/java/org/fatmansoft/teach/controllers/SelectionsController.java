package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.SelectionsRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/selections")

public class SelectionsController {
    @Autowired
    private SelectionsRepository selectionsRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;//添加依赖

}
