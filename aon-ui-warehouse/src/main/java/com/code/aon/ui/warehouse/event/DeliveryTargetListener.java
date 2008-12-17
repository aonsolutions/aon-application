package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;

/**
 * A DeliveryController listener that manages the Customer
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class DeliveryTargetListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(DeliveryTargetListener.class.getName());
	
	/**
	 * If exists, assigns the customer to the Delivery else
	 * creates it and assigns
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)event.getController(); 
		Delivery delivery = (Delivery)deliveryController.getTo();
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), delivery.getCustomer().getRegistry().getId());
			Iterator iter = customerBean.getList(criteria).iterator();
			if(!iter.hasNext()){
				IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
				criteria = new Criteria();
				criteria.addEqualExpression(targetBean.getFieldName(ICommercialAlias.TARGET_REGISTRY_ID), delivery.getCustomer().getRegistry().getId());
				iter = targetBean.getList(criteria).iterator();
				if(iter.hasNext()){
					Target target = (Target)iter.next();
					delivery.getCustomer().setRegistry(target.getRegistry());
					delivery.getCustomer().setScope(obtainScope(deliveryController.getScopeId()));
					delivery.getCustomer().setStatus(CustomerStatus.ACTIVE);
					customerBean.insert(delivery.getCustomer());
				}
			}else{
				((Delivery)((DeliveryController)event.getController()).getTo()).setCustomer((Customer)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting customer for target with id= " + delivery.getCustomer().getRegistry().getId(), e);
		}
	}

	/**
	 * If exists, assigns the customer to the Delivery else
	 * creates it and assigns
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)event.getController(); 
		Delivery delivery = (Delivery)deliveryController.getTo();
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), delivery.getCustomer().getRegistry().getId());
			Iterator iter = customerBean.getList(criteria).iterator();
			if(!iter.hasNext()){
				IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
				criteria = new Criteria();
				criteria.addEqualExpression(targetBean.getFieldName(ICommercialAlias.TARGET_REGISTRY_ID), delivery.getCustomer().getRegistry().getId());
				iter = targetBean.getList(criteria).iterator();
				if(iter.hasNext()){
					Target target = (Target)iter.next();
					delivery.getCustomer().setRegistry(target.getRegistry());
					delivery.getCustomer().setScope(obtainScope(deliveryController.getScopeId()));
					delivery.getCustomer().setStatus(CustomerStatus.ACTIVE);
					customerBean.insert(delivery.getCustomer());
				}
			}else{
				((Delivery)((DeliveryController)event.getController()).getTo()).setCustomer((Customer)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting customer for target with id= " + delivery.getCustomer().getRegistry().getId(), e);
		}
	}
	
	/**
	 * Returns workplace.
	 * 
	 * @return workplace
	 */
	private Scope obtainScope(Integer scopeId) {
		try {
			IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(scopeBean.getFieldName(IConfigAlias.SCOPE_ID), scopeId);
			List<ITransferObject> scopeLst = scopeBean.getList(criteria);
			if (scopeLst.size() > 0) {
				Scope scope = (Scope)scopeLst.get(0);
				return scope;
			}
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
		}
		return null;
	}
}