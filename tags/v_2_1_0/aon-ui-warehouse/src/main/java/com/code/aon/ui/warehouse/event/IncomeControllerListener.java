package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.Purchase;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * An Income listener
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class IncomeControllerListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomeControllerListener.class.getName());

	/**
	 * Assigns default status and suppliers address, the first of the supplier, before inserting
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((Income)((IncomeController)event.getController()).getTo()).setIncomeStatus(IncomeStatus.PENDING);
		((Income) ((IncomeController) event.getController()).getTo()).setRegistryAddress(obtainRegistryAddress(((Income) ((IncomeController) event.getController()).getTo()).getSupplier().getId()));
		super.beforeBeanAdded(event);
	}

	/**
	 * Assigns suppliers address before updating, becouse supplier can be updated
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		((Income) ((IncomeController) event.getController()).getTo()).setRegistryAddress(obtainRegistryAddress(((Income) ((IncomeController) event.getController()).getTo()).getSupplier().getId()));
		super.beforeBeanUpdated(event);
	}
	
	/**
	 * Notifies if incomes status is closed and stops the deleting process 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		if( ((Income)event.getController().getTo()).getIncomeStatus().equals(IncomeStatus.CLOSED) ){
			String args = "Income status: Closed";
			AonUtil.addFatalMessage(args);
			throw new AbortProcessingException(args);
		}
	}
	
	/**
	 * On selecting a new income assigns its warehouse to the controller  
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		IncomeController incomeController = (IncomeController)event.getController();
		incomeController.setWarehouseId(obtainWarehouseId((Income)incomeController.getTo()));
		super.afterBeanSelected(event);
	}

	/**
	 * When creates a new income assign controllers warehouse to null
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((IncomeController)event.getController()).setWarehouseId(null);
		super.afterBeanCreated(event);
	}

	/**
	 * When the income is updated, recovers the purchase related and updates it 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
			Income income = (Income)event.getController().getTo();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				Purchase purchase = incomeDetail.getPurchaseDetail().getPurchase();
				purchase.setDiscountExpression(new DiscountExpression("0.0"));
				purchase.setIssueDate(incomeDetail.getIncome().getIssueTime());
				purchase.setNumber(incomeDetail.getIncome().getNumber());
				purchase.setPayMethod(null);
				purchase.setSecurityLevel(incomeDetail.getIncome().getSecurityLevel());
				purchase.setSeries(incomeDetail.getIncome().getSeries());
				purchase.setSupplier(incomeDetail.getIncome().getSupplier());
				purchaseBean.update(purchase);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtains a registry address, the first recovered, giving a registry ident
	 * 
	 * @param id registry ident
	 * @return registry address
	 */
	@SuppressWarnings("unchecked")
	private RegistryAddress obtainRegistryAddress(Integer id) {
		try {
			IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),id);
			Iterator iter = registryAddressBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (RegistryAddress) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining RegistryAddress for registry with id: " + id, e);
		}
		return null;
	}

	/**
	 * Recover the warehouse ident related to a income
	 * 
	 * @param income related income
	 * @return the warehouse ident
	 */
	@SuppressWarnings("unchecked")
	private Integer obtainWarehouseId(Income income) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((IncomeDetail)iter.next()).getWarehouse().getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining warehouse for income with id= " + income.getId(), e);
		}
		return null;
	}
}