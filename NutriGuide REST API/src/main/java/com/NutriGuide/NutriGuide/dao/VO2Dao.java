package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.VO2;

public interface VO2Dao extends JpaRepository<VO2, Long>{

	@Query("SELECT T FROM VO2 T WHERE T.farmerId=?1")
	VO2 getVO2ByfarmerId(long farmerId);
}
