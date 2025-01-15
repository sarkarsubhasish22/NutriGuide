package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.CCDao;
import com.NutriGuide.NutriGuide.entities.CC;

@Service
public class CCServiceImpl implements CCService{

	@Autowired
	private CCDao ccDao;
	
	@Override
	public CC addCC(CC cc) {
		
		ccDao.save(cc);
		return cc;
	}

}
