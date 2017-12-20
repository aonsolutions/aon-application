package com.code.aon.registry;

import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.enumeration.StreetType;

public interface IAddress {
	
	public StreetType getStreetType();
	public void setStreetType(StreetType streetType);
	
	public String getAddress();
	public void setAddress(String address);
	
	public String getNumber();
	public void setNumber(String number);

	public String getAddress2();
	public void setAddress2(String address2);
	
	public String getAddress3();
	public void setAddress3(String address3);
	
	public String getZip();
	public void setZip(String zip);
	
	public String getCity();
	public void setCity(String city);
	
	public String getProvince();
	public void setProvince(String province);
	
	public GeoZone getGeozone();
	public void setGeozone(GeoZone geoZone);

	public String getAlias();
	public void setAlias(String alias);
	
    public String getFullAddress();

    public String getShortAddress();

    public String getLocation();

}