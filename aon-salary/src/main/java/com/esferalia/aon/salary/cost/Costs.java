package com.esferalia.aon.salary.cost;

import com.code.aon.AonVersion;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class Costs extends Deductions {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IDeduction atepIt;
	private IDeduction atepIms;
	
	public Costs() {
		super();
	}
	
	
	public IDeduction getAtepIt() {
		return atepIt;
	}
	public void setAtepIt(IDeduction atepIt) {
		this.atepIt = atepIt;
	}

	public IDeduction getAtepIms() {
		return atepIms;
	}
	public void setAtepIms(IDeduction atepIms) {
		this.atepIms = atepIms;
	}
	
	public IDeduction getFogasa() {
		return get(DeductionType.FOGASA);
	}
	public void setFogasa(IDeduction fogasa) {
		put(DeductionType.FOGASA, fogasa);
	}
	
}
