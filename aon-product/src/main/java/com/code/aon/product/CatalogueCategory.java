package com.code.aon.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CatalogueCategoryDB;

@Entity
@Table(name="catalogue_category")
@Heritable
public class CatalogueCategory extends CatalogueCategoryDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
