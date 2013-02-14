package com.code.aon.geozone;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.GeoZoneDB;

@Entity
@Table(name="geozone")
@Heritable
public class GeoZone extends GeoZoneDB {

	private static final long serialVersionUID = 1L;
	
}