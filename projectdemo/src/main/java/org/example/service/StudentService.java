package org.example.service;

import org.example.records.Course;
import org.example.records.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final RestTemplate restTemplate;

    public StudentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Course> getCourses() {
        ResponseEntity<List<Course>> response =
                restTemplate.exchange(
                        "http://localhost:/course?page=0&pageSize=10",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Course>>() {}
                );


        return response.getBody();

    }
}

