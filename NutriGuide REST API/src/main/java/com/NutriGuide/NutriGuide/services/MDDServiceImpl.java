package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.MDDDao;
import com.NutriGuide.NutriGuide.entities.MDD;

@Service
public class MDDServiceImpl implements MDDService{

	@Autowired
	private MDDDao mddDao;

	@Override
	public MDD addMDD(MDD mdd) {
		
		mddDao.save(mdd);
		return mdd;
	}
	
	
}
