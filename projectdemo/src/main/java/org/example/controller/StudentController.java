package org.example.controller;


import jakarta.websocket.server.PathParam;
import org.example.records.Student;
import org.example.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService service;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @GetMapping("/{id}")
    public ResponseEntity<Student>getStudent(@PathVariable String id){
       Student st = new Student("id-001","sekhar",3.0);
       logger.info("student response--{}",st);
        logger.debug("student response for sfdfd developer--{}",st);
        logger.info("jfdlskj->{}",service.getCourses());
       return ResponseEntity.ok(st);
    }
}



//🔹 Improvements
//
//Access Modifier → In Spring controllers, methods should usually be public (not private), otherwise proxies can’t always intercept them properly.
//
//Use the incoming id → Right now you’re hardcoding "id-001". Better to use the parameter.
//
//        Meaningful return values → For a missing student, return 404 Not Found.
//
//Constructor-based immutability → Since you’re using record, you’re already good (no setters).
//
//Optional logging → Helps in debugging.
