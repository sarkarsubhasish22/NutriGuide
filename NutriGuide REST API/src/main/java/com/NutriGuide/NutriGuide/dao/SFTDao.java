package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.SFT;

public interface SFTDao extends JpaRepository<SFT,Long>{

	@Query("SELECT T FROM SFT T WHERE T.farmerId=?1")
	SFT getSFTByfarmerId(long farmerId);
}
