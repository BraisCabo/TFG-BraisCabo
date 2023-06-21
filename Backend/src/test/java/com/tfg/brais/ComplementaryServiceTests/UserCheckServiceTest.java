package com.tfg.brais.ComplementaryServiceTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.EmailValidator;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

public class UserCheckServiceTest {

    private UserRepository userRepository;

    private EmailValidator emailValidator;

    private UserCheckService userCheckService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        emailValidator = Mockito.mock(EmailValidator.class);
        userCheckService = new UserCheckService(userRepository, emailValidator);
    }

    @Nested
    @DisplayName("loadUserPrincipalTests")
    class LoadUserPrincipalTests {

        @Test
        public void testLoadUserPrincipalNullPrincipal() throws Exception {
            ResponseEntity<User> loadUserPrincipal = userCheckService.loadUserPrincipal(1L, null);
            assertTrue(loadUserPrincipal.getStatusCode().is4xxClientError());
        }

        @Test
        public void testLoadUserPrincipalNullId() throws Exception {
            Principal principal = Mockito.mock(Principal.class);
            Mockito.when(principal.getName()).thenReturn("test");
            ResponseEntity<User> loadUserPrincipal = userCheckService.loadUserPrincipal(0L, principal);
            assertTrue(loadUserPrincipal.getStatusCode().is4xxClientError());
        }

        @Test
        public void testLoadUserPrincipalExistUser() throws Exception {
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setId(1L);
            Principal principal = Mockito.mock(Principal.class);
            Mockito.when(principal.getName()).thenReturn("test@gmail.com");

            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
            ResponseEntity<User> loadUserPrincipal = userCheckService.loadUserPrincipal(1L, principal);
            assertTrue(loadUserPrincipal.getStatusCode().is2xxSuccessful());
            User userResponse = loadUserPrincipal.getBody();
            assertTrue(userResponse.getEmail().equals("test@gmail.com"));
            assertTrue(userResponse.getName().equals("test"));
            assertTrue(userResponse.getLastName().equals("test"));
        }

        @Test
        public void testLoadUserPrincipalDontExistUser() throws Exception {
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setId(1L);
            Principal principal = Mockito.mock(Principal.class);
            Mockito.when(principal.getName()).thenReturn("test@gmail.com");

            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
            ResponseEntity<User> loadUserPrincipal = userCheckService.loadUserPrincipal(1L, principal);
            assertTrue(loadUserPrincipal.getStatusCode().is4xxClientError());
        }

        @Test
        public void testLoadUserPrincipalDifferentId() throws Exception {
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setId(2L);
            Principal principal = Mockito.mock(Principal.class);
            Mockito.when(principal.getName()).thenReturn("test@gmail.com");

            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
            ResponseEntity<User> loadUserPrincipal = userCheckService.loadUserPrincipal(1L, principal);
            assertTrue(loadUserPrincipal.getStatusCode().is4xxClientError());
        }

    }

    @Nested
    @DisplayName("FindByMailTest")
    public class FindByMailTest {

        @Test
        public void testFindByMailNullEmail() throws Exception {
            ResponseEntity<User> findByMail = userCheckService.findByMail(null);
            assertTrue(findByMail.getStatusCode().is4xxClientError());
        }

        @Test
        public void testFindByMailEmptyEmail() throws Exception {
            ResponseEntity<User> findByMail = userCheckService.findByMail("");
            assertTrue(findByMail.getStatusCode().is4xxClientError());
        }

        @Test
        public void testFindByMailValidEmail() throws Exception {
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setId(1L);
            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
            ResponseEntity<User> findByMail = userCheckService.findByMail("test@gmail.com");
            assertTrue(findByMail.getStatusCode().is2xxSuccessful());
            User userResponse = findByMail.getBody();
            assertTrue(userResponse.getEmail().equals("test@gmail.com"));
            assertTrue(userResponse.getName().equals("test"));
            assertTrue(userResponse.getLastName().equals("test"));
        }

        @Test
        public void testFindByNotFoundEmail() throws Exception {
            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
            ResponseEntity<User> findByMail = userCheckService.findByMail("test@gmail.com");
            assertTrue(findByMail.getStatusCode().is4xxClientError());
        }

    }

    @Nested
    @DisplayName("Validate new user tests")
    public class ValidateNewUserTests {

        @Test
        public void testValidateNewUserNullUser() throws Exception {
            startMock();
            boolean validateNewUser = userCheckService.validateNewUser(null);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserNullEmail() throws Exception {
            startMock();
            User user = new User();
            user.setEmail(null);
            boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserEmptyEmail() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("");
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserNullName() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName(null);
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserEmptyName() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("");
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserNullLastName() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName(null);
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserEmptyLastName() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("");
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserNullPassword() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setEncodedPassword(null);
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserEmptyPassword() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setEncodedPassword("");
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        public void testValidateNewUserShortPassword() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setEncodedPassword("1234567");
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        @Test
        public void testValidateNewUserValidUser() throws Exception {
            startMock();
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setEncodedPassword("12345678");
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(validateNewUser);
        }

        @Test
        public void testValidateNewUserUsedEmail() throws Exception {
            User user = new User();
            user.setEmail("test@gmail.com");
            user.setName("test");
            user.setLastName("test");
            user.setEncodedPassword("12345678");
            Mockito.when(emailValidator.validate("test@gmail.com")).thenReturn(true);
            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
            Boolean validateNewUser = userCheckService.validateNewUser(user);
            assertTrue(!validateNewUser);
        }

        private void startMock(){
            Mockito.when(emailValidator.validate("test@gmail.com")).thenReturn(true);
            Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
        }

    }


    @Nested
    @DisplayName("Validate edit user tests")
    public class ValidateEditUserTests{

        @Test
        public void testValidateEditUserNullNewUser() throws Exception {
            boolean validateEditUser = userCheckService.validateEditUser(null, new User());
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserNullOldUser() throws Exception {
            boolean validateEditUser = userCheckService.validateEditUser(new User(), null);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserSameRoles() throws Exception {
            User newUser = new User();
            newUser.setRoles("USER" ,"ADMIN");
            User oldUser = new User();
            oldUser.setRoles("USER");
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserNoAdmin() throws Exception {
            User newUser = new User();
            newUser.setRoles("USER" ,"ADMIN");
            User oldUser = new User();
            oldUser.setRoles("USER", "ADMIN");
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserNullEmail() throws Exception {
            User newUser = new User();
            newUser.setRoles("USER");
            newUser.setEmail(null);
            User oldUser = new User();
            oldUser.setRoles("USER");
            oldUser.setEmail("test@gmail.com");
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserChangedEmail() throws Exception {
            User newUser = new User();
            newUser.setRoles("USER");
            newUser.setEmail("test1@gmail.com");
            User oldUser = new User();
            oldUser.setRoles("USER");
            oldUser.setEmail("test@gmail.com");
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserNullName() throws Exception {
            User newUser = getUser();
            newUser.setName(null);
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserEmptyName() throws Exception {
            User newUser = getUser();
            newUser.setName("");
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserNullLastName() throws Exception {
            User newUser = getUser();
            newUser.setLastName(null);
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserEmptyLastName() throws Exception {
            User newUser = getUser();
            newUser.setLastName("");
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        @Test
        public void testValidateEditUserNullPassword() throws Exception {
            User newUser = getUser();
            newUser.setEncodedPassword(null);
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(validateEditUser);
        }

        @Test
        public void testValidateEditUserEmptyPassword() throws Exception {
            User newUser = getUser();
            newUser.setEncodedPassword("");
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(validateEditUser);
        }

        @Test
        public void testValidateEditUserShortPassword() throws Exception{
            User newUser = getUser();
            newUser.setEncodedPassword("1234567");
            User oldUser = getUser();
            boolean validateEditUser = userCheckService.validateEditUser(newUser, oldUser);
            assertTrue(!validateEditUser);
        }

        private User getUser(){
            User newUser = new User();
            newUser.setRoles("USER");
            newUser.setEmail("test@gmail.com");
            newUser.setName("test");
            newUser.setLastName("test");
            newUser.setEncodedPassword("12345678");
            return newUser;
        }
    }
}
