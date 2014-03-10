package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CatalogueDB;

@Entity
@Table(name="catalogue")
@Heritable
public class Catalogue extends CatalogueDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
