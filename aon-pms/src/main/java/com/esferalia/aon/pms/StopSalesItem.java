package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.StopSalesItemDB;

@Entity
@Table(name="stop_sales_item")
public class StopSalesItem extends StopSalesItemDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
