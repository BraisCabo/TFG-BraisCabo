package com.tfg.brais.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.brais.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String mail);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.name, ' ', u.lastName)) LIKE %?1%")
    Page<User> findAllByName(String name, Pageable pageable);
    
}
