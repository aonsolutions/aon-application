package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.StopSalesTariffDB;

@Entity
@Table(name="stop_sales_tariff")
public class StopSalesTariff extends StopSalesTariffDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
