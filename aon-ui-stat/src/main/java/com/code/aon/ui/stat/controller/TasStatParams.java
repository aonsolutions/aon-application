package com.code.aon.ui.stat.controller;


import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Target;
import com.code.aon.common.AonVersion;
import com.code.aon.tas.TasItem;
import com.code.aon.ui.util.AonUtil;

public class TasStatParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static String PERCENT = "%";
	
	private Date fromDate;
	private Date toDate;
	private TasItem tasItem;
	private Target target;

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}
	
	public TasItem getTasItem() {
		return tasItem;
	}
	public void setTasItem(TasItem tasItem) {
		this.tasItem = tasItem;
	}


	public com.code.aon.stat.tas.TasStatParams getStatParams() {
		com.code.aon.stat.tas.TasStatParams p = new com.code.aon.stat.tas.TasStatParams(AonUtil.getDomainName());
		p.setFromDate(getFromDate());
		p.setToDate(getToDate());
		p.setTarget( getTarget() != null?getTarget().getId():null );
		if (getTasItem() != null && getTasItem().getId() != null) {
			p.setTasItem( getTasItem().getId() );	
		} else  if (getTasItem() != null) {
			p.setModel(getTasItem().getModel() != null? getTasItem().getModel().getId() : null);
			p.setPublicCode(StringUtils.isNotBlank(getTasItem().getPublicCode())?PERCENT+getTasItem().getPublicCode()+PERCENT:null);
			p.setPrivateCode(StringUtils.isNotBlank(getTasItem().getPrivateCode())?PERCENT+getTasItem().getPrivateCode()+PERCENT:null);
			p.setDescription(StringUtils.isNotBlank(getTasItem().getDescription())?PERCENT+getTasItem().getDescription()+PERCENT:null);
			p.setAddInfo(StringUtils.isNotBlank(getTasItem().getAddInfo())?PERCENT+getTasItem().getAddInfo()+PERCENT:null);
		}
		return p;
	}
}
