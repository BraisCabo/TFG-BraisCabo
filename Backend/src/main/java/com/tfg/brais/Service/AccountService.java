package com.tfg.brais.Service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.security.jwt.Encoder;
import com.tfg.brais.security.jwt.UserLoginService;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private Encoder passwordEncoder;

    @Autowired
    private UserLoginService userService;

    private boolean emailUsed(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    private boolean checkNull(String stringToCheck) {
        return stringToCheck == null || stringToCheck.isEmpty() || stringToCheck.isBlank();
    }

    private boolean checkPassword(String password) {
        return checkNull(password) || password.length() < 8;
    }

    public boolean validateMail(String email) {
        return !emailValidator.validate(email) || this.emailUsed(email);
    }

    public ResponseEntity<User> register(User user, UriComponentsBuilder path) {
        if (validateMail(user.getEmail())) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        if (checkPassword(user.getEncodedPassword()) || checkNull(user.getName()) || checkNull(user.getLastName())) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        user.setRoles("USER");
        userRepository.save(user);

        return ResponseEntity.created(path.buildAndExpand(user.getId()).toUri())
                .headers(userService.generateFreshToken(user.getEmail())).body(user);
    }

    public ResponseEntity<List<User>> findAll() {
        List<User> list = userRepository.findAll();
        if (list.isEmpty()) {
            return new ResponseEntity<List<User>>(HttpStatusCode.valueOf(404));
        } else {
            return ResponseEntity.ok(list);
        }
    }

    public ResponseEntity<User> findByMail(String mail) {
        try {
            return ResponseEntity.ok(userRepository.findByEmail(mail).get());
        } catch (Exception e) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<User> getMe(Principal userPrincipal) {
        try {
            return ResponseEntity.ok(this.findByMail(userPrincipal.getName()).getBody());
        } catch (Exception e) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<User> editUser(Principal userPrincipal, long id, User user) {
        if (userPrincipal == null) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        ResponseEntity<User> response = this.findByMail(userPrincipal.getName());

        if (response.getStatusCode().is4xxClientError()) {
            return response;
        }

        User oldUser = response.getBody();

        if (!oldUser.getId().equals(id)) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }

        if ((!oldUser.getRoles().equals(user.getRoles()) && !user.getRoles().isEmpty())
                || oldUser.getRoles().contains("ADMIN")) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }

        if (validateMail(user.getEmail()) && !user.getEmail().equals(oldUser.getEmail())) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }

        if (checkNull(user.getName()) || checkNull(user.getLastName()) || (user.getEncodedPassword() != null
                && !user.getEncodedPassword().isEmpty() && checkPassword(user.getEncodedPassword()))) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        if (user.getEncodedPassword() != null && !user.getEncodedPassword().isEmpty()) {
            oldUser.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        } else {
            user.setEncodedPassword(oldUser.getEncodedPassword());
        }
        oldUser.updateUser(user);
        userRepository.save(oldUser);

        return ResponseEntity.ok().headers(userService.generateFreshToken(user.getEmail())).body(oldUser);
    }

}
