package com.code.aon.ui.stat.controller;

import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.stat.Stat;
import com.code.aon.stat.StatParams;
import com.code.aon.stat.engine.StatEngine;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CommercialStatEngineController {
	
	private static final String COMMERCIAL_TRACKING_TARGET_ID = "CommercialTracking.project.target.id";

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
	private DataModel pendingOffersModel;
	private DataModel visitsModel;
	private DataModel pendingVisitsModel;
	private DataModel summaryModel;
	private DataModel activityModel;
	private DataModel offersModel;
	private List<OfferDetail> doneOffersList;
	private List<OfferDetail> closedOffersList;
	private List<OfferDetail> lostOffersList;
	private List<OfferDetail> pendingOffersList;
	private List<CommercialTracking> visitsList;
	private List<CommercialTracking> pendingVisitsList;
	private List<ControlSummary> summary;
	private List<ControlSummary> activitySummary;
	private List<Integer> summaryGraph;
	private List<Offer> offerList;
	private Integer numVisits;
	private Integer numPendingVisits;
	private Integer numOffers;
	private Integer numAprovedOffers;
	private Integer numPendingOffers;
	private Integer numLostOffers;
	private Integer numVisitsTot;
	private Integer numPendingVisitsTot;
	private Integer numOffersTot;
	private Integer numAprovedOffersTot;
	private Integer numLostOffersTot;
	private Integer numPendingOffersTot;
	private Integer count;
	private Target target;
	private Product product;
	private ProductCategory productCategory;
	private Integer controlType;
	private Boolean commercialActivityStatus;
	private Boolean showCommercialActivities;
	private Boolean showZeroLines;
	private String sellerName;
	private String categoryName;
	private String productName;
	private String zoneName;
	private String targetName;
	private List<CommercialTracking> activitiesList;
	private static final String COMMERCIAL_TRACKING_CONTROLLER_NAME = "commercialTracking";
	private static final String OFFER_CONTROLLER_NAME = "offer";
	private String offerBackAction;
	private IPriceStrategy priceStrategy;

	
	
	
	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public String getOfferBackAction() {
		return offerBackAction;
	}

	public void setOfferBackAction(String offerBackAction) {
		this.offerBackAction = offerBackAction;
	}

	public DataModel getOffersModel() {
		if (offersModel == null) {
			offersModel = new ListDataModel(getOfferList());
		}
		return offersModel;
	}

	public void setOffersModel(DataModel offersModel) {
		this.offersModel = offersModel;
	}

	public List<Offer> getOfferList() {
		return offerList;
	}

	public void setOfferList(List<Offer> offerList) {
		this.offerList = offerList;
	}

	public Boolean getShowZeroLines() {
		return showZeroLines;
	}

	public void setShowZeroLines(Boolean showZeroLines) {
		this.showZeroLines = showZeroLines;
	}

	public List<CommercialTracking> getActivitiesList() {
		return activitiesList;
	}

	public void setActivitiesList(List<CommercialTracking> activitiesList) {
		this.activitiesList = activitiesList;
	}

	public Boolean getCommercialActivityStatus() {
		return commercialActivityStatus;
	}

	public void setCommercialActivityStatus(Boolean commercialActivityStatus) {
		this.commercialActivityStatus = commercialActivityStatus;
	}

	public List<ControlSummary> getActivitySummary() {
		return activitySummary;
	}

	public void setActivitySummary(List<ControlSummary> activitySummary) {
		this.activitySummary = activitySummary;
	}

	public Integer getNumPendingOffersTot() {
		return numPendingOffersTot;
	}

	public void setNumPendingOffersTot(Integer numPendingOffersTot) {
		this.numPendingOffersTot = numPendingOffersTot;
	}

	public Integer getControlType() {
		return controlType;
	}

	public void setControlType(Integer controlType) {
		this.controlType = controlType;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public List<Integer> getSummaryGraph() {
		return summaryGraph;
	}

	public void setSummaryGraph(List<Integer> summaryGraph) {
		this.summaryGraph = summaryGraph;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getNumVisitsTot() {
		return numVisitsTot;
	}

	public void setNumVisitsTot(Integer numVisitsTot) {
		this.numVisitsTot = numVisitsTot;
	}

	public Integer getNumPendingVisitsTot() {
		return numPendingVisitsTot;
	}

	public void setNumPendingVisitsTot(Integer numPendingVisitsTot) {
		this.numPendingVisitsTot = numPendingVisitsTot;
	}

	public Integer getNumOffersTot() {
		return numOffersTot;
	}

	public void setNumOffersTot(Integer numOffersTot) {
		this.numOffersTot = numOffersTot;
	}

	public Integer getNumAprovedOffersTot() {
		return numAprovedOffersTot;
	}

	public void setNumAprovedOffersTot(Integer numAprovedOffersTot) {
		this.numAprovedOffersTot = numAprovedOffersTot;
	}

	public Integer getNumLostOffersTot() {
		return numLostOffersTot;
	}

	public void setNumLostOffersTot(Integer numLostOffersTot) {
		this.numLostOffersTot = numLostOffersTot;
	}

	public DataModel getSummaryModel() {
		if (summaryModel == null) {
			summaryModel = new ListDataModel(getSummary());
		}
		return summaryModel;

	}

	public void setSummaryModel(DataModel summaryModel) {
		this.summaryModel = summaryModel;
	}

	public DataModel getActivityModel() {
		if (activityModel == null) {
			activityModel = new ListDataModel(getActivitySummary());
		}
		return activityModel;
	}

	public void setActivityModel(DataModel activityModel) {
		this.activityModel = activityModel;
	}

	public List<ControlSummary> getSummary() {
		return summary;
	}

	public void setSummary(List<ControlSummary> summary) {
		this.summary = summary;
	}

	public Integer getNumVisits() {
		return numVisits;
	}

	public void setNumVisits(Integer numVisits) {
		this.numVisits = numVisits;
	}

	public Integer getNumPendingVisits() {
		return numPendingVisits;
	}

	public void setNumPendingVisits(Integer numPendingVisits) {
		this.numPendingVisits = numPendingVisits;
	}

	public Integer getNumOffers() {
		return numOffers;
	}

	public void setNumOffers(Integer numOffers) {
		this.numOffers = numOffers;
	}

	public Integer getNumAprovedOffers() {
		return numAprovedOffers;
	}

	public void setNumAprovedOffers(Integer numAprovedOffers) {
		this.numAprovedOffers = numAprovedOffers;
	}

	public Integer getNumLostOffers() {
		return numLostOffers;
	}

	public void setNumLostOffers(Integer numLostOffers) {
		this.numLostOffers = numLostOffers;
	}

	public Integer getNumPendingOffers() {
		return numPendingOffers;
	}

	public void setNumPendingOffers(Integer numPendingOffers) {
		this.numPendingOffers = numPendingOffers;
	}

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

	public DataModel getPendingOffersModel() {
		if (pendingOffersModel == null) {
			pendingOffersModel = new ListDataModel(getPendingOffersList());
		}
		return pendingOffersModel;
	}

	public void setPendingOffersModel(DataModel pendingOffersModel) {
		this.pendingOffersModel = pendingOffersModel;
	}

	public List<OfferDetail> getPendingOffersList() {
		return pendingOffersList;
	}

	public void setPendingOffersList(List<OfferDetail> pendingOffersList) {
		this.pendingOffersList = pendingOffersList;
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

	public void onSellerType(ActionEvent event) {
		setControlType(0);
	}

	public void onTargetType(ActionEvent event) {
		setControlType(1);
	}

	public void onProductType(ActionEvent event) {
		setControlType(2);
	}
	
	public void onCategoryType(ActionEvent event) {
		setControlType(3);
	}
	
	public void onGeozoneType(ActionEvent event) {
		setControlType(4);
	}

	public Date getFromDate() {
		return this.params.getFromDate();
	}

	public Date getToDate() {
		return this.params.getToDate();
	}

	public Boolean getShowCommercialActivities() {
		if (controlType == 0 || controlType == 1) {
			return true;
		} else
			return false;
	}

	public Boolean getRegistryType() {
		if (controlType == 0) {
			return true;
		} else
			return false;
	}
	
	public double getOfferTotalPrice() throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(((Offer) getOffersModel().getRowData()), ((Offer) getOffersModel().getRowData()).getTarget());
	}	
	public double getDoneOfferTotalPrice() throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(((OfferDetail) getDoneOffersModel().getRowData()).getOffer(), ((OfferDetail)getDoneOffersModel().getRowData()).getOffer().getTarget());
	}
	public double getClosedOfferTotalPrice() throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(((OfferDetail) getClosedOffersModel().getRowData()).getOffer(), ((OfferDetail) getClosedOffersModel().getRowData()).getOffer().getTarget());
	}
	public double getLostOfferTotalPrice() throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(((OfferDetail) getLostOffersModel().getRowData()).getOffer(), ((OfferDetail)getLostOffersModel().getRowData()).getOffer().getTarget());
	}
	public double getPendingOfferTotalPrice() throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(((OfferDetail) getPendingOffersModel().getRowData()).getOffer(), ((OfferDetail) getPendingOffersModel().getRowData()).getOffer().getTarget());
	}

	public void setShowCommercialActivities(Boolean showCommercialActivities) {
		this.showCommercialActivities = showCommercialActivities;
	}

	public void onReset(ActionEvent event) {
		params = new StatParams();
		params.setLocale(FacesContext.getCurrentInstance().getViewRoot()
				.getLocale());
		OfferStatus[] defaultOfferStatus = { OfferStatus.APPROVED };
		params.setOfferStatuses(defaultOfferStatus);
		setSeller(null);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		params.setFromDate(c.getTime());
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		params.setToDate(c.getTime());
		count = 0;
		setShowZeroLines(false);

	}

	public void onSellerControlStats(ActionEvent e) {
		try {
			setNumAprovedOffers(0);
			setNumLostOffers(0);
			setNumVisits(0);
			setNumPendingVisits(0);
			setNumPendingOffers(0);
			setNumOffers(0);

			setNumVisitsTot(0);
			setNumAprovedOffersTot(0);
			setNumLostOffersTot(0);
			setNumPendingVisitsTot(0);
			setNumOffersTot(0);
			setNumPendingOffersTot(0);

			setClosedOffersModel(null);
			setDoneOffersModel(null);
			setLostOffersModel(null);
			setPendingOffersModel(null);
			setVisitsModel(null);
			setPendingVisitsModel(null);

			getDoneOffers();
			getDoneVisitsModel();
			getPendingVisitModel();

		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void getSellerControlStats() {
		try {
			setNumAprovedOffers(0);
			setNumLostOffers(0);
			setNumVisits(0);
			setNumPendingVisits(0);
			setNumPendingOffers(0);
			setNumOffers(0);

			if (count < 1) {
				setNumVisitsTot(0);
				setNumAprovedOffersTot(0);
				setNumLostOffersTot(0);
				setNumPendingVisitsTot(0);
				setNumOffersTot(0);
				setNumPendingOffersTot(0);
			}

			setClosedOffersModel(null);
			setDoneOffersModel(null);
			setLostOffersModel(null);
			setVisitsModel(null);
			setPendingVisitsModel(null);
			setPendingOffersModel(null);
			setSummaryModel(null);

			getDoneOffers();
			getDoneVisitsModel();
			getPendingVisitModel();

		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void getTargetControlStats() {
		try {
			setNumAprovedOffers(0);
			setNumLostOffers(0);
			setNumVisits(0);
			setNumPendingVisits(0);
			setNumPendingOffers(0);
			setNumOffers(0);

			if (count < 1) {
				setNumVisitsTot(0);
				setNumAprovedOffersTot(0);
				setNumLostOffersTot(0);
				setNumPendingVisitsTot(0);
				setNumOffersTot(0);
				setNumPendingOffersTot(0);
			}

			setClosedOffersModel(null);
			setDoneOffersModel(null);
			setLostOffersModel(null);
			setVisitsModel(null);
			setPendingVisitsModel(null);
			setPendingOffersModel(null);
			setSummaryModel(null);

			getTargetDoneOffers();
			getTargetDoneVisitsModel();
			getTargetPendingVisitModel();

		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void getProductControlStats() {
		try {
			setNumAprovedOffers(0);
			setNumLostOffers(0);
			setNumVisits(0);
			setNumPendingVisits(0);
			setNumPendingOffers(0);
			setNumOffers(0);

			if (count < 1) {
				setNumVisitsTot(0);
				setNumAprovedOffersTot(0);
				setNumLostOffersTot(0);
				setNumPendingVisitsTot(0);
				setNumOffersTot(0);
				setNumPendingOffersTot(0);
			}

			setClosedOffersModel(null);
			setDoneOffersModel(null);
			setLostOffersModel(null);
			setPendingOffersModel(null);
			setVisitsModel(null);
			setPendingVisitsModel(null);
			setSummaryModel(null);

			getProductDoneOffers();

		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void getCategoryControlStats() {
		try {
			setNumAprovedOffers(0);
			setNumLostOffers(0);
			setNumVisits(0);
			setNumPendingVisits(0);
			setNumPendingOffers(0);
			setNumOffers(0);

			if (count < 1) {
				setNumVisitsTot(0);
				setNumAprovedOffersTot(0);
				setNumLostOffersTot(0);
				setNumPendingVisitsTot(0);
				setNumOffersTot(0);
				setNumPendingOffersTot(0);
			}

			setClosedOffersModel(null);
			setDoneOffersModel(null);
			setLostOffersModel(null);
			setPendingOffersModel(null);
			setVisitsModel(null);
			setPendingVisitsModel(null);
			setSummaryModel(null);

			getCategoryDoneOffers();

		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void getDoneVisitsModel() throws ManagerBeanException {
		PreparedStatement ps = null;

		String select = "select CommercialTracking "
				+ "from CommercialTracking as CommercialTracking "
				+ " where " + DomainManager.getSQLWhereClause("CommercialTracking.domain") 
				+ " AND CommercialTracking.status = 1 AND  CommercialTracking.seller.id = "
				+ seller.getId()
				+ " AND   CommercialTracking.date >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND CommercialTracking.date <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' order by  CommercialTracking.activity.id,CommercialTracking.date desc";

		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		visitsList = query.list();
		setNumVisits(visitsList.size());
	}

	private void getPendingVisitModel() throws ManagerBeanException {
		String select = "select CommercialTracking "
				+ "from CommercialTracking as CommercialTracking "
				+ " where " + DomainManager.getSQLWhereClause("CommercialTracking.domain")
				+ " AND CommercialTracking.status = 0 AND  CommercialTracking.seller.id = "
				+ seller.getId()
				+ " AND   CommercialTracking.date >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND CommercialTracking.date <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' order by CommercialTracking.activity.id,CommercialTracking.date desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingVisitsList = query.list();
		setNumPendingVisits(pendingVisitsList.size());
	}

	private void getDoneOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
				+ "from OfferDetail as OfferDetail "
				+ " where " + DomainManager.getSQLWhereClause("OfferDetail.domain")
				+ " AND OfferDetail.offer.seller.id = " + seller.getId()
				+ " AND   OfferDetail.offer.issueDate >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND OfferDetail.offer.issueDate <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' group by OfferDetail.offer.id"
				+ " order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		doneOffersList = query.list();
		closedOffersList = new LinkedList<OfferDetail>();
		lostOffersList = new LinkedList<OfferDetail>();
		pendingOffersList = new LinkedList<OfferDetail>();

		for (int i = 0; i < doneOffersList.size(); i++) {
			numOffers++;
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.APPROVED) {
				numAprovedOffers++;
				closedOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.REFUSED) {
				numLostOffers++;
				lostOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.PENDING) {
				numPendingOffers++;
				pendingOffersList.add(doneOffersList.get(i));
			}
		}
	}

	public void onSummary(ActionEvent e) throws ManagerBeanException {
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		List<ITransferObject> list;
		list = sellerBean.getList(null);
		summary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list) {
			Seller inv = (Seller) to;
			ControlSummary s = new ControlSummary();
			this.setSeller(inv);
			getSellerControlStats();
			count++;
			s.setId(inv.getId());
			s.setName(inv.getRegistry().getFullName());
			s.setNumVisits(numVisits);
			s.setNumOffers(numOffers);
			s.setNumPendingVisits(numPendingVisits);
			s.setNumAprovedOffers(numAprovedOffers);
			s.setNumLostOffers(numLostOffers);
			s.setNumPendingOffers(numPendingOffers);
			numVisitsTot += numVisits;
			numOffersTot += numOffers;
			numPendingVisitsTot += numPendingVisits;
			numAprovedOffersTot += numAprovedOffers;
			numLostOffersTot += numLostOffers;
			numPendingOffersTot += numPendingOffers;
			if (showZeroLines) {
				summary.add(s);
			} else if (numVisits != 0 || numOffers != 0
					|| numPendingVisits != 0) {
				summary.add(s);
			}
		}
		count = 0;
		setReportName(AonUtil.getMessage(bundle, "seller_stat_control_seller"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_seller"));
	}

	public void onTargetSummary(ActionEvent e) throws ManagerBeanException {
		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		List<ITransferObject> list;
		list = targetBean.getList(null);
		summary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list) {
			Target tg = (Target) to;
			ControlSummary s = new ControlSummary();
			this.setTarget(tg);
			getTargetControlStats();
			count++;
			s.setId(tg.getId());
			s.setName(tg.getRegistry().getFullName());
			s.setNumVisits(numVisits);
			s.setNumOffers(numOffers);
			s.setNumPendingVisits(numPendingVisits);
			s.setNumAprovedOffers(numAprovedOffers);
			s.setNumLostOffers(numLostOffers);
			s.setNumPendingOffers(numPendingOffers);
			numVisitsTot += numVisits;
			numOffersTot += numOffers;
			numPendingVisitsTot += numPendingVisits;
			numAprovedOffersTot += numAprovedOffers;
			numLostOffersTot += numLostOffers;
			numPendingOffersTot += numPendingOffers;
			if (showZeroLines) {
				summary.add(s);
			} else if (numVisits != 0 || numOffers != 0
					|| numPendingVisits != 0) {
				summary.add(s);
			}
		}
		count = 0;
		setReportName(AonUtil.getMessage(bundle, "seller_stat_control_target"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_target"));

	}

	public void onProductSummary(ActionEvent e) throws ManagerBeanException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		List<ITransferObject> list;
		list = productBean.getList(null);
		summary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list) {
			Product pro = (Product) to;
			ControlSummary s = new ControlSummary();
			this.setProduct(pro);
			getProductControlStats();
			count++;
			s.setId(pro.getId());
			s.setName(pro.getName());
			s.setNumVisits(numVisits);
			s.setNumOffers(numOffers);
			s.setNumPendingVisits(numPendingVisits);
			s.setNumAprovedOffers(numAprovedOffers);
			s.setNumLostOffers(numLostOffers);
			s.setNumPendingOffers(numPendingOffers);
			numVisitsTot += numVisits;
			numOffersTot += numOffers;
			numPendingVisitsTot += numPendingVisits;
			numAprovedOffersTot += numAprovedOffers;
			numLostOffersTot += numLostOffers;
			numPendingOffersTot += numPendingOffers;
			if (showZeroLines) {
				summary.add(s);
			} else if (numOffers != 0) {
				summary.add(s);
			}
		}
		count = 0;
		setReportName(AonUtil.getMessage(bundle, "seller_stat_control_product"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
	}
	
	public void onCategorySummary(ActionEvent e) throws ManagerBeanException {
		IManagerBean productBean = BeanManager.getManagerBean(ProductCategory.class);
		List<ITransferObject> list;
		list = productBean.getList(null);
		summary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list) {
			ProductCategory pc = (ProductCategory) to;
			ControlSummary s = new ControlSummary();
			this.setProductCategory(pc);
			getCategoryControlStats();
			count++;
			s.setId(pc.getId());
			s.setName(pc.getName());
			s.setNumVisits(numVisits);
			s.setNumOffers(numOffers);
			s.setNumPendingVisits(numPendingVisits);
			s.setNumAprovedOffers(numAprovedOffers);
			s.setNumLostOffers(numLostOffers);
			s.setNumPendingOffers(numPendingOffers);
			numVisitsTot += numVisits;
			numOffersTot += numOffers;
			numPendingVisitsTot += numPendingVisits;
			numAprovedOffersTot += numAprovedOffers;
			numLostOffersTot += numLostOffers;
			numPendingOffersTot += numPendingOffers;
			if (showZeroLines) {
				summary.add(s);
			} else if (numOffers != 0) {
				summary.add(s);
			}
		}
		count = 0;
		setReportName(AonUtil.getMessage(bundle, "seller_stat_control_category"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_category"));
	}

	public void onSellerDetail(ActionEvent e) throws ManagerBeanException {

		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(sellerBean
				.getFieldName(IEntityAlias.SELLER_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list;
		list = sellerBean.getList(criteria);
		this.setSeller((Seller) list.get(0));
		getSellerControlStats();

	}

	public void onTargetDetail(ActionEvent e) throws ManagerBeanException {

		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(targetBean
				.getFieldName(IEntityAlias.TARGET_REGISTRY_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list;
		list = targetBean.getList(criteria);
		this.setTarget((Target) list.get(0));
		getTargetControlStats();

	}

	public void onProductDetail(ActionEvent e) throws ManagerBeanException {

		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(productBean
				.getFieldName(IEntityAlias.PRODUCT_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list;
		list = productBean.getList(criteria);
		this.setProduct((Product) list.get(0));
		getProductControlStats();

	}
	
	public void onCategoryDetail(ActionEvent e) throws ManagerBeanException {

		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean
				.getFieldName(IEntityAlias.PRODUCT_CATEGORY_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list;
		list = categoryBean.getList(criteria);
		this.setProductCategory((ProductCategory) list.get(0));
		getCategoryControlStats();

	}

	public void onSellerActivities(ActionEvent e) throws ManagerBeanException {

		setCommercialActivityStatus(true);
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		Criteria cri = new Criteria();
		cri.addEqualExpression(sellerBean.getFieldName(IEntityAlias.SELLER_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list = sellerBean.getList(cri);
		this.setSeller((Seller) list.get(0));

		setActivityModel(null);
		IManagerBean commercialActivityBean = BeanManager
				.getManagerBean(CommercialActivity.class);
		List<ITransferObject> list1 = new LinkedList<ITransferObject>();
		list1 = commercialActivityBean.getList(null);
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		List<ITransferObject> list2 = new LinkedList<ITransferObject>();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_SELLER_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		criteria.addGreaterThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getFromDate());
		;
		criteria.addLessThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getToDate());
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_STATUS),
				CommercialTrackingStatus.CLOSED);
		list2 = commercialTrackingBean.getList(criteria);

		activitySummary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list1) {
			CommercialActivity act = (CommercialActivity) to;
			ControlSummary s = new ControlSummary();
			s.setId(act.getId());
			s.setName(act.getName());
			int count = 0;
			for (ITransferObject to2 : list2) {
				CommercialTracking ct = (CommercialTracking) to2;
				if (act.getId().equals(ct.getActivity().getId())) {
					count++;
				}
			}
			s.setNumVisits(count);
			if (s.getNumVisits() != 0) {
				activitySummary.add(s);
			}
		}
		setReportName(AonUtil.getMessage(bundle, "report_activities_view"));
		setItemTitle(AonUtil.getMessage(bundle, "report_activity"));

	}

	public void onActivitySelect(ActionEvent e) throws ManagerBeanException {
		BasicController c = (BasicController) AonUtil
				.getRegisteredBean(COMMERCIAL_TRACKING_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_ID),
				((CommercialTracking) this.getVisitsModel().getRowData())
						.getId());
		c.clearCriteria();
		c.setCriteria(criteria);
		c.onSearch(e);
		c.onSelectFirst(e);
	}
	
	public void onOfferPdf(ActionEvent e) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_ID), ((Offer) this.getOffersModel().getRowData()).getId());
		FormUtil.getController(OFFER_CONTROLLER_NAME).setCriteria(criteria);
	}
	
	public void onDoneOfferPdf(ActionEvent e) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_ID), ((OfferDetail) this.getDoneOffersModel().getRowData()).getOffer().getId());
		FormUtil.getController(OFFER_CONTROLLER_NAME).setCriteria(criteria);
	}
	
	public void onLostOfferPdf(ActionEvent e) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_ID), ((OfferDetail) this.getLostOffersModel().getRowData()).getOffer().getId());
		FormUtil.getController(OFFER_CONTROLLER_NAME).setCriteria(criteria);
	}
	
	public void onPendingOfferPdf(ActionEvent e) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_ID), ((OfferDetail) this.getPendingOffersModel().getRowData()).getOffer().getId());
		FormUtil.getController(OFFER_CONTROLLER_NAME).setCriteria(criteria);
	}
	
	public void onClosedOfferPdf(ActionEvent e) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_ID), ((OfferDetail) this.getClosedOffersModel().getRowData()).getOffer().getId());
		FormUtil.getController(OFFER_CONTROLLER_NAME).setCriteria(criteria);
	}

	public void onActivityList(ActionEvent e) throws ManagerBeanException {
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		Criteria criteria = new Criteria();
		criteria
				.addEqualExpression(
						commercialTrackingBean
								.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_ACTIVITY_ID),
						((ControlSummary) getActivityModel().getRowData())
								.getId());
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_SELLER_ID),
				this.getSeller().getId());
		criteria.addGreaterThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getFromDate());
		;
		criteria.addLessThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getToDate());
		CommercialTrackingStatus c;
		if (commercialActivityStatus) {
			c = CommercialTrackingStatus.CLOSED;
		} else
			c = CommercialTrackingStatus.PENDING;
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_STATUS), c);
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list = commercialTrackingBean.getList(criteria);

		activitiesList = new LinkedList<CommercialTracking>();
		for (ITransferObject to : list) {
			CommercialTracking cmt = (CommercialTracking) to;

			activitiesList.add(cmt);
		}
		setVisitsModel(new ListDataModel(getActivitiesList()));

		setReportName(AonUtil.getMessage(bundle, "report_activities_view"));
	}

	public void onPendingSellerActivities(ActionEvent e)
			throws ManagerBeanException {
		setCommercialActivityStatus(false);
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		Criteria cri = new Criteria();
		cri.addEqualExpression(sellerBean.getFieldName(IEntityAlias.SELLER_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list;
		list = sellerBean.getList(cri);
		this.setSeller((Seller) list.get(0));

		setActivityModel(null);
		IManagerBean commercialActivityBean = BeanManager
				.getManagerBean(CommercialActivity.class);
		List<ITransferObject> list1;
		list1 = commercialActivityBean.getList(null);
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		List<ITransferObject> list2;
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_SELLER_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		criteria.addGreaterThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getFromDate());
		;
		criteria.addLessThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getToDate());
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_STATUS),
				CommercialTrackingStatus.PENDING);
		list2 = commercialTrackingBean.getList(criteria);

		activitySummary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list1) {
			CommercialActivity act = (CommercialActivity) to;
			ControlSummary s = new ControlSummary();
			s.setId(act.getId());
			s.setName(act.getName());
			int count = 0;
			for (ITransferObject to2 : list2) {
				CommercialTracking ct = (CommercialTracking) to2;
				if (act.getId().equals(ct.getActivity().getId())) {
					count++;
				}
			}

			s.setNumVisits(count);
			if (s.getNumVisits() != 0) {
				activitySummary.add(s);
			}
		}

		setReportName(AonUtil.getMessage(bundle, "report_activities_view"));
		setItemTitle(AonUtil.getMessage(bundle, "report_activity"));
	}

	public void onTargetActivities(ActionEvent e) throws ManagerBeanException {
		setCommercialActivityStatus(true);
		IManagerBean TargetBean = BeanManager.getManagerBean(Target.class);
		Criteria cri = new Criteria();
		cri.addEqualExpression(TargetBean
				.getFieldName(IEntityAlias.TARGET_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list = TargetBean.getList(cri);
		this.setTarget((Target) list.get(0));

		setActivityModel(null);
		IManagerBean commercialActivityBean = BeanManager
				.getManagerBean(CommercialActivity.class);
		List<ITransferObject> list1 = new LinkedList<ITransferObject>();
		list1 = commercialActivityBean.getList(null);
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		List<ITransferObject> list2 = new LinkedList<ITransferObject>();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(COMMERCIAL_TRACKING_TARGET_ID,
				((ControlSummary) getSummaryModel().getRowData()).getId());
		criteria.addGreaterThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getFromDate());
		;
		criteria.addLessThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getToDate());
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_STATUS),
				CommercialTrackingStatus.CLOSED);
		list2 = commercialTrackingBean.getList(criteria);

		activitySummary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list1) {
			CommercialActivity act = (CommercialActivity) to;
			ControlSummary s = new ControlSummary();
			s.setId(act.getId());
			s.setName(act.getName());
			int count = 0;
			for (ITransferObject to2 : list2) {
				CommercialTracking ct = (CommercialTracking) to2;
				if (act.getId().equals(ct.getActivity().getId())) {
					count++;
				}
			}
			s.setNumVisits(count);
			if (s.getNumVisits() != 0) {
				activitySummary.add(s);
			}
		}
		setReportName(AonUtil.getMessage(bundle, "report_activities_view"));
		setItemTitle(AonUtil.getMessage(bundle, "report_activity"));

	}

	public void onPendingTargetActivities(ActionEvent e)
			throws ManagerBeanException {
		setCommercialActivityStatus(false);
		IManagerBean TargetBean = BeanManager.getManagerBean(Target.class);
		Criteria cri = new Criteria();
		cri.addEqualExpression(TargetBean
				.getFieldName(IEntityAlias.TARGET_ID),
				((ControlSummary) getSummaryModel().getRowData()).getId());
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list = TargetBean.getList(cri);
		this.setTarget((Target) list.get(0));

		setActivityModel(null);
		IManagerBean commercialActivityBean = BeanManager
				.getManagerBean(CommercialActivity.class);
		List<ITransferObject> list1 = new LinkedList<ITransferObject>();
		list1 = commercialActivityBean.getList(null);
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		List<ITransferObject> list2 = new LinkedList<ITransferObject>();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(COMMERCIAL_TRACKING_TARGET_ID,
				((ControlSummary) getSummaryModel().getRowData()).getId());
		criteria.addGreaterThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getFromDate());
		;
		criteria.addLessThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getToDate());
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_STATUS),
				CommercialTrackingStatus.PENDING);
		list2 = commercialTrackingBean.getList(criteria);

		activitySummary = new LinkedList<ControlSummary>();
		for (ITransferObject to : list1) {
			CommercialActivity act = (CommercialActivity) to;
			ControlSummary s = new ControlSummary();
			s.setId(act.getId());
			s.setName(act.getName());
			int count = 0;
			for (ITransferObject to2 : list2) {
				CommercialTracking ct = (CommercialTracking) to2;
				if (act.getId().equals(ct.getActivity().getId())) {
					count++;
				}
			}
			s.setNumVisits(count);
			if (s.getNumVisits() != 0) {
				activitySummary.add(s);
			}
		}
		setReportName(AonUtil.getMessage(bundle, "report_activities_view"));
		setItemTitle(AonUtil.getMessage(bundle, "report_activity"));

	}

	public void onTargetActivityList(ActionEvent e) throws ManagerBeanException {
		IManagerBean commercialTrackingBean = BeanManager
				.getManagerBean(CommercialTracking.class);
		Criteria criteria = new Criteria();
		criteria
				.addEqualExpression(
						commercialTrackingBean
								.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_ACTIVITY_ID),
						((ControlSummary) getActivityModel().getRowData())
								.getId());
		criteria.addEqualExpression(COMMERCIAL_TRACKING_TARGET_ID,
				this.getTarget().getId());
		criteria.addGreaterThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getFromDate());
		;
		criteria.addLessThanOrEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_DATE),
				this.params.getToDate());
		CommercialTrackingStatus c;
		if (commercialActivityStatus) {
			c = CommercialTrackingStatus.CLOSED;
		} else
			c = CommercialTrackingStatus.PENDING;
		criteria.addEqualExpression(commercialTrackingBean
				.getFieldName(IEntityAlias.COMMERCIAL_TRACKING_STATUS), c);
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list = commercialTrackingBean.getList(criteria);
		activitiesList = new LinkedList<CommercialTracking>();
		for (ITransferObject to : list) {
			CommercialTracking cmt = (CommercialTracking) to;
			activitiesList.add(cmt);
		}
		setVisitsModel(new ListDataModel(getActivitiesList()));

		setReportName(AonUtil.getMessage(bundle, "report_activities_view"));
	}

	public class ControlSummary {

		private Integer id;
		private String name;
		private Integer numVisits;
		private Integer numPendingVisits;
		private Integer numOffers;
		private Integer numAprovedOffers;
		private Integer numLostOffers;
		private Integer numPendingOffers;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getNumVisits() {
			return numVisits;
		}

		public void setNumVisits(Integer numVisits) {
			this.numVisits = numVisits;
		}

		public Integer getNumPendingVisits() {
			return numPendingVisits;
		}

		public void setNumPendingVisits(Integer numPendingVisits) {
			this.numPendingVisits = numPendingVisits;
		}

		public Integer getNumOffers() {
			return numOffers;
		}

		public void setNumOffers(Integer numOffers) {
			this.numOffers = numOffers;
		}

		public Integer getNumAprovedOffers() {
			return numAprovedOffers;
		}

		public void setNumAprovedOffers(Integer numAprovedOffers) {
			this.numAprovedOffers = numAprovedOffers;
		}

		public Integer getNumLostOffers() {
			return numLostOffers;
		}

		public void setNumLostOffers(Integer numLostOffers) {
			this.numLostOffers = numLostOffers;
		}

		public Integer getNumPendingOffers() {
			return numPendingOffers;
		}

		public void setNumPendingOffers(Integer numPendingOffers) {
			this.numPendingOffers = numPendingOffers;
		}

	}

	public void onCategoryStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialCategoryStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle,
					"stat_report_commercial_category"));
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
			getCommercialCategoryProductStats();

		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private void getCommercialCategoryProductStats()
			throws ManagerBeanException {
		StatEngine se = new StatEngine();
		params.setCategory(category);
		List<Stat> list = new LinkedList<Stat>();
		list.addAll(se.getCommercialCategoryProductsStats(params));
		setProductStats(list);
		calculateTotals(list);
		setReportName(AonUtil.getMessage(bundle,
				"stat_report_commercial_product"));
		setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
		productStatModel = null;
	}

	public void onCommercialCategoryOfferStats(ActionEvent e)
			throws ManagerBeanException {
		setOffersModel(null);
		setControlType(3);
		setCategoryName(((Stat)  yearStatModel.getRowData()).getName());

		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_PRODUCT_PRODUCT_CATEGORY_ID),((Stat) yearStatModel.getRowData()).getKey());
		criteria.addBetweenExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE), this.params.getFromDate(), this.params.getToDate());
		if (!ArrayUtils.isEmpty(this.params.getOfferStatuses())) {
						addEnumToCriteria(criteria, offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_STATUS), this.params.getOfferStatuses());
		}
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE),false);
		Projection projection = Projection.group(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER));
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Iterator iter = offerDetailBean.getList(new ProjectionList(projection), criteria).iterator();
		offerList= new LinkedList<Offer>();
    	while(iter.hasNext()){
    		Offer od = (Offer)iter.next();
			offerList.add(od);
    	}
    	setOfferBackAction("commercial_category_stats_year");
	}
	
	public void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	

	public void onCommercialProductOfferStats(ActionEvent e)
			throws ManagerBeanException {
		setOffersModel(null);
		setControlType(2);
		setProductName(((Stat) productStatModel.getRowData()).getName());
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_ID),((Stat) productStatModel.getRowData()).getKey());
		criteria.addBetweenExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE), this.params.getFromDate(), this.params.getToDate());
		if (!ArrayUtils.isEmpty(this.params.getOfferStatuses())) {
						addEnumToCriteria(criteria, offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_STATUS), this.params.getOfferStatuses());
		}
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE),false);
		Projection projection = Projection.group(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER));
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Iterator iter = offerDetailBean.getList(new ProjectionList(projection), criteria).iterator();
		offerList= new LinkedList<Offer>();
    	while(iter.hasNext()){
    		Offer od = (Offer)iter.next();
			offerList.add(od);
    	}
		setOfferBackAction("commercial_product_stats");
	}

	public void onCommercialGeozoneOfferStats(ActionEvent e)
			throws ManagerBeanException {
		setOffersModel(null);
		setZoneName(((Stat)  yearStatModel.getRowData()).getName());
		
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ADDRESS_GEOZONE_ID),((Stat) yearStatModel.getRowData()).getKey());
		criteria.addBetweenExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE), this.params.getFromDate(), this.params.getToDate());
		if (!ArrayUtils.isEmpty(this.params.getOfferStatuses())) {
						addEnumToCriteria(criteria, offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_STATUS), this.params.getOfferStatuses());
		}
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE),false);
		Projection projection = Projection.group(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER));
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Iterator iter = offerDetailBean.getList(new ProjectionList(projection), criteria).iterator();
		offerList= new LinkedList<Offer>();
    	while(iter.hasNext()){
    		Offer od = (Offer)iter.next();
			offerList.add(od);
    	}
		setOfferBackAction("commercial_geozone_stats_year");
	}

	public void onCommercialSellerOfferStats(ActionEvent e)
			throws ManagerBeanException {
		setOffersModel(null);
		setSellerName(((Stat)  yearStatModel.getRowData()).getName());
		
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_SELLER_ID),((Stat) yearStatModel.getRowData()).getKey());
		criteria.addBetweenExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE), this.params.getFromDate(), this.params.getToDate());
		if (!ArrayUtils.isEmpty(this.params.getOfferStatuses())) {
						addEnumToCriteria(criteria, offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_STATUS), this.params.getOfferStatuses());
		}
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE),false);
		Projection projection = Projection.group(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER));
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Iterator iter = offerDetailBean.getList(new ProjectionList(projection), criteria).iterator();
		offerList= new LinkedList<Offer>();
    	while(iter.hasNext()){
    		Offer od = (Offer)iter.next();
			offerList.add(od);
    	}	
		setOfferBackAction("commercial_seller_stats_year");
	}

	public void onCommercialTargetOfferStats(ActionEvent e)
			throws ManagerBeanException {
		setOffersModel(null);
		setTargetName(((Stat)  yearStatModel.getRowData()).getName());
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_TARGET_ID),((Stat) yearStatModel.getRowData()).getKey());
		criteria.addBetweenExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE), this.params.getFromDate(), this.params.getToDate());
		if (!ArrayUtils.isEmpty(this.params.getOfferStatuses())) {
						addEnumToCriteria(criteria, offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_STATUS), this.params.getOfferStatuses());
		}
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ISSUE_DATE),false);
		Projection projection = Projection.group(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER));
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Iterator iter = offerDetailBean.getList(new ProjectionList(projection), criteria).iterator();
		offerList= new LinkedList<Offer>();
    	while(iter.hasNext()){
    		Offer od = (Offer)iter.next();
			offerList.add(od);
    	}
		setOfferBackAction("commercial_target_stats_year");
	}

	public void onProductStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			// params.setCategory(category);
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialCategoryProductsStats(params));
			setProductStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle,
					"stat_report_commercial_product"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_product"));
			productStatModel = null;

		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onSellerStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialSellerStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle,
					"stat_report_commercial_seller"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_seller"));
			yearStatModel = null;

		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los datos. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onTargetStats(ActionEvent event) {
		try {
			StatEngine se = new StatEngine();
			List<Stat> list = new LinkedList<Stat>();
			list.addAll(se.getCommercialTargetStats(params));
			setYearStats(list);
			calculateTotals(list);
			setReportName(AonUtil.getMessage(bundle,
					"stat_report_commercial_target"));
			setItemTitle(AonUtil.getMessage(bundle, "stat_target"));
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
			setReportName(AonUtil.getMessage(bundle,
					"stat_report_commercial_geozone"));
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

	private void getTargetDoneVisitsModel() throws ManagerBeanException {
		PreparedStatement ps = null;

		String select = "select CommercialTracking "
				+ "from CommercialTracking as CommercialTracking "
				+ " where " +DomainManager.getSQLWhereClause("CommercialTracking.domain")
				+ " AND CommercialTracking.status = 1 AND  CommercialTracking.project.target.id = "
				+ target.getId()
				+ " AND   CommercialTracking.date >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND CommercialTracking.date <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' order by CommercialTracking.activity.id,CommercialTracking.date desc";

		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		visitsList = query.list();
		setNumVisits(visitsList.size());
	}

	private void getTargetPendingVisitModel() throws ManagerBeanException {
		String select = "select CommercialTracking "
				+ "from CommercialTracking as CommercialTracking "
				+ " where " + DomainManager.getSQLWhereClause("CommercialTracking.domain")
				+ " AND  CommercialTracking.status = 0 AND  CommercialTracking.project.target.id = "
				+ target.getId()
				+ " AND   CommercialTracking.date >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND CommercialTracking.date <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' order by CommercialTracking.activity.id,CommercialTracking.date desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		pendingVisitsList = query.list();
		setNumPendingVisits(pendingVisitsList.size());
	}

	private void getTargetDoneOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
				+ "from OfferDetail as OfferDetail "
				+ " where " + DomainManager.getSQLWhereClause("OfferDetail.domain")
				+ " AND OfferDetail.offer.target.id = " + target.getId()
				+ " AND   OfferDetail.offer.issueDate >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND OfferDetail.offer.issueDate <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' group by OfferDetail.offer.id"
				+ " order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		doneOffersList = query.list();
		closedOffersList = new LinkedList<OfferDetail>();
		lostOffersList = new LinkedList<OfferDetail>();
		pendingOffersList = new LinkedList<OfferDetail>();

		for (int i = 0; i < doneOffersList.size(); i++) {
			numOffers++;
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.APPROVED) {
				numAprovedOffers++;
				closedOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.REFUSED) {
				numLostOffers++;
				lostOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.PENDING) {
				numPendingOffers++;
				pendingOffersList.add(doneOffersList.get(i));
			}
		}
	}

	private void getProductDoneOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
				+ "from OfferDetail as OfferDetail "
				+ " where " + DomainManager.getSQLWhereClause("OfferDetail.domain")
				+ " AND OfferDetail.item.product = " + product.getId()
				+ " AND OfferDetail.offer.issueDate >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND OfferDetail.offer.issueDate <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' group by OfferDetail.offer.id"
				+ " order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		doneOffersList = query.list();
		closedOffersList = new LinkedList<OfferDetail>();
		lostOffersList = new LinkedList<OfferDetail>();
		pendingOffersList = new LinkedList<OfferDetail>();

		for (int i = 0; i < doneOffersList.size(); i++) {
			numOffers++;
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.APPROVED) {
				numAprovedOffers++;
				closedOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.REFUSED) {
				numLostOffers++;
				lostOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.PENDING) {
				numPendingOffers++;
				pendingOffersList.add(doneOffersList.get(i));
			}
		}
	}
	
	private void getCategoryDoneOffers() throws ManagerBeanException {
		String select = "select OfferDetail "
				+ "from OfferDetail as OfferDetail "
				+ " where " +DomainManager.getSQLWhereClause("OfferDetail.domain")
				+ " AND OfferDetail.item.product.category = " + productCategory.getId()
				+ " AND OfferDetail.offer.issueDate >= '"
				+ new java.sql.Date(this.params.getFromDate().getTime())
				+ "' AND OfferDetail.offer.issueDate <= '"
				+ new java.sql.Date(this.params.getToDate().getTime())
				+ "' group by OfferDetail.offer.id"
				+ " order by OfferDetail.offer.issueDate desc";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		doneOffersList = query.list();
		closedOffersList = new LinkedList<OfferDetail>();
		lostOffersList = new LinkedList<OfferDetail>();
		pendingOffersList = new LinkedList<OfferDetail>();

		for (int i = 0; i < doneOffersList.size(); i++) {
			numOffers++;
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.APPROVED) {
				numAprovedOffers++;
				closedOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.REFUSED) {
				numLostOffers++;
				lostOffersList.add(doneOffersList.get(i));
			}
			if (doneOffersList.get(i).getOffer().getStatus() == OfferStatus.PENDING) {
				numPendingOffers++;
				pendingOffersList.add(doneOffersList.get(i));
			}
		}
	}

}