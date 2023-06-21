package com.tfg.brais.Model.DTOS;

import com.tfg.brais.Model.User;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String email;

    private String password;

    private String name;

    private String lastName;

    public UserRegisterDTO() {
    }

    public User createUser(){
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setName(this.name);
        user.setLastName(this.lastName);
        return user;
    }
}
