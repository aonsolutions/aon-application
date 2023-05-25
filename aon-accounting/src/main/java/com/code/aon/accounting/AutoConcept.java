package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AutoConceptDB;

@Entity
@Table(name="auto_concept")
@Heritable
public class AutoConcept extends AutoConceptDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}