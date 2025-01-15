package com.NutriGuide.NutriGuide.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.BMIDao;
import com.NutriGuide.NutriGuide.entities.BMI;

@Service
public class BMIServiceImpl implements BMIService{

	@Autowired
	private BMIDao bmiDao;

	@Override
	public BMI addBMI(BMI bmi) {
		
		bmiDao.save(bmi);
		return bmi;
	}
	
	
}
