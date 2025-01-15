package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BMI {

	@Id
	private long farmerId;
	private float farmerWeight;
	private float farmerHeight;
	private float bmi;
	public BMI(long farmerId, float farmerWeight, float farmerHeight, float bmi) {
		super();
		this.farmerId = farmerId;
		this.farmerWeight = farmerWeight;
		this.farmerHeight = farmerHeight;
		this.bmi = bmi;
	}
	public BMI() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public float getFarmerWeight() {
		return farmerWeight;
	}
	public void setFarmerWeight(float farmerWeight) {
		this.farmerWeight = farmerWeight;
	}
	public float getFarmerHeight() {
		return farmerHeight;
	}
	public void setFarmerHeight(float farmerHeight) {
		this.farmerHeight = farmerHeight;
	}
	public float getBmi() {
		return bmi;
	}
	public void setBmi(float bmi) {
		this.bmi = bmi;
	}
	@Override
	public String toString() {
		return "BMI [farmerId=" + farmerId + ", farmerWeight=" + farmerWeight + ", farmerHeight=" + farmerHeight
				+ ", bmi=" + bmi + "]";
	}
	
	
}
