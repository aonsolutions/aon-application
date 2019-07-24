package com.code.aon.ui.commercial.controller;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.AuditableSearchController;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class OfferExportGwtController extends AuditableSearchController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	//-------------------- OFFER FILTER
	private static OfferStatus[] statuses;
	private static String serie;
	
	private static Integer target;
	private static Integer seller;
	private static Integer supplier;
	private static Integer project;
	
	private static Date fromDate;
	private static Date toDate;
	private static String fromDateStr; 
	private static String toDateStr;
	
	private static Integer fromNumber;
	private static Integer toNumber;
	
	private static Integer type;
	private static Integer workplace;
	private static Integer scope;
	
	private Boolean signed;
	private Boolean confidential;
	
	private static String  statusesStr;
	
	public static OfferExportGwtController getInstance() { 
		return new OfferExportGwtController();
	}
	
	public String getInitialize(){
		return "";
	}

	public OfferExportGwtController() {
	}

	public OfferStatus[] getStatuses() {
		return statuses;
	}
	public static void setStatuses(com.code.aon.commercial.enumeration.OfferStatus[] statuses) {
		
		OfferExportGwtController.statuses = new OfferStatus[statuses.length];
		String s = "";
		for (Integer i=0; i<statuses.length; i++){
			s = s+"-"+statuses[i].ordinal();
			OfferExportGwtController.statuses[i] = OfferStatus.values()[statuses[i].ordinal()];
		}
		OfferExportGwtController.statusesStr= s;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		OfferExportGwtController.serie = serie;
	}

	public Integer getTarget() {
		return target;
	}

	public static  void setTarget(Integer target) {
		OfferExportGwtController.target = target;
	}

	public Integer getSeller() {
		return seller;
	}

	public static  void setSeller(Integer seller) {
		OfferExportGwtController.seller = seller;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		setFromDateStr(AonDateUtils.simpleFormat(fromDate));
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		setToDateStr(AonDateUtils.simpleFormat(toDate));
	}

	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		OfferExportGwtController.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		OfferExportGwtController.toNumber = toNumber;
	}

	public Integer getType() {
		return type;
	}

	public static  void setType(Integer type) {
		OfferExportGwtController.type = type;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Integer workplace) {
		OfferExportGwtController.workplace = workplace;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		OfferExportGwtController.scope = scope;
	}

	public Boolean getSigned() {
		return signed;
	}

	public void setSigned(Boolean signed) {
		this.signed = signed;
	}

	public Boolean getConfidential() {
		return confidential;
	}

	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}

	public String getStatusesStr() {
		return statusesStr;
	}

	public void setStatusesStr(String statusesStr) {
		OfferExportGwtController.statusesStr = statusesStr;
	}

	public Integer getProject() {
		return project;
	}

	public static  void setProject(Integer project) {
		OfferExportGwtController.project = project;
	}

	public Integer getSupplier() {
		return supplier;
	}

	public static  void setSupplier(Integer supplier) {
		OfferExportGwtController.supplier = supplier;
	}

	public String getFromDateStr() {
		return fromDateStr;
	}

	public void setFromDateStr(String fromDateStr) {
		OfferExportGwtController.fromDateStr = fromDateStr;
	}

	public String getToDateStr() {
		return toDateStr;
	}

	public void setToDateStr(String toDateStr) {
		OfferExportGwtController.toDateStr = toDateStr;
	}
	
}