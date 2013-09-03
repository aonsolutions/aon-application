package com.code.aon.ui.purchase.controller;

import static com.code.aon.ui.common.ICommonConstants.QUANTITY_PATTERN;
import static com.code.aon.ui.purchase.IPurchaseMessages.PURCHASE_DETAIL_LINE;
import static com.code.aon.ui.purchase.IPurchaseMessages.PURCHASE_DETAIL_UNITS;
import static com.code.aon.ui.purchase.IPurchaseMessages.PURCHASE_TO_INCOME;
import static com.code.aon.ui.purchase.IPurchaseMessages.PURCHASE_TRANSFERED_TO;

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
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.purchase.IPurchaseMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseDetailController extends LinesController implements IPurchaseConstants {

	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	private PurchaseDetail purchaseDetail;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
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

	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	public void onPurchaseDetailProjectShow(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			setPurchaseDetail((PurchaseDetail)this.getModel().getRowData());
		}
	}

	public void addPurchaseDetailProject(ActionEvent event) throws ManagerBeanException {
		if(purchaseDetail.getProject()!=null && purchaseDetail.getProject().getId()==null){
			purchaseDetail.setProject(null);
		}
		getManagerBean().update(purchaseDetail);
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
			purchaseDetail.setDescription(item.getFullName());
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
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(ICommonMessages.BUNDLE_NAME, QUANTITY_PATTERN));

		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			info.append("<aon:div>");
			info.append(AonUtil.getMessage(IPurchaseMessages.BUNDLE_KEY, PURCHASE_TRANSFERED_TO));
			info.append(" ");
			info.append(AonUtil.getMessage(IPurchaseMessages.BUNDLE_KEY, PURCHASE_TO_INCOME));
			info.append(" ");
			info.append(incomeDetail.getIncome().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(IPurchaseMessages.BUNDLE_KEY, PURCHASE_DETAIL_LINE));
			info.append(" ");
			info.append(incomeDetail.getLine());
			if (purchaseDetail.getQuantity() > incomeDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(incomeDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage(IPurchaseMessages.BUNDLE_KEY, PURCHASE_DETAIL_UNITS));
				info.append(")");
			}
			info.append("</aon:div>");
		}
		return info.toString();
	}

	public void onLoadIncome(ActionEvent event) throws ManagerBeanException {
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
		criteria.addOrder(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_ID), false);
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			BasicController incomeController = (BasicController)AonUtil.getRegisteredBean(INCOME_CONTROLLER_NAME);
			incomeController.onLoad(event, incomeDetail.getIncome().getId(), PURCHASE_FORM_NAME, PURCHASE_DETAIL_CONTROLLER_NAME + ".onBackPurchase");
		}
	}

	public void onBackPurchase(ActionEvent event) throws ManagerBeanException {
		PurchaseController purchaseController = (PurchaseController) AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		purchaseController.refresh(event);
		onSearch(event);
	}
	
	public void onLoadProposal(ActionEvent event) throws ManagerBeanException {
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		BasicController proposalController = (BasicController)AonUtil.getRegisteredBean(IPurchaseConstants.PROPOSAL_CONTROLLER_NAME);
		proposalController.onLoad(event, purchaseDetail.getProposalDetail().getProposal().getId(), "purchase_form", null);
	}

}