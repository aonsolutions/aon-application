package com.code.aon.ui.stat.controller;

import static com.code.aon.ui.common.ICommonMessages.CATEGORY;
import static com.code.aon.ui.common.ICommonMessages.CREDITOR;
import static com.code.aon.ui.common.ICommonMessages.CUSTOMER;
import static com.code.aon.ui.common.ICommonMessages.DAY;
import static com.code.aon.ui.common.ICommonMessages.EXPENSE;
import static com.code.aon.ui.common.ICommonMessages.MONTH;
import static com.code.aon.ui.common.ICommonMessages.PRODUCT;
import static com.code.aon.ui.common.ICommonMessages.STAT_ABC_CREDITOR;
import static com.code.aon.ui.common.ICommonMessages.STAT_ABC_EXPENSE;
import static com.code.aon.ui.common.ICommonMessages.STAT_ABC_SUPPLIER;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_ACUMULADO;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_ACUMULADO2;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_ACUMULADO_CREDITOR;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_CUSTOMERS;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_EXPENSE;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_SUPPLIERS;
import static com.code.aon.ui.common.ICommonMessages.STAT_REPORT_ABC_CATEGORY;
import static com.code.aon.ui.common.ICommonMessages.STAT_REPORT_ABC_CUSTOMER;
import static com.code.aon.ui.common.ICommonMessages.STAT_REPORT_ABC_PRODUCT;
import static com.code.aon.ui.common.ICommonMessages.STAT_REPORT_CATEGORY;
import static com.code.aon.ui.common.ICommonMessages.STAT_REPORT_SUMMARY;
import static com.code.aon.ui.common.ICommonMessages.SUPPLIER;
import static com.code.aon.ui.common.ICommonMessages.YEAR;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.stat.Stat;
import com.code.aon.stat.StatParams;
import com.code.aon.stat.engine.StatEngine;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class StatEngineController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<Stat> yearStats;
	private List<Stat> monthStats;
	private List<Stat> dayStats;
	private List<Stat> customerStats;
	private List<Stat> abcStats;
	private List<Stat> productStats;
	private Integer currentYear;
	private Integer currentMonth;
	private Integer currentDay;
	private Integer segmentId;
	private Integer category;
	private Integer product;
	private Integer customer;
	private Integer month;
	private Integer year;
	private Double totalAmount;
	private Integer numInvoices;
	private Integer numProducts;
	private Double promAmount;
	private DataScrollerState yearStatState;
	private DataScrollerState monthStatState;
	private DataScrollerState dayStatState;
	private DataScrollerState customerStatState;
	private DataScrollerState abcStatState;
	private DataScrollerState invoicesState;
	private DataScrollerState productState;
	private StatParams params;
	private String reportName;
	private String itemTitle;
	private List<Invoice> invoices;
	private IPriceStrategy priceStrategy;
	private String backAction;
	private String invoiceBackAction;
	private Integer invoiceType;
	private InvoiceType iType;
	private Integer daysYear;
	private Integer dayMonth;
	private StatParams paramsBackUp;
	private Integer summaryMonth;
	private Integer checkLevel;
	
	private StatEngine statEngine;
	
	public StatEngine getStatEngine() throws ManagerBeanException{
		if(statEngine == null){
			CompanyCollectionsController companyCollections = (CompanyCollectionsController) AonUtil.getRegisteredBean(IStatConstants.COMPANY_COLLECTIONS_CONTROLLER_NAME);
			statEngine = new StatEngine( companyCollections.getCurrentUserWorkPlacesIds() );
		}
		return statEngine;
	}
	
	public DataModel getProductModel() {
		setProductModel(null);
		return getProductState().getDirectModel();
	}

	public void setProductModel(DataModel productModel) {
		if ( productModel == null ) {
			setProductState(null);
		} else {
			getProductState().setModel(productModel);
		}				
	}
	
	public DataScrollerState getProductState() {
		if (productState == null) {
			productState = new DataScrollerState(new SerializableListDataModel(getProductStats()), "statsDataTable");
		}		
		return productState;
	}

	public void setProductState(DataScrollerState productState) {
		this.productState = productState;
	}

	public List<Stat> getProductStats() {
		return productStats;
	}

	public void setProductStats(List<Stat> productStats) {
		this.productStats = productStats;
	}

	public Integer getCheckLevel() {
		return checkLevel;
	}

	public void setCheckLevel(Integer checkLevel) {
		this.checkLevel = checkLevel;
	}

	public Integer getSummaryMonth() {
		return summaryMonth;
	}

	public void setSummaryMonth(Integer summaryMonth) {
		this.summaryMonth = summaryMonth;
	}

	public Integer getDayMonth() {
		return dayMonth;
	}

	public void setDayMonth(Integer dayMonth) {
		this.dayMonth = dayMonth;
	}

	public StatParams getParamsBackUp() {
		return paramsBackUp;
	}

	public void setParamsBackUp(StatParams paramsBackUp) {
		this.paramsBackUp = paramsBackUp;
	}

	public Integer getDaysYear() {
		return daysYear;
	}

	public void setDaysYear(Integer daysYear) {
		this.daysYear = daysYear;
	}

	public Integer getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(Integer invoiceType) {
		this.invoiceType = invoiceType;
	}

	public InvoiceType getIType() {
		return iType;
	}

	public void setIType(InvoiceType type) {
		iType = type;
	}

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}
	
	public String getInvoiceBackAction() {
		return invoiceBackAction;
	}

	public void setInvoiceBackAction(String invoiceBackAction) {
		this.invoiceBackAction = invoiceBackAction;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public List<Stat> getYearStats() {
		return yearStats;
	}

	public void setYearStats(List<Stat> yearStats) {
		this.yearStats = yearStats;
	}

	public List<Stat> getMonthStats() {
		return monthStats;
	}

	public void setMonthStats(List<Stat> monthStats) {
		this.monthStats = monthStats;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public List<Stat> getCustomerStats() {
		return customerStats;
	}

	public void setCustomerStats(List<Stat> clientStats) {
		this.customerStats = clientStats;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public List<Stat> getDayStats() {
		return dayStats;
	}

	public void setDayStats(List<Stat> dayStats) {
		this.dayStats = dayStats;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(int currentDay) {
		this.currentDay = currentDay;
	}

	public DataModel getYearStatModel() {
		return yearStatState.getDirectModel();
	}

	public void setYearStatModel(DataModel yearStatModel) {
		if ( yearStatModel == null ) {
			setYearStatState(null);
		} else {
			getYearStatState().setModel(yearStatModel);
		}		
	}
	
	public DataScrollerState getYearStatState() {
		if (yearStatState == null) {
			yearStatState = new DataScrollerState(new SerializableListDataModel(getYearStats()), "statsDataTable");
		}		
		return yearStatState;
	}

	public void setYearStatState(DataScrollerState yearStatState) {
		this.yearStatState = yearStatState;
	}

	public DataModel getMonthStatModel() {
		return monthStatState.getDirectModel();
	}

	public void setMonthStatModel(DataModel monthStatModel) {
		if ( monthStatModel == null ) {
			setMonthStatState(null);
		} else {
			getMonthStatState().setModel(monthStatModel);
		}				
	}

	public DataScrollerState getMonthStatState() {
		if (monthStatState == null) {
			monthStatState = new DataScrollerState(new SerializableListDataModel(getMonthStats()), "statsDataTable");
		}				
		return monthStatState;
	}

	public void setMonthStatState(DataScrollerState monthStatState) {
		this.monthStatState = monthStatState;
	}


	public DataModel getDayStatModel() {
		return getDayStatState().getDirectModel();
	}

	public void setDayStatModel(DataModel dayStatModel) {
		if ( dayStatModel == null ) {
			setDayStatState(null);
		} else {
			getDayStatState().setModel(dayStatModel);
		}			
	}
	
	public DataScrollerState getDayStatState() {
		if (dayStatState == null) {
			dayStatState = new DataScrollerState(new SerializableListDataModel(getDayStats()), "statsDataTable");
		}						
		return dayStatState;
	}

	public void setDayStatState(DataScrollerState dayStatState) {
		this.dayStatState = dayStatState;
	}

	public DataModel getCustomerStatModel() {
		return getCustomerStatState().getDirectModel();
	}

	public void setCustomerStatModel(DataModel clientStatModel) {
		if ( clientStatModel == null ) {
			setCustomerStatState(null);
		} else {
			getCustomerStatState().setModel(clientStatModel);
		}			
	}
	
	public DataScrollerState getCustomerStatState() {
		if (customerStatState == null) {
			customerStatState = new DataScrollerState(new SerializableListDataModel(getCustomerStats()), "statsDataTable");
		}						
		return customerStatState;
	}

	public void setCustomerStatState(DataScrollerState customerStatState) {
		this.customerStatState = customerStatState;
	}

	public DataModel getAbcStatModel() {
		return getAbcStatState().getDirectModel();
	}

	public void setAbcStatModel(DataModel abcStatModel) {
		if ( abcStatModel == null ) {
			setAbcStatState(null);
		} else {
			getAbcStatState().setModel(abcStatModel);
		}					
	}
	
	public DataScrollerState getAbcStatState() {
		if (abcStatState == null) {
			abcStatState = new DataScrollerState(new SerializableListDataModel(getAbcStats()), "statsDataTable");
		}								
		return abcStatState;
	}

	public void setAbcStatState(DataScrollerState abcStatState) {
		this.abcStatState = abcStatState;
	}

	public DataModel getInvoicesModel() {
		return getInvoicesState().getDirectModel();
	}

	public void setInvoicesModel(DataModel invoicesModel) {
		if ( invoicesModel == null ) {
			setInvoicesState(null);
		} else {
			getInvoicesState().setModel(invoicesModel);
		}					
	}

	public DataScrollerState getInvoicesState() {
		if (invoicesState == null) {
			invoicesState = new DataScrollerState(new SerializableListDataModel(getInvoices()), "invoices");
		}								
		return invoicesState;
	}

	public void setInvoicesState(DataScrollerState invoicesState) {
		this.invoicesState = invoicesState;
	}

	public Integer getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Integer segmentId) {
		this.segmentId = segmentId;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public StatParams getParams() {
		return params;
	}

	public void setParams(StatParams params) {
		this.params = params;
	}

	public void setCurrentYear(Integer currentYear) {
		this.currentYear = currentYear;
	}

	public void setCurrentMonth(Integer currentMonth) {
		this.currentMonth = currentMonth;
	}

	public void setCurrentDay(Integer currentDay) {
		this.currentDay = currentDay;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getNumInvoices() {
		return numInvoices;
	}

	public void setNumInvoices(Integer numInvoices) {
		this.numInvoices = numInvoices;
	}

	public Integer getNumProducts() {
		return numProducts;
	}
	
	public void setNumProducts(Integer numProducts) {
		this.numProducts = numProducts;
	}

	public Double getPromAmount() {
		return promAmount;
	}

	public void setPromAmount(Double promAmount) {
		this.promAmount = promAmount;
	}

	public List<Stat> getAbcStats() {
		return abcStats;
	}

	public void setAbcStats(List<Stat> abcStats) {
		this.abcStats = abcStats;
	}

	public Integer getProduct() {
		return product;
	}

	public void setProduct(Integer product) {
		this.product = product;
	}

	public Integer getCustomer() {
		return customer;
	}

	public void setCustomer(Integer customer) {
		this.customer = customer;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public void onSaleType(ActionEvent event) {
		setInvoiceType(1);
		setIType(InvoiceType.SALES);
	}

	public void onPurchaseType(ActionEvent event) {
		setInvoiceType(0);
		setIType(InvoiceType.PURCHASE);
	}
	
	public void onExpenseType(ActionEvent event) {
		setInvoiceType(2);
		setIType(InvoiceType.EXPENSES);
	}

	public String getMonthName() {

		String name = Month.getMonthByValue(currentMonth).getName(
				AonUtil.getCurrentLocale());
		return name;
	}

	public void onReset(ActionEvent event) {
		params = new StatParams(AonUtil.getDomainName());
		params.setLocale(FacesContext.getCurrentInstance().getViewRoot()
				.getLocale());
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		setSummaryMonth(c.get(Calendar.MONTH) + 1);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		params.setFromDate(c.getTime());
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		params.setToDate(c.getTime());
		setCurrentYear(c.get(Calendar.YEAR));
	}

	public void onAnualStats(ActionEvent event) {
		try {
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			setParamsBackUp(params);
			list.addAll(getStatEngine().getYearStats(params));
			setYearStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO));
				setItemTitle(AonUtil.getMessage(YEAR));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO2));
				setItemTitle(AonUtil.getMessage(YEAR));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(STAT_MENU_EXPENSE));
				setItemTitle(AonUtil.getMessage(YEAR));
			}
			setYearStatModel(null);
			setBackAction("customer_stat_list_year");
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onYearSelect(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String year = params.get("year");
			currentYear = Integer.parseInt(year);
			getMonthStatistics();
			setBackAction("customer_stat_list_month");

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private void getMonthStatistics() throws ManagerBeanException {
		
		Calendar init = new GregorianCalendar();
		Calendar fin = new GregorianCalendar();
		init.setTime(params.getFromDate());
		fin.setTime(params.getToDate());

		if (init.get(Calendar.YEAR) == currentYear.intValue()
				&& fin.get(Calendar.YEAR) == currentYear.intValue()) {
			params.setInvoiceType(invoiceType);
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(getStatEngine().getMonthsStats(params));
			setMonthStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO));
				setItemTitle(AonUtil.getMessage(MONTH));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO2));
				setItemTitle(AonUtil.getMessage(MONTH));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(STAT_MENU_EXPENSE));
				setItemTitle(AonUtil.getMessage(MONTH));
			}
	
			setMonthStatModel(null);
		}
		if (init.get(Calendar.YEAR) != currentYear.intValue()
				&& fin.get(Calendar.YEAR) != currentYear.intValue()) {
			Calendar fec = new GregorianCalendar(currentYear, 0, 1);
			Calendar fecfin = new GregorianCalendar(currentYear, 11, 31);
			params.setFromDate(fec.getTime());
			params.setToDate(fecfin.getTime());
			params.setInvoiceType(invoiceType);
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(getStatEngine().getMonthsStats(params));
			setMonthStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO));
				setItemTitle(AonUtil.getMessage(MONTH));
			} else {
				setReportName(AonUtil
						.getMessage(STAT_MENU_ACUMULADO2));
				setItemTitle(AonUtil.getMessage(MONTH));
			}
			setMonthStatModel(null);
		}

	}

	public void onMonthSelect(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String month = params.get("month");
		currentMonth = Integer.parseInt(month);
		getDayStatititics();
		setBackAction("customer_stat_list_day");
	}

	private void getDayStatititics() {
		try {
			Calendar cal = new GregorianCalendar();
			StatParams params = new StatParams(AonUtil.getDomainName());
			params.setLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());
			cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, currentYear);
			cal.set(Calendar.MONTH, currentMonth);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			params.setFromDate(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			params.setToDate(cal.getTime());
			params.setInvoiceType(invoiceType);
			params.setWorkPlace(this.getParams().getWorkPlace());
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(getStatEngine().getDaysStats(params));
			setDayStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO));
				setItemTitle(AonUtil.getMessage(DAY));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO2));
				setItemTitle(AonUtil.getMessage(DAY));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(STAT_MENU_EXPENSE));
				setItemTitle(AonUtil.getMessage(DAY));
			}
			setDayStatModel(null);
			setDaysYear(currentYear);
			setDayMonth(currentMonth);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCustomerStats(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> paramss = ec.getRequestParameterMap();
			if (paramss.get("year") != null) {
				Integer year = Integer.parseInt(paramss.get("year"));
				setCurrentYear(year);
			}
			if (paramss.get("month") != null) {
				Integer month = Integer.parseInt(paramss.get("month"));
				setCurrentMonth(month);
			}
			if (paramss.get("day") != null) {
				Integer day = Integer.parseInt(paramss.get("day"));
				setCurrentDay(day);
			}
			StatParams params = new StatParams(AonUtil.getDomainName());
			params.setLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());
			refreshControllerDates(params);
			params.setWorkPlace(this.getParams().getWorkPlace());
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			setDaysYear(currentYear);
			list.addAll(getStatEngine().getCustomerStats(params));
			setCustomerStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_MENU_CUSTOMERS));
				setItemTitle(AonUtil.getMessage(ICommonMessages.CUSTOMER));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(STAT_MENU_SUPPLIERS));
				setItemTitle(AonUtil.getMessage(ICommonMessages.SUPPLIER));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO_CREDITOR));
				setItemTitle(AonUtil.getMessage(CREDITOR));
			}			
			setCustomerStatModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onInvoiceList(ActionEvent event) {
		try {
			invoices = new LinkedList<Invoice>();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String cus = params.get("customer");
			Integer customer = Integer.parseInt(cus);
			Calendar fecini = null;
			Calendar fecfin = null;
		
			if (currentYear != null && currentMonth == null) {

				fecini = new GregorianCalendar(currentYear, 0, 1);
				fecfin = new GregorianCalendar(currentYear, 11, 31);
				setInvoiceBackAction("customer_stat_list_year");

			} else if (currentMonth != null && currentDay == null) {

				fecini = new GregorianCalendar(currentYear, currentMonth , 1);
				fecfin = new GregorianCalendar(currentYear, currentMonth , 31);
				fecfin.set(Calendar.DAY_OF_MONTH, fecfin.getActualMaximum(Calendar.DAY_OF_MONTH));
				setInvoiceBackAction("customer_stat_list_month");

			} else if (currentYear != null && currentMonth != null && currentDay != null) {

				fecini = new GregorianCalendar(currentYear, currentMonth , currentDay);
				fecfin = new GregorianCalendar(currentYear, currentMonth , currentDay);
				setInvoiceBackAction("customer_stat_list_day");

			}

			StatParams parameters = new StatParams(AonUtil.getDomainName());
			parameters.setFromDate(fecini.getTime());
			parameters.setToDate(fecfin.getTime());
			
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_REGISTRY_ID),customer);
			criteria.addBetweenExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE),parameters.getFromDate(), parameters.getToDate());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), iType);
			
			if(this.params.getWorkPlace()!=null && this.params.getWorkPlace().getId()!=null){
				criteria.addEqualExpression("Invoice.lines.workPlace.id",this.params.getWorkPlace().getId());
			}
					
			List<ITransferObject> list;
			list = invoiceBean.getList(criteria);
			for (ITransferObject to : list) {
				Invoice inv = (Invoice) to;
				invoices.add(inv);
			}
			setInvoicesModel(null);

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onRegistryInvoiceList(ActionEvent event) {
		try {
			invoices = new LinkedList<Invoice>();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> requestParams = ec.getRequestParameterMap();
			String registry = requestParams.get("customer");
			Integer registryId = Integer.parseInt(registry);
			
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), registryId);
			criteria.addBetweenExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), params.getFromDate(), params.getToDate());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), iType);

			List<ITransferObject> list;
			list = invoiceBean.getList(criteria);
			for (ITransferObject to : list) {
				Invoice inv = (Invoice) to;
				invoices.add(inv);
			}
			setInvoiceBackAction("abc_customer_stats");
			setInvoicesModel(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onCategoryInvoiceList(ActionEvent event) {
		try {
			invoices = new LinkedList<Invoice>();
			ExternalContext ec = FacesContext.getCurrentInstance()
			.getExternalContext();
			Map<String, String> paramss = ec.getRequestParameterMap();
			String cus = paramss.get("customer");
			Integer customer = Integer.parseInt(cus);
			
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), customer);
			criteria.addBetweenExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), params.getFromDate(), params.getToDate());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), iType);
			
			if(this.params.getWorkPlace()!=null && this.params.getWorkPlace().getId()!=null){
				criteria.addEqualExpression("Invoice.lines.workPlace.id",this.params.getWorkPlace().getId());
			}
			
			if(checkLevel==0){
				criteria.addEqualExpression("Invoice.lines.item.product.category.id",this.params.getCategory());
				setBackAction("category_stats_year");
				setInvoiceBackAction("category_customer_list");
			}else{
				criteria.addEqualExpression("Invoice.lines.item.product.id",this.params.getProduct());
				setBackAction("category_product_stats");
				setInvoiceBackAction("category_customer_list");
			}			
			List<ITransferObject> list;
			list = invoiceBean.getList(criteria);
			for (ITransferObject to : list) {
				Invoice inv = (Invoice) to;
				invoices.add(inv);
			}
			setInvoicesModel(null);
			
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onProductInvoiceList(ActionEvent event) {
		try {
			
			invoices = new LinkedList<Invoice>();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> paramss = ec.getRequestParameterMap();
			String product = paramss.get("product");
			setProduct(Integer.parseInt(product));
			
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addBetweenExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), params.getFromDate(), params.getToDate());
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), iType);
			criteria.addEqualExpression("Invoice.lines.item.product.id",this.getProduct());
			
			if(this.params.getWorkPlace()!=null && this.params.getWorkPlace().getId()!=null){
				criteria.addEqualExpression("Invoice.lines.workPlace.id",this.params.getWorkPlace().getId());
			}
			
			setInvoiceBackAction("abc_product_stats");
							
			List<ITransferObject> list;
			list = invoiceBean.getList(criteria);
			for (ITransferObject to : list) {
				Invoice inv = (Invoice) to;
				invoices.add(inv);
			}
			setInvoicesModel(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice) getInvoicesModel().getRowData();
		// TODO: si se muestra con iva, que se muestre siempre en todas las pantallas 
//		return getPriceStrategy().getTotalPrice(invoice, invoice);
		return invoice.getTaxableBase();
	}

	public void onCategoryStats(ActionEvent event) {
		try {
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			list.addAll(getStatEngine().getCategoryStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(CATEGORY));
			setYearStatModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCategorySelect(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String category = params.get("category");
			setCategory(Integer.parseInt(category));
			getCategoryProductStatistics();
			

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onCategoryRegistryStats(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			this.params.setCategory(Integer.parseInt(params.get("category")));
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(getStatEngine().getCategoryCustomerStats(this.params));
			setCustomerStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(ICommonMessages.CUSTOMER));
			setCustomerStatModel(null);
			setBackAction("category_stats_year");
			setCheckLevel(0);
			setInvoicesModel(null);
		}

		catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}
	
	public void onProductRegistryStats(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			this.params.setProduct(Integer.parseInt(params.get("product")));
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(getStatEngine().getProductRegistryStats(this.params));
			setCustomerStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(ICommonMessages.CUSTOMER));
			setCustomerStatModel(null);
			setBackAction("category_product_stats");
			setCheckLevel(1);
			setInvoicesModel(null);
		}

		catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	public void onSummaryStats(ActionEvent event) {
		try {
			summaryMonth--;
			Calendar c = new GregorianCalendar(1970, 0, 1);
			c.setTime(new Date());
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.MONTH, summaryMonth);
			c.set(Calendar.YEAR, currentYear - 1);
			params.setFromDate(c.getTime());
			c.set(Calendar.MONTH, summaryMonth);
			c.set(Calendar.YEAR, currentYear);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			params.setToDate(c.getTime());
			params.setInvoiceType(invoiceType);
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(getStatEngine().getSummaryStats(params));
			setMonthStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(STAT_REPORT_SUMMARY));
			setItemTitle(AonUtil.getMessage(MONTH));
			setMonthStatModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRegistryAbcStats(ActionEvent event) {
		try {
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			list.addAll(getStatEngine().getABCStatsByCustomer(params));
			setAbcStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_REPORT_ABC_CUSTOMER));
				setItemTitle(AonUtil.getMessage(CUSTOMER));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(STAT_ABC_SUPPLIER));
				setItemTitle(AonUtil.getMessage(SUPPLIER));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(STAT_ABC_CREDITOR));
				setItemTitle(AonUtil.getMessage(CREDITOR));
			}	
			setAbcStatModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onProductAbcStats(ActionEvent event) {
		try {
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			list.addAll(getStatEngine().getABCStatsByProduct(params));
			setAbcStats(list);
			calculateTotals(list);
			if (invoiceType == 1) {
				setReportName(AonUtil.getMessage(STAT_REPORT_ABC_PRODUCT));
				setItemTitle(AonUtil.getMessage(PRODUCT));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(STAT_REPORT_ABC_PRODUCT));
				setItemTitle(AonUtil.getMessage(PRODUCT));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(STAT_ABC_EXPENSE));
				setItemTitle(AonUtil.getMessage(EXPENSE));
			}	
			
			setAbcStatModel(null);
			setCheckLevel(1);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCategoryAbcStats(ActionEvent event) {
		try {
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			list.addAll(getStatEngine().getABCStatsByCategory(params));
			setAbcStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(STAT_REPORT_ABC_CATEGORY));
			setItemTitle(AonUtil.getMessage(CATEGORY));
			setAbcStatModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	/*public void onAbcProductSelect(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String product = params.get("product");
			setProduct(Integer.parseInt(product));
			getProductStatistics();

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}*/

	public void onAbcRegistrySelect(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String customer = params.get("customer");
			setCustomer(Integer.parseInt(customer));
			getRegistryProductStatistics();

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onAbcCustomerSelect(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String customer = params.get("customer");
			setCustomer(Integer.parseInt(customer));
			getCustomerStatistics();

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	

	private void getRegistryProductStatistics() throws ManagerBeanException {
		params.setCustomer(customer);
		params.setInvoiceType(invoiceType);
		List<Stat> list = new LinkedList<Stat>();
		list.addAll(getStatEngine().getRegistryProductStats(params));
		setProductStats(list);
		calculateTotals(list);
		setReportName(AonUtil.getMessage(STAT_REPORT_ABC_PRODUCT));
		setItemTitle(AonUtil.getMessage(PRODUCT));
		setProductModel(null);
	}

	
	private void getCustomerStatistics() throws ManagerBeanException {
		params.setCustomer(customer);
		params.setInvoiceType(invoiceType);
		List<Stat> list = new LinkedList<Stat>();
		list.addAll(getStatEngine().getAbcCustomerStats(params));
		setAbcStats(list);
		calculateTotals(list);
		setReportName(AonUtil.getMessage(STAT_REPORT_ABC_CATEGORY));
		setItemTitle(AonUtil.getMessage(PRODUCT));
		setAbcStatModel(null);
	}

	private void getCategoryProductStatistics() throws ManagerBeanException {
		params.setCategory(category);
		List<Stat> list = new LinkedList<Stat>();
		params.setInvoiceType(invoiceType);
		list.addAll(getStatEngine().getCategoryProductsStats(params));
		setMonthStats(list);
		calculateTotals(list);
		if (invoiceType == 1) {
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(PRODUCT));
		}
		if (invoiceType == 0) {
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(PRODUCT));
		}
		if (invoiceType == 2) {
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(ICommonMessages.EXPENSE));
		}	
		
		setMonthStatModel(null);
	}

	public void calculateTotals(List<Stat> list) {
		int i = 0;
		setTotalAmount(0.00);
		setNumInvoices(0);
		setNumProducts(0);
		setPromAmount(0.00);

		while (i < list.size()) {

			totalAmount += list.get(i).getAmount();
			numInvoices += (int) list.get(i).getNumInvoice();
			numProducts += (int) list.get(i).getProductCount();

			i++;
		}
		promAmount += totalAmount / numInvoices;
	}

	public void refreshControllerDates(StatParams statParams) {
		Calendar cal = new GregorianCalendar();
		Integer year = null;
		Integer month = 0;
		Integer day = 1;
		ExternalContext ec = FacesContext.getCurrentInstance()
				.getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();

		if (params.get("year") != null) {
			year = Integer.parseInt(params.get("year"));
			cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
			cal.set(Calendar.DAY_OF_MONTH, 1);
			statParams.setFromDate(cal.getTime());
			cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
			cal.set(Calendar.DAY_OF_MONTH, 31);
			statParams.setToDate(cal.getTime());
			this.currentYear = year;
		}
		if (params.get("month") != null) {
			month = Integer.parseInt(params.get("month"));
			cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, currentYear);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			statParams.setFromDate(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			statParams.setToDate(cal.getTime());
		}
		if (params.get("day") != null) {
			day = Integer.parseInt(params.get("day"));
			cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, currentYear);
			cal.set(Calendar.MONTH, currentMonth);
			cal.set(Calendar.DAY_OF_MONTH, day);
			statParams.setFromDate(cal.getTime());
			statParams.setToDate(cal.getTime());
		}
	}

	
//	public void onYearChanged(ActionEvent event) {
//		try {
//			getMonthStatistics();
//		} catch (ManagerBeanException e) {
//			String msg = "Error al obtener los datos. " + e.getMessage();
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
//		}
//	}

	public void onDayBackAction(ActionEvent event) throws ManagerBeanException {
		setCurrentYear(daysYear);
		getMonthStatistics();
		setCurrentDay(null);
	}

	public void onCustomerDayBackAction(ActionEvent event)
			throws ManagerBeanException {
		setCurrentYear(daysYear);
		setCurrentMonth(dayMonth);
		getDayStatititics();
	}

	public void onProductBackAction(ActionEvent event)
			throws ManagerBeanException {
		try {
			List<Stat> list = new LinkedList<Stat>();
			params.setInvoiceType(invoiceType);
			list.addAll(getStatEngine().getCategoryStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(STAT_REPORT_CATEGORY));
			setItemTitle(AonUtil.getMessage(CATEGORY));
			setYearStatModel(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRegistryYearBackAction(ActionEvent event)
			throws ManagerBeanException {
		setParams(paramsBackUp);
		List<Stat> list = new LinkedList<Stat>();
		list.addAll(getStatEngine().getYearStats(params));
		calculateTotals(list);
		setReportName(AonUtil.getMessage(STAT_MENU_ACUMULADO));
		setItemTitle(AonUtil.getMessage(YEAR));
		setYearStatModel(null);
		setBackAction("customer_stat_list_year");
		setCurrentMonth(null);

	}
	
	public void onInvoicePdf(ActionEvent event) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), ((Invoice) this.getInvoicesModel().getRowData()).getId());
		FormUtil.getController("invoicePrint").setCriteria(criteria);
	}
	
	public void  onSalesByCountry(ActionEvent event)  {
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			List<Stat> stats = getStatEngine().getSalesByCountry(c, getParams().getFromDate(), getParams().getToDate());
			String beforePage = "<html>"
				+	"<head>"
				+		"<script type='text/javascript' src='https://www.google.com/jsapi'></script>"
				+		"<script type='text/javascript'>"
				+			"google.load('visualization', '1', {'packages': ['geochart']});"
				+			"google.setOnLoadCallback(drawRegionsMap);"
				+			"function drawRegionsMap() {"
				+				"var data = google.visualization.arrayToDataTable(["
				+				"['Pais', 'Ventas']";
			String page = "";
			for (Stat stat : stats) {
				page += ",['"+stat.getName()+"',"+stat.getAmount()+"]";
			}
			String afterPage = "]);"
				+				"var options = {};"
				+				"var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));"
				+				"chart.draw(data, options);"
				+			"};"
				+		"</script>"
				+	"</head>"
				+	"<body>"
				+			"<div id='chart_div' style='width: 900px; height: 500px;'></div>"
				+	"</body>"
				+"</html>";
			
	        FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			if ( response instanceof HttpServletResponseWrapper) {
				response = (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
			}
			response.setContentType( MimeType.MIME_HTML.getName() );
			response.getWriter().print(beforePage);
			response.getWriter().print(page);
			response.getWriter().print(afterPage);
			response.flushBuffer();
	        context.responseComplete();    			
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonConnectionException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(c);
		}
	}

	public void  onSalesByProvince(ActionEvent event)  {
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			List<Stat> stats = getStatEngine().getSalesByProvince(c, getParams().getFromDate(), getParams().getToDate());
			String beforePage = "<html>"
				+	"<head>"
				+		"<script type='text/javascript' src='https://www.google.com/jsapi'></script>"
				+		"<script type='text/javascript'>"
				+			"google.load('visualization', '1', {'packages': ['geochart']});"
				+			"google.setOnLoadCallback(drawRegionsMap);"
				+			"function drawRegionsMap() {"
				+				"var data = google.visualization.arrayToDataTable(["
				+				"['Pais', 'Ventas']";
			String page = "";
			for (Stat stat : stats) {
				if (StringUtils.isNotEmpty(stat.getName())) {
					page += ",['"+stat.getName()+"',"+stat.getAmount()+"]";	
				}
			}
			String afterPage = "]);"
				+				"var options = {" 
				+				"region: 'ES',"
				+				"displayMode: 'markers',"
				+				"resolution: 'provinces',"
				+				"colorAxis: {colors: ['red', 'green']}"
				+				"};"
				+				"var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));"
				+				"chart.draw(data, options);"
				+			"};"
				+		"</script>"
				+	"</head>"
				+	"<body>"
				+			"<div id='chart_div' style='width: 900px; height: 500px;'></div>"
				+	"</body>"
				+"</html>";
			
	        FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			if ( response instanceof HttpServletResponseWrapper) {
				response = (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
			}
			response.setContentType( MimeType.MIME_HTML.getName() );
			response.getWriter().print(beforePage);
			response.getWriter().print(page);
			response.getWriter().print(afterPage);
			response.flushBuffer();
	        context.responseComplete();    			
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonConnectionException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(c);
		}
	}

}
