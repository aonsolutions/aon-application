package com.code.aon.commercial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CommissionItemDB;

@Entity
@Table(name="commission_item")
public class CommissionItem extends CommissionItemDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
