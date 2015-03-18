package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum FiscalModel implements Serializable{
	
	M111("111"), 
	M115("115"), 
	M123("123"), 
	M130("130"),
	M131("131"),
	M303_RG("303 R.G."),
	M303_RS("303 R.S."),
	M340("340"),
	M347("347"),
	M349("349"),
	M390("390"),
	M390_HF("390 H.F."),
	M180("180"),
	M190("190"),
	M310("310"),
	M311("311"),
	M200("200")
	;

	private String name;
	
	private FiscalModel(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}