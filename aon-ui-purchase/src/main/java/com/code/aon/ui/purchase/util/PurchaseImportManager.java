package com.code.aon.ui.purchase.util;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseImportManager {

	public Purchase copyPurchase(Purchase source, String series, int number, Supplier supplier, Date date) throws ManagerBeanException {
		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		Integer sourceId = source.getId();
		Purchase purchase = source;
		purchase.setId(null);
		purchase.setSeries(series);
		purchase.setNumber(number);
		purchase.setSupplier(supplier);

		purchase.setIssueDate(date);
		purchase.setStatus(PurchaseStatus.PENDING);
		purchase.setScope(null);
		purchase.setLines(null);
		purchase = (Purchase) purchaseBean.insert(purchase);

		importPurchase(sourceId, purchase);
		return purchase;
	}

	private void importPurchase(Integer sourceId, Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), sourceId);
		List<ITransferObject> purchaseDetailList = purchaseDetailBean.getList(criteria);
		for (ITransferObject to : purchaseDetailList) {
			PurchaseDetail sourceDetail = (PurchaseDetail)to;
			boolean lastDetail = sourceDetail.equals(purchaseDetailList.get(purchaseDetailList.size()-1));
			PurchaseDetail purchaseDetail = sourceDetail;
			purchaseDetail.setId(null);
			purchaseDetail.setPurchase(purchase);
			purchaseDetailBean.insert(purchaseDetail);

			if (lastDetail) {
				purchase = purchaseDetail.getPurchase();
			}
		}
	}

}
