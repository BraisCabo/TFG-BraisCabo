package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.tfg.brais.Model.User;

import lombok.Data;

@Data
public class UserBasicDTO {
    Long id;
    String name;
    String lastName;

    public UserBasicDTO(){}

    public UserBasicDTO(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.lastName = user.getLastName();
    }

    public Page<UserBasicDTO> convertPage (Page<User> userPage){
        if (userPage == null){
            return null;
        }
        Page<UserBasicDTO> newPage = new PageImpl<UserBasicDTO>(convertList(userPage.getContent()), userPage.getPageable(), userPage.getTotalElements());
        return newPage;
    }

    private List<UserBasicDTO> convertList(List<User> userList){
        List<UserBasicDTO> list = new ArrayList<>();
        for(User user: userList){
            list.add(new UserBasicDTO(user));
        }
        return list;
    }
}
