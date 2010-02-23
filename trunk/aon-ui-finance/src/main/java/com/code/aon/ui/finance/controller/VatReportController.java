package com.code.aon.ui.finance.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.VatPeriod;
import com.code.aon.finance.enumeration.VatReportOrder;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.enumeration.VatType;
import com.code.aon.finance.vat.Vat;
import com.code.aon.finance.vat.VatCollection;
import com.code.aon.finance.vat.VatCollectionParameters;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.vat.VatReportTypeBreakdown;
import com.code.aon.ui.finance.vat.VatTypeBreakdown;
import com.code.aon.ui.util.AonUtil;

public class VatReportController implements ICollectionProvider, IFinanceMessages {

	private static final Double GENERAL_PERCENT = new Double(16);
	private static final Double REDUCED_PERCENT = new Double(7);
	private static final Double SUPERREDUCED_PERCENT = new Double(4);
	private static final Double EXENT_PERCENT = new Double(0);
	private static final Double OTHER_PERCENT = new Double(-1);
	private static final Double SURCHARGE_GENERAL_PERCENT = new Double(4);
	private static final Double SURCHARGE_REDUCED_PERCENT = new Double(1);
	private static final Double SURCHARGE_SUPERREDUCED_PERCENT = new Double(0.5);

	private Date date;
	private Date fromDate;
	private Date toDate;

	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	
	private String fromSeries;
	private String toSeries;
	private Integer fromNumber;
	private Integer toNumber;

	private VatType vatType;
	private Integer year;
	private VatPeriod vatPeriod;

	private VatReportOrder order;
	private SecurityLevel securityLevel;
	private Map<VatType,VatTypeBreakdown> summary;
	private DataModel model;
	private String title;

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

	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}
	public void setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
	}

	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}
	public void setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
	}

	public String getFromSeries() {
		return fromSeries;
	}
	public void setFromSeries(String fromSeries) {
		this.fromSeries = fromSeries;
	}

	public String getToSeries() {
		return toSeries;
	}
	public void setToSeries(String toSeries) {
		this.toSeries = toSeries;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}
	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}

	public VatReportOrder getOrder() {
		return order;
	}
	public void setOrder(VatReportOrder order) {
		this.order = order;
	}

	public VatType getVatType() {
		return vatType;
	}
	public void setVatType(VatType vatType) {
		this.vatType = vatType;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public VatPeriod getVatPeriod() {
		return vatPeriod;
	}
	public void setVatPeriod(VatPeriod vatPeriod) {
		this.vatPeriod = vatPeriod;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Map<VatType, VatTypeBreakdown> getSummary() {
		return summary;
	}

	public void setSummary(Map<VatType, VatTypeBreakdown> summary) {
		this.summary = summary;
	}

	public void onReset(ActionEvent event) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		setDate(c.getTime());
		setYear(c.get(Calendar.YEAR));
		setVatPeriod( VatPeriod.getQuarterlyVatPeriod( c.get(Calendar.MONTH )) );
		setFromDate(getVatPeriod().getStartDate(getYear()));	
		setToDate(getVatPeriod().getDueDate(getYear()));
		setFromSeries(null);
		setFromNumber(null);
		setToSeries(null);
		setToNumber(null);
		setVatType(VatType.OUTPUT);
		setSecurityLevel(null);
		setSummary(null);
	}

	
	public Map<VatType,VatTypeBreakdown> search() {
		try {
			return getVatTypes(getParameters());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private VatCollectionParameters getParameters() {
		VatCollectionParameters vcp = new VatCollectionParameters();
		vcp.setDate(getDate());
		vcp.setFromDate(getFromDate());
		vcp.setToDate(getToDate());
		
		vcp.setFromInvoiceDate(getToInvoiceDate());
		vcp.setToInvoiceDate(getToInvoiceDate());
		vcp.setFromSeries(getFromSeries());
		vcp.setToSeries(getToSeries());
		vcp.setFromNumber(getFromNumber());
		vcp.setToNumber(getToNumber());
		
		vcp.setVatType(getVatType());
		vcp.setSecurityLevel(getSecurityLevel());
		vcp.setVatPercent(null);
		vcp.setVatType(null);
		vcp.setVatReportType(null);
		return vcp;
	}

	public Map<VatType,VatTypeBreakdown> getVatTypes(VatCollectionParameters params) throws ManagerBeanException {
		VatCollection vc = new VatCollection();
		List<Vat> list = vc.getVatList(params);
		Map<VatType,VatTypeBreakdown> map = new HashMap<VatType, VatTypeBreakdown>();
		map.put(VatType.INPUT,new VatTypeBreakdown(VatType.INPUT));
		map.put(VatType.OUTPUT,new VatTypeBreakdown(VatType.OUTPUT));
		map.put(VatType.INVESTMENT,new VatTypeBreakdown(VatType.INVESTMENT));
		for (Vat vat:list) {
			VatTypeBreakdown vtb = map.get(vat.getVatType());
			if (vtb == null) {
				vtb = new VatTypeBreakdown(vat.getVatType());
				map.put(vat.getVatType(),vtb);
			}
			vtb.addVat(vat);
			expandVatToOtherTypes(map,vat.getVatType(),vat);
		}
		return map;
	}
	

	private void expandVatToOtherTypes(Map<VatType,VatTypeBreakdown> map,VatType vatType,Vat vat) {
		VatType type1;		
		VatType type2;
		if (vatType == VatType.INPUT) {
			type1 = VatType.OUTPUT;
			type2 = VatType.INVESTMENT;
		} else if (vatType == VatType.OUTPUT) {
			type1 = VatType.INPUT;
			type2 = VatType.INVESTMENT;
		} else {
			type1 = VatType.INPUT;
			type2 = VatType.OUTPUT;
		}
		vat.setBase(0);
		vat.setVatQuota(0);
		vat.setSurchargeQuota(0);
		
		VatTypeBreakdown vtb = map.get(type1);
		if (vtb == null) {
			vtb = new VatTypeBreakdown(type1);
			map.put(type1,vtb);
		}
		vtb.addVat(vat);
		
		vtb = map.get(type2);
		if (vtb == null) {
			vtb = new VatTypeBreakdown(type2);
			map.put(type2,vtb);
		}
		vtb.addVat(vat);
	}
	public void onSearch(ActionEvent event) {
		setSummary( search());
	}

	public void setTitle(VatCollectionParameters params) {
		Locale locale = AonUtil.getCurrentLocale();
		StringBuilder buf = new StringBuilder(); 
		if (params.getVatType() == VatType.OUTPUT) {
			buf.append(AonUtil.getMessage(BUNDLE_KEY,"finance_output_vat_report"));
		} else if (params.getVatType() == VatType.INPUT) {
			buf.append(AonUtil.getMessage(BUNDLE_KEY,"finance_input_vat_report"));	
		} else if (params.getVatType() == VatType.INVESTMENT) {
			buf.append(AonUtil.getMessage(BUNDLE_KEY,"finance_investment_vat_report"));
		}
		if (params.getVatReportType() != null) {
			Double percent = params.getVatPercent(); 
			buf.append(" (");
			if (params.getVatReportType() == VatReportType.GENERAL) {
				buf.append(VatReportType.GENERAL.getName(locale));
			} else if (params.getVatReportType() == VatReportType.SURCHARGE) {
				buf.append(VatReportType.SURCHARGE.getName(locale));
				percent = params.getSurchargePercent();
			} else if (params.getVatReportType() == VatReportType.INTRACOMMUNITY) {
				buf.append(VatReportType.INTRACOMMUNITY.getName(locale));	
			} else if (params.getVatReportType() == VatReportType.EXTRACOMMUNITY) {
				buf.append(VatReportType.EXTRACOMMUNITY.getName(locale));	
			}
			buf.append(" ");
			buf.append(percent);
			buf.append("%");
			buf.append(")");
		}
		this.title = buf.toString();
	}

	public String getTitle() {
		return this.title;	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		return (Collection) getModel().getWrappedData();
	}

	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public VatType getOutput() {
		return VatType.OUTPUT;
	}
	public VatType getInput() {
		return VatType.INPUT;
	}
	public VatType getInvestment() {
		return VatType.INVESTMENT;
	}
	public VatReportType getGeneral() {
		return VatReportType.GENERAL;
	}
	public VatReportType getSurcharge() {
		return VatReportType.SURCHARGE;
	}
	public VatReportType getIntracommunity() {
		return VatReportType.INTRACOMMUNITY;
	}
	public VatReportType getExtracommunity() {
		return VatReportType.EXTRACOMMUNITY;
	}
	public Double getGeneralPercent() {
		return GENERAL_PERCENT;
	}
	public Double getReducedPercent() {
		return REDUCED_PERCENT;
	}
	public Double getSuperReducedPercent() {
		return SUPERREDUCED_PERCENT;
	}
	public Double getSurchargeGeneralPercent() {
		return SURCHARGE_GENERAL_PERCENT;
	}
	public Double getSurchargeReducedPercent() {
		return SURCHARGE_REDUCED_PERCENT;
	}
	public Double getSurchargeSuperReducedPercent() {
		return SURCHARGE_SUPERREDUCED_PERCENT;
	}
	public Double getExentPercent() {
		return EXENT_PERCENT;
	}
	public Double getOtherPercent() {
		return OTHER_PERCENT;
	}
	
	public double getOutputGeneralTotalBase() {
		return getGeneralTotalBase(VatType.OUTPUT);	
	}
	public double getInputGeneralTotalBase() {
		return getGeneralTotalBase(VatType.INPUT);	
	}
	public double getInvestmentGeneralTotalBase() {
		return getGeneralTotalBase(VatType.INVESTMENT);	
	}

	private double getGeneralTotalBase(VatType vatType) {
		double b1 = 0;
		VatTypeBreakdown vb = summary.get(vatType);
		if (vb != null) {
			VatReportTypeBreakdown vrb = vb.getMap().get(VatReportType.GENERAL);
			if (vrb != null) {
				b1 = vrb.getBase();
			}
		}
		return b1;
	}
	public double getOutputGeneralTotalQuota() {
		return getGeneralTotalQuota(VatType.OUTPUT);
	}
	public double getInputGeneralTotalQuota() {
		return getGeneralTotalQuota(VatType.INPUT);
	}
	public double getInvestmentGeneralTotalQuota() {
		return getGeneralTotalQuota(VatType.INVESTMENT);
	}
	private double getGeneralTotalQuota(VatType vatType) {
		double q1 = 0;
		double q2 = 0;
		VatTypeBreakdown vb = summary.get(vatType);
		if (vb != null) {
			VatReportTypeBreakdown vrb = vb.getMap().get(VatReportType.GENERAL);
			if (vrb != null) {
				q1 = vrb.getQuota();
			}
			VatReportTypeBreakdown vrb2 = vb.getMap().get(VatReportType.SURCHARGE);
			if (vrb2 != null) {
				q2 = vrb2.getQuota();
			}
		}
		return CommonUtil.round(q1 + q2);
	}

	public double getResult() {
		return CommonUtil.round(getInputGeneralTotalQuota() + getInvestmentGeneralTotalQuota() - getOutputGeneralTotalQuota());
	}

	public void onDetail(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			VatCollectionParameters vcp = getParameters();
			String vatType = params.get("vatType");
			if ( !StringUtils.isEmpty(vatType)) {
				vcp.setVatType(VatType.valueOf(vatType));
			}
			String vatReportType = params.get("vatReportType");
			if ( !StringUtils.isEmpty(vatReportType)) {
				vcp.setVatReportType(VatReportType.valueOf(vatReportType));
			}
			String vatPercent = params.get("vatPercent");
			if ( !StringUtils.isEmpty(vatPercent)) {
				VatReportType vrt  = VatReportType.valueOf(vatReportType);
				if (vrt == VatReportType.SURCHARGE) {
					vcp.setSurchargePercent(Double.parseDouble(vatPercent));
				} else {
					vcp.setVatPercent(Double.parseDouble(vatPercent));	
				}
			}
			setTitle(vcp);
			VatCollection vc = new VatCollection();
			List<Vat> list = vc.getVatDetailList(vcp,getOrder());
			setModel(new ListDataModel(list));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public void onYearChanged(ValueChangeEvent event) {
		setFromDate(null);
		setToDate(null);
		if (event.getNewValue() != null) {
			Integer year = (Integer) event.getNewValue(); 
			setFromDate(getVatPeriod().getStartDate(year));	
			setToDate(getVatPeriod().getDueDate(year));
		}
	}
	
	public void onVatPeriodChanged(ValueChangeEvent event) {
		setFromDate(null);
		setToDate(null);
		if (getYear() != null) {
			VatPeriod vp = (VatPeriod) event.getNewValue(); 
			setFromDate(vp.getStartDate(getYear()));	
			setToDate(vp.getDueDate(getYear()));
		} else {
			String msg = "El Periodo IVA es necesario para calcular las fecha de incio y fin del periodo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
}
