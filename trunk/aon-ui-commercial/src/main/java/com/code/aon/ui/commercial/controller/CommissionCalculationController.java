package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.OfferDetailCommission;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailCommissionStatus;
import com.code.aon.commercial.strategy.BasicCommissionStrategy;
import com.code.aon.commercial.strategy.ICommissionStrategy;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class CommissionCalculationController {

	private Seller seller;
	private Date fromDate;
	private Date toDate;
	private Series series;
	private Integer fromNumber;
	private Integer toNumber;
	private Target target;
	private boolean confidential;
	private WorkPlace workPlace;
	private ICommissionStrategy commissionStrategy;
	private IPriceStrategy priceStrategy;
	
	public Seller getSeller() {
		return seller;
	}
	public void setSeller(Seller seller) {
		this.seller = seller;
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

	public Series getSeries() {
		return series;
	}
	public void setSeries(Series series) {
		this.series = series;
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
	
	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}

	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public void onInitialize(ActionEvent event) {
		setSeller(new Seller());
		setTarget(new Target());
		setConfidential(false);
	}

	public void onCalculate(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(OfferDetail.class.getName());
		try {
			IManagerBean commissionBean = BeanManager.getManagerBean(OfferDetailCommission.class);

			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			List<ITransferObject> offerDetailList = getOfferDetailList();
			for (ITransferObject to : offerDetailList) {
				OfferDetail offerDetail = (OfferDetail)to;
				OfferDetailCommission offerDetailCommission;

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(commissionBean.getFieldName(ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_ID), offerDetail.getId());
				Iterator<?> iterator = commissionBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					offerDetailCommission = (OfferDetailCommission)iterator.next();
				} else {
					offerDetailCommission = new OfferDetailCommission();
					offerDetailCommission.setOfferDetail(offerDetail);
					offerDetailCommission.setStatus(OfferDetailCommissionStatus.PENDING);
				}

				if (offerDetailCommission.getStatus() == OfferDetailCommissionStatus.PENDING) {
					double commission = getCommissionStrategy().getCommission(offerDetail);
					double amount = CommonUtil.round(getPriceStrategy().getBasePrice(offerDetail) * commission / 100);

					offerDetailCommission.setCommission(commission);
					offerDetailCommission.setAmount(amount);
					commissionBean.insertOrUpdate(offerDetailCommission);
				}
			}

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			
			IController commissionController = FormUtil.getController("offerDetailCommission");
			Criteria criteria = getCriteria(true);
			commissionController.setCriteria(criteria);
			commissionController.onSearch(null);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg =  "Unable to rollback transaction!";
			}
			String msg =  "Error calculating commissions. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private List<ITransferObject> getOfferDetailList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = getCriteria(false);
		return bean.getList(criteria);
	}

	private Criteria getCriteria(boolean offerDetailCommission) throws ManagerBeanException {
		String sellerAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SELLER_ID : 
				ICommercialAlias.OFFER_DETAIL_OFFER_SELLER_ID;
		String dateAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_ISSUE_DATE : 
				ICommercialAlias.OFFER_DETAIL_OFFER_ISSUE_DATE;
		String seriesAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SERIES : 
				ICommercialAlias.OFFER_DETAIL_OFFER_SERIES;
		String numberAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_NUMBER : 
				ICommercialAlias.OFFER_DETAIL_OFFER_NUMBER;
		String targetAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_TARGET_ID : 
				ICommercialAlias.OFFER_DETAIL_OFFER_TARGET_ID;
		String workPlaceAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_WORK_PLACE_ID : 
				ICommercialAlias.OFFER_DETAIL_OFFER_WORK_PLACE_ID;
		String securityLevelAlias = offerDetailCommission ? 
				ICommercialAlias.OFFER_DETAIL_COMMISSION_OFFER_DETAIL_OFFER_SECURITY_LEVEL : 
				ICommercialAlias.OFFER_DETAIL_OFFER_SECURITY_LEVEL;

		IManagerBean bean = BeanManager.getManagerBean(offerDetailCommission ? OfferDetailCommission.class : OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(securityLevelAlias), isConfidential()?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL);
		if (!offerDetailCommission) {
			criteria.addGreaterThanExpression(bean.getFieldName(ICommercialAlias.OFFER_DETAIL_QUANTITY), new Double(0));
			criteria.addGreaterThanExpression(bean.getFieldName(ICommercialAlias.OFFER_DETAIL_PRICE), new Double(0));
		} else {
			criteria.addEqualExpression(bean.getFieldName(ICommercialAlias.OFFER_DETAIL_COMMISSION_STATUS), OfferDetailCommissionStatus.PENDING);
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(bean.getFieldName(sellerAlias), getSeller().getId());
		}
		if (getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(dateAlias), getFromDate());
		}
		if (getToDate() != null) {
			criteria.addLessThanOrEqualExpression(bean.getFieldName(dateAlias), getToDate());
		}
		if (getSeries() != null && getSeries().getId() != null) {
			criteria.addEqualExpression(bean.getFieldName(seriesAlias), getSeries().getId());
		}
		if (getFromNumber() != null) {
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(numberAlias), getFromNumber());
		}
		if (getToNumber() != null) {
			criteria.addLessThanOrEqualExpression(bean.getFieldName(numberAlias), getToNumber());
		}
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(bean.getFieldName(targetAlias), getTarget().getId());
		}
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(bean.getFieldName(workPlaceAlias), getWorkPlace().getId());
		}
		return criteria;
	}
	
	private ICommissionStrategy getCommissionStrategy() {
		if (commissionStrategy == null) {
			commissionStrategy = new BasicCommissionStrategy();
		}
		return commissionStrategy;
	}

	private IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new BasicPriceStrategy();
		}
		return priceStrategy;
	}

}