package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.PFIDao;
import com.NutriGuide.NutriGuide.entities.PFI;

@Service
public class PFIServiceImpl implements PFIService{

	@Autowired
	private PFIDao pfiDao;

	@Override
	public PFI addPFI(PFI pfi) {
		pfiDao.save(pfi);
		return pfi;
	}
	
}
