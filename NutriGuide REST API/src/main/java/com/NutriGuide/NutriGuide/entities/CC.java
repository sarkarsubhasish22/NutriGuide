package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CC {

	@Id
	private long farmerId;
	private float cc;
	public CC(long farmerId, float cc) {
		super();
		this.farmerId = farmerId;
		this.cc = cc;
	}
	public CC() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public float getCc() {
		return cc;
	}
	public void setCc(float cc) {
		this.cc = cc;
	}
	@Override
	public String toString() {
		return "CC [farmerId=" + farmerId + ", cc=" + cc + "]";
	}
	
	
}
