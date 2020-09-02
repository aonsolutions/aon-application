package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.view.client.ProvidesKey;

public class CCCInfo implements Serializable{
	
	private String ccc;
	private String cccRegimeCode;
	private String cccAccount;
	private Byte type;
	private String typeStr;
	private String geozone;
	private String geozoneCode;
	private Integer activityId;
	private String activityDescription;
	private Integer cccId;
	private Boolean useByContracts;
	private String enterpriseDesciption;
	private Integer enterpriseId;
	private List<Date> CRADates;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<CCCInfo> KEY_PROVIDER = new ProvidesKey<CCCInfo>() {
      @Override
      public Object getKey(CCCInfo item) {
        return item == null ? null : item.getCccId();
      }
    };
	
	public CCCInfo(){
		super();
	}
	
	public CCCInfo(String ccc, Byte type, String geozone, String geozoneCode, Integer activityId, Integer cccId) {
		super();
		this.ccc = ccc;
		this.type = type;
		this.geozone = geozone;
		this.geozoneCode = geozoneCode;
		this.activityId = activityId;
		this.cccId = cccId;
	}
	
	public CCCInfo(String ccc, String cccRegime, String cccAccount, Byte type, String geozone, String geozoneCode, Integer activityId, Integer cccId, Boolean useByContracts) {
		super();
		this.ccc = ccc;
		this.cccRegimeCode = cccRegime;
		this.cccAccount = cccAccount;
		this.type = type;
		this.geozone = geozone;
		this.geozoneCode = geozoneCode;
		this.activityId = activityId;
		this.cccId = cccId;
		this.useByContracts = useByContracts;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public String getCccRegimeCode() {
		return cccRegimeCode;
	}

	public void setCccRegimeCode(String cccRegimeCode) {
		this.cccRegimeCode = cccRegimeCode;
	}

	public String getCccAccount() {
		return cccAccount;
	}

	public void setCccAccount(String cccAccount) {
		this.cccAccount = cccAccount;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getGeozone() {
		return geozone;
	}

	public void setGeozone(String geozone) {
		this.geozone = geozone;
	}

	public String getGeozoneCode() {
		return geozoneCode;
	}

	public void setGeozoneCode(String geozoneCode) {
		this.geozoneCode = geozoneCode;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}
	
	public Integer getCCCId() {
		return cccId;
	}

	public void setCCCId(Integer cccId) {
		this.cccId = cccId;
	}

	public Integer getCccId() {
		return cccId;
	}

	public void setCccId(Integer cccId) {
		this.cccId = cccId;
	}

	public Boolean isUseByContracts() {
		return useByContracts;
	}

	public void setUseByContracts(Boolean useByContracts) {
		this.useByContracts = useByContracts;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	public String getEnterpriseDesciption() {
		return enterpriseDesciption;
	}

	public void setEnterpriseDesciption(String enterpriseDesciption) {
		this.enterpriseDesciption = enterpriseDesciption;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
	public List<Date> getCRADates() {
		return this.CRADates;
	}
	
	public void setCRADates(List<Date> craDatesList) {
		this.CRADates = craDatesList;
	}
	
	@Override
	public String toString() {
		return enterpriseDesciption + " (" + activityDescription + " -> " + cccAccount + ")";
	}
	
}