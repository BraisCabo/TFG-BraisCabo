package com.tfg.brais.Model;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @JsonIgnore
    private String encodedPassword;

    private String name;

    private String lastName;

    private String ltiId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    public void setRoles(String ... roles){
        this.roles = List.of(roles);
    }

    public void setPassword(String password) {
        setEncodedPassword(password);
    }

    public void setRolesList(List<String> roles2) {
        this.roles = roles2;
    }

    public void updateUser(User newUser){
        this.email = newUser.email;
        this.lastName = newUser.lastName;
        this.name = newUser.name;
    }

}
