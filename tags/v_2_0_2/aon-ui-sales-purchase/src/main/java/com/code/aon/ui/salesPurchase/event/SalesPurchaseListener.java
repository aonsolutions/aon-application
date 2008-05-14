package com.code.aon.ui.salesPurchase.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.salesPurchase.SalesPurchase;
import com.code.aon.salesPurchase.dao.ISalesPurchaseAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.salesPurchase.controller.SalesPurchaseController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Listener added to DeliveryDetailController.
 */
public class SalesPurchaseListener extends ControllerAdapter {
	
	/** LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SalesPurchaseListener.class.getName());
	
	/** SalesPurchase Controller name. */
	private static final String SALES_PURCHASE_CONTROLLER = "salesPurchase";

	/**
	 * After bean selected. Updates the SalesPurchaseController references(withPurchase, warehouseId)
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		 DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
		 try {
			IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesPurchaseBean.getFieldName(ISalesPurchaseAlias.SALES_PURCHASE_SALES_DETAIL_ID), deliveryDetail.getSalesDetail().getId());
			Iterator iter = salesPurchaseBean.getList(criteria).iterator();
			SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
			if(iter.hasNext()){
				salesPurchaseController.setWithPurchase(true);
				SalesPurchase salesPurchase = (SalesPurchase)iter.next();
				salesPurchaseController.setPurchaseWarehouseId(obtainPurchaseWarehouseId(salesPurchase));
				((SalesPurchase)salesPurchaseController.getTo()).setId(salesPurchase.getId());
				((SalesPurchase)salesPurchaseController.getTo()).setPurchaseDetail(salesPurchase.getPurchaseDetail());
				((SalesPurchase)salesPurchaseController.getTo()).setSalesDetail(salesPurchase.getSalesDetail());
			}else{
				salesPurchaseController.setWithPurchase(false);
				salesPurchaseController.setPurchaseWarehouseId(null);
				salesPurchaseController.onReset(null);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error after select deliverydetail with id= " + deliveryDetail.getId(), e);
		}
	}

	/**
	 * After bean added. Adds a <code>SalesPurchase</code> related with the current DeliveryDetail
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
		if(salesPurchaseController.isWithPurchase()){
			((SalesPurchase)salesPurchaseController.getTo()).setSalesDetail(deliveryDetail.getSalesDetail());
			PurchaseDetail purchaseDetail = insertPurchaseDetail(((SalesPurchase)salesPurchaseController.getTo()).getPurchaseDetail());
			try {
				IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
				((SalesPurchase)salesPurchaseController.getTo()).setPurchaseDetail(purchaseDetail);
				salesPurchaseBean.insert(salesPurchaseController.getTo());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error after bean added for deliveryDetail with id= " + deliveryDetail.getId(), e);
			}
		}
		salesPurchaseController.setWithPurchase(false);
		salesPurchaseController.setPurchaseWarehouseId(null);
	}
	
	/**
	 * After bean updated. Adds a <code>SalesPurchase</code> related with the current DeliveryDetail if
	 * the current deliveryDetail is withPurchase. 
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
		if(salesPurchaseController.isWithPurchase()){
			if(((SalesPurchase)salesPurchaseController.getTo()).getPurchaseDetail().getId()!= null){
				((SalesPurchase)salesPurchaseController.getTo()).setPurchaseDetail(updatePurchaseDetail(((SalesPurchase)salesPurchaseController.getTo()).getPurchaseDetail(), deliveryDetail));
			}else{
				((SalesPurchase)salesPurchaseController.getTo()).setSalesDetail(deliveryDetail.getSalesDetail());
				PurchaseDetail purchaseDetail = insertPurchaseDetail(((SalesPurchase)salesPurchaseController.getTo()).getPurchaseDetail());
				try {
					IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
					((SalesPurchase)salesPurchaseController.getTo()).setPurchaseDetail(purchaseDetail);
					salesPurchaseBean.insert(salesPurchaseController.getTo());
				} catch (ManagerBeanException e) {
					LOGGER.log(Level.SEVERE, "Error after bean added for deliveryDetail with id= " + deliveryDetail.getId(), e);
				}
			}
		}
		salesPurchaseController.setPurchaseWarehouseId(null);
	}

	/**
	 * After bean created. Reset the SalesPurchaseController references(withPurchase, warehouseId)
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
		salesPurchaseController.setWithPurchase(false);
		salesPurchaseController.setPurchaseWarehouseId(null);
		salesPurchaseController.onReset(null);
	}

	/**
	 * Before bean removed. Removes the related <code>SalesPurchase</code> if it exists
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
		try {
			if(salesPurchaseController.isWithPurchase()){
				IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
				salesPurchaseBean.remove(salesPurchaseController.getTo());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting salesPurchase for purchaseDetail with id=" + ((SalesPurchase)salesPurchaseController.getTo()).getPurchaseDetail().getId(), e);
		}
	}

	/**
	 * Adds the purchaseDetail
	 * 
	 * @param purchaseDetail the purchase detail
	 * 
	 * @return the purchase detail
	 */
	private PurchaseDetail insertPurchaseDetail(PurchaseDetail purchaseDetail) {
		IManagerBean purchaseDetailBean;
		Income income = null;
		if(purchaseDetail.getPurchase().getId() == null){
			purchaseDetail.setPurchase(insertPurchase(purchaseDetail.getPurchase()));
			income = insertIncome(purchaseDetail.getPurchase());
		}else{
			 income = obtainIncome(purchaseDetail.getPurchase());
			 updateIncome(income, purchaseDetail.getPurchase());
			 updatePurchase(purchaseDetail.getPurchase());
		}
		try {
			purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetail = (PurchaseDetail) purchaseDetailBean.insert(purchaseDetail);
			insertIncomeDetail(income, purchaseDetail);
			return purchaseDetail;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting purchaseDetail", e);
		}
		return null;
	}

	/**
	 * Adds the purchase
	 * 
	 * @param purchase the purchase
	 * 
	 * @return the purchase
	 */
	private Purchase insertPurchase(Purchase purchase) {
		try {
			IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
			return (Purchase)purchaseBean.insert(purchase);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting purchase", e);
		}
		return null;
	}
	
	/**
	 * Adds the income
	 * 
	 * @param purchase the purchase
	 * 
	 * @return the income
	 */
	private Income insertIncome(Purchase purchase) {
		Income income = new Income();
		income.setIssueTime(purchase.getIssueDate());
		income.setNumber(purchase.getNumber());
		income.setSeries(purchase.getSeries());
		income.setSupplier(purchase.getSupplier());
		income.setIncomeStatus(IncomeStatus.PENDING);
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			return (Income) incomeBean.insert(income);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error inserting income for purchase with id= " + purchase.getId(), e);
		}
		return null;
	}
	
	/**
	 * Adds the incomeDetail
	 * 
	 * @param purchaseDetail the purchase detail
	 * @param income the income
	 */
	private void insertIncomeDetail(Income income, PurchaseDetail purchaseDetail) {
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
		IncomeDetail incomeDetail = new IncomeDetail();
		incomeDetail.setIncome(income);
		incomeDetail.setPurchaseDetail(purchaseDetail);
		incomeDetail.setItem(purchaseDetail.getItem());
        incomeDetail.setDescription(purchaseDetail.getDescription());
		incomeDetail.setPrice(purchaseDetail.getPrice());
		incomeDetail.setQuantity(purchaseDetail.getQuantity());
		incomeDetail.setWarehouse(new Warehouse());
		incomeDetail.getWarehouse().setId(salesPurchaseController.getPurchaseWarehouseId());
		incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			incomeDetailBean.insert(incomeDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error inserting incomeDetail for purchaseDetail with id= " + purchaseDetail.getId(), e);
		}
	}
	
	/**
	 * Obtains the income related with the parameter <code>purchase</code>
	 * 
	 * @param purchase the purchase
	 * 
	 * @return the income
	 */
	@SuppressWarnings("unchecked")
	private Income obtainIncome(Purchase purchase) {
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			Criteria criteria = new Criteria();
			if(purchase.getSeries() != null){
				criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SERIES), purchase.getSeries());
			}
			criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_NUMBER), new Integer(purchase.getNumber()));
			criteria.addEqualExpression(incomeBean.getFieldName(IWarehouseAlias.INCOME_SUPPLIER_ID), purchase.getSupplier().getId());
			Iterator iter = incomeBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Income)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining income for purchase with id= " + purchase.getId(), e);
		}
		return null;
	}
	
	/**
	 * Updates the purchaseDetail.
	 * 
	 * @param purchaseDetail the purchase detail
	 * @param deliveryDetail the delivery detail
	 * 
	 * @return the purchase detail
	 */
	private PurchaseDetail updatePurchaseDetail(PurchaseDetail purchaseDetail, DeliveryDetail deliveryDetail) {
		Purchase purchase = updatePurchase(purchaseDetail.getPurchase());
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			purchaseDetail.setItem(deliveryDetail.getItem());
            purchaseDetail.setDescription(deliveryDetail.getDescription());
			purchaseDetail.setPrice(deliveryDetail.getPrice());
			purchaseDetail.setPurchase(purchase);
			purchaseDetail.setQuantity(deliveryDetail.getQuantity());
			purchaseDetail = (PurchaseDetail)purchaseDetailBean.update(purchaseDetail);
			updateIncomeDetail(purchaseDetail);
			return purchaseDetail;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating purchaseDetail for deliveryDetail with id= " + deliveryDetail.getId(), e);
		}
		return null;
	}

	/**
	 * Updates the incomeDetail.
	 * 
	 * @param purchaseDetail the purchase detail
	 */
	@SuppressWarnings("unchecked")
	private void updateIncomeDetail(PurchaseDetail purchaseDetail) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER);
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				incomeDetail.setIncome(updateIncome(incomeDetail.getIncome(), purchaseDetail.getPurchase()));
				incomeDetail.setItem(purchaseDetail.getItem());
                incomeDetail.setDescription(purchaseDetail.getDescription());
				incomeDetail.setPrice(purchaseDetail.getPrice());
				incomeDetail.setPurchaseDetail(purchaseDetail);
				incomeDetail.setQuantity(purchaseDetail.getQuantity());
				incomeDetail.setWarehouse(new Warehouse());
				incomeDetail.getWarehouse().setId(salesPurchaseController.getPurchaseWarehouseId());
				incomeDetailBean.update(incomeDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating incomeDetail for purchaseDetail with id=" + purchaseDetail.getId(), e);
		}
		
	}

	/**
	 * Updates the income.
	 * 
	 * @param purchase the purchase
	 * @param income the income
	 * 
	 * @return the income
	 */
	private Income updateIncome(Income income, Purchase purchase) {
		try {
			IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
			income.setSeries(purchase.getSeries());
			income.setNumber(purchase.getNumber());
			income.setIssueTime(purchase.getIssueDate());
			income.setSupplier(purchase.getSupplier());
			return (Income)incomeBean.update(income);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating income for purchase with id=" + purchase.getId(), e);
		}
		return null;
	}

	/**
	 * Updates the purchase.
	 * 
	 * @param purchase the purchase
	 * 
	 * @return the purchase
	 */
	private Purchase updatePurchase(Purchase purchase) {
		try {
			IManagerBean purhaseBean = BeanManager.getManagerBean(Purchase.class);
			return (Purchase)purhaseBean.update(purchase);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating purchase with id=" + purchase.getId() , e);
		}
		return null;
	}
	
	/**
	 * Obtains the warehouse id related with the parameter <code>salesPurchase</code> 
	 * 
	 * @param salesPurchase the sales purchase
	 * 
	 * @return the integer
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private Integer obtainPurchaseWarehouseId(SalesPurchase salesPurchase) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), salesPurchase.getPurchaseDetail().getId());
		Iterator iter = incomeDetailBean.getList(criteria).iterator();
		if(iter.hasNext()){
			IncomeDetail incomeDetail = (IncomeDetail)iter.next();
			return incomeDetail.getWarehouse().getId();
		}
		return null;
	}
}
