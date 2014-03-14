package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SystemDeductionDB;
import com.esferalia.aon.salary.enumeration.DeductionType;

@Entity
@Table(name = "system_deduction")
@Heritable
public class SystemDeduction extends SystemDeductionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getFullDescription() {
		return (getDeductionConcept() == null || StringUtils
				.isEmpty(getDeductionConcept().getCode())) ? getDescription()
				: getDeductionConcept().getCode()
						+ " - "
						+ (StringUtils.isEmpty(getDescription()) ? getDeductionConcept()
								.getDescription() : getDescription());
	}
	
	@Transient 
	public String getResolvedDescription(){
		String description = getDescription();
		if ( description != null )
			return description;
		DeductionConcept concept = getDeductionConcept();
		if (concept == null)
			return null;
		return concept.getDescription();
		
	}
	
	@Transient
	public DeductionType getResolvedType() {
		DeductionType type = getDeductionType();
		if (type != null)
			return type;
		DeductionConcept concept = getDeductionConcept();
		if (concept == null)
			return null;
		return concept.getType();

	}

	// TODO
	private DeductionType deductionType;

	@Transient
	public DeductionType getDeductionType() {
		if (this.getType() != null) {
			deductionType = this.getType();
		}
		return deductionType;
	}

	public void setDeductionType(DeductionType deductionType) {
		this.deductionType = deductionType;
		this.setType(deductionType);
	}

}
