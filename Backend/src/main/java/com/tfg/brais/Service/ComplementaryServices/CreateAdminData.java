package com.tfg.brais.Service.ComplementaryServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.security.jwt.Encoder;

import jakarta.annotation.PostConstruct;

@Service
public class CreateAdminData {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Encoder passEncoder;


    @PostConstruct
    public void init(){
        if (userRepository.findByEmail("admin").isPresent()) {
            return;
        }
        User user = new User();
        user.setEmail("admin");
        user.setName("admin");
        user.setEncodedPassword(passEncoder.encode("admin"));
        user.setLastName("admin");
        user.setRoles("USER", "ADMIN");
        userRepository.save(user);
    }
}
