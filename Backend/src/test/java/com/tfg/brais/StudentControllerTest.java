package com.tfg.brais;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port = 8080;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testFindAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(createURL("/users/"), User[].class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Realiza las verificaciones adicionales que necesites
    }

    private String createURL(String uri) {
        return "http://localhost:" + port + uri;
    }
}