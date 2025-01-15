package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.User;


public interface UserDao extends JpaRepository<User,Long>{
	
	@Query("SELECT T.userId FROM User T WHERE T.email=?1 ")
	long getUserIdByEmail(String email);
	
}
