package com.tfg.brais.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.security.jwt.Encoder;

@Service
public class AccountService {
    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private Encoder passwordEncoder;


    private boolean emailUsed(String email){
        return this.userRepository.findByEmail(email).isPresent();
    }

    private boolean checkNull(String stringToCheck){
        return stringToCheck == null || stringToCheck.isEmpty() || stringToCheck.isBlank();
    }

    private boolean checkPassword(String password){
        return checkNull(password) || password.length() < 8;
    }

    public ResponseEntity<User> register(User user, UriComponentsBuilder path){
        if (!emailValidator.validate(user.getEmail()) || this.emailUsed(user.getEmail())){
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        if(checkPassword(user.getEncodedPassword()) || checkNull(user.getName()) || checkNull(user.getLastName())){
            return new ResponseEntity<User>(HttpStatusCode.valueOf(403));
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        user.setRoles("USER");
        userRepository.save(user);
        return ResponseEntity.created(path.buildAndExpand(user.getId()).toUri()).body(user);
    }


}
