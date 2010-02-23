package com.code.aon.ui.purchase.controller;

import java.text.DecimalFormat;
import java.util.Iterator;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class PurchaseDetailController extends LinesController {

	private boolean longDescription;

	private IPriceStrategy priceStrategy;
	
	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		PurchaseDetail purchaseDetail = (PurchaseDetail)getTo();
		if (StringUtils.equals(purchaseDetail.getItem().getProduct().getName().trim(), purchaseDetail.getDescription().trim())) {
			String longDescription = purchaseDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				purchaseDetail.setDescription(purchaseDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isPending() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
			if (purchaseDetail.getStatus() != null) {
				return purchaseDetail.getStatus().equals(PurchaseDetailStatus.PENDING);
			}
		}
		return false;
	}

	public boolean isSettled() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
			if (purchaseDetail.getStatus() != null) {
				return purchaseDetail.getStatus().equals(PurchaseDetailStatus.SETTLED);
			}
		}
		return false;
	}

	public void onItemChanged(LookupChangeEvent event) {
		PurchaseDetail purchaseDetail = (PurchaseDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			purchaseDetail.setItem(item);
			purchaseDetail.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
			if (purchaseDetail.getQuantity() == 0) {
				purchaseDetail.setQuantity(1);
			}

			price = item.getPurchasePrice();
		}
		purchaseDetail.setPrice(price);
	}	

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage("bundle", "aon_decimal3_truncate_pattern"));

		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			info.append("<p>");
			info.append(AonUtil.getMessage("purchaseBundle", "purchase_transfered_to"));
			info.append(" ");
			info.append(AonUtil.getMessage("purchaseBundle", "purchase_to_income"));
			info.append(" ");
			info.append(incomeDetail.getIncome().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("purchaseBundle", "purchase_detail_line"));
			info.append(" ");
			info.append(incomeDetail.getLine());
			if (purchaseDetail.getQuantity() > incomeDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(incomeDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage("purchaseBundle", "purchase_detail_units"));
				info.append(")");
			}
			info.append("</p>");
		}
		return info.toString();
	}

}