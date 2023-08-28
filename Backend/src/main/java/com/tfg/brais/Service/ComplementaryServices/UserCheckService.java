package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;

@Service
public class UserCheckService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailValidator emailValidator;

    public UserCheckService(UserRepository userRepository, EmailValidator emailValidator) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
    }

    public boolean emailUsed(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    private boolean checkNull(String stringToCheck) {
        return stringToCheck == null || stringToCheck.isEmpty() || stringToCheck.isBlank();
    }

    private boolean checkPassword(String password) {
        return checkNull(password) || password.length() < 8;
    }

    private boolean validateMail(String email) {
        return !emailValidator.validate(email) || this.emailUsed(email);
    }

    public ResponseEntity<User> loadUserPrincipal(long userId, Principal userPrincipal) {
        if (userPrincipal == null || userId == 0) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }

        ResponseEntity<User> response = this.findByMail(userPrincipal.getName());

        if (response.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }

        User user = response.getBody();

        if (!user.getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<User> loadUserNoCkeck(Principal userPrincipal){
        if(userPrincipal == null) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        return  this.findByMail(userPrincipal.getName());
    }

    public ResponseEntity<User> findByMail(String mail) {
        if(mail == null || mail.isEmpty() || mail.isBlank()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
        try {
            return ResponseEntity.ok(userRepository.findByEmail(mail).get());
        } catch (Exception e) {
            return new ResponseEntity<User>(HttpStatusCode.valueOf(404));
        }
    }

    public boolean validateNewUser(User user) {
        if(user == null) {
            return false;
        }
        if (validateMail(user.getEmail())) {
            return false;
        }
        if (checkPassword(user.getEncodedPassword()) || checkNull(user.getName()) || checkNull(user.getLastName())) {
            return false;
        }
        return true;
    }

    public boolean validateEditUser(User newUser, User oldUser) {
        if (newUser == null || oldUser == null) {
            return false;
        }
        if ((!oldUser.getRoles().equals(newUser.getRoles()) && !newUser.getRoles().isEmpty())
                || oldUser.getRoles().contains("ADMIN")) {
            return false;
        }

        if (newUser.getEmail() == null || !newUser.getEmail().equals(oldUser.getEmail())) {
            return false;
        }

        if (checkNull(newUser.getName()) || checkNull(newUser.getLastName()) || (newUser.getEncodedPassword() != null
                && !newUser.getEncodedPassword().isEmpty() && checkPassword(newUser.getEncodedPassword()))) {
            return false;
        }
        return true;
    }

    public boolean validateEditPassword(User newUser, User oldUser) {
         if (newUser == null || oldUser == null) {
            return false;
        }

        if (checkPassword(newUser.getEncodedPassword())) {
            return false;
        }
        return true;
    }
}
