package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Farmer")
@Table(name="Farmer")
public class Farmer {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long farmerId;
	private long userId;
	private String farmerName;
	private int farmerAge;
	private String farmerGender;
	private String farmerState;
	private String farmerDistrict;
	private String farmerVillage;
	private String farmerEducation;
	public Farmer(long farmerId, long userId, String farmerName, int farmerAge, String farmerGender, String farmerState,
			String farmerDistrict, String farmerVillage, String farmerEducation) {
		super();
		this.farmerId = farmerId;
		this.userId = userId;
		this.farmerName = farmerName;
		this.farmerAge = farmerAge;
		this.farmerGender = farmerGender;
		this.farmerState = farmerState;
		this.farmerDistrict = farmerDistrict;
		this.farmerVillage = farmerVillage;
		this.farmerEducation = farmerEducation;
	}
	public Farmer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getFarmerName() {
		return farmerName;
	}
	public void setFarmerName(String farmerName) {
		this.farmerName = farmerName;
	}
	public int getFarmerAge() {
		return farmerAge;
	}
	public void setFarmerAge(int farmerAge) {
		this.farmerAge = farmerAge;
	}
	public String getFarmerGender() {
		return farmerGender;
	}
	public void setFarmerGender(String farmerGender) {
		this.farmerGender = farmerGender;
	}
	public String getFarmerState() {
		return farmerState;
	}
	public void setFarmerState(String farmerState) {
		this.farmerState = farmerState;
	}
	public String getFarmerDistrict() {
		return farmerDistrict;
	}
	public void setFarmerDistrict(String farmerDistrict) {
		this.farmerDistrict = farmerDistrict;
	}
	public String getFarmerVillage() {
		return farmerVillage;
	}
	public void setFarmerVillage(String farmerVillage) {
		this.farmerVillage = farmerVillage;
	}
	public String getFarmerEducation() {
		return farmerEducation;
	}
	public void setFarmerEducation(String farmerEducation) {
		this.farmerEducation = farmerEducation;
	}
	@Override
	public String toString() {
		return "Farmer [farmerId=" + farmerId + ", userId=" + userId + ", farmerName=" + farmerName + ", farmerAge="
				+ farmerAge + ", farmerGender=" + farmerGender + ", farmerState=" + farmerState + ", farmerDistrict="
				+ farmerDistrict + ", farmerVillage=" + farmerVillage + ", farmerEducation=" + farmerEducation + "]";
	}
	
	
	
}
