package com.code.aon.warehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.WarehouseTransferDetailDB;

@Entity
@Table(name="warehouse_transfer_detail")
public class WarehouseTransferDetail extends WarehouseTransferDetailDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}