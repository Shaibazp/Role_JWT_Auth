package com.question.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.question.entity.UserInfo;
import com.question.repository.UserInfoRepository;

@Component
public class UserInfoUserDetailsService implements UserDetailsService 
{

	@Autowired
    private UserInfoRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		Optional<UserInfo> user = repository.findByName(username);
		
		return user.map(UserInfoUserDetails::new).orElseThrow(()-> new UsernameNotFoundException("User Not Found : "+username ));
	}

}
