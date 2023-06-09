package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.util.List;

import com.tfg.brais.Model.User;
import com.tfg.brais.Service.AccountService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@Controller
@RequestMapping("/api/users")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/")
    public ResponseEntity<User> register(@RequestBody User user) {
        UriComponentsBuilder path = fromCurrentRequest().path("/{id}");
        return accountService.register(user, path);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> findAll() {
        return this.accountService.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(HttpServletRequest request){
        return this.accountService.getMe(request.getUserPrincipal());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> editUser(HttpServletRequest request, @PathVariable long id, @RequestBody User user){
        return this.accountService.editUser(request.getUserPrincipal(), id, user);
    }

}
