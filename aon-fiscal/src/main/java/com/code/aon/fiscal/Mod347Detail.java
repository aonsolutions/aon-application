package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.fiscal.enumeration.Mod347Type;
import com.code.aon.registry.RegistryDocument;
import com.esferalia.aon.entity.master.Mod347DetailDB;

@Entity
@Table(name="fs_mod347_detail")
public class Mod347Detail extends Mod347DetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean vatAccrualDataInitialized;
	private boolean pendingVatAccrual;
	private double previousAmount;
	
	@Transient
	public Country getIntracommunityCountry() {
		String c = StringUtils.substring(getOperatorNif(),0,2);
		return StringUtils.isBlank(c)?null:Country.valueOf(c);
	}
	public void setIntracommunityCountry(Country intracommunityCountry) {
		setOperatorNif((intracommunityCountry==null?"  ":intracommunityCountry.getValue()) + getIntracommunityDocument()); 
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
	public boolean isPendingVatAccrual() {
		return pendingVatAccrual;
	}
	public void setPendingVatAccrual(boolean pendingVatAccrual) {
		this.pendingVatAccrual = pendingVatAccrual;
	}
	@Transient
	public double getPreviousAmount() {
		return previousAmount;
	}
	public void setPreviousAmount(double previousAmount) {
		this.previousAmount = previousAmount;
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
	@Transient
	public boolean isCashDisabled() {
		return (isTypeA() || isTypeD() || isTypeE() || isTypeG());
	}
	@Transient
	public boolean isDocumentValid() {
		if (getCountry() == Country.ES) {
			RegistryDocument rd = new RegistryDocument(getDocument());
			return rd.isValid();
		} else {
			Country country = getIntracommunityCountry();
			String doc = getIntracommunityDocument();
			int len = StringUtils.length(doc); 
			return (   
					(( Country.AT == country) && StringUtils.isAlphanumeric(doc) && len==9 )
				||  (( Country.BE == country) && StringUtils.isNumeric(doc) 	 && (len==9 || len==10 )) 
				||  (( Country.BG == country) && StringUtils.isNumeric(doc) 	 && (len==9 || len==10)) 
				||  (( Country.CY == country) && StringUtils.isAlphanumeric(doc) && len==9 ) 
				||  (( Country.CZ == country) && StringUtils.isNumeric(doc) 	 && (len==8 || len == 9 || len == 10 )) 
				||  (( Country.DE == country) && StringUtils.isNumeric(doc) 	 && len==9 ) 
				||  (( Country.DK == country) && StringUtils.isNumeric(doc) 	 && len==8 )
				||  (( Country.EE == country) && StringUtils.isNumeric(doc) 	 && len==9 )
				||  (( Country.GR == country) && StringUtils.isNumeric(doc) 	 && len==9 )
				||  (( Country.FI == country) && StringUtils.isNumeric(doc) 	 && len==8 )
				||  (( Country.FR == country) && StringUtils.isAlphanumeric(doc) && len==11 )
				||  (( Country.GB == country) && StringUtils.isAlphanumeric(doc) && (len==5 || len == 9 || len == 12) ) 
				||  (( Country.HR == country) && StringUtils.isNumeric(doc) 	 && len==11 ) 
				||  (( Country.HU == country) && StringUtils.isNumeric(doc) 	 && len==8 )
				||  (( Country.IE == country) && StringUtils.isAlphanumeric(doc) && (len==8 || len==9) )
				||  (( Country.IT == country) && StringUtils.isNumeric(doc) 	 && len==11 )
				||  (( Country.LT == country) && StringUtils.isNumeric(doc) 	 && (len==9 || len == 12) )
				||  (( Country.LU == country) && StringUtils.isNumeric(doc) 	 && len==8 )
				||  (( Country.LV == country) && StringUtils.isNumeric(doc) 	 && len==11 ) 
				||  (( Country.MT == country) && StringUtils.isNumeric(doc) 	 && len==8 ) 
				||  (( Country.NL == country) && StringUtils.isAlphanumeric(doc) && len==12 ) 
				||  (( Country.PL == country) && StringUtils.isNumeric(doc) 	 && len==10 ) 
				||  (( Country.PT == country) && StringUtils.isNumeric(doc) 	 && len==9 ) 
				||  (( Country.RO == country) && StringUtils.isNumeric(doc) 	 && (len>=2 && len<=10)) 
				||  (( Country.SE == country) && StringUtils.isNumeric(doc) 	 && len==12 )
				||  (( Country.SI == country) && StringUtils.isNumeric(doc) 	 && len==8 ) 
				||  (( Country.SK == country) && StringUtils.isNumeric(doc) 	 && (len==9 || len == 10) ) 
				 );
		}
		
	}
	
	
}
