package com.code.aon.ui.stat.controller;

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
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.stat.Stat;
import com.code.aon.stat.StatParams;
import com.code.aon.stat.engine.StatEngine;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class StatEngineController {

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
	private DataModel yearStatModel;
	private DataModel monthStatModel;
	private DataModel dayStatModel;
	private DataModel customerStatModel;
	private DataModel abcStatModel;
	private DataModel invoicesModel;
	private DataModel productModel;
	private StatParams params;
	private String reportName;
	private String itemTitle;
	private static final String bundle = "statBundle";
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
		if (productModel == null) {
			productModel  = new ListDataModel(getProductStats());
		}
		return productModel ;
	}

	public void setProductModel(DataModel productModel) {
		this.productModel = productModel;
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
		if (yearStatModel == null) {
			yearStatModel = new ListDataModel(getYearStats());
		}
		return yearStatModel;
	}

	public void setYearStatModel(DataModel yearStatModel) {
		this.yearStatModel = yearStatModel;
	}

	public DataModel getMonthStatModel() {
		if (monthStatModel == null) {
			monthStatModel = new ListDataModel(getMonthStats());
		}
		return monthStatModel;
	}

	public void setMonthStatModel(DataModel monthStatModel) {
		this.monthStatModel = monthStatModel;
	}

	public DataModel getDayStatModel() {
		if (dayStatModel == null) {
			dayStatModel = new ListDataModel(getDayStats());
		}
		return dayStatModel;
	}

	public void setDayStatModel(DataModel dayStatModel) {
		this.dayStatModel = dayStatModel;
	}

	public DataModel getCustomerStatModel() {
		if (customerStatModel == null) {
			customerStatModel = new ListDataModel(getCustomerStats());
		}
		return customerStatModel;
	}

	public void setCustomerStatModel(DataModel clientStatModel) {
		this.customerStatModel = clientStatModel;
	}

	public DataModel getAbcStatModel() {
		if (abcStatModel == null) {
			abcStatModel = new ListDataModel(getAbcStats());
		}
		return abcStatModel;
	}

	public void setAbcStatModel(DataModel abcStatModel) {
		this.abcStatModel = abcStatModel;
	}

	public DataModel getInvoicesModel() {
		if (invoicesModel == null) {
			invoicesModel = new ListDataModel(getInvoices());
		}
		return invoicesModel;
	}

	public void setInvoicesModel(DataModel invoicesModel) {
		this.invoicesModel = invoicesModel;
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
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_year"));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado2"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_year"));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_expense"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_year"));
			}
			yearStatModel = null;
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
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_month"));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado2"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_month"));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_expense"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_month"));
			}
	
			monthStatModel = null;
		}
		/*
		 * if (init.get(Calendar.YEAR) != currentYear.intValue() &&
		 * fin.get(Calendar.YEAR) ==currentYear.intValue()) { Calendar fecini =
		 * new GregorianCalendar(currentYear, 0, 1);
		 * params.setFromDate(fecini.getTime());
		 * params.setInvoiceType(invoiceType); List<Stat> list = new
		 * LinkedList<Stat>(); System.out.println(params.getFromDate());
		 * System.out.println(params.getToDate());
		 * list.addAll(se.getMonthsStats(params)); setMonthStats(list);
		 * calculateTotals(list); setReportName(AonUtil.getMessage(bundle,
		 * "stat_menu_acumulado")); setItemTitle(AonUtil.getMessage(bundle,
		 * "stat_month")); monthStatModel = null; }
		 * 
		 * if (init.get(Calendar.YEAR) == currentYear.intValue() &&
		 * fin.get(Calendar.YEAR) != currentYear.intValue()) { Calendar fecfin =
		 * new GregorianCalendar(currentYear, 11, 31);
		 * params.setToDate(fecfin.getTime()); List<Stat> list = new
		 * LinkedList<Stat>(); System.out.println(params.getFromDate());
		 * System.out.println(params.getToDate());
		 * list.addAll(se.getMonthsStats(params)); setMonthStats(list);
		 * calculateTotals(list); setReportName(AonUtil.getMessage(bundle,
		 * "stat_menu_acumulado")); setItemTitle(AonUtil.getMessage(bundle,
		 * "stat_month")); monthStatModel = null; }
		 */
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
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_month"));
			} else {
				setReportName(AonUtil
						.getMessage(bundle, "stat_menu_acumulado2"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_month"));
			}
			monthStatModel = null;
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
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_day"));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado2"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_day"));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_expense"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_day"));
			}
			dayStatModel = null;
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
				setReportName(AonUtil.getMessage(bundle, "stat_menu_customers"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_customer"));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_suppliers"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_supplier"));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado_creditor"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_creditor"));
			}			
			customerStatModel = null;
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
			ExternalContext ec = FacesContext.getCurrentInstance()
					.getExternalContext();
			Map<String, String> paramss = ec.getRequestParameterMap();
			String cus = paramss.get("customer");
			Integer customer = Integer.parseInt(cus);
			
			IManagerBean invoiceBean = BeanManager
					.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean
					.getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), customer);
			criteria.addBetweenExpression(invoiceBean
					.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), params.getFromDate(), params.getToDate());
			criteria.addEqualExpression(invoiceBean
					.getFieldName(IEntityAlias.INVOICE_TYPE), iType);

			List<ITransferObject> list;
			list = invoiceBean.getList(criteria);
			for (ITransferObject to : list) {
				Invoice inv = (Invoice) to;
				invoices.add(inv);
			}
			setInvoiceBackAction("abc_customer_stats");
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
		Invoice invoice = (Invoice) this.invoicesModel.getRowData();
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
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_category"));
			yearStatModel = null;
			
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
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_customer"));
			customerStatModel = null;
			setBackAction("category_stats_year");
			setCheckLevel(0);
			invoicesModel=null;
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
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_customer"));
			customerStatModel = null;
			setBackAction("category_product_stats");
			setCheckLevel(1);
			invoicesModel=null;
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
			setReportName(AonUtil.getMessage(bundle, "stat_report_summary"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_month"));
			monthStatModel = null;
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
				setReportName(AonUtil.getMessage(bundle, "stat_report_abcCustomer"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_customer"));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(bundle, "stat_abc_supplier"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_supplier"));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(bundle, "stat_abc_creditor"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_creditor"));
			}	
			abcStatModel = null;
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
				setReportName(AonUtil.getMessage(bundle, "stat_report_abcProduct"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
			}
			if (invoiceType == 0) {
				setReportName(AonUtil.getMessage(bundle, "stat_report_abcProduct"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
			}
			if (invoiceType == 2) {
				setReportName(AonUtil.getMessage(bundle, "stat_abc_expense"));
				setItemTitle(AonUtil.getMessage(bundle, "stat_expense"));
			}	
			
			abcStatModel = null;
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
			setReportName(AonUtil.getMessage(bundle, "stat_report_abcCategory"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_category"));
			abcStatModel = null;
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
		setReportName(AonUtil.getMessage(bundle, "stat_report_abcProduct"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
		productModel = null;
	}

	
	private void getCustomerStatistics() throws ManagerBeanException {
		params.setCustomer(customer);
		params.setInvoiceType(invoiceType);
		List<Stat> list = new LinkedList<Stat>();
		list.addAll(getStatEngine().getAbcCustomerStats(params));
		setAbcStats(list);
		calculateTotals(list);
		setReportName(AonUtil.getMessage(bundle, "stat_report_abcCategory"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
		abcStatModel = null;
	}

	private void getCategoryProductStatistics() throws ManagerBeanException {
		params.setCategory(category);
		List<Stat> list = new LinkedList<Stat>();
		params.setInvoiceType(invoiceType);
		list.addAll(getStatEngine().getCategoryProductsStats(params));
		setMonthStats(list);
		calculateTotals(list);
		if (invoiceType == 1) {
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
		}
		if (invoiceType == 0) {
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
		}
		if (invoiceType == 2) {
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_expense"));
		}	
		
		monthStatModel = null;
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
			setReportName(AonUtil.getMessage(bundle, "stat_report_category"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_category"));
			yearStatModel = null;
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
		setReportName(AonUtil.getMessage(bundle, "stat_menu_acumulado"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_year"));
		yearStatModel = null;
		setBackAction("customer_stat_list_year");
		setCurrentMonth(null);

	}
	
	public void onInvoicePdf(ActionEvent event) throws ManagerBeanException {
		BasicController controller = (BasicController) FormUtil.getController("saleInvoice");
		controller.select(event, ((Invoice) this.getInvoicesModel().getRowData()).getId());
	}
	
}