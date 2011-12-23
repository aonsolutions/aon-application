package com.code.aon.ui.stat.controller;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DefaultPieDataset;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.groupware.DailyTracking;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectStatEngineController {

	private String backAction;

	private Project project;
	private IPriceStrategy invoicePriceStrategy;
	private IPriceStrategy priceStrategy;
	private DataModel approvedOfferModel;
	private DataModel saleInvoiceModel;
	private DataModel costInvoiceModel;
	private DataModel dailyTrackingModel;
	private List<Offer> approvedOfferList;
	private List<Invoice> saleInvoiceList;
	private List<Invoice> costInvoiceList;
	private List<DailyTracking> dailyTrackingList;
	private double totalOffered;
	private double totalSales;
	private double totalCosts;
	private double totalInvoiceCosts;
	private double totalLabour;

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
		this.project = project;
	}

	public IPriceStrategy getInvoicePriceStrategy() {
		if (invoicePriceStrategy == null) {
			invoicePriceStrategy = new InvoicePriceStrategy();
		}
		return invoicePriceStrategy;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public DataModel getApprovedOfferModel() {
		if (approvedOfferModel == null) {
			approvedOfferModel = new ListDataModel(getApprovedOfferList());
		}
		return approvedOfferModel;
	}

	public void setApprovedOfferModel(DataModel approvedOfferModel) {
		this.approvedOfferModel = approvedOfferModel;
	}

	public DataModel getSaleInvoiceModel() {
		if (saleInvoiceModel == null) {
			saleInvoiceModel = new ListDataModel(getSaleInvoiceList());
		}
		return saleInvoiceModel;
	}

	public void setSaleInvoiceModel(DataModel saleInvoiceModel) {
		this.saleInvoiceModel = saleInvoiceModel;
	}

	public DataModel getCostInvoiceModel() {
		if (costInvoiceModel == null) {
			costInvoiceModel = new ListDataModel(getCostInvoiceList());
		}
		return costInvoiceModel;
	}

	public void setCostInvoiceModel(DataModel costInvoiceModel) {
		this.costInvoiceModel = costInvoiceModel;
	}

	public DataModel getDailyTrackingModel() {
		if (dailyTrackingModel == null) {
			dailyTrackingModel = new ListDataModel(getDailyTrackingList());
		}
		return dailyTrackingModel;
	}

	public void setDailyTrackingModel(DataModel dailyTrackingModel) {
		this.dailyTrackingModel = dailyTrackingModel;
	}

	public List<Offer> getApprovedOfferList() {
		try {
			if (approvedOfferList == null) {
				approvedOfferList = getApprovedOffers();
			}
			return approvedOfferList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setApprovedOfferList(List<Offer> approvedOfferList) {
		this.approvedOfferList = approvedOfferList;
	}

	public List<Invoice> getSaleInvoiceList() {
		try {
			if (saleInvoiceList == null) {
				saleInvoiceList = getSaleInvoices();
			}
			return saleInvoiceList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setSaleInvoiceList(List<Invoice> saleInvoiceList) {
		this.saleInvoiceList = saleInvoiceList;
	}

	public List<Invoice> getCostInvoiceList() {
		try {
			if (costInvoiceList == null) {
				costInvoiceList = getCostInvoices();
			}
			return costInvoiceList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setCostInvoiceList(List<Invoice> costInvoiceList) {
		this.costInvoiceList = costInvoiceList;
	}

	public List<DailyTracking> getDailyTrackingList() {
		try {
			if (dailyTrackingList == null) {
				dailyTrackingList = getDailyTrackings();
			}
			return dailyTrackingList;
		} catch (ManagerBeanException e) {
			String msg = "Unable to load data.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setDailyTrackingList(List<DailyTracking> dailyTrackingList) {
		this.dailyTrackingList = dailyTrackingList;
	}

	public double getTotalOffered() {
		if (totalOffered == 0) {
			for (Offer offer : getApprovedOfferList()) {
				totalOffered += CommonUtil.round(getPriceStrategy()
						.getTotalPrice(offer, offer.getTarget()));
			}
		}
		return CommonUtil.round(totalOffered);
	}

	public void setTotalOffered(double totalOffered) {
		this.totalOffered = totalOffered;
	}

	public double getTotalSales() {
		if (totalSales == 0) {
			for (Invoice invoice : getSaleInvoiceList()) {
				totalSales = CommonUtil.round(totalSales + invoice.getTotal());
			}
		}
		return CommonUtil.round(totalSales);
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public double getTotalCosts() {
		if (totalCosts == 0) {
			totalCosts = CommonUtil.round(totalCosts + getTotalInvoiceCosts());
			totalCosts = CommonUtil.round(totalCosts + getTotalLabour());
		}
		return CommonUtil.round(totalCosts);
	}

	public void setTotalCosts(double totalCosts) {
		this.totalCosts = totalCosts;
	}

	public double getTotalInvoiceCosts() {
		if (totalInvoiceCosts == 0) {
			for (Invoice invoice : getCostInvoiceList()) {
				totalInvoiceCosts = CommonUtil.round(totalInvoiceCosts + invoice.getTotal());
			}
		}
		return totalInvoiceCosts;
	}

	public void setTotalInvoiceCosts(double totalInvoiceCosts) {
		this.totalInvoiceCosts = totalInvoiceCosts;
	}

	public double getTotalLabour() {
		if (totalLabour == 0) {
			for (DailyTracking dt : getDailyTrackingList()) {
				totalLabour = CommonUtil.round(totalLabour + dt.getAmount());
			}
		}
		return totalLabour;
	}

	public void setTotalLabour(double totalLabour) {
		this.totalLabour = totalLabour;
	}

	public double getTotalResult() {
		return CommonUtil.round(getTotalSales() - getTotalCosts());
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getApprovedOffers() throws ManagerBeanException {
		String select = "select Offer from Offer as Offer where Offer.status in (1, 4) AND Offer.project.id = "
				+ project.getId() + " order by Offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getSaleInvoices() throws ManagerBeanException {
		String select = "select Invoice from Invoice as Invoice where Invoice.type = 1 AND Invoice.project.id = "
				+ project.getId() + " order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getCostInvoices() throws ManagerBeanException {
		String select = "select Invoice from Invoice as Invoice where Invoice.type <> 1 AND Invoice.project.id = "
				+ project.getId() + " order by Invoice.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<DailyTracking> getDailyTrackings() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DailyTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.DAILY_TRACKING_PROJECT_ID),
				project.getId());
		List<?> list = bean.getList(criteria);
		return (List<DailyTracking>) list;
	}

	public void initializeProjectData() {
		setApprovedOfferModel(null);
		setSaleInvoiceModel(null);
		setCostInvoiceModel(null);
		setApprovedOfferList(null);
		setSaleInvoiceList(null);
		setCostInvoiceList(null);
		setDailyTrackingModel(null);
		setDailyTrackingList(null);
		setTotalOffered(0);
		setTotalSales(0);
		setTotalCosts(0);
		setTotalLabour(0);
		setTotalInvoiceCosts(0);
	}

	public double getApprovedOfferTotal() throws ManagerBeanException {
		Offer offer = (Offer) getApprovedOfferModel().getRowData();
		return getPriceStrategy().getTotalPrice(offer, offer.getTarget());
	}

	public String backAction() {
		if (StringUtils.isNotBlank(getBackAction())) {
			return getBackAction();
		}
		return "projectCommercial_form";
	}

	public Date getLastModified() {
		return new Date();
	}

	public void paintPieChart(OutputStream out, Object data) throws IOException {
		DefaultPieDataset chartDataset = new DefaultPieDataset();
		String incomeAlias = AonUtil.getMessage("statBundle","stat_income_amount");
		String invoiceCostAlias = AonUtil.getMessage("statBundle", "stat_invoice_cost_amount");
		String labourAlias = AonUtil.getMessage("statBundle","stat_labour_amount");
		chartDataset.setValue(incomeAlias, getTotalSales());
		chartDataset.setValue(invoiceCostAlias, getTotalInvoiceCosts());
		chartDataset.setValue(labourAlias, getTotalLabour());
		JFreeChart chart = ChartFactory.createPieChart3D(null, chartDataset,
				true, true, false);
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setSectionPaint(incomeAlias, new Color(176, 224, 230));
		plot.setSectionPaint(invoiceCostAlias, new Color(000, 149, 182));
		plot.setSectionPaint(labourAlias, new Color(021,  96, 189));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDarkerSides(true);
		plot.setLabelBackgroundPaint(new Color(240, 255, 255));
		plot.setNoDataMessage(AonUtil.getMessage("bundle","aon_search_no_results"));
		plot.setOutlinePaint(null);
		plot.setCircular(false);
		int width = 350;
		int height = 200;
		float quality = 1;
		ChartUtilities.writeChartAsJPEG(out, quality, chart, width, height);
	}

	public void paintBarChart(OutputStream out, Object data) throws IOException {
		String offeredAlias = AonUtil.getMessage("statBundle","stat_offer_amount");
		String incomeAlias = AonUtil.getMessage("statBundle","stat_income_amount");
		String invoiceCostAlias = AonUtil.getMessage("statBundle", "stat_invoice_cost_amount");
		String labourAlias = AonUtil.getMessage("statBundle","stat_labour_amount");
		String costAlias = AonUtil.getMessage("statBundle","stat_cost_amount");
		String resultAlias = AonUtil.getMessage("statBundle","stat_result");
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(getTotalOffered(), offeredAlias, offeredAlias);
		dataset.addValue(getTotalSales(), incomeAlias, incomeAlias);
		dataset.addValue(getTotalInvoiceCosts(), invoiceCostAlias, costAlias);
		dataset.addValue(getTotalLabour(), labourAlias, costAlias);
		dataset.addValue(getTotalResult(),resultAlias, resultAlias);
		
		JFreeChart chart = ChartFactory.createStackedBarChart3D(null,null,null,
				dataset,PlotOrientation.HORIZONTAL,true,true,false);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		StackedBarRenderer3D renderer = (StackedBarRenderer3D) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(123, 104, 138));
		renderer.setSeriesPaint(1, new Color(176, 224, 230));
		renderer.setSeriesPaint(2, new Color(000, 149, 182));
		renderer.setSeriesPaint(3, new Color(021,  96, 189));
		renderer.setSeriesPaint(4, getTotalResult()>=0?Color.BLUE:Color.RED);
		plot.setNoDataMessage(AonUtil.getMessage("bundle","aon_search_no_results"));
		plot.setRangeGridlinePaint(new Color(150,150,150));
		plot.setBackgroundPaint(Color.WHITE);
		int width = 350;
		int height = 200;
		float quality = 1;
		ChartUtilities.writeChartAsJPEG(out, quality, chart, width, height);
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
				taskBean.getFieldName(IEntityAlias.TASK_START_DATE),
				fromDate);
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
		JFreeChart chart = ChartFactory.createGanttChart(null, null, null, collection, true, false, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setRangeGridlinePaint(new Color(150,150,150));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setNoDataMessage(AonUtil.getMessage("bundle","aon_search_no_results"));
		int width = 1024;
		int height = i * 15;
		height = height < 125 ? 125 : height;
		float quality = 1;
		ChartUtilities.writeChartAsJPEG(out, quality, chart, width, height);
	}
}