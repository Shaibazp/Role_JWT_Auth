package com.question.filter;

import java.io.IOException;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.question.config.UserInfoUserDetailsService;
import com.question.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter
{

	@Autowired
	private JwtService jwtservice;
	
	@Autowired
	private UserInfoUserDetailsService userservice;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException 
	{
		String header = request.getHeader("Authorization");
		String token = "";
		String username = "";
		if(header != null && header.startsWith("Bearer "))
		{
			token = header.substring(7);
			username = jwtservice.extractUsername(token);
			
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null)
		{
			UserDetails userDetails = userservice.loadUserByUsername(username);
			if(jwtservice.validateToken(token, userDetails)) 
			{
				UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authtoken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
