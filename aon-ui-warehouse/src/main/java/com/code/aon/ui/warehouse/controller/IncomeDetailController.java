package com.code.aon.ui.warehouse.controller;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;

public class IncomeDetailController extends LinesController implements IWarehouseConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	private boolean showItemPackageWindow;
	private IncomeDetail incomeDetail;
	
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

		IncomeDetail incomeDetail = (IncomeDetail)getTo();
		if (StringUtils.equals(incomeDetail.getItem().getFullName().trim(), incomeDetail.getDescription().trim())) {
			String longDescription = incomeDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				incomeDetail.setDescription(incomeDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isShowItemPackageWindow() {
		return showItemPackageWindow;
	}

	public void setShowItemPackageWindow(boolean value) {
		this.showItemPackageWindow = value;
	}

	public IncomeDetail getIncomeDetail() {
		return incomeDetail;
	}

	public void setIncomeDetail(IncomeDetail incomeDetail) {
		this.incomeDetail = incomeDetail;
	}

	public void onIncomeDetailProjectShow(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			setIncomeDetail((IncomeDetail)this.getModel().getRowData());
		}
	}

	public void addIncomeDetailProject(ActionEvent event) throws ManagerBeanException {
		if(incomeDetail.getProject()!=null && incomeDetail.getProject().getId()==null){
			incomeDetail.setProject(null);
		}
		getManagerBean().update(incomeDetail);
	}

	public boolean isEditable() throws ManagerBeanException {
		IncomeDetail incomeDetail = (IncomeDetail)this.getTo();
		if (incomeDetail != null) {
			return isEditable(incomeDetail);
		}
		return false;
	}

	public boolean isModelEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isEditable((IncomeDetail)this.getModel().getRowData());
		}
		return false;
	}

	private boolean isEditable(IncomeDetail incomeDetail) throws ManagerBeanException {
		return (incomeDetail.getPurchaseDetail() == null || incomeDetail.getPurchaseDetail().getId() == null);
	}

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			Income income = (Income)getMasterController().getTo();

			IncomeDetail incomeDetail = (IncomeDetail)getTo();
			incomeDetail.setItem(item);
			incomeDetail.setDescription(item.getFullName());
			if (incomeDetail.getQuantity() == 0) {
				incomeDetail.setQuantity(1);
			}
			incomeDetail.setPrice(getPriceStrategy().getUnitPurchasePrice(incomeDetail, income.getIssueTime(), income.getSupplier()));
		}
	}	

	public void onItemPackageShow(ActionEvent event) {
		IncomeDetail incomeDetail = (IncomeDetail)getTo();
		incomeDetail.getItem().initializePackQuantities(incomeDetail.getQuantity());
	}

	public void onAssignItemPackage(ActionEvent event) {
		IncomeDetail incomeDetail = (IncomeDetail)getTo();
		incomeDetail.setQuantity(incomeDetail.getItem().getPackStockQuantity());
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		IncomeDetail incomeDetail = (IncomeDetail)this.getModel().getRowData();
		if (incomeDetail.getPurchaseDetail() != null && incomeDetail.getPurchaseDetail().getId() != null) {
			info.append(AonUtil.getMessage(ICommonMessages.SOURCE));
			info.append(" ");
			info.append(AonUtil.getMessage(ICommonMessages.INVOICE_SALES));
			info.append(" ");
			info.append(incomeDetail.getPurchaseDetail().getPurchase().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ICommonMessages.LINE));
			info.append(" ");
			info.append(incomeDetail.getPurchaseDetail().getLine());
		}
		return info.toString();
	}

	public void onLoadPurchase(ActionEvent event) throws ManagerBeanException {
		IncomeDetail incomeDetail = (IncomeDetail)this.getModel().getRowData();
		BasicController purchaseController = (BasicController)AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		purchaseController.onLoad(event, incomeDetail.getPurchaseDetail().getPurchase().getId(), INCOME_FORM_NAME, null);
	}
	
	public void onRemoveModelRow(ActionEvent event) throws ManagerBeanException{
		if(this.getModel().isRowAvailable()){
			this.onSelect(event);
			this.onRemove(event);
		}
	}
	
	public void onRefresh(ActionEvent event) {
		initializeModel();
	}

}