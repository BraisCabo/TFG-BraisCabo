package com.tfg.brais.LTI;


import java.util.Arrays;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import edu.uoc.lti.oidc.OIDCLaunchSession;

@Service
public class MyOidcLaunchSession implements OIDCLaunchSession {
   private HttpServletRequest request;
   public static final List<String> KEYS = Arrays.asList("currentLti1.3State", "currentLti1.3Nonce", "currentLti1.3TargetLinkUri", "currentLti1.3ClientId", "currentLti1.3DeploymentId");

   public MyOidcLaunchSession() {
   }

   public void clear() {
      HttpSession session = this.request.getSession(false);
      if (session != null) {
         this.setState((String)null);
         this.setTargetLinkUri((String)null);
         this.setNonce((String)null);
         this.setClientId((String)null);
         this.setRegistrationId((String)null);
         this.setDeploymentId((String)null);
      }

   }

   public void init(HttpServletRequest request) {
      this.request = request;
   }

   public void setState(String s) {
      this.setAttribute("currentLti1.3State", s);
   }

   public void setNonce(String s) {
      this.setAttribute("currentLti1.3Nonce", s);
   }

   public void setTargetLinkUri(String s) {
      this.setAttribute("currentLti1.3TargetLinkUri", s);
   }

   public void setClientId(String s) {
      this.setAttribute("currentLti1.3ClientId", s);
   }

   public void setDeploymentId(String s) {
      this.setAttribute("currentLti1.3DeploymentId", s);
   }

   private void setAttribute(String name, String value) {
      if (value == null) {
         this.request.getSession().removeAttribute(name);
      } else {
         this.request.getSession().setAttribute(name, value);
      }

   }

   public String getState() {
      return this.getAttribute("currentLti1.3State");
   }

   public String getNonce() {
      return this.getAttribute("currentLti1.3Nonce");
   }

   public String getTargetLinkUri() {
      return this.getAttribute("currentLti1.3TargetLinkUri");
   }

   public String getClientId() {
      return this.getAttribute("currentLti1.3ClientId");
   }

   public String getDeploymentId() {
      return this.getAttribute("currentLti1.3DeploymentId");
   }

   public void setRegistrationId(String s) {
      this.setAttribute("currentLti1.3RegistrationId", s);
   }

   public String getRegistrationId() {
      return this.getAttribute("currentLti1.3RegistrationId");
   }

   private String getAttribute(String name) {
      Object state = this.request.getSession().getAttribute(name);
      return state != null ? state.toString() : null;
   }
}

