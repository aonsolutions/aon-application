package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;

public class Mod2002014Correction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Mod2002014CorrectionKey key;
	private Double increase;
	private Double decrease;
	
	public Mod2002014CorrectionKey getKey() {
		return key;
	}
	public void setKey(Mod2002014CorrectionKey key) {
		this.key = key;
	}
	public Double getIncrease() {
		return increase;
	}
	public void setIncrease(Double increase) {
		this.increase = increase;
	}
	public Double getDecrease() {
		return decrease;
	}
	public void setDecrease(Double decrease) {
		this.decrease = decrease;
	}
	
}
