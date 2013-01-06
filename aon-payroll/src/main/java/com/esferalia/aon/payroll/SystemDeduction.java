package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SystemDeductionDB;

@Entity
@Table(name="system_deduction")
@Heritable
public class SystemDeduction extends SystemDeductionDB {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public String getFullDescription() {
		return (getDeductionConcept() == null || StringUtils.isEmpty(getDeductionConcept().getCode()))?
				getDescription():
				getDeductionConcept().getCode()+ " - " + (StringUtils.isEmpty(getDescription())?getDeductionConcept().getDescription():
					getDescription());
	}

}
