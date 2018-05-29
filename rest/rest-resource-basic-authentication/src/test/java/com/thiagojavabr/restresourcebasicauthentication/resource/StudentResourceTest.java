package com.thiagojavabr.restresourcebasicauthentication.resource;

import com.thiagojavabr.restresourcebasicauthentication.config.TestConstants;
import com.thiagojavabr.restresourcebasicauthentication.domain.Student;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentResourceTest {
    @Value("${local.server.port}")
    private String serverPort;
    private String baseUri;
    @Autowired
    private TestRestTemplate restTemplate;

    // Asserts
    private static final int STUDENTS_LIST_SIZE = 3;
    private static final Long STUDENT_ID_TO_FIND_ONE = 1L;
    private static final Long STUDENT_ID_TO_DELETE = 2L;
    private static final long STUDENT_ID_TO_UPDATE = 3L;

    @Before
    public void setup(){
        baseUri = String.format("http://localhost:%s/students", serverPort);

        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add(TestConstants.AUTHORIZATION_HEADER, TestConstants.API_AUTHENTICATION_BASIC);
                    return execution.execute(request, body);
                }
        ));

    }

    @Test
    public void testGet(){
        ResponseEntity<List> responseEntity = this.restTemplate.getForEntity(baseUri, List.class);
        List students = responseEntity.getBody();
        assertThat(students.size()).isEqualTo(STUDENTS_LIST_SIZE);
    }

    @Test
    public void testGetById(){
        ResponseEntity<Student> responseEntity = this.restTemplate.getForEntity(baseUri + "/{id}", Student.class, STUDENT_ID_TO_FIND_ONE);
        Student student = responseEntity.getBody();
        assertThat(student).isNotNull();
    }

    @Test
    public void testCreate(){
        Student student = new Student();
        student.setName("George");
        student.setAge(22);
        student.setCourse("Medicine");
        ResponseEntity<Student> responseEntity = this.restTemplate.postForEntity(baseUri, student, Student.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testUpdate(){
        String newName = "Carl";

        String requestBody = "{\"name\":\"" + newName + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(TestConstants.AUTHORIZATION_HEADER,TestConstants.API_AUTHENTICATION_BASIC);
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);
        ResponseEntity<Student> response = this.restTemplate.exchange(baseUri + "/{id}", HttpMethod.PUT, entity, Student.class, STUDENT_ID_TO_UPDATE);
        Student student = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(student.getId()).isEqualTo(STUDENT_ID_TO_UPDATE);
        assertThat(student.getName()).isEqualTo(newName);
    }

    @Test
    public void testDelete(){
        this.restTemplate.delete(baseUri + "/{id}", STUDENT_ID_TO_DELETE);
        ResponseEntity<Student> responseEntity = this.restTemplate.getForEntity(baseUri + "/{id}", Student.class, STUDENT_ID_TO_DELETE);
        Student student = responseEntity.getBody();
        assertThat(student).isNull();
    }
}
