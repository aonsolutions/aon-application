package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.BonusConceptDB;

@Entity
@Table(name="bonus_concept")
@Heritable
public class BonusConcept extends BonusConceptDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
