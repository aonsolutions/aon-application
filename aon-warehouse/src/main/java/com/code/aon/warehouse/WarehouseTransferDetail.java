package com.code.aon.warehouse;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.WarehouseTransferDetailDB;

@Entity
@Table(name="warehouse_transfer_detail")
public class WarehouseTransferDetail extends WarehouseTransferDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}