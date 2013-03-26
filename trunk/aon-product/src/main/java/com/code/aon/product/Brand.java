package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.BrandDB;

@Entity
@Table(name="brand")
@Heritable
public class Brand extends BrandDB {

	private static final long serialVersionUID = 1L;

}