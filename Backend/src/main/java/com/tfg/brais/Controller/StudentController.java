package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.security.jwt.Encoder;

import jakarta.annotation.PostConstruct;


@Controller
@RestController
@RequestMapping("/api/users")
public class StudentController {
    
    @Autowired
    private UserRepository users;

    @Autowired
    private Encoder enc;

    @PostConstruct
    public void fill(){
        User u = new User();
        u.getRoles().add("USER");
        u.setEmail("1");
        u.setEncodedPassword(enc.getPasswordEncoder().encode("1"));
        users.save(u);
    }

    @GetMapping("/")
    private ResponseEntity<List<User>> findAllUsers(){
        List<User> u=  users.findAll();
        if (u.isEmpty()){
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }else{
            return ResponseEntity.ok(u);
        }
    }

    @GetMapping("/get")
    private ResponseEntity<List<User>> f(){
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
    
}
