package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.SFTDao;
import com.NutriGuide.NutriGuide.entities.SFT;

@Service
public class SFTServiceImpl implements SFTService{

	@Autowired
	private SFTDao sftDao;

	@Override
	public SFT addSFT(SFT sft) {
		
		sftDao.save(sft);
		return sft;
	}
	
	
}
