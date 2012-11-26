package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CatalogueDB;

@Entity
@Table(name="catalogue")
public class Catalogue extends CatalogueDB {

	private static final long serialVersionUID = 1L;

}
