package com.tfg.brais;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.tfg.brais.Model.User;
import com.tfg.brais.security.jwt.LoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTest {

    @LocalServerPort
    private int port = 8080;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRegisterOneUser() {
        // Register normal user
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "Cabo", "braiscabofelpete@gmail.com", "12345678");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Obtener el cuerpo de la solicitud enviada
        User createdUser = response.getBody();

        // Comparar campos específicos con los valores esperados
        assertEquals("Brais", createdUser.getName());
        assertEquals("Cabo", createdUser.getLastName());
        assertEquals("braiscabofelpete@gmail.com", createdUser.getEmail());
        assertEquals(null, createdUser.getEncodedPassword());

        String locationHeader = response.getHeaders().getFirst("Location");

        // Define el patrón de expresión regular para comparar con la ubicación de la
        // cabecera
        String pattern = "http://localhost:\\d+/api/users/\\d+";

        // Verifica si la ubicación de la cabecera coincide con el patrón
        boolean matches = locationHeader.matches(pattern);

        // Realiza la afirmación o cualquier otra lógica basada en el resultado
        assertTrue(matches);
    }

    @Test
    public void testRegisterUsedEmail() {
        // Register normal user
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "Cabo", "braiscabofelpete@gmail.com", "12345678");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testRegisterNullPassword() {
        HttpEntity<String> requestEntity = createUSERJSONNoPass("Brais", "Cabo", "brais@gmail.com");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testShortPassword() {
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "Cabo", "brais@gmail.com", "1234567");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testIncorrectEmail() {
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "Cabo", "braisgmail.com", "12345678");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testNoEmail() {
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "Cabo", "", "12345678");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testNoName() {
        HttpEntity<String> requestEntity = createUSERJSON("", "Cabo", "brais@gmail.com", "12345678");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testNoLastName() {
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "", "brais@gmail.com", "12345678");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testRegisterOtherUser() {
        HttpEntity<String> requestEntity = createUSERJSON("Brais", "Cabo", "brais@gmail.com", "87654321");
        ResponseEntity<User> response = restTemplate.postForEntity(createURL("/users/"), requestEntity, User.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Obtener el cuerpo de la solicitud enviada
        User createdUser = response.getBody();

        // Comparar campos específicos con los valores esperados
        assertEquals("Brais", createdUser.getName());
        assertEquals("Cabo", createdUser.getLastName());
        assertEquals("brais@gmail.com", createdUser.getEmail());
        assertEquals(null, createdUser.getEncodedPassword());

        String locationHeader = response.getHeaders().getFirst("Location");

        // Define el patrón de expresión regular para comparar con la ubicación de la
        // cabecera
        String pattern = "http://localhost:\\d+/api/users/\\d+";

        // Verifica si la ubicación de la cabecera coincide con el patrón
        boolean matches = locationHeader.matches(pattern);

        // Realiza la afirmación o cualquier otra lógica basada en el resultado
        assertTrue(matches);
    }

    @Test
    public void testUserNormalLogin() {
        // Crear el objeto LoginRequest con los valores correctos
        LoginRequest loginRequest = new LoginRequest("braiscabofelpete@gmail.com", "12345678");
    
        // Enviar la solicitud POST al endpoint correspondiente y capturar la respuesta
        ResponseEntity<Object> response = restTemplate.postForEntity(createURL("/auth/login"), loginRequest, Object.class);
    
        // Verificar el código de estado de la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testLoginIncorrectMail() {
        // Crear el objeto LoginRequest con los valores correctos
        LoginRequest loginRequest = new LoginRequest("braiscabofelepte@gmail.com", "12345678");
    
        // Enviar la solicitud POST al endpoint correspondiente y capturar la respuesta
        ResponseEntity<Object> response = restTemplate.postForEntity(createURL("/auth/login"), loginRequest, Object.class);
    
        // Verificar el código de estado de la respuesta
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testLoginIncorrectPassword() {
        // Crear el objeto LoginRequest con los valores correctos
        LoginRequest loginRequest = new LoginRequest("braiscabofelpete@gmail.com", "12345687");
    
        // Enviar la solicitud POST al endpoint correspondiente y capturar la respuesta
        ResponseEntity<Object> response = restTemplate.postForEntity(createURL("/auth/login"), loginRequest, Object.class);
    
        // Verificar el código de estado de la respuesta
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testLoginChangedPasswords() {
        // Crear el objeto LoginRequest con los valores correctos
        LoginRequest loginRequest = new LoginRequest("braiscabofelpete@gmail.com", "87654321");
    
        // Enviar la solicitud POST al endpoint correspondiente y capturar la respuesta
        ResponseEntity<Object> response = restTemplate.postForEntity(createURL("/auth/login"), loginRequest, Object.class);
    
        // Verificar el código de estado de la respuesta
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    

    private String createURL(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

    private HttpEntity<String> createUSERJSON(String name, String lastName, String email, String password) {
        String json = String.format(
                "{ \"name\":\"%s\", \"lastName\": \"%s\", \"email\" : \"%s\", \"password\" : \"%s\" }", name, lastName,
                email, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(json, headers);
    }

    private HttpEntity<String> createUSERJSONNoPass(String name, String lastName, String email) {
        String json = String.format("{ \"name\":\"%s\", \"lastName\": \"%s\", \"email\" : \"%s\"}", name, lastName,
                email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(json, headers);
    }
    
}