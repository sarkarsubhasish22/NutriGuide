package com.NutriGuide.NutriGuide.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.NutriGuide.NutriGuide.entities.MDD;

public interface MDDDao extends JpaRepository<MDD,Long>{

	@Query("SELECT T FROM MDD T WHERE T.farmerId=?1")
	MDD getMDDByfarmerId(long farmerId);
}
