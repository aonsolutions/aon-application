package com.code.aon.ui.purchase.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.project.Project;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseSource;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.SalesDetail;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseManager.class.getName());
	
	public void transferSalesDetails(Purchase purchase, List<SalesDetail> salesDetailList, Warehouse warehouse) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			for (SalesDetail salesDetail : salesDetailList) {
				if (salesDetail.getTransfered() > 0) {
					transferSalesDetail(sessionName, purchase, salesDetail, warehouse);
				}
			}

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private PurchaseDetail transferSalesDetail(String sessionName, Purchase purchase, SalesDetail salesDetail, Warehouse warehouse) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		PurchaseDetail purchaseDetail = new PurchaseDetail();
		purchaseDetail.setPurchase(purchase);
		purchaseDetail.setLine(calculateNextLine(purchase));
		purchaseDetail.setItem(salesDetail.getItem());
		purchaseDetail.setDescription(salesDetail.getDescription());
		purchaseDetail.setQuantity(salesDetail.getTransfered());
		purchaseDetail.setPrice(salesDetail.getPrice());
		purchaseDetail.setDiscountExpression(salesDetail.getDiscountExpression());
		purchaseDetail.setSource(PurchaseSource.SALES);
		purchaseDetail.setSourceId(salesDetail.getId());
		purchaseDetailBean.restoreNullSubPOJOs(purchaseDetail);
		purchaseDetail = (PurchaseDetail)purchaseDetailBean.insert(purchaseDetail);

		Project salesProject = salesDetail.getSales().getProject();
		if ((purchase.getProject() == null || purchase.getProject().getId() == null) && salesProject != null && salesProject.getId() != null ) {
			IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
			purchase.setProject(salesProject);
			purchaseBean.restoreNullSubPOJOs(purchase);
			purchase = (Purchase)HibernateUtil.getSession(sessionName).merge(purchase);	
			purchase = (Purchase)purchaseBean.update(purchase);
		}

		return purchaseDetail;
	}

	private	Integer calculateNextLine(Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		Projection projection = Projection.max(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
		Object value = purchaseDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	
}
