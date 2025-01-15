package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SFT {

	@Id
	private long farmerId;
	private float farmerAge;
	private float biceps;
	private float triceps;
	private float subscapular;
	private float suprailiac;
	private float bodyDensity;
	private float percentBodyFat;
	private float fatMass;
	private float fatFreeMass;
	public SFT(long farmerId, float farmerAge, float biceps, float triceps, float subscapular, float suprailiac, float bodyDensity,
			float percentBodyFat, float fatMass, float fatFreeMass) {
		super();
		this.farmerId = farmerId;
		this.farmerAge = farmerAge;
		this.biceps = biceps;
		this.triceps = triceps;
		this.subscapular = subscapular;
		this.suprailiac = suprailiac;
		this.bodyDensity = bodyDensity;
		this.percentBodyFat = percentBodyFat;
		this.fatMass = fatMass;
		this.fatFreeMass = fatFreeMass;
	}
	public SFT() {
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
	public float getBiceps() {
		return biceps;
	}
	public void setBiceps(float biceps) {
		this.biceps = biceps;
	}
	public float getTriceps() {
		return triceps;
	}
	public void setTriceps(float triceps) {
		this.triceps = triceps;
	}
	public float getSubscapular() {
		return subscapular;
	}
	public void setSubscapular(float subscapular) {
		this.subscapular = subscapular;
	}
	public float getSuprailiac() {
		return suprailiac;
	}
	public void setSuprailiac(float suprailiac) {
		this.suprailiac = suprailiac;
	}
	public float getBodyDensity() {
		return bodyDensity;
	}
	public void setBodyDensity(float bodyDensity) {
		this.bodyDensity = bodyDensity;
	}
	public float getPercentBodyFat() {
		return percentBodyFat;
	}
	public void setPercentBodyFat(float percentBodyFat) {
		this.percentBodyFat = percentBodyFat;
	}
	public float getFatMass() {
		return fatMass;
	}
	public void setFatMass(float fatMass) {
		this.fatMass = fatMass;
	}
	public float getFatFreeMass() {
		return fatFreeMass;
	}
	public void setFatFreeMass(float fatFreeMass) {
		this.fatFreeMass = fatFreeMass;
	}
	@Override
	public String toString() {
		return "SFT [farmerId=" + farmerId + ",farmerAge=" + farmerAge + ", biceps=" + biceps + ", triceps=" + triceps + ", subscapular="
				+ subscapular + ", suprailiac=" + suprailiac + ", bodyDensity=" + bodyDensity + ", percentBodyFat="
				+ percentBodyFat + ", fatMass=" + fatMass + ", fatFreeMass=" + fatFreeMass + "]";
	}
	
	
}
