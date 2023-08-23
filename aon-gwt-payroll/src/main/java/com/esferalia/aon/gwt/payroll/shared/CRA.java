package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class CRA implements Serializable{
	
	private Integer code; //ID UNIQUE
	private Integer domain;
	private String creationDate;
	private byte status;
	private String date;
	
	private Boolean isConsignment;
	private List<CCCInfo> includeCCCs;
	
	private String type;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<CRA> KEY_PROVIDER = new ProvidesKey<CRA>() {
      @Override
      public Object getKey(CRA item) {
        return item == null ? null : item.getCode();
      }
    };
	
	public CRA() {
		super();
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Date getCreationDate() {
		return parse(creationDate);
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = Shared.format(creationDate);
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return parse(date);
	}

	public void setDate(Date date) {
		this.date = Shared.format(date);
	}

	public Boolean getIsConsignment() {
		return isConsignment;
	}

	public void setIsConsignment(Boolean isConsignment) {
		this.isConsignment = isConsignment;
	}

	public List<CCCInfo> getIncludeCCCs() {
		return includeCCCs;
	}

	public void setIncludeCCCs(List<CCCInfo> includeCCCs) {
		this.includeCCCs = includeCCCs;
	}

	public String getActivityName() {
		if(getIncludeCCCs().isEmpty())
			return "EMPTY CCCS";
		
		if(getIncludeCCCs().size() > 1)
			return "REMESA";
		
		return getIncludeCCCs().get(0).getActivityDescription();
	}

	public String getCccProvince() {
		if(getIncludeCCCs().isEmpty())
			return "EMPTY CCCS";
		
		if(getIncludeCCCs().size() > 1)
			return "-";
		
		return ProvinceContract.getName(getIncludeCCCs().get(0).getGeozoneCode());
	}

	public Byte getCccType() {
		if(getIncludeCCCs().isEmpty())
			return (byte) -1;
		
		if(getIncludeCCCs().size() > 1)
			return (byte) -1;
		
		return getIncludeCCCs().get(0).getType();
	}

	public String getCcc() {
		if(getIncludeCCCs().isEmpty())
			return "EMPTY CCCS";
		
		if(getIncludeCCCs().size() > 1)
			return "REMESA";
		
		return getIncludeCCCs().get(0).getCccAccount();
	}

}
