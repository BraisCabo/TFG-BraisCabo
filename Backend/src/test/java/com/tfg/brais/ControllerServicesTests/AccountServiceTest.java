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
import com.tfg.brais.Model.DTOS.UserBasicDTO;
import com.tfg.brais.Model.DTOS.UserDetailedDTO;
import com.tfg.brais.Model.DTOS.UserRegisterDTO;
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
            ResponseEntity<UserDetailedDTO> me = accountService.getMe(principal);
            assertTrue(me.getStatusCode().is2xxSuccessful());
            assertTrue(me.getBody().equals(new UserDetailedDTO(user)));
        }
    }

    @Nested
    public class RegisterTest{

        @Test
        public void registerTestIncorrectUser() {
            UserRegisterDTO user = createuser();
            when(userCheckService.validateNewUser(any())).thenReturn(false);
            assertTrue(accountService.register(user, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void registerTestCorrectUser() {
            UserRegisterDTO user = createuser();
            User user2 = new User();
            when(userCheckService.validateNewUser(any())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("12345678");
            when(userRepository.save(any())).thenReturn(user2);
            when(userLoginService.generateFreshToken(anyString())).thenReturn(null);
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
            ResponseEntity<UserDetailedDTO> register = accountService.register(user, uriComponentsBuilder);
            assertTrue(register.getStatusCode().is2xxSuccessful());}

        private UserRegisterDTO createuser(){
            UserRegisterDTO user = new UserRegisterDTO();
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
            assertTrue(accountService.findAll(null, "test").getStatusCode().is2xxSuccessful());
        }

        @Test
        public void findAllTestCorrect() {
            List<User> users = new ArrayList<>();
            users.add(new User());
            when(userRepository.findAllByName(anyString(), any())).thenReturn(new PageImpl<>(users));
            ResponseEntity<Page<UserBasicDTO>> findAll = accountService.findAll(null, "test");
            assertTrue(findAll.getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class EditUserTest{
        

        @Test
        public void editUserTestIncorrectPrincipal() {
            UserRegisterDTO user = createuser();
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(accountService.editUser(null, 1L, user).getStatusCode().is4xxClientError());
        }

        @Test
        public void editUserTestIncorrectNewUser(){
            UserRegisterDTO user = createuser();
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(createuser().createUser()));
            when(userCheckService.validateEditPassword(any(), any())).thenReturn(false);
            assertTrue(accountService.editUser(null, 1L, user).getStatusCode().is4xxClientError());
        }

        @Test
        public void editUserTestCorrect(){
            UserRegisterDTO newUser = createuser();
            newUser.setPassword("123456789");
            UserRegisterDTO oldUser = createuser();
            oldUser.setPassword("12345678");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(oldUser.createUser()));
            when(userCheckService.validateEditPassword(any(), any())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("123456789");
            when(userRepository.save(any())).thenReturn(newUser.createUser());
            when(userLoginService.generateFreshToken(anyString())).thenReturn(null);
            ResponseEntity<UserDetailedDTO> editUser = accountService.editUser(null, 1L, newUser);
            assertTrue(editUser.getStatusCode().is2xxSuccessful());
        }

        private UserRegisterDTO createuser(){
            UserRegisterDTO user = new UserRegisterDTO();
            user.setPassword("1234567");
            return user;
        }
    }
}
