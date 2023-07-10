package com.tfg.brais.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tfg.brais.security.jwt.Encoder;
import com.tfg.brais.security.jwt.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RestSecurityConfig {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Autowired
	private Encoder passEncoder;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf((csrf) -> csrf.disable())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
						.requestMatchers(HttpMethod.POST, "/api/users/").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/users/me").hasRole("USER")
						.requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/users/**/subjects/").hasRole("USER")
						.requestMatchers(HttpMethod.POST, "/api/subjects/**/exams/").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/").hasRole("USER")
						.requestMatchers(HttpMethod.POST, "/api/subjects/**/exams/files").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**/questions").hasRole("USER")
						.requestMatchers(HttpMethod.PUT, "/api/subjects/**/exams/**").hasRole("USER")
						.requestMatchers(HttpMethod.PATCH, "/api/subjects/**/exams/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/students/").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/teachers/").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/users/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/users/**/califications").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**/uploads/").hasRole("USER")
						.requestMatchers(HttpMethod.POST, "/api/subjects/**/exams/**/uploads/files").hasRole("USER")
						.requestMatchers(HttpMethod.POST, "/api/subjects/**/exams/**/uploads/questions").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**/uploads/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**/uploads/**/questions").hasRole("USER")
						.requestMatchers(HttpMethod.DELETE, "/api/subjects/**/exams/**/uploads/**").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**/uploads/**/files").hasRole("USER")
						.requestMatchers(HttpMethod.GET, "/api/subjects/**/exams/**/uploads/files").hasRole("USER")
						.requestMatchers(HttpMethod.POST, "/api/subjects/**/exams/**/uploads/**/califications").hasRole("USER")
						.requestMatchers(HttpMethod.PUT, "/api/subjects/**/exams/**/uploads/**/califications").hasRole("USER")
						.requestMatchers(HttpMethod.DELETE, "/api/subjects/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/subjects/").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/subjects/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/users/").hasRole("ADMIN")
						.anyRequest().permitAll()
						);
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passEncoder.getPasswordEncoder());
	}

	// Expose AuthenticationManager as a Bean to be used in other services
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}