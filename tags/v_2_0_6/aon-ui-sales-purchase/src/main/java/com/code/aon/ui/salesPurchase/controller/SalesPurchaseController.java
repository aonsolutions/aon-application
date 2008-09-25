package com.code.aon.ui.salesPurchase.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.supplier.Supplier;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;

/**
 * Controller used in the SalesPurchase maintenance.
 */
public class SalesPurchaseController extends BasicController {

	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";

	private boolean incomeRelated;
	
	private Income income;
	
	private IncomeDetail incomeDetail;


	public boolean isIncomeRelated() {
		return incomeRelated;
	}

	public void setIncomeRelated(boolean incomeRelated) {
		this.incomeRelated = incomeRelated;
	}

	public Income getIncome() {
		return income;
	}

	public void setIncome(Income income) {
		this.income = income;
	}

	public IncomeDetail getIncomeDetail() {
		return incomeDetail;
	}

	public void setIncomeDetail(IncomeDetail incomeDetail) {
		this.incomeDetail = incomeDetail;
	}

	@SuppressWarnings("unused")
	public void onSave(ActionEvent event) throws ManagerBeanException {
		setIncomeRelated(true);

		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), getIncomeDetail().getItem().getId());
		Iterator iterator = itemBean.getList(criteria, 0, 1).iterator();
		if (iterator.hasNext()) {
			getIncomeDetail().setItem((Item)iterator.next());
		}

		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		((DeliveryDetail)deliveryDetailController.getTo()).setItem(getIncomeDetail().getItem());
		((DeliveryDetail)deliveryDetailController.getTo()).setDescription(getIncomeDetail().getDescription());
		((DeliveryDetail)deliveryDetailController.getTo()).setPrice(getIncomeDetail().getItem().getPrice());
	}

	@SuppressWarnings("unused")
	public void onClose(ActionEvent event) {
		if(!isIncomeRelated()){
			this.onInitialize();
		}
	}

	public void onInitialize(){
		createNewIncome();
		createNewIncomeDetail();
		setIncomeRelated(false);
	}
	
	private void createNewIncomeDetail() {
		this.incomeDetail = new IncomeDetail();
		this.incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
		this.incomeDetail.setIncome(new Income());
		this.incomeDetail.setItem(new Item());
		this.incomeDetail.getItem().setProduct(new Product());
		this.incomeDetail.getItem().getProduct().setCategory(new ProductCategory());
		this.incomeDetail.setWarehouse(new Warehouse());
	}

	private void createNewIncome() {
		this.income = new Income();
		this.income.setSupplier(new Supplier());
	}
}