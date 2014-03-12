package com.code.aon.commercial;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.CommercialTermDB;

@Entity
@Table(name="commercial_term")
public class CommercialTerm extends CommercialTermDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}