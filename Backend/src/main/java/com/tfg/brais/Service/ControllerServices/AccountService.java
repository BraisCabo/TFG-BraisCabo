package com.tfg.brais.Service.ControllerServices;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;
import com.tfg.brais.security.jwt.Encoder;
import com.tfg.brais.security.jwt.UserLoginService;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Encoder passwordEncoder;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserCheckService userCheckService;

    public AccountService(UserRepository userRepository, Encoder passwordEncoder, UserLoginService userLoginService, UserCheckService userCheckService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userLoginService = userLoginService;
        this.userCheckService = userCheckService;
    }

    public ResponseEntity<User> register(User user, UriComponentsBuilder path) {
        if (!this.userCheckService.validateNewUser(user)) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        user.setRoles("USER");
        userRepository.save(user);

        return ResponseEntity.created(path.buildAndExpand(user.getId()).toUri())
                .headers(userLoginService.generateFreshToken(user.getEmail())).body(user);
    }

    public ResponseEntity<Page<User>> findAll(PageRequest pageRequest, String name) {
        Page<User> page = userRepository.findAllByName((name == null ? "" : name ), pageRequest);
        return ResponseEntity.ok(page);
    }

    public ResponseEntity<User> getMe(Principal userPrincipal) {
        try {
            return ResponseEntity.ok(this.userCheckService.findByMail(userPrincipal.getName()).getBody());
        } catch (Exception e) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<User> editUser(Principal userPrincipal, long id, User user) {

        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(id, userPrincipal);
        if(userCheckResponse.getStatusCode().is4xxClientError()){
            return userCheckResponse;
        }
        User oldUser = userCheckResponse.getBody();

        if (userCheckService.validateEditUser(user, oldUser)){
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }

        if (user.getEncodedPassword() != null && !user.getEncodedPassword().isEmpty()) {
            oldUser.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        } else {
            user.setEncodedPassword(oldUser.getEncodedPassword());
        }

        oldUser.updateUser(user);
        userRepository.save(oldUser);

        return ResponseEntity.ok().headers(userLoginService.generateFreshToken(user.getEmail())).body(oldUser);
    }
}
