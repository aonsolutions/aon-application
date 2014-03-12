package com.code.aon.ui.fiscal.controller;


import static com.code.aon.ui.common.ICommonMessages.FINANCE_INPUT_VAT_REPORT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVESTMENT_VAT_REPORT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_OUTPUT_VAT_REPORT;

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

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatReportType;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.fiscal.vat.Vat;
import com.code.aon.fiscal.vat.VatCollection;
import com.code.aon.fiscal.vat.VatCollectionParameters;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.vat.VatReportTypeBreakdown;
import com.code.aon.ui.fiscal.vat.VatTypeBreakdown;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;

public class VatReportController extends DataScrollerState implements ICollectionProvider {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	private Period period;
	private com.code.aon.accounting.Period accountPeriod;

	private boolean coverVisible;
	private boolean counterVisible;
	private int pageCounter;
	
	private InvoiceReportOrder order;
	private SecurityLevel securityLevel;
	private Map<VatType,VatTypeBreakdown> summary;
	private String title;

	private String invoiceViewer;

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

	public InvoiceReportOrder getOrder() {
		return order;
	}
	public void setOrder(InvoiceReportOrder order) {
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

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		if (this.period != period) {
			setAccountPeriod(null);
		}
		this.period = period;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isCoverVisible() {
		return coverVisible;
	}
	public void setCoverVisible(boolean coverVisible) {
		this.coverVisible = coverVisible;
	}

	public boolean isCounterVisible() {
		return counterVisible;
	}
	public void setCounterVisible(boolean counterVisible) {
		this.counterVisible = counterVisible;
	}

	public int getPageCounter() {
		return pageCounter;
	}
	public void setPageCounter(int pageCounter) {
		this.pageCounter = pageCounter;
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
		setPeriod( Period.getQuarterlyPeriod( c.get(Calendar.MONTH )) );
		setFromDate(getPeriod().getStartDate(getYear()));	
		setToDate(getPeriod().getDueDate(getYear()));
		setFromInvoiceDate(null);
		setToInvoiceDate(null);
		setFromSeries(null);
		setFromNumber(null);
		setToSeries(null);
		setToNumber(null);
		setVatType(VatType.OUTPUT);
		setCounterVisible(false);
		setCoverVisible(false);
		setPageCounter(0);
		setSecurityLevel(AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL);
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
		String domain = AonUtil.getAuthPrincipal().getDomain();
		int domainId = DomainManager.getCurrentDomain();
		VatCollectionParameters vcp = new VatCollectionParameters(domainId,domain);
		vcp.setDate(getDate());
		vcp.setFromDate(getFromDate());
		vcp.setToDate(getToDate());
		
		vcp.setFromInvoiceDate(getFromInvoiceDate());
		vcp.setToInvoiceDate(getToInvoiceDate());
		vcp.setFromSeries(StringUtils.isBlank(getFromSeries())?null:getFromSeries());
		vcp.setToSeries(StringUtils.isBlank(getToSeries())?null:getToSeries());
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
			buf.append(AonUtil.getMessage(FINANCE_OUTPUT_VAT_REPORT));
		} else if (params.getVatType() == VatType.INPUT) {
			buf.append(AonUtil.getMessage(FINANCE_INPUT_VAT_REPORT));	
		} else if (params.getVatType() == VatType.INVESTMENT) {
			buf.append(AonUtil.getMessage(FINANCE_INVESTMENT_VAT_REPORT));
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
			} else if (params.getVatReportType() == VatReportType.OTHER_ISP) {
				buf.append(VatReportType.OTHER_ISP.getName(locale));	
			} else if (params.getVatReportType() == VatReportType.CAN_CEU_MEL) {
				buf.append(VatReportType.CAN_CEU_MEL.getName(locale));	
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
	
	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		return (Collection<?>) getModel().getWrappedData();
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
	public VatReportType getCanCeuMel() {
		return VatReportType.CAN_CEU_MEL;
	}
	public VatReportType getOtherISP() {
		return VatReportType.OTHER_ISP;
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
	private double getExtracommunitaryInputQuota() {
		double q1 = 0;
		double q2 = 0;
		VatTypeBreakdown vb = summary.get(VatType.INPUT);
		if (vb != null) {
			VatReportTypeBreakdown vrb = vb.getMap().get(VatReportType.EXTRACOMMUNITY);
			if (vrb != null) {
				q1 = vrb.getQuota();
			}
		}
		vb = summary.get(VatType.INVESTMENT);
		VatReportTypeBreakdown vrb2 = vb.getMap().get(VatReportType.EXTRACOMMUNITY);
		if (vrb2 != null) {
			q2 = vrb2.getQuota();
		}
		return CommonUtil.round(q1 + q2);
	}
	private double getCanCeuMelInputQuota() {
		double q1 = 0;
		double q2 = 0;
		VatTypeBreakdown vb = summary.get(VatType.INPUT);
		if (vb != null) {
			VatReportTypeBreakdown vrb = vb.getMap().get(VatReportType.CAN_CEU_MEL);
			if (vrb != null) {
				q1 = vrb.getQuota();
			}
		}
		vb = summary.get(VatType.INVESTMENT);
		VatReportTypeBreakdown vrb2 = vb.getMap().get(VatReportType.CAN_CEU_MEL);
		if (vrb2 != null) {
			q2 = vrb2.getQuota();
		}
		return CommonUtil.round(q1 + q2);
	}

	public double getResult() {
		return CommonUtil.round(
				getOutputGeneralTotalQuota() 
				- getInputGeneralTotalQuota()
				- getInvestmentGeneralTotalQuota() 
				- getExtracommunitaryInputQuota()
				- getCanCeuMelInputQuota()
				);
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
			setModel(new SerializableListDataModel(list));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onAccountingBookDetail(ActionEvent event) {
		try {
			VatCollectionParameters vcp = getParameters();
			vcp.setVatType( getVatType());
			setTitle(vcp);
			VatCollection vc = new VatCollection();
			List<Vat> list = vc.getVatDetailList(vcp,getOrder());
			setModel(new SerializableListDataModel(list));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onYearChanged(ValueChangeEvent event) {
		setFromDate(null);
		setToDate(null);
		if (event.getNewValue() != null) {
			Integer year = (Integer) event.getNewValue(); 
			setFromDate(getPeriod().getStartDate(year));	
			setToDate(getPeriod().getDueDate(year));
		}
	}
	
	public void onPeriodChanged(ValueChangeEvent event) {
		setFromDate(null);
		setToDate(null);
		if (getYear() != null) {
			Period vp = (Period) event.getNewValue(); 
			setFromDate(vp.getStartDate(getYear()));	
			setToDate(vp.getDueDate(getYear()));
		} else {
			String msg = "El Periodo IVA es necesario para calcular las fecha de inicio y fin del periodo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public com.code.aon.accounting.Period getAccountPeriod() {
		try {
			if (accountPeriod == null && getYear() != null) {
				AccountingUtil u = new AccountingUtil();
				com.code.aon.accounting.Period period = u.getPeriod(getFromDate());
				return period;
			}
		} catch (ManagerBeanException e) {
			// return null
		}
		return accountPeriod;
	}
	public void setAccountPeriod(com.code.aon.accounting.Period period) {
		this.accountPeriod = period;
	}
	
	
	public void onShowInvoice(ActionEvent event) {
		Vat vat = (Vat) getDirectModel().getRowData();
		String invoiceControllerName = "";
		if (vat.getInvoiceType() == InvoiceType.SALES) {
			invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(IFinanceConstants.SALE_INVOICE_FORM_NAME);
		} else if (vat.getInvoiceType() == InvoiceType.PURCHASE) {
			invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(IFinanceConstants.PURCHASE_INVOICE_FORM_NAME);
		} else if (vat.getInvoiceType() == InvoiceType.EXPENSES) {
			invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(IFinanceConstants.EXPENSE_INVOICE_FORM_NAME);
		} else if (vat.getInvoiceType() == InvoiceType.UNDEDUCTIBLE) {
			invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME);
		}

		BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(invoiceControllerName);
		try {
			invoiceController.onLoad(event, vat.getInvoiceId(), 
					"vat_report_detail", "");
		} catch (ManagerBeanException e) {
			String msg = "No se pudo navegar a la factura.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public String getInvoiceViewer() {
		return invoiceViewer;
	}

	public void setInvoiceViewer(String invoiceViewer) {
		this.invoiceViewer = invoiceViewer;
	}
	
}
