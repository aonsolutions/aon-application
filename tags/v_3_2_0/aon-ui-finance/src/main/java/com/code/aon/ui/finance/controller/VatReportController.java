package com.code.aon.ui.finance.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.accounting.vat.Vat;
import com.code.aon.accounting.vat.VatCollection;
import com.code.aon.accounting.vat.VatCollectionParameters;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.VatType;
import com.code.aon.ui.util.AonUtil;

public class VatReportController implements ICollectionProvider{

	private boolean summaryMode;
	private Date date;
	private Date fromDate;
	private Date toDate;
	private VatType vatType;
	private SecurityLevel securityLevel;
	private DataModel model;
	private List<String> months;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

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

	public VatType getVatType() {
		return vatType;
	}

	public void setVatType(VatType vatType) {
		this.vatType = vatType;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isSummaryMode() {
		return summaryMode;
	}

	public void setSummaryMode(boolean summaryMode) {
		this.summaryMode = summaryMode;
	}

	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<String> getMonths() {
		if (months==null){
			months = new LinkedList<String>();
			months.add( AonUtil.getMessage("aon_january"));
			months.add( AonUtil.getMessage("aon_february"));
			months.add( AonUtil.getMessage("aon_march"));
			months.add( AonUtil.getMessage("aon_april"));
			months.add( AonUtil.getMessage("aon_may"));
			months.add( AonUtil.getMessage("aon_june"));
			months.add( AonUtil.getMessage("aon_july"));
			months.add( AonUtil.getMessage("aon_august"));
			months.add( AonUtil.getMessage("aon_september"));
			months.add( AonUtil.getMessage("aon_october"));
			months.add( AonUtil.getMessage("aon_november"));
			months.add( AonUtil.getMessage("aon_december"));
		}
		return months;
	}
	public void onReset(ActionEvent event) {
		setDate(new Date());
		setFromDate(null);
		setToDate(null);
		setVatType(VatType.OUTPUT);
		setSecurityLevel(null);
		setModel(null);
		setSummaryMode(false);
	}

	public List<Vat> search() {
		try {
			VatCollectionParameters vcp = new VatCollectionParameters();
			vcp.setDate(getDate());
			vcp.setFromDate(getFromDate());
			vcp.setToDate(getToDate());
			vcp.setVatType(getVatType());
			vcp.setSecurityLevel(getSecurityLevel());
			VatCollection vc = new VatCollection();
			return vc.getList(vcp,isSummaryMode());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onSearch(ActionEvent event) {
		setModel(new ListDataModel(search()));
	}

	public String getTitle() {
		if (getVatType() == VatType.OUTPUT) {
			return AonUtil.getMessage("financeBundle","finance_output_vat_report");	
		} else if (getVatType() == VatType.INPUT) {
			return AonUtil.getMessage("financeBundle","finance_input_vat_report");	
		} else if (getVatType() == VatType.INVESTMENT) {
			return AonUtil.getMessage("financeBundle","finance_investment_vat_report");
		}
		return null;	
	}
	
	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection(false);
	}

	@Override
	public Collection<?> getCollection() {
		return isSummaryMode()?search():(List<?>)(getModel().getWrappedData());
	}

}
