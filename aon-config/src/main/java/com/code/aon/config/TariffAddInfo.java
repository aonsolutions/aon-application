package com.code.aon.config;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TariffAddInfoDB;

@Entity
@Table(name="tariff_addinfo")
@Heritable
public class TariffAddInfo extends TariffAddInfoDB {

	private static final long serialVersionUID = 1L;

	public TariffAddInfo() {
		setValueDate(new Date());
	}

}
