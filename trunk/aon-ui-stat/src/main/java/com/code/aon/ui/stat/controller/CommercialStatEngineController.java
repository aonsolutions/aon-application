package com.code.aon.ui.stat.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.seller.Seller;
import com.code.aon.stat.Stat;
import com.code.aon.stat.StatParams;
import com.code.aon.stat.engine.StatEngine;
import com.code.aon.ui.util.AonUtil;

public class CommercialStatEngineController {

	private StatParams params;
	private List<Stat> yearStats;
	private List<Stat> productStats;
	private DataModel yearStatModel;
	private DataModel productStatModel;
	private String reportName;
	private String itemTitle;
	private static final String bundle = "statBundle";
	private Double totalAmount;
	private Integer numInvoices;
	private Double promAmount;
	private Seller seller;	
	private Integer category;
	private DataModel doneOffersModel;
	private DataModel closedOffersModel;
	private DataModel lostOffersModel;
	private DataModel visitsModel;
	private DataModel pendingVisitsModel;
	private List<OfferDetail> doneOffersList;
	private List<OfferDetail> closedOffersList;
	private List<OfferDetail> lostOffersList;
	private List<CommercialTracking> visitsList;
	private List<CommercialTracking> pendingVisitsList;
	
	
	public DataModel getPendingVisitsModel() {
		if (pendingVisitsModel == null) {
			pendingVisitsModel = new ListDataModel(getPendingVisitsList());
		}
		return pendingVisitsModel;
	}

	public void setPendingVisitsModel(DataModel pendingVisitsModel) {
		this.pendingVisitsModel = pendingVisitsModel;
	}

	
	public DataModel getDoneOffersModel() {
		if (doneOffersModel == null) {
			doneOffersModel = new ListDataModel(getDoneOffersList());
		}
		return doneOffersModel;
	}

	public void setDoneOffersModel(DataModel doneOffersModel) {
		this.doneOffersModel = doneOffersModel;
	}

	public DataModel getClosedOffersModel() {
		if (closedOffersModel == null) {
			closedOffersModel = new ListDataModel(getClosedOffersList());
		}
		return closedOffersModel;
	}

	public void setClosedOffersModel(DataModel closedOffersModel) {
		this.closedOffersModel = closedOffersModel;
	}

	public DataModel getLostOffersModel() {
		if (lostOffersModel == null) {
			lostOffersModel = new ListDataModel(getLostOffersList());
		}
		return lostOffersModel;
	}

	public void setLostOffersModel(DataModel lostOffersModel) {
		this.lostOffersModel = lostOffersModel;
	}

	public DataModel getVisitsModel() {
		if (visitsModel == null) {
			visitsModel = new ListDataModel(getVisitsList());
		}
		return visitsModel;
	}

	public void setVisitsModel(DataModel visitsModel) {
		this.visitsModel = visitsModel;
	}

	public List<OfferDetail> getDoneOffersList() {
		return doneOffersList;
	}

	public void setDoneOffersList(List<OfferDetail> doneOffersList) {
		this.doneOffersList = doneOffersList;
	}

	public List<OfferDetail> getClosedOffersList() {
		return closedOffersList;
	}

	public void setClosedOffersList(List<OfferDetail> closedOffersList) {
		this.closedOffersList = closedOffersList;
	}

	public List<OfferDetail> getLostOffersList() {
		return lostOffersList;
	}

	public void setLostOffersList(List<OfferDetail> lostOffersList) {
		this.lostOffersList = lostOffersList;
	}

	public List<CommercialTracking> getVisitsList() {
		return visitsList;
	}

	public void setVisitsList(List<CommercialTracking> visitsList) {
		this.visitsList = visitsList;
	}

	
	public List<CommercialTracking> getPendingVisitsList() {
		return pendingVisitsList;
	}

	public void setPendingVisitsList(List<CommercialTracking> pendingVisitsList) {
		this.pendingVisitsList = pendingVisitsList;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public DataModel getProductStatModel() {
		if (productStatModel == null) {
			productStatModel = new ListDataModel(getProductStats());
		}
		return productStatModel;
	}

	public void setProductStatModel(DataModel productStatModel) {
		this.productStatModel = productStatModel;
	}

	public List<Stat> getProductStats() {
		return productStats;
	}

	public void setProductStats(List<Stat> productStats) {
		this.productStats = productStats;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
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

	public Double getPromAmount() {
		return promAmount;
	}

	public void setPromAmount(Double promAmount) {
		this.promAmount = promAmount;
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
	
	public DataModel getYearStatModel() {
		if (yearStatModel == null) {
			yearStatModel = new ListDataModel(getYearStats());
		}
		return yearStatModel;
	}

	public void setYearStatModel(DataModel yearStatModel) {
		this.yearStatModel = yearStatModel;
	}

	public List<Stat> getYearStats() {
		return yearStats;
	}

	public void setYearStats(List<Stat> yearStats) {
		this.yearStats = yearStats;
	}

	public StatParams getParams() {
		return params;
	}

	public void setParams(StatParams params) {
		this.params = params;
	}
	
	public void onReset(ActionEvent event) {
		params = new StatParams();
		params.setLocale(FacesContext.getCurrentInstance().getViewRoot()
				.getLocale());
		params.setOfferStatuses(null);
		setSeller(null);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		params.setFromDate(c.getTime());
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		params.setToDate(c.getTime());
		
	}
	
	public void onSellerControlStats(ActionEvent e){
		try {
			setClosedOffersModel(null);
			setDoneOffersModel(null);
			setLostOffersModel(null);
			setVisitsModel(null);
			setPendingVisitsModel(null);
			
			//getClosedOffers();
			getDoneOffers();
			//getLostOffers();
			getDoneVisitsModel();
			getPendingVisitModel();
				
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void getDoneVisitsModel()throws ManagerBeanException {
		String select = "select CommercialTracking "
			+ "from CommercialTracking as CommercialTracking "
			+ "where  CommercialTracking.status = 1 AND  CommercialTracking.seller.id = " +seller.getId()
			+ "order by CommercialTracking.date desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		visitsList = query.list();
	}
	
	private void getPendingVisitModel()throws ManagerBeanException {
		String select = "select CommercialTracking "
			+ "from CommercialTracking as CommercialTracking "
			+ "where  CommercialTracking.status = 0 AND  CommercialTracking.seller.id = " +seller.getId()
			+ "order by CommercialTracking.date desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingVisitsList = query.list();
	}

	private void getLostOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
			+ "from OfferDetail as OfferDetail "
			+ "where  OfferDetail.Offer.status = 2 AND  OfferDetail.offer.seller.id = " +seller.getId()
			+ "order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		lostOffersList= query.list();
	}

	private void getDoneOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
			+ "from OfferDetail as OfferDetail "
			+ "where  OfferDetail.offer.seller.id = " +seller.getId()
			+ "order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		doneOffersList= query.list();
	}

	private void getClosedOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
			+ "from OfferDetail as OfferDetail "
			+ "where  OfferDetail.Offer.status = 1 AND OfferDetail.offer.seller.id = " +seller.getId()
			+ "order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		closedOffersList= query.list();
	}

	public void onCategoryStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialCategoryStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle, "stat_report_commercial_category"));
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
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String category = params.get("category");
			setCategory(Integer.parseInt(category));
			getCommercialCategoryProductStats();

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	private void getCommercialCategoryProductStats() throws ManagerBeanException {
		StatEngine se = new StatEngine();
		params.setCategory(category);
		List<Stat> list = new LinkedList<Stat>();
		list.addAll(se.getCommercialCategoryProductsStats(params));
		setProductStats(list);
		calculateTotals(list);
		setReportName(AonUtil.getMessage(bundle, "stat_report_commercial_product"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
		productStatModel = null;
	}
	
	public void onSellerStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialSellerStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle, "stat_report_commercial_seller"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_seller"));
			yearStatModel = null;
			
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onGeozoneStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialGeozoneStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle, "stat_report_commercial_geozone"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_geozone"));
			yearStatModel = null;
			
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void calculateTotals(List<Stat> list) {
		int i = 0;
		setTotalAmount(0.00);
		setNumInvoices(0);
		setPromAmount(0.00);

		while (i < list.size()) {

			totalAmount += list.get(i).getAmount();
			numInvoices += (int) list.get(i).getNumInvoice();

			i++;
		}
		promAmount += totalAmount / numInvoices;
	}
	
	
	
	
}