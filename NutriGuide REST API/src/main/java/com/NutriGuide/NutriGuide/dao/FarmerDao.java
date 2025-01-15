package com.NutriGuide.NutriGuide.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.Farmer;

public interface FarmerDao extends JpaRepository<Farmer,Long>{

	@Query("SELECT T FROM Farmer T WHERE T.userId=?1")
	List<Farmer> getFarmerByuserId(long userId);
	
	@Query("SELECT T FROM Farmer T WHERE T.farmerId=?1")
	Farmer getFarmerByfarmerId(long farmerId);
}
