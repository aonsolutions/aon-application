package com.code.aon.geozone;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.GeoTreeDB;

@Entity
@Table(name="geotree")
@Heritable
public class GeoTree extends GeoTreeDB {

	private static final long serialVersionUID = 1L;

}