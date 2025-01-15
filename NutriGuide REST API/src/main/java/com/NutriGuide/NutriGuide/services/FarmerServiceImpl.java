package com.NutriGuide.NutriGuide.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.FarmerDao;
import com.NutriGuide.NutriGuide.entities.Farmer;

@Service
public class FarmerServiceImpl implements FarmerService{

	@Autowired
	private FarmerDao farmerDao;
	
	@Override
	public List<Farmer> getFarmer() {
		
		return farmerDao.findAll();
	}

	@Override
	public Farmer addFarmer(Farmer farmer) {
				
		farmerDao.save(farmer);
		return farmer;
	}

	@Override
	public List<Farmer> getFarmerByuserId(long userId) {
		
		return farmerDao.getFarmerByuserId(userId);
	}

	

	
}
