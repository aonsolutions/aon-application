package com.code.aon.config;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TariffAddInfoDB;

@Entity
@Table(name="tariff_addinfo")
@Heritable
public class TariffAddInfo extends TariffAddInfoDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public TariffAddInfo() {
		setValueDate(new Date());
	}

}
