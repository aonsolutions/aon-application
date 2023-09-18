package com.code.aon.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ProductCategoryDB;

@Entity
@Table(name="pcategory")
@Heritable
public final class ProductCategory extends ProductCategoryDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}