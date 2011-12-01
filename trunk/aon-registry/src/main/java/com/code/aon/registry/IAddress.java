package com.code.aon.registry;

import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.enumeration.StreetType;

public interface IAddress {
	
	public StreetType getStreetType();
	
	public String getAddress();
	
	public String getNumber();

	public String getAddress2();
	
	public String getAddress3();
	
	public String getZip();
	
	public String getCity();
	
	public GeoZone getGeozone();

    public String getFullAddress();

    public String getShortAddress();

}