package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.FiscalModelDetailDB;

@Entity
@Table(name="fs_model_detail")
public class FiscalModelDetail extends FiscalModelDetailDB {
	
	private static final long serialVersionUID = 1L;

	public void addAccumulatedAmount(double amount) {
		setAccumulatedAmount( CommonUtil.round(getAccumulatedAmount()) + amount);
	}
	public void addDeclaredAmount(double amount) {
		setDeclaredAmount( CommonUtil.round(getDeclaredAmount()) + amount);
	}
	public void addResultAmount(double amount) {
		setResultAmount( CommonUtil.round(getResultAmount()) + amount);
	}
	public void addAdjustAmount(double amount) {
		setAdjustAmount( CommonUtil.round(getAdjustAmount()) + amount);
	}
	public void addAmount(double amount) {
		setAmount( CommonUtil.round(getAmount()) + amount);
	}
	
}
