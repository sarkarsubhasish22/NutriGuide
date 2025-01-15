package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.VO2Dao;
import com.NutriGuide.NutriGuide.entities.VO2;

@Service
public class VO2ServiceImpl implements VO2Service{

	@Autowired
	private VO2Dao vo2Dao;

	@Override
	public VO2 addVO2(VO2 vo2) {
		
		vo2Dao.save(vo2);
		return vo2;
	}
	
	
}
