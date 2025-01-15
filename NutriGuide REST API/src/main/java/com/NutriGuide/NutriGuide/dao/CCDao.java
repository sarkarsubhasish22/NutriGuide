package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.CC;

public interface CCDao extends JpaRepository<CC,Long>{

	@Query("SELECT T FROM CC T WHERE T.farmerId=?1")
	CC getCCByfarmerId(long farmerId);
}
