package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.Mod347DetailDB;

@Entity
@Table(name="fs_mod347_detail")
public class Mod347Detail extends Mod347DetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public String getAddress() {
    	StringBuffer buf = new StringBuffer();
    	buf.append(StringUtils.isNotBlank(getAssetStreetType())?getAssetStreetType():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreet())?getAssetStreet():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetNumberType())?getAssetStreetNumberType():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetNumber())?getAssetStreetNumber():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetNumberSuffix())?getAssetStreetNumberSuffix():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetBlock())?getAssetStreetBlock():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetHall())?getAssetStreetHall():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetStair())?getAssetStreetStair():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetFloor())?getAssetStreetFloor():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetDoor())?getAssetStreetDoor():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetComplement())?getAssetStreetComplement():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetCity())?getAssetStreetCity():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetTown())?getAssetStreetTown():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetProvince())?getAssetStreetProvince():"");
    	buf.append(buf.length() > 0 ? " " : "");
    	buf.append(StringUtils.isNotBlank(getAssetStreetZip())?getAssetStreetZip():"");
    	return buf.toString();
	}
	
}
