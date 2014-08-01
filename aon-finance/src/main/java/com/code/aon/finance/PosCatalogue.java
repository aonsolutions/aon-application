package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.PosCatalogueDB;

@Entity
@Table(name="pos_catalogue")
public class PosCatalogue extends PosCatalogueDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
