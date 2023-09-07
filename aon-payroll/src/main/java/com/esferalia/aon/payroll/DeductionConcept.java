package com.esferalia.aon.payroll;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.DeductionConceptDB;

@Entity
@Table(name="deduction_concept")
@Heritable
public class DeductionConcept extends DeductionConceptDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
