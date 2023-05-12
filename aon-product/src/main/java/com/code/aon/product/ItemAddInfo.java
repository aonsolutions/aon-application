package com.code.aon.product;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ItemAddInfoDB;

@Entity
@Table(name="item_addinfo")
@Heritable
public class ItemAddInfo extends ItemAddInfoDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ItemAddInfo() {
		setValueDate(new Date());
	}

}
