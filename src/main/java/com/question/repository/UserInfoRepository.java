package com.question.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.question.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> 
{
	Optional<UserInfo> findByName(String username);
}
