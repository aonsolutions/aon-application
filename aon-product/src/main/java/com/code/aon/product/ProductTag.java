package com.code.aon.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProductTagDB;

@Entity
@Table(name="product_tag")
public class ProductTag extends ProductTagDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
