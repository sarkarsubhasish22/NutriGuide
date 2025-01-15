package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.MUACDao;
import com.NutriGuide.NutriGuide.entities.MUAC;

@Service
public class MUACServiceImpl implements MUACService{

	@Autowired
	private MUACDao muacDao;
	
	@Override
	public MUAC addMUAC(MUAC muac) {
		
		muacDao.save(muac);
		return muac;
	}
	
	
	
}
