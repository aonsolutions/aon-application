package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.AmortizationTypeDB;

@Entity
@Table(name="amortization_type")
public class AmortizationType extends AmortizationTypeDB {

	private static final long serialVersionUID = 1L;
	@Transient
    public int getYears() {
		if (getPercentage() != null && getPercentage()!= 0) {
			return (int) CommonUtil.round( 100 / getPercentage(),0);	
		}
		return 0;
	}

	public void setYears(int years) {
		if (years != 0) {
			setPercentage(CommonUtil.round( 100.0 / years));
		} else {
			setPercentage(0.0);	
		}
	}
}