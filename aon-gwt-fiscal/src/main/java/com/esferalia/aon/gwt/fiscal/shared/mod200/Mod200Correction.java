package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Mod200Correction implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1L;
	
	private Mod200CorrectionKey key;
	private Double increase;
	private Double decrease;
	
	public Mod200CorrectionKey getKey() {
		return key;
	}
	public void setKey(Mod200CorrectionKey key) {
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
