package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.tfg.brais.Model.DTOS.SubjectChangesDTO;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Service.ControllerServices.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RequestMapping("/api")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/subjects/")
    @Operation(summary = "Create a new subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subject created"),
        @ApiResponse(responseCode = "403", description = "Subject already exists or any field is incorrect")
    })
    public ResponseEntity<SubjectDetailedDTO> createSubject(@RequestBody SubjectChangesDTO subjectDTO) {
        UriComponentsBuilder path = fromCurrentRequest().path("/{id}");
        return adminService.createSubject(subjectDTO, path);
    }

    @PutMapping("/subjects/{id}")
    @Operation(summary = "Edit a subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject edited"),
        @ApiResponse(responseCode = "403", description = "Subject name already exists in other subjetx or any field is incorrect"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<SubjectDetailedDTO> editSubject(@RequestBody SubjectChangesDTO subjectDTO, @PathVariable long id){
        return this.adminService.editSubject(id, subjectDTO);
    }

    @DeleteMapping("/subjects/{id}")
    @Operation(summary = "Delete a subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject deleted"),
        @ApiResponse(responseCode = "403", description = "User is not allowed to delete this subject"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<SubjectDetailedDTO> deleteById(@PathVariable long id){
        return this.adminService.deleteById(id);
    }
}
