package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.fiscal.enumeration.Mod347Type;
import com.esferalia.aon.entity.master.Mod347DetailDB;

@Entity
@Table(name="fs_mod347_detail")
public class Mod347Detail extends Mod347DetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean vatAccrualDataInitialized;
	
	@Transient
	public Country getIntracommunityCountry() {
		String c = StringUtils.substring(getOperatorNif(),0,2);
		return StringUtils.isBlank(c)?null:Country.valueOf(c);
	}
	public void setIntracommunityCountry(Country intracommunityCountry) {
		setOperatorNif((intracommunityCountry==null?"":intracommunityCountry.getValue()) + getIntracommunityDocument()); 
	}
	@Transient
	public String getIntracommunityDocument() {
		return StringUtils.substring(getOperatorNif(),2);
	}
	public void setIntracommunityDocument(String intracommunityDocument) {
		setOperatorNif((getIntracommunityCountry()==null?"":getIntracommunityCountry().getValue()) + intracommunityDocument);
	}
	@Transient
	public boolean isVatAccrualDataInitialized() {
		return vatAccrualDataInitialized;
	}
	public void setVatAccrualDataInitialized(boolean vatAccrualDataInitialized) {
		this.vatAccrualDataInitialized = vatAccrualDataInitialized;
	}

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

	@Transient
	public boolean isTypeA() {
		return getType() == Mod347Type.A;
	}
	@Transient
	public boolean isTypeB() {
		return getType() == Mod347Type.B;
	}
	@Transient
	public boolean isTypeC() {
		return getType() == Mod347Type.C;
	}
	@Transient
	public boolean isTypeD() {
		return getType() == Mod347Type.D;
	}
	@Transient
	public boolean isTypeE() {
		return getType() == Mod347Type.E;
	}
	@Transient
	public boolean isTypeF() {
		return getType() == Mod347Type.F;
	}
	@Transient
	public boolean isTypeG() {
		return getType() == Mod347Type.G;
	}
	
}
