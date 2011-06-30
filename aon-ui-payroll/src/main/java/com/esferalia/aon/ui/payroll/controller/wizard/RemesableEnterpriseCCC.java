package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.Serializable;

import com.esferalia.aon.payroll.EnterpriseCCC;

public class RemesableEnterpriseCCC implements Serializable {

	
	private boolean selected;
	private EnterpriseCCC enterpriseCCC;

	public EnterpriseCCC getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public void setEnterpriseCCC(EnterpriseCCC enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	
}
