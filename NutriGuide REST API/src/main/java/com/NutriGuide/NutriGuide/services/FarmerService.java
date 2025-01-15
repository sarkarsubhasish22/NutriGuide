package com.NutriGuide.NutriGuide.services;

import java.util.List;

import com.NutriGuide.NutriGuide.entities.Farmer;

public interface FarmerService {

	public Farmer addFarmer(Farmer farmer);

	public List<Farmer> getFarmer();
	
	public List<Farmer> getFarmerByuserId(long userId);
	
}
