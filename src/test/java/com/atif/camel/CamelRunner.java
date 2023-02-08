package com.atif.camel;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.web.client.RestTemplate;
import sample.camel.entity.Employee;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {sample.camel.RestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CamelRunner {

    @Autowired
    private RestTemplate restTemplate;
    static Long id;

    //TEST CASE FOR RETRIEVING ALL EMPLOYEES
    @Test
    @Order(5)
    public void findAll() {
        ResponseEntity<List<Employee>> response = restTemplate.exchange("http://localhost:8083/camel/api/findall",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {
                });
        assertThat(response);
    }

    // TEST CASE FOR FIND BY ID

    @Test
    @Order(2)
    public void findbyid() {
        String str = Long.toString(id);
        ResponseEntity<Employee> response = restTemplate.getForEntity("http://localhost:8083/camel/api/find/"+str,
                Employee.class);
        assertTrue(response.getBody().getName().equals("Aarzoo"));

    }

    //TEST CASE FOR CREATING EMPLOYEE

    @Test
    @Order(1)
    public void createEmployee(){
        Employee emp = new Employee();
        emp.setName("Aarzoo");
        emp.setEmail_id("arzoo@gmail.com");
        emp.setJob_pos("Assistant professor");
        emp.setL_name("Malik");
        emp.setMob_num("5656565");
         String response = restTemplate.postForObject("http://localhost:8083/camel/api/create",emp,String.class);
        int p = response.indexOf(':');
        int q = response.lastIndexOf('"');
        String c = response.substring(p + 2, q);
         Long i = Long.parseLong(c);
        System.out.println("Employee Created");
        id = i;
        System.out.println(id);
    }

    @Test
    @Order(3)
    public void updateEmployee(){
        Employee emp = new Employee();
        emp.setJob_pos("ase");
        String str = Long.toString(id);
        String response = restTemplate.postForObject("http://localhost:8083/camel/api/update/"+str,emp,String.class);
        System.out.println(response);
    }

    @Test
    @Order(4)
    public void deleteEmployee(){
        String str = Long.toString(id);
        String response = restTemplate.postForObject("http://localhost:8083/camel/api/delete/"+str,null,String.class);
        System.out.println(response);
    }
}