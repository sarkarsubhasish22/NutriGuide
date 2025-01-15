package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.BMI;

public interface BMIDao extends JpaRepository<BMI,Long>{

	@Query("SELECT T FROM BMI T WHERE T.farmerId=?1")
	BMI getBMIByfarmerId(long farmerId);
}
