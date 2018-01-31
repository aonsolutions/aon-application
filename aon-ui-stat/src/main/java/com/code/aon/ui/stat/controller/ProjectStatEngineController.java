package com.code.aon.ui.stat.controller;

import static com.code.aon.ui.common.ICommonMessages.SEARCH_NO_RESULTS;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.groupware.enumeration.TaskStatus;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.stat.DailyTracking;
import com.code.aon.stat.Delivery;
import com.code.aon.stat.Invoice;
import com.code.aon.stat.Offer;
import com.code.aon.stat.PagedList;
import com.code.aon.stat.engine.ProjectStatEngine;
import com.code.aon.stat.engine.ProjectStatParams;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectStatEngineController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String TOTAL_ERROR_MSG = "No se pudieron calcular los totales";

	private static String SALE_INVOICE_CONTROLLER_NAME = "saleInvoice";
	private static String PURCHASE_INVOICE_CONTROLLER_NAME = "purchaseInvoice";
	private static String EXPENSE_INVOICE_CONTROLLER_NAME = "expenseInvoice";
	private static String UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME = "undeductibleInvoice";
	private static String SALE_INVOICE_FORM_NAME = "saleInvoice_form";
	private static String PURCHASE_INVOICE_FORM_NAME = "purchaseInvoice_form";
	private static String EXPENSE_INVOICE_FORM_NAME = "expenseInvoice_form";
	private static String UNDEDUCTIBLE_INVOICE_FORM_NAME = "undeductibleInvoice_form";
	private static String OFFER_CONTROLLER_NAME = "offer";;

	private String backAction;
	private String selectedTab;

	private Project project;
	private Date fromDate;
	private Date toDate;

	private PagedList<Offer> approvedOfferPage;
	private PagedList<Invoice> saleInvoicePage;
	private PagedList<Invoice> costInvoicePage;
	private PagedList<Delivery> deliveryPage;
	private PagedList<DailyTracking> dailyTrackingPage;
	
	private double totalOffered;
	private double totalSales;
	private double totalCosts;
	private double totalInvoiceCosts;
	private double totalDailyTracking;
	private double totalIncome;
	private double totalDelivery;

	private String invoiceViewer;
	private ProjectStatEngine engine;
	
	public ProjectStatEngine getEngine() {
		if (engine == null) {
			engine = new ProjectStatEngine();
		}
		return engine;
	}
	
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Date getFromDate() {
		if (fromDate == null) {
			setFromDate(CommonUtil.getYearFirstDay(2000));
		}
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		if (toDate == null) {
			setToDate(CommonUtil.getYearLastDay(2020));
		}
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getInvoiceViewer() {
		return invoiceViewer;
	}

	public void setInvoiceViewer(String invoiceViewer) {
		this.invoiceViewer = invoiceViewer;
	}

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		int currentProject = this.project==null?-1:this.project.getId()==null?-1:this.project.getId();
		int assignableProject = project==null?-1:project.getId()==null?-1:project.getId();
		this.project = project;
		if (currentProject != assignableProject) {
			initializeTotals();
		}
	}
	
	private void initializeTotals() {
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			this.totalOffered = getEngine().getTotalOffered(c, getParams());
			this.totalInvoiceCosts = getEngine().getTotalInvoiceCosts(c, getParams());
			this.totalSales = getEngine().getTotalSales(c, getParams());
			this.totalIncome = getEngine().getTotalIncome(c, getParams());
			this.totalDelivery = getEngine().getTotalDelivery(c, getParams());
			this.totalDailyTracking = getEngine().getTotalDailyTracking(c, getParams());
		} catch (AonConnectionException e) {
			AonUtil.addErrorMessage(TOTAL_ERROR_MSG);
			throw new AbortProcessingException(TOTAL_ERROR_MSG,e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(TOTAL_ERROR_MSG);
			throw new AbortProcessingException(TOTAL_ERROR_MSG,e);
		} finally {
			DatabaseUtil.closeQuietly(c);
		}
	}

	public double getTotalOffered() {
		return CommonUtil.round(totalOffered);
	}

	public double getTotalSales() {
		return CommonUtil.round(this.totalSales) + getTotalDelivery();
	}

	public double getTotalCosts() {
		totalCosts = CommonUtil.round(getTotalInvoiceCosts() + getTotalDailyTracking() + getTotalIncome());
		return CommonUtil.round(totalCosts);
	}

	public double getTotalInvoiceCosts() {
		return CommonUtil.round(totalInvoiceCosts);
	}

	public double getTotalDailyTracking() {
		return CommonUtil.round(totalDailyTracking);
	}
	
	public double getTotalIncome() {
		return CommonUtil.round(totalIncome);
	}
	
	public double getTotalDelivery() {
		return CommonUtil.round(totalDelivery);
	}

	public double getTotalResult() {
		return CommonUtil.round(getTotalSales() - getTotalCosts());
	}

	public void onRefresh(ActionEvent event) {
		initializeTotals();
		initializeProjectData();
	}
	
	public void onNextSaleInvoiceList(ActionEvent event) {
		this.saleInvoicePage.setList(null); 
	}
	public void onPreviousSaleInvoiceList(ActionEvent event) {
		try {
			getSaleInvoicePage().preparePreviousPage();
			this.saleInvoicePage.setList(null);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo recuperar la lista de facturas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void onNextCostInvoiceList(ActionEvent event) {
		this.costInvoicePage.setList(null); 
	}
	public void onPreviousCostInvoiceList(ActionEvent event) {
		try {
			getCostInvoicePage().preparePreviousPage();
			this.costInvoicePage.setList(null);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo recuperar la lista de facturas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onNextDailyTrackingList(ActionEvent event) {
		this.dailyTrackingPage.setList(null); 
	}
	public void onPreviousDailyTrackingList(ActionEvent event) {
		try {
			getDailyTrackingPage().preparePreviousPage();
			this.dailyTrackingPage.setList(null);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo recuperar la lista de mano de obra";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
		
	public void onNextApprovedOfferList(ActionEvent event) {
		this.approvedOfferPage.setList(null); 
	}
	public void onPreviousApprovedOfferList(ActionEvent event) {
		try {
			getApprovedOfferPage().preparePreviousPage();
			this.approvedOfferPage.setList(null);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo recuperar la lista de presupuestos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void initializeProjectData() {
		this.saleInvoicePage = null;
		this.costInvoicePage = null;
		this.approvedOfferPage = null;
		this.dailyTrackingPage = null;
	}

	public String backAction() {
		setFromDate(null);
		setToDate(null);
		initializeTotals();
		initializeProjectData();
		if (StringUtils.isNotBlank(getBackAction())) {
			return getBackAction();
		}
		return "projectCommercial_form";
	}

	public Date getLastModified() {
		return new Date();
	}

	public void paintGanttChart(OutputStream out, Object data)
			throws IOException, ManagerBeanException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = ctx.getViewRoot().getLocale();
		Date fromDate = CommonUtil.getYearFirstDay(new Date());
		IManagerBean taskBean = BeanManager
				.getManagerBean(com.code.aon.groupware.Task.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				taskBean.getFieldName(IEntityAlias.TASK_PROJECT_ID),
				getProject().getId());
		criteria.addGreaterThanOrEqualExpression(
				taskBean.getFieldName(IEntityAlias.TASK_START_DATE), fromDate);
		List<ITransferObject> list = taskBean.getList(criteria);
		TaskSeries s1 = new TaskSeries(TaskStatus.PENDING.getName(locale));
		TaskSeries s2 = new TaskSeries(TaskStatus.FINISHED.getName(locale));
		TaskSeries s3 = new TaskSeries(TaskStatus.IN_PROGRESS.getName(locale));
		int i = 0;
		for (ITransferObject to : list) {
			com.code.aon.groupware.Task task = (com.code.aon.groupware.Task) to;
			if (!task.isDeleted()) {
				Date start = task.getStartDate();
				Date end = task.getEndDate() == null ? task.getDueDate() : task
						.getEndDate();
				if (end.before(start)) {
					start = end;
				}
				(task.isFinished() ? s2 : (task.isInProgress() ? s3 : s1))
						.add(new Task(task.getDescription(), start, end));
				++i;
			}
		}
		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		collection.add(s2);
		collection.add(s3);
		JFreeChart chart = ChartFactory.createGanttChart(null, null, null,
				collection, true, false, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setRangeGridlinePaint(new Color(150, 150, 150));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setNoDataMessage(AonUtil.getMessage(SEARCH_NO_RESULTS));
		int width = 1024;
		int height = i * 15;
		height = height < 125 ? 125 : height;
		float quality = 1;
		ChartUtilities.writeChartAsJPEG(out, quality, chart, width, height);
	}

	public void onInvoice(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String invoiceId = params.get("invoiceId");
		String  invoiceType = params.get("invoiceType");
		System.out.println( invoiceId );
		if (StringUtils.isEmpty(invoiceId)) {
			String msg = "No se ha podido determinar la factura a la que navegar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (StringUtils.isEmpty(invoiceType)) {
			String msg = "No se ha podido determinar el tipo de la factura a la que navegar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		Integer id = null;
		Integer type = null;
		try {
			id = Integer.parseInt(invoiceId);
			type = Integer.parseInt(invoiceType);
		} catch (NumberFormatException e) {
			String msg = "No se ha podido determinar la factura a la que navegar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		String invoiceControllerName = "";
		if (type == InvoiceType.SALES.ordinal()) {
			invoiceControllerName = SALE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(SALE_INVOICE_FORM_NAME);
		} else if (type  == InvoiceType.PURCHASE.ordinal()) {
			invoiceControllerName = PURCHASE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(PURCHASE_INVOICE_FORM_NAME);
		} else if (type  == InvoiceType.EXPENSES.ordinal()) {
			invoiceControllerName = EXPENSE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(EXPENSE_INVOICE_FORM_NAME);
		} else if (type  == InvoiceType.UNDEDUCTIBLE.ordinal()) {
			invoiceControllerName = UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(UNDEDUCTIBLE_INVOICE_FORM_NAME);
		}

		BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(invoiceControllerName);
		try {
			invoiceController.onLoad(event, id , "project_stats", "projectStat.onRefresh");
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido determinar la factura a la que navegar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public String invoiceAction() {
		return getInvoiceViewer();
	}

	public void onOffer(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String offerId = params.get("offerId");
		Integer id = null;
		try {
			id = Integer.parseInt(offerId);
		} catch (NumberFormatException e) {
			String msg = "No se ha podido determinar el presupuesto al que navegar";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		BasicController offerController = (BasicController)AonUtil.getRegisteredBean(OFFER_CONTROLLER_NAME);
		offerController.onLoad(event, id, "project_stats", "projectStat.onRefresh");
	}
	

	public PagedList<Invoice> getSaleInvoicePage() throws ManagerBeanException {
		if (saleInvoicePage == null) {
			saleInvoicePage = new PagedList<Invoice>();
		}
		if (saleInvoicePage.getList() == null) {
			Connection c = null;
			try {
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				getEngine().fillSaleInvoicePage(c,saleInvoicePage, getParams());
			} catch (AonConnectionException e) {
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return saleInvoicePage;
	}
	
	public PagedList<Invoice> getCostInvoicePage() throws ManagerBeanException {
		if (costInvoicePage == null) {
			costInvoicePage = new PagedList<Invoice>();
		}
		if (costInvoicePage.getList() == null) {
			Connection c = null;
			try {
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				getEngine().fillCostInvoicePage(c,costInvoicePage, getParams());
			} catch (AonConnectionException e) {
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return costInvoicePage;
	}
	
	public PagedList<Delivery> getDeliveryPage() throws ManagerBeanException {
		if (deliveryPage == null) {
			deliveryPage = new PagedList<Delivery>();
		}
		if (deliveryPage.getList() == null) {
		deliveryPage = new PagedList<Delivery>();
			Connection c = null;
			try {
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				deliveryPage.setList( new LinkedList<Delivery>() );	
				ProjectStatParams params = getParams();
				getEngine().fillIncomePage(c,deliveryPage, params);
				getEngine().fillDeliveryPage(c,deliveryPage, params);
			} catch (AonConnectionException e) {
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return deliveryPage;
	}

	public PagedList<Offer> getApprovedOfferPage() throws ManagerBeanException {
		if (approvedOfferPage == null) {
			approvedOfferPage = new PagedList<Offer>();
		}
		if (approvedOfferPage.getList() == null) {
			Connection c = null;
			try {
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				getEngine().fillApprovedOfferPage(c,approvedOfferPage, getParams());
			} catch (AonConnectionException e) {
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return approvedOfferPage;
	}
	
	public PagedList<DailyTracking> getDailyTrackingPage() throws ManagerBeanException {
		if (dailyTrackingPage  == null) {
			dailyTrackingPage = new PagedList<DailyTracking>();
		}
		if (dailyTrackingPage.getList() == null) {
			Connection c = null;
			try {
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				getEngine().fillDailyTrackingPage(c, dailyTrackingPage, getParams());
			} catch (AonConnectionException e) {
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return dailyTrackingPage;
	}
	
	private ProjectStatParams getParams() {
		ProjectStatParams params = new ProjectStatParams();
		params.setProjectId(project.getId());
		params.setFromDate(getFromDate());
		params.setToDate(getToDate());
		return params;
	}
	
}
