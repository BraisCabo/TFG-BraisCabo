// Source code is decompiled from a .class file using FernFlower decompiler.
package com.tfg.brais.LTI;

import edu.uoc.elc.lti.tool.Key;
import edu.uoc.elc.lti.tool.Registration;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.ToolBuilders;
import edu.uoc.elc.lti.tool.Key.KeyBuilder;
import edu.uoc.elc.lti.tool.oidc.AuthRequestUrlBuilder;
import edu.uoc.elc.lti.tool.oidc.LoginRequest;
import edu.uoc.elc.lti.tool.oidc.LoginResponse;
import edu.uoc.elc.lti.tool.validator.LTIResourceLinkLaunchValidatable;
import edu.uoc.elc.spring.lti.security.openid.LoginRequestFactory;
import edu.uoc.elc.spring.lti.security.openid.OIDCFilter;
import edu.uoc.elc.spring.lti.tool.ToolDefinitionBean;
import edu.uoc.elc.spring.lti.tool.registration.RegistrationService;
import edu.uoc.lti.clientcredentials.ClientCredentialsRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller 
public class LTIController{
   
   @Autowired
   private LtiInfo ltiInfo;

   @Autowired
   private LtiTool ltiTool;

   @PostMapping("/lti/login")
   public String loginOnLti(HttpServletRequest request, HttpServletResponse response){
      LoginRequest loginRequest = getLoginRequest(request);
      try {
         new URI(loginRequest.getTarget_link_uri());
         String authRequest = getLoginResponse(loginRequest);
         response.sendRedirect(authRequest);
         return null;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   @PostMapping("/lti/")
   public String getExam(String id_token, String state, HttpServletRequest request){
      Tool tool = this.ltiTool.getTool(request);
      tool.validate(id_token, state);
      if (!tool.isValid()) {
         System.out.println(tool.getReason());
      }
      return null;
   }

   private LoginRequest getLoginRequest(HttpServletRequest request){
      return LoginRequest.builder().iss(request.getParameter("iss")).login_hint(request.getParameter("login_hint")).target_link_uri(request.getParameter("target_link_uri")).lti_message_hint(request.getParameter("lti_message_hint")).lti_deployment_id(request.getParameter("lti_deployment_id")).client_id(request.getParameter("client_id")).build();
   }

   private String getLoginResponse(LoginRequest loginRequest){
      LoginResponse loginResponse = LoginResponse.builder().client_id(loginRequest.getClient_id() != null ? loginRequest.getClient_id() : this.ltiInfo.getClientId()).redirect_uri(loginRequest.getTarget_link_uri()).login_hint(loginRequest.getLogin_hint()).state((new BigInteger(50, new SecureRandom())).toString(16)).nonce((new BigInteger(50, new SecureRandom())).toString(16)).lti_message_hint(loginRequest.getLti_message_hint()).build();
      try {
         new URI(loginRequest.getTarget_link_uri());
         this.ltiTool.setState(loginResponse.getState());
         this.ltiTool.setNonce(loginResponse.getNonce());
         return AuthRequestUrlBuilder.build(this.ltiInfo.getOidcAuthUrl(), loginResponse);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }
}
