package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.TariffCatalogueDB;

@Entity
@Table(name="tariff_catalogue")
public class TariffCatalogue extends TariffCatalogueDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
