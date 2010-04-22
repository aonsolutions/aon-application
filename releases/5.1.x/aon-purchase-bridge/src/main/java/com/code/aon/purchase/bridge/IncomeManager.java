package com.code.aon.purchase.bridge;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeDetailSource;
import com.code.aon.warehouse.enumeration.IncomeDetailType;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeManager {

	public Income purchaseIncome(Purchase purchase, String series, int number, Date issueDate, Warehouse warehouse, IncomeDetailType type)
		throws ManagerBeanException {
		updatePurchaseStatus(purchase);
		Income income = createIncome(purchase, series, number, issueDate);
		createIncomeDetails(income, purchase, warehouse, type);
		return income;
	}

	private void updatePurchaseStatus(Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		purchase.setStatus(PurchaseStatus.CLOSED);
		purchaseBean.restoreNullSubPOJOs(purchase);
		purchaseBean.update(purchase);
	}

	private Income createIncome(Purchase purchase, String series, int number, Date issueDate) throws ManagerBeanException {
		Income income = new Income();
		income.setSeries(series);
		income.setNumber(number);
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
		income.setBank(purchase.getBank());
		income.setBankAccount(purchase.getBankAccount());

		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		return (Income)incomeBean.insert(income);
	}

	private void createIncomeDetails(Income income, Purchase purchase, Warehouse warehouse, IncomeDetailType type) throws ManagerBeanException {
		int line = 0;

		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_STATUS), PurchaseDetailStatus.SETTLED));
		criteria.addOrder(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE));
		Iterator<?> iterator = purchaseDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)iterator.next();
			IncomeDetail incomeDetail = new IncomeDetail();
			incomeDetail.setIncome(income);
			incomeDetail.setLine(++line);
			incomeDetail.setItem(purchaseDetail.getItem());
			incomeDetail.setDescription(purchaseDetail.getDescription());
			incomeDetail.setWarehouse(warehouse);
			incomeDetail.setQuantity(CommonUtil.round(purchaseDetail.getQuantity() - purchaseDetail.getDelivered()));
			incomeDetail.setPrice(purchaseDetail.getPrice());
			incomeDetail.setDiscountExpression(purchaseDetail.getDiscountExpression());
			incomeDetail.setType(type);
			incomeDetail.setSource(IncomeDetailSource.PURCHASE);
			incomeDetail.setPurchaseDetail(purchaseDetail);
			incomeDetailBean.insert(incomeDetail);

			purchaseDetail.setDelivered(purchaseDetail.getQuantity());
			purchaseDetail.setStatus(PurchaseDetailStatus.SETTLED);
			purchaseDetailBean.update(purchaseDetail);
		}
	}

	public IncomeDetail transferIncomeDetail(Income income, PurchaseDetail purchaseDetail, Warehouse warehouse) throws ManagerBeanException {
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
		incomeDetail.setType(IncomeDetailType.MANUAL);
		incomeDetail.setSource(IncomeDetailSource.PURCHASE);
		incomeDetail.setPurchaseDetail(purchaseDetail);
		incomeDetail = (IncomeDetail)incomeDetailBean.insert(incomeDetail);

		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		purchaseDetail.setDelivered(purchaseDetail.getDelivered() + purchaseDetail.getTransfered());
		purchaseDetail.setStatus((purchaseDetail.getQuantity() > purchaseDetail.getDelivered()) ? PurchaseDetailStatus.PARTIAL_SETTLED : PurchaseDetailStatus.SETTLED);
		purchaseDetailBean.update(purchaseDetail);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), purchaseDetail.getPurchase().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(purchaseDetailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_STATUS), PurchaseDetailStatus.SETTLED));
		if (purchaseDetailBean.getCount(criteria) == 0) {
			updatePurchaseStatus(purchaseDetail.getPurchase());
		}

		return incomeDetail;
	}

	private	Integer calculateNextLine(Income income) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		Projection projection = Projection.max(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
		Object value = incomeDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
