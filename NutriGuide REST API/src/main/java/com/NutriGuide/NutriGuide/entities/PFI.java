package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PFI {
	
	@Id
	private long farmerId;
	private float HR_1;
	private float HR_2;
	private float HR_3;
	private float DA;
	private float PFI;
	public PFI(long farmerId, float hR_1, float hR_2, float hR_3, float dA, float pFI) {
		super();
		this.farmerId = farmerId;
		HR_1 = hR_1;
		HR_2 = hR_2;
		HR_3 = hR_3;
		DA = dA;
		PFI = pFI;
	}
	public PFI() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public float getHR_1() {
		return HR_1;
	}
	public void setHR_1(float hR_1) {
		HR_1 = hR_1;
	}
	public float getHR_2() {
		return HR_2;
	}
	public void setHR_2(float hR_2) {
		HR_2 = hR_2;
	}
	public float getHR_3() {
		return HR_3;
	}
	public void setHR_3(float hR_3) {
		HR_3 = hR_3;
	}
	public float getDA() {
		return DA;
	}
	public void setDA(float dA) {
		DA = dA;
	}
	public float getPFI() {
		return PFI;
	}
	public void setPFI(float pFI) {
		PFI = pFI;
	}
	@Override
	public String toString() {
		return "PFI [farmerId=" + farmerId + ", HR_1=" + HR_1 + ", HR_2=" + HR_2 + ", HR_3=" + HR_3 + ", DA=" + DA
				+ ", PFI=" + PFI + "]";
	}
	
	
		
	
}
