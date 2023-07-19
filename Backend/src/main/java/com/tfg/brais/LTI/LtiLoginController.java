// Source code is decompiled from a .class file using FernFlower decompiler.
package com.tfg.brais.LTI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.security.jwt.UserLoginService;

@Controller
public class LtiLoginController {

    @Autowired
    private LtiService ltiService;

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/lti/login")
    public String loginOnLti(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<String> loginResponse = ltiService.loginOnLti(request, response);
        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            return null;
        }
        return "redirect:/error";
    }

    @PostMapping("/lti/")
    public ResponseEntity<Void> getExam(String id_token, String state, HttpServletRequest request) {
        HttpHeaders headers;
        ResponseEntity<UserExamModel> resourceResponse = ltiService.getResource(id_token, state, request);
        
        if (resourceResponse.getStatusCode().is2xxSuccessful()) {
            Exam exam = resourceResponse.getBody().getExam();
            String route = "/subject/" + exam.getSubject().getId() + "/exam/" + exam.getId();
            headers = userLoginService.generateFreshToken(resourceResponse.getBody().getEmail());
            headers.add(HttpHeaders.LOCATION, route);
        } else {
            headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/error");
        }
        return new ResponseEntity<Void>(headers, HttpStatusCode.valueOf(302));
    }
}
