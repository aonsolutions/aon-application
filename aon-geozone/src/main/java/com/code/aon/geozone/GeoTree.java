package com.code.aon.geozone;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.GeoTreeDB;

@Entity
@Table(name="geotree")
@Heritable
public class GeoTree extends GeoTreeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}