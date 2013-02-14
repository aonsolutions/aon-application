package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.entity.master.ContractBonusDB;

@Entity
@Table(name="contract_bonus")
public class ContractBonus extends ContractBonusDB {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public String getFullDescription() {
		return (getBonusConcept() == null || getBonusConcept().getId()==null )?
				getDescription():
					getBonusConcept().getId()+ " - " + (StringUtils.isEmpty(getDescription())?getBonusConcept().getDescription():
					getDescription());
	}
	
}
