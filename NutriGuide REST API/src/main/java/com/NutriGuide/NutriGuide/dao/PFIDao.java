package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.PFI;

public interface PFIDao extends JpaRepository<PFI,Long>{

	@Query("SELECT T FROM PFI T WHERE T.farmerId=?1")
	PFI getPFIByfarmerId(long farmerId);
}
