package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.brais.security.jwt.AuthResponse;
import com.tfg.brais.security.jwt.LoginRequest;
import com.tfg.brais.security.jwt.UserLoginService;
import com.tfg.brais.security.jwt.AuthResponse.Status;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {

	@Autowired
	private UserLoginService userService;

	@PostMapping("/login")
	@Operation(summary = "Login")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User logged in"),
			@ApiResponse(responseCode = "403", description = "User credentials incorrect") })
	public ResponseEntity<AuthResponse> login(
			@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken,
			@RequestBody LoginRequest loginRequest) {
		return userService.login(loginRequest, accessToken, refreshToken);
	}

	@PostMapping("/logout")
	@Operation(summary = "Logout")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User logged out") })
	public ResponseEntity<AuthResponse> logOut(HttpServletRequest request, HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(request, response)));
	}
}