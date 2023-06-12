package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.tfg.brais.Model.User;
import com.tfg.brais.Model.UserSubjectDTO;
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
    public ResponseEntity<Page<User>> findAll(String name, int page, int size) {
        return this.accountService.findAll(PageRequest.of(page, size), name);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(HttpServletRequest request){
        return this.accountService.getMe(request.getUserPrincipal());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> editUser(HttpServletRequest request, @PathVariable long id, @RequestBody User user){
        return this.accountService.editUser(request.getUserPrincipal(), id, user);
    }

    @GetMapping("/{id}/subjects/")
    public ResponseEntity<UserSubjectDTO> findAllSubjects(@PathVariable long id, HttpServletRequest request){
        return this.accountService.findAllUserSubjects(id, request.getUserPrincipal());
    }

}
