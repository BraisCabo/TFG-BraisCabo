package com.tfg.brais.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.brais.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
