package org.course.controller;

import org.course.record.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CoursesController {

    private static final Logger logger = LoggerFactory.getLogger(CoursesController.class);

    @GetMapping
    public ResponseEntity<List<Course>> getCourses(@RequestParam("page") Integer pageNum,@RequestParam("pageSize") Integer pageSize){
        Course course1 = new Course("001","MCA",10000.0,"AU");
        Course course2 = new Course("001","MCA",10000.0,"AU");
        List<Course> couseList = new ArrayList<>();
        couseList.add(course1);
        couseList.add(course2);
        logger.info("course List--->{}",couseList);
        return ResponseEntity.ok(couseList);
    }
}
