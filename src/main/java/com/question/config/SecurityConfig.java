package com.question.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.question.filter.JwtAuthFilter;
import com.question.service.JwtService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig 
{
	@Autowired
    private JwtService authFilter;
	
	@Autowired
	private JwtAuthFilter filters;
	@Bean
    //authentication
    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("Basant")
//                .password(encoder.encode("Pwd1"))
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withUsername("John")
//                .password(encoder.encode("Pwd2"))
//                .roles("USER","ADMIN","HR")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
        return new UserInfoUserDetailsService();
    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity security) throws Exception
	{
		return security.csrf().disable()
				.authorizeHttpRequests()
				.requestMatchers("/products/new","/products/authenticate").permitAll()
				.and()
				.authorizeHttpRequests().requestMatchers("/products/**")
				.authenticated().and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authenticationProvider(authprovider())
				.addFilterBefore(filters, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	public AuthenticationProvider authprovider()
	{
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(encoder());
		return authenticationProvider;
	}
	
	@Bean
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
	{
		return configuration.getAuthenticationManager(); 
	}
}
