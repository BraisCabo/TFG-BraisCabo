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

import com.tfg.brais.Model.DTOS.UserBasicDTO;
import com.tfg.brais.Model.DTOS.UserDetailedDTO;
import com.tfg.brais.Model.DTOS.UserRegisterDTO;
import com.tfg.brais.Model.DTOS.SubjectUsersDTO;
import com.tfg.brais.Service.ControllerServices.AccountService;
import com.tfg.brais.Service.ControllerServices.UserSubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Controller
@RequestMapping("/api/users")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserSubjectService userSubjectService;

    @PostMapping("/")
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "403", description = "User already exists or any field is incorrect")
    })
    public ResponseEntity<UserDetailedDTO> register(@RequestBody UserRegisterDTO user) {
        UriComponentsBuilder path = fromCurrentRequest().path("/{id}");
        return accountService.register(user, path);
    }

    @GetMapping("/")
    @Operation(summary = "Get all users by name, page and page size")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users found"),
    })
    public ResponseEntity<Page<UserBasicDTO>> findAll(String name, int page, int size) {
        return this.accountService.findAll(PageRequest.of(page, size), name);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the user logged")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not logged")
    })
    public ResponseEntity<UserDetailedDTO> getMe(HttpServletRequest request){
        return this.accountService.getMe(request.getUserPrincipal());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User edited"),
        @ApiResponse(responseCode = "403", description = "User already exists or any field is incorrect"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDetailedDTO> editUser(HttpServletRequest request, @PathVariable long id, @RequestBody UserRegisterDTO user){
        return this.accountService.editUser(request.getUserPrincipal(), id, user);
    }

    @GetMapping("/{id}/subjects/")
    @Operation(summary = "Get all subjects of a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subjects found"),
        @ApiResponse(responseCode = "403", description = "Not allowed to check user information"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<SubjectUsersDTO> findAllSubjects(@PathVariable long id, HttpServletRequest request){
        return this.userSubjectService.findAllUserSubjects(id, request.getUserPrincipal());
    }

}
