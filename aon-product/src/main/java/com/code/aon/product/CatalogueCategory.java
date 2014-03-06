package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CatalogueCategoryDB;

@Entity
@Table(name="catalogue_category")
@Heritable
public class CatalogueCategory extends CatalogueCategoryDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
