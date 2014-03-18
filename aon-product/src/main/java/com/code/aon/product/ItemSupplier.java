package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ItemSupplierDB;

@Entity
@Table(name="item_supplier", uniqueConstraints = @UniqueConstraint(columnNames={"item", "supplier"}))
public class ItemSupplier extends ItemSupplierDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}