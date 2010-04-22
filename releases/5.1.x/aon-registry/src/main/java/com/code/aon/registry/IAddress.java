package com.code.aon.registry;

import com.code.aon.geozone.GeoZone;

public interface IAddress {
	
	public String getAddress();
	
	public String getAddress2();
	
	public String getZip();
	
	public String getCity();
	
	public GeoZone getGeozone();

}