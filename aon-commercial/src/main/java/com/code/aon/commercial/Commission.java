package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CommissionDB;

@Entity
@Table(name="commission")
public class Commission extends CommissionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
