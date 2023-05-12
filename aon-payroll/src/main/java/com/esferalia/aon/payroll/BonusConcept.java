package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.BonusConceptDB;

@Entity
@Table(name="bonus_concept")
@Heritable
public class BonusConcept extends BonusConceptDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
