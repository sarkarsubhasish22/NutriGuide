package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MUAC {

	@Id
	private long farmerId;
	private float muac;
	public MUAC(long farmerId, float muac) {
		super();
		this.farmerId = farmerId;
		this.muac = muac;
	}
	
	public MUAC() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public float getMuac() {
		return muac;
	}
	public void setMuac(float muac) {
		this.muac = muac;
	}
	
	@Override
	public String toString() {
		return "MUAC [farmerId=" + farmerId + ", muac=" + muac + "]";
	}
	
	
	
	
		
	
}
