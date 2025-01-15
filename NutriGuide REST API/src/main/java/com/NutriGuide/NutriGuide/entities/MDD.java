package com.NutriGuide.NutriGuide.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MDD {

	@Id
	private long farmerId;
	private int mDD;
	public MDD(long farmerId, int mDD) {
		super();
		this.farmerId = farmerId;
		this.mDD = mDD;
	}
	public MDD() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(long farmerId) {
		this.farmerId = farmerId;
	}
	public int getMdd() {
		return mDD;
	}
	public void setMdd(int mDD) {
		this.mDD = mDD;
	}
	@Override
	public String toString() {
		return "MDD [farmerId=" + farmerId + ", mDD=" + mDD + "]";
	}
	
	
	
}
