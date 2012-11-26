package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.BonusConceptDB;

@Entity
@Table(name="bonus_concept")
public class BonusConcept extends BonusConceptDB {
	
	private static final long serialVersionUID = 1L;

}
