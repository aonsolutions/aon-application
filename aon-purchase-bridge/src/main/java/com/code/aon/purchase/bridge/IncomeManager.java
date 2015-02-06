package com.code.aon.purchase.bridge;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.project.Project;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(IncomeManager.class.getName());

	public Income purchaseIncome(Purchase purchase, String referenceCode, Date issueDate, Warehouse warehouse)	throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Income income = createIncome(purchase, referenceCode, issueDate);
			createIncomeDetails(sessionName, income, purchase, warehouse);
			updatePurchaseStatus(sessionName, purchase);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			
			return income;
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

	private Income createIncome(Purchase purchase, String referenceCode, Date issueDate) throws ManagerBeanException {
		Income income = new Income();
		income.setProject(purchase.getProject());
		income.setReferenceCode(referenceCode);
		income.setSupplier(purchase.getSupplier());
		income.setRegistryAddress(purchase.getRegistryAddress());
		income.setIssueTime(issueDate);
		income.setSecurityLevel(purchase.getSecurityLevel());
		income.setStatus(IncomeStatus.PENDING);
		income.setWorkPlace(purchase.getWorkPlace());
		income.setScope(purchase.getScope());
		income.setPayMethod(purchase.getPayMethod());
		income.setNumberOfPayments(purchase.getNumberOfPayments());
		income.setDaysToFirstPayment(purchase.getDaysToFirstPayment());
		income.setDaysBetweenPayments(purchase.getDaysBetweenPayments());
		income.setPaymentDays(purchase.getPaymentDays());
		income.setBankAccount(purchase.getBankAccount());
		income.setBankAlias(purchase.getBankAlias());
		income.setBic(purchase.getBic());

		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		incomeBean.restoreNullSubPOJOs(income);
		return (Income)incomeBean.insert(income);
	}

	private void createIncomeDetails(String sessionName, Income income, Purchase purchase, Warehouse warehouse) throws ManagerBeanException {
		int line = 0;

		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		criteria.addNotEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_STATUS), PurchaseDetailStatus.SETTLED);
		criteria.addOrder(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
		Iterator<?> iterator = purchaseDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)iterator.next();
			IncomeDetail incomeDetail = new IncomeDetail();
			incomeDetail.setIncome(income);
			incomeDetail.setProject(purchaseDetail.getProject());
			incomeDetail.setLine(++line);
			incomeDetail.setItem(purchaseDetail.getItem());
			incomeDetail.setDescription(purchaseDetail.getDescription());
			incomeDetail.setWarehouse(warehouse);
			incomeDetail.setQuantity(CommonUtil.round(purchaseDetail.getQuantity() - purchaseDetail.getDelivered()));
			incomeDetail.setPrice(purchaseDetail.getPrice());
			incomeDetail.setDiscountExpression(purchaseDetail.getDiscountExpression());
			incomeDetail.setPurchaseDetail(purchaseDetail);
			incomeDetailBean.restoreNullSubPOJOs(incomeDetail);
			incomeDetailBean.insert(incomeDetail);

			purchaseDetail.setDelivered(purchaseDetail.getQuantity());
			purchaseDetail.setStatus(PurchaseDetailStatus.SETTLED);
			purchaseDetailBean.restoreNullSubPOJOs(purchaseDetail);
			purchaseDetail = (PurchaseDetail)HibernateUtil.getSession(sessionName).merge(purchaseDetail);	
			purchaseDetailBean.update(purchaseDetail);
		}
	}

	public IncomeDetail transferIncomeDetail(Income income, PurchaseDetail purchaseDetail, Warehouse warehouse) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Double transferQuantity = purchaseDetail.getTransfered();
			boolean forcePendingQuantityCancel = purchaseDetail.isForcePendingQuantityCancel();

			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			IncomeDetail incomeDetail = new IncomeDetail();
			incomeDetail.setIncome(income);
			incomeDetail.setLine(calculateNextLine(income));
			incomeDetail.setItem(purchaseDetail.getItem());
			incomeDetail.setDescription(purchaseDetail.getDescription());
			incomeDetail.setWarehouse(warehouse);
			incomeDetail.setQuantity(purchaseDetail.getTransfered());
			incomeDetail.setPrice(purchaseDetail.getPrice());
			incomeDetail.setDiscountExpression(purchaseDetail.getDiscountExpression());
			incomeDetail.setPurchaseDetail(purchaseDetail);
			if (transferQuantity != 0) {
				incomeDetailBean.restoreNullSubPOJOs(incomeDetail);
				incomeDetail = (IncomeDetail)incomeDetailBean.insert(incomeDetail);
			}

			Project purchaseProject = purchaseDetail.getPurchase().getProject();
			if ((income.getProject() == null || income.getProject().getId() == null) && purchaseProject != null && purchaseProject.getId() != null ) {
				IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
				income.setProject(purchaseProject);
				incomeBean.restoreNullSubPOJOs(income);
				income = (Income)HibernateUtil.getSession(sessionName).merge(income);	
				income = (Income)incomeBean.update(income);
			}

			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetail = (PurchaseDetail) purchaseDetailBean.get(purchaseDetail.getId());
			purchaseDetail.setForcePendingQuantityCancel(forcePendingQuantityCancel);
			purchaseDetail.setDelivered(CommonUtil.round(purchaseDetail.getDelivered() + transferQuantity, 3));
			purchaseDetail.setStatus((purchaseDetail.getPendingQuantity() > 0) ? PurchaseDetailStatus.PARTIAL_SETTLED : PurchaseDetailStatus.SETTLED);
			purchaseDetailBean.restoreNullSubPOJOs(purchaseDetail);
			purchaseDetail = (PurchaseDetail)HibernateUtil.getSession(sessionName).merge(purchaseDetail);	
			purchaseDetailBean.update(purchaseDetail);

			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchaseDetail.getPurchase().getId());
			criteria.addNotEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_STATUS), PurchaseDetailStatus.SETTLED);
			if (purchaseDetailBean.getCount(criteria) == 0) {
				updatePurchaseStatus(sessionName, purchaseDetail.getPurchase());
			}

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			
			return incomeDetail;
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

	private	Integer calculateNextLine(Income income) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		Projection projection = Projection.max(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
		Object value = incomeDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void updatePurchaseStatus(String sessionName, Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		purchase.setStatus(PurchaseStatus.SERVED);
		purchaseBean.restoreNullSubPOJOs(purchase);
		purchase = (Purchase)HibernateUtil.getSession(sessionName).merge(purchase);	
		purchaseBean.update(purchase);
	}

}
