package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class CRA implements Serializable{
	
	private Integer code; //ID UNIQUE
	private Date creationDate;
	private byte status;
	private Date date;
	
	private String ccc;
	private Byte cccType;
	private String cccProvince;
	private String activityName;
	
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public Byte getCccType() {
		return cccType;
	}

	public void setCccType(Byte cccType) {
		this.cccType = cccType;
	}

	public String getCccProvince() {
		return cccProvince;
	}

	public void setCccProvince(String cccProvince) {
		this.cccProvince = cccProvince;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
