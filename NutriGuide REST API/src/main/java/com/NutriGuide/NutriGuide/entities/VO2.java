package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VO2 {

	@Id
	private long farmerId;
	private float farmerAge;
	private float farmerWeight;
	private float vo2;
	public VO2(long farmerId, float farmerAge, float farmerWeight, float vo2) {
		super();
		this.farmerId = farmerId;
		this.farmerAge = farmerAge;
		this.farmerWeight = farmerWeight;
		this.vo2 = vo2;
	}
	public VO2() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public float getFarmerAge() {
		return farmerAge;
	}
	public void setFarmerAge(float farmerAge) {
		this.farmerAge = farmerAge;
	}
	public float getFarmerWeight() {
		return farmerWeight;
	}
	public void setFarmerWeight(float farmerWeight) {
		this.farmerWeight = farmerWeight;
	}
	public float getVo2() {
		return vo2;
	}
	public void setVo2(float vo2) {
		this.vo2 = vo2;
	}
	@Override
	public String toString() {
		return "VO2 [farmerId=" + farmerId + ", farmerAge=" + farmerAge + ", farmerWeight=" + farmerWeight + ", vo2="
				+ vo2 + "]";
	}
	
	
		
	
}
