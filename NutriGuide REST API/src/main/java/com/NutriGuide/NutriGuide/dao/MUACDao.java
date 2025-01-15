package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.MUAC;

public interface MUACDao extends JpaRepository<MUAC, Long>{

	@Query("SELECT T FROM MUAC T WHERE T.farmerId=?1")
	MUAC getMUACByfarmerId(long farmerId);
}
