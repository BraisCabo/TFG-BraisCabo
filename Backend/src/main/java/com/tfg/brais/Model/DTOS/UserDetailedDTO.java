package com.tfg.brais.Model.DTOS;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tfg.brais.Model.User;

import lombok.Data;

@Data
public class UserDetailedDTO{

    private Long id;

    private String email;

    @JsonIgnore
    private String encodedPassword;

    private String name;

    private String lastName;

    private List<String> roles;

    public UserDetailedDTO(){}

    public UserDetailedDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.encodedPassword = user.getEncodedPassword();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.roles = user.getRoles();
    }

    public User createUser(){
        User user = new User();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setEncodedPassword(this.encodedPassword);
        user.setName(this.name);
        user.setLastName(this.lastName);
        user.setRolesList(roles);
        return user;
    }

    public void setPassword(String password) {
        setEncodedPassword(password);
    }

    public void setRoles(String ... roles){
        this.roles = List.of(roles);
    }


    public void setRolesList(List<String> roles2) {
        this.roles = roles2;
    }
}
