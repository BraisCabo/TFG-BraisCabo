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
import com.tfg.brais.Model.DTOS.UserBasicDTO;
import com.tfg.brais.Model.DTOS.UserDetailedDTO;
import com.tfg.brais.Model.DTOS.UserRegisterDTO;
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

    public ResponseEntity<UserDetailedDTO> register(UserRegisterDTO newUser, UriComponentsBuilder path) {
        User user = newUser.createUser();
        if (!this.userCheckService.validateNewUser(user)) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        user.setRoles("USER");
        user = userRepository.save(user);

        return ResponseEntity.created(path.buildAndExpand(user.getId()).toUri())
                .headers(userLoginService.generateFreshToken(user.getEmail())).body(new UserDetailedDTO(user));
    }

    public ResponseEntity<Page<UserBasicDTO>> findAll(PageRequest pageRequest, String name) {
        UserBasicDTO userBasic = new UserBasicDTO();
        Page<UserBasicDTO> page = userBasic.convertPage(userRepository.findAllByName((name == null ? "" : name ), pageRequest));
        return ResponseEntity.ok(page);
    }

    public ResponseEntity<UserDetailedDTO> getMe(Principal userPrincipal) {
        try {
            return ResponseEntity.ok(new UserDetailedDTO(this.userCheckService.findByMail(userPrincipal.getName()).getBody()));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<UserDetailedDTO> editUser(Principal userPrincipal, long userId, UserRegisterDTO newUser) {
        User user = newUser.createUser();
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(userId, userPrincipal);
        if(userCheckResponse.getStatusCode().is4xxClientError()){
            return new ResponseEntity<>(HttpStatusCode.valueOf(userCheckResponse.getStatusCode().value()));
        }
        User oldUser = userCheckResponse.getBody();

        if (userCheckService.validateEditUser(user, oldUser)){
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }

        if (user.getEncodedPassword() != null && !user.getEncodedPassword().isEmpty()) {
            oldUser.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        } else {
            user.setEncodedPassword(oldUser.getEncodedPassword());
        }

        oldUser.updateUser(user);
        userRepository.save(oldUser);

        return ResponseEntity.ok().headers(userLoginService.generateFreshToken(user.getEmail())).body(new UserDetailedDTO(oldUser));
    }
}
