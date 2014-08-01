package com.code.aon.warehouse;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.WarehouseDB;

@Entity
@Table(name="warehouse")
public class Warehouse extends WarehouseDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}