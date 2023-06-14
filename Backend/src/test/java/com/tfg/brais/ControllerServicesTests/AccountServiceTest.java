package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;
import com.tfg.brais.Service.ControllerServices.AccountService;
import com.tfg.brais.security.jwt.UserLoginService;
import com.tfg.brais.security.jwt.Encoder;

public class AccountServiceTest {

    private UserRepository userRepository;

    private Encoder passwordEncoder;

    private UserLoginService userLoginService;

    private UserCheckService userCheckService;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(Encoder.class);
        userLoginService = Mockito.mock(UserLoginService.class);
        userCheckService = Mockito.mock(UserCheckService.class);
        accountService = new AccountService(userRepository, passwordEncoder, userLoginService, userCheckService);
    }

    @Nested
    public class GetMeTest{

        @Test
        public void getMeTestNoFound() {
            when(userCheckService.findByMail(anyString())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(accountService.getMe(null).getStatusCode().is4xxClientError());
        }

        @Test
        public void getMeTestFound() {
            User user = new User();
            user.setId(1L);
            user.setName("test");
            user.setLastName("test");
            user.setEmail("test@gmail.com");
            Principal principal = Mockito.mock(Principal.class);
            when(principal.getName()).thenReturn("test");
            when(userCheckService.findByMail(anyString())).thenReturn(ResponseEntity.ok(user));
            ResponseEntity<User> me = accountService.getMe(principal);
            assertTrue(me.getStatusCode().is2xxSuccessful());
            assertTrue(me.getBody().equals(user));
        }
    }

    @Nested
    public class RegisterTest{

        @Test
        public void registerTestIncorrectUser() {
            User user = createuser();
            when(userCheckService.validateNewUser(any())).thenReturn(false);
            assertTrue(accountService.register(user, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void registerTestCorrectUser() {
            User user = createuser();
            when(userCheckService.validateNewUser(any())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("12345678");
            when(userRepository.save(any())).thenReturn(user);
            when(userLoginService.generateFreshToken(anyString())).thenReturn(null);
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
            ResponseEntity<User> register = accountService.register(user, uriComponentsBuilder);
            assertTrue(register.getStatusCode().is2xxSuccessful());
            user.setRoles("USER");
            assertTrue(register.getBody().equals(user));
        }

        private User createuser(){
            User user = new User();
            user.setId(1L);
            user.setName("test");
            user.setLastName("test");
            user.setEmail("test@gmail.com");
            user.setPassword("12345678");
            return user;
        }
    }

    @Nested
    public class FindAllTest{

        @Test
        public void findAllTestEmpty() {
            when(userRepository.findAllByName(anyString(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));
            assertTrue(accountService.findAll(null, "test").getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllTestCorrect() {
            List<User> users = new ArrayList<>();
            users.add(new User());
            when(userRepository.findAllByName(anyString(), any())).thenReturn(new PageImpl<>(users));
            ResponseEntity<Page<User>> findAll = accountService.findAll(null, "test");
            assertTrue(findAll.getStatusCode().is2xxSuccessful());
            assertTrue(findAll.getBody().getContent().equals(users));
        }
    }

    @Nested
    public class EditUserTest{
        

        @Test
        public void editUserTestIncorrectPrincipal() {
            User user = createuser();
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(accountService.editUser(null, 1L, user).getStatusCode().is4xxClientError());
        }

        @Test
        public void editUserTestIncorrectNewUser(){
            User user = createuser();
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(createuser()));
            when(userCheckService.validateEditUser(any(), any())).thenReturn(true);
            assertTrue(accountService.editUser(null, 1L, user).getStatusCode().is4xxClientError());
        }

        @Test
        public void editUserTestNewPassword(){
            User newUser = createuser();
            newUser.setPassword("123456789");
            User oldUser = createuser();
            oldUser.setPassword("12345678");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(oldUser));
            when(userCheckService.validateEditUser(any(), any())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("123456789");
            when(userRepository.save(any())).thenReturn(newUser);
            when(userLoginService.generateFreshToken(anyString())).thenReturn(null);
            ResponseEntity<User> editUser = accountService.editUser(null, 1L, newUser);
            assertTrue(editUser.getStatusCode().is2xxSuccessful());
            assertTrue(editUser.getBody().equals(newUser));
        }

        @Test
        public void editUserTestNoPassword(){
            User newUser = createuser();
            newUser.setName("test2");
            User oldUser = createuser();
            oldUser.setPassword("12345678");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(oldUser));
            when(userCheckService.validateEditUser(any(), any())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("12345678");
            when(userRepository.save(any())).thenReturn(newUser);
            when(userLoginService.generateFreshToken(anyString())).thenReturn(null);
            ResponseEntity<User> editUser = accountService.editUser(null, 1L, newUser);
            assertTrue(editUser.getStatusCode().is2xxSuccessful());
            newUser.setPassword("12345678");
            assertTrue(editUser.getBody().equals(newUser));
        }

        private User createuser(){
            User user = new User();
            user.setId(1L);
            user.setName("test");
            user.setLastName("test");
            user.setEmail("test@gmail.com");
            user.setPassword("12345678");
            return user;
        }
    }
}
