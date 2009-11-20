package com.code.aon.finance.invoicing.remover;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeDetailType;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(IncomeInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.INCOME);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			if (invoiceDetail.getSourceId() != null) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());
				if (incomeDetail.getType().equals(IncomeDetailType.MANUAL)) {
					IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
					Income income = incomeDetail.getIncome();
					income.setStatus(IncomeStatus.PENDING);
					incomeBean.update(income);
				} else {
					IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
					PurchaseDetail purchaseDetail = incomeDetail.getPurchaseDetail();
					purchaseDetail.setDelivered(0);
					purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
					purchaseDetailBean.update(purchaseDetail);

					IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
					Purchase purchase = purchaseDetail.getPurchase();
					purchase.setStatus(PurchaseStatus.PENDING);
					purchaseBean.update(purchase);

					incomeDetailBean.remove(incomeDetail);

					Income income = incomeDetail.getIncome();
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
					if (incomeDetailBean.getCount(criteria) == 0) {
						IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
						incomeBean.remove(income);
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

}