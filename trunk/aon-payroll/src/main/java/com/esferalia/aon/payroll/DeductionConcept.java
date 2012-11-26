package com.esferalia.aon.payroll;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.DeductionConceptDB;

@Entity
@Table(name="deduction_concept")
public class DeductionConcept extends DeductionConceptDB {
	
	private static final long serialVersionUID = 1L;

}
