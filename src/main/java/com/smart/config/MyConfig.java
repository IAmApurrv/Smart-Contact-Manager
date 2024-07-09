package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MyConfig {
	// @Bean
	// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	// 	http
	// 			.authorizeRequests(authorizeRequests -> authorizeRequests
	// 					.requestMatchers("/admin/**").hasRole("ADMIN")
	// 					.requestMatchers("/user/**").hasRole("USER")
	// 					.requestMatchers("/**").permitAll())
	// 			.formLogin(loginConfigurer -> loginConfigurer
	// 					.loginPage("/login")
	// 					.loginProcessingUrl("/dologin")
	// 					.defaultSuccessUrl("/user/dashboard"))
	// 			.logout(logout -> logout
	// 					.logoutUrl("/logout")
	// 					.logoutSuccessUrl("/")
	// 					.invalidateHttpSession(true))
	// 			.csrf().disable();
	// 	return http.build();
	// }
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/**").permitAll())
				.formLogin(loginConfigurer -> loginConfigurer
						.loginPage("/login")
						.loginProcessingUrl("/dologin")
						.defaultSuccessUrl("/user/dashboard"))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true))
				.csrf().disable();
		return http.build();
	}
	// http.csrf().disable().authorizeHttpRequests()
	// .requestMatchers("/admin/**").hasRole("ADMIN")
	// .requestMatchers("/user/**").hasRole("USER")
	// .requestMatchers("/**").permitAll()
	// .anyRequest().authenticated().and().formLogin()
	// .loginPage("/login")
	// .loginProcessingUrl("/dologin")
	// .defaultSuccessUrl("/user/dashboard"); // failureUrl("/loginfail")
	// return http.build();

	public AuthenticationManager authenticationManager(AuthenticationManagerBuilder amb) throws Exception {
		return (AuthenticationManager) amb.authenticationProvider(authenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;

	}

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// configure method
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

}