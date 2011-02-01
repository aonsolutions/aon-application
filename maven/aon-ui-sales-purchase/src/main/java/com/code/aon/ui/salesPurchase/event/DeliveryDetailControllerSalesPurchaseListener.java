package com.code.aon.ui.salesPurchase.event;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.salesPurchase.controller.SalesPurchaseController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryDetailControllerSalesPurchaseListener extends ControllerAdapter {

	private static final String SALES_PURCHASE_CONTROLLER_NAME = "salesPurchase";
	
	private static final String INCOME_CONTROLLER_NAME = "income";
	
	private static final String INCOME_DETAIL_CONTROLLER_NAME = "incomeDetail";
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER_NAME);
		salesPurchaseController.onInitialize();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER_NAME);
			if(salesPurchaseController.isIncomeRelated()){
				Income income = salesPurchaseController.getIncome();
				Income dbIncome = obtainDataBaseIncome(income);
				if(dbIncome == null){
					dbIncome = insertIncome(income, salesPurchaseController.getIncomeDetail().getWarehouse().getId());
				}else{
					incomeControllerForUpdate(dbIncome, salesPurchaseController.getIncomeDetail().getWarehouse().getId());
				}
				IncomeDetail incomeDetail = salesPurchaseController.getIncomeDetail();
				incomeDetail.setIncome(dbIncome);
				insertIncomeDetail(incomeDetail);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Income obtainDataBaseIncome(Income income) throws ManagerBeanException {
		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SERIES), income.getSeries());
		criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_NUMBER), income.getNumber());
		criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), income.getSupplier().getId());
		Iterator iter = incomeBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (Income)iter.next();
		}
		return null;
	}
	
	private Income insertIncome(Income income, Integer warehouseId) throws ManagerBeanException {
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		incomeController.onReset(null);
		incomeController.setWarehouseId(warehouseId);
		Income to = (Income)incomeController.getTo();
		to.setIncomeStatus(income.getIncomeStatus());
		to.setIssueTime(income.getIssueTime());
		to.setNumber(income.getNumber());
		to.setRegistryAddress(income.getRegistryAddress());
		to.setSecurityLevel(income.getSecurityLevel());
		to.setSeries(income.getSeries());
		to.setSupplier(income.getSupplier());
		incomeController.accept(null);
		return (Income)incomeController.getTo();
	}
	
	private void incomeControllerForUpdate(Income dbIncome, Integer warehouseId) throws ManagerBeanException {
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		incomeController.setWarehouseId(warehouseId);
		Criteria criteria =  new Criteria();
		criteria.addEqualExpression(incomeController.getFieldName(IWarehouseAlias.INCOME_SERIES), dbIncome.getSeries());
		criteria.addEqualExpression(incomeController.getFieldName(IWarehouseAlias.INCOME_NUMBER), dbIncome.getNumber());
		criteria.addEqualExpression(incomeController.getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), dbIncome.getSupplier().getId());
		incomeController.setCriteria(criteria);
		incomeController.onSearch(null);
		incomeController.getModel().setRowIndex(0);
		incomeController.onSelect(null);
	}
	
	private void insertIncomeDetail(IncomeDetail incomeDetail) throws ManagerBeanException {
		IncomeDetailController incomeDetailController = (IncomeDetailController)AonUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
		incomeDetailController.onReset((ActionEvent)null);
		IncomeDetail to = (IncomeDetail)incomeDetailController.getTo(); 
		to.setDescription(incomeDetail.getDescription());
		to.setDiscountExpression(incomeDetail.getDiscountExpression());
		to.setIncome(incomeDetail.getIncome());
		to.setItem(incomeDetail.getItem());
		to.setPrice(incomeDetail.getPrice());
		to.setPurchaseDetail(null);
		to.setQuantity(incomeDetail.getQuantity());
		to.setWarehouse(incomeDetail.getWarehouse());
		incomeDetailController.accept(null);
	}
}