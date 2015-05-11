package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Mod131Key;

public class Mod131 extends FiscalModel implements Serializable {

	
	private static final long serialVersionUID = 3614782856588153510L;
	
	public boolean hasEpigraph(int idx) {
		return false;
	}

	public String getEpigraph(int idx) {
		return getDescription(Mod131Key.ACTIVITIES[idx][0]);
	}
	public void setEpigraph(int idx,String epigraph) {
		putDescription(Mod131Key.ACTIVITIES[idx][0], epigraph);
	}
	public double getNetYield(int idx) {
		return getAmount(Mod131Key.ACTIVITIES[idx][1]);
	}
	public void setNetYield(int idx,double netYield) {
		putAmount(Mod131Key.ACTIVITIES[idx][1], netYield);
	}
	public double getPercent(int idx) {
		return getAmount(Mod131Key.ACTIVITIES[idx][2]);
	}
	public void setPercent(int idx,double percent) {
		putAmount(Mod131Key.ACTIVITIES[idx][2], percent);
	}
	public double getResult(int idx) {
		return getAmount(Mod131Key.ACTIVITIES[idx][3]);
	}
	public void setResult(int idx,double result) {
		putAmount(Mod131Key.ACTIVITIES[idx][3],result);
	}
	
	public double getResult() {
		return getAmount(Mod131Key.C15);		
	}

}
