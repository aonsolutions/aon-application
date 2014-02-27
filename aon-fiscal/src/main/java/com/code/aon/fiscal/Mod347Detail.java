package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.Mod347DetailDB;

@Entity
@Table(name="fs_mod347_detail")
public class Mod347Detail extends Mod347DetailDB {
	
	private static final long serialVersionUID = 1L;
	
	@Transient
	public String getAddress() {
    	StringBuffer buf = new StringBuffer();
    	buf.append(getAssetStreetType());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreet());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetNumberType());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetNumber());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetNumberSuffix());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetBlock());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetHall());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetStair());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetFloor());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetDoor());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetComplement());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetCity());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetTown());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetProvince());
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(getAssetStreetZip());
    	return buf.toString();
	}
	
}
