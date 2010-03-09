package com.code.aon.ui.tasDelivery.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tas.enumeration.SupportOrderStatus;
import com.code.aon.tasCommercial.TasOffer;
import com.code.aon.tasCommercial.dao.ITasCommercialAlias;
import com.code.aon.tasDelivery.TasDelivery;
import com.code.aon.tasDelivery.dao.ITasDeliveryAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;

/**
 * Listener added to the DeliveryController.
 */
public class DeliveryTasListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DeliveryTasListener.class.getName());
	
	/**
	 * After bean added. If the current delivery has a support order related, adds a <code>TasDelivery</code> 
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)event.getController();
		if(deliveryController.getSupportOrderId() != null){
			TasDelivery tasDelivery = new TasDelivery();
			tasDelivery.setDelivery((Delivery)deliveryController.getTo());
			tasDelivery.setSupportOrder(obtainSupportOrder(deliveryController.getSupportOrderId()));
            tasDelivery.setOffer(obtainOffer(deliveryController.getOfferId()));
			try {
				IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
				tasDelivery = (TasDelivery)tasDeliveryBean.insert(tasDelivery);
				closeStatuses(tasDelivery);
				updateSupportOrderEmployee(tasDelivery.getSupportOrder(), deliveryController.getEmployeeId());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error inserting TasDelivery for Delivery with id= " + ((Delivery)deliveryController.getTo()).getId(), e);
			}
		}
	}
	
	/**
	 * After bean updated. Updates the related <code>TasDelivery</code> if necessary
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			DeliveryController deliveryController = (DeliveryController)event.getController();
			if(deliveryController.getSupportOrderId() != null){
				SupportOrder supportOrder = obtainSupportOrder(deliveryController.getSupportOrderId());
				if(supportOrder.getEmployee() == null && deliveryController.getEmployeeId() != null) {
					updateSupportOrderEmployee(supportOrder, deliveryController.getEmployeeId());
				}
				if(supportOrder.getEmployee() != null && !supportOrder.getEmployee().getId().equals(deliveryController.getEmployeeId())){
					updateSupportOrderEmployee(supportOrder, deliveryController.getEmployeeId());
				}
			}
			if(deliveryController.getSupportOrderId() != null && deliveryController.getOfferId() != null){
				TasDelivery tasDelivery = obtainTasDelivery(deliveryController.getSupportOrderId(), ((Delivery)deliveryController.getTo()).getId());
				tasDelivery.setOffer(obtainOffer(deliveryController.getOfferId()));
				IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
				tasDeliveryBean.update(tasDelivery);
				closeStatuses(tasDelivery);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private TasDelivery obtainTasDelivery(Integer supportOrderId, Integer deliveryId) throws ManagerBeanException {
		IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_SUPPORT_ORDER_ID), supportOrderId);
		criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID), deliveryId);
		Iterator iter = tasDeliveryBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (TasDelivery)iter.next();
		}
		return null;
	}

	/**
	 * Before bean removed. Removes the related <code>TasDelivery</code> if necessary
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Delivery delivery = (Delivery)((DeliveryController)event.getController()).getTo();
		try {
			IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID), delivery.getId());
			Iterator iter = tasDeliveryBean.getList(criteria).iterator();
			if(iter.hasNext()){
				TasDelivery tasDelivery = (TasDelivery)iter.next();
				tasDeliveryBean.remove(tasDelivery);
				setStatusesPending(tasDelivery.getSupportOrder());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing TasDelivery for delivery with id= " + delivery.getId(), e);
		}
	}
	
	/**
	 * After bean selected. Gets the the Employee id from the supportOrder of the related <code>TasDelivery</code>
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
        DeliveryController deliveryController = (DeliveryController)event.getController();
        deliveryController.setSupportOrderId(null);
        deliveryController.setDescription(null);
        deliveryController.setOfferId(null);
        deliveryController.setEmployeeId(null);

        Delivery delivery = (Delivery)deliveryController.getTo();
		try {
			IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
			Criteria criteria = new Criteria();
            criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID), delivery.getId());
			Iterator iter = tasDeliveryBean.getList(criteria).iterator();
			if(iter.hasNext()) {
				TasDelivery tasDelivery = (TasDelivery)iter.next();
                deliveryController.setSupportOrderId(tasDelivery.getSupportOrder().getId());
                deliveryController.setDescription(tasDelivery.getSupportOrder().getDescription());
                if(tasDelivery.getOffer() != null){
                    deliveryController.setOfferId(tasDelivery.getOffer().getId());
                }
                if(tasDelivery.getSupportOrder().getEmployee() != null){
					deliveryController.setEmployeeId(tasDelivery.getSupportOrder().getEmployee().getId());
				}
            }
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining TasDeliveryId for delivery with id= " + delivery.getId(), e);
		}
	}
	
	/**
	 * After bean reset. Sets to null the supportOrderId, employeeId and description of the controller
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)event.getController();
        deliveryController.setSupportOrderId(null);
        deliveryController.setOfferId(null);
		deliveryController.setEmployeeId(null);
		deliveryController.setDescription(null);
	}
	
	/**
	 * After bean created. Sets to null the supportOrderId, employeeId and description of the controller
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)event.getController();
		deliveryController.setSupportOrderId(null);
        deliveryController.setOfferId(null);
		deliveryController.setEmployeeId(null);
		deliveryController.setDescription(null);
	}
	
	/**
	 * Obtain support order. Obtains the supportOrder with id equals to parameter <code>supportOrderId</code>.
	 * 
	 * @param supportOrderId the support order id
	 * 
	 * @return the support order
	 */
	@SuppressWarnings("unchecked")
	private SupportOrder obtainSupportOrder(Integer supportOrderId) {
		try {
			IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), supportOrderId);
			Iterator iter = supportOrderBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (SupportOrder)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining SupportOrder with id= " + supportOrderId, e);
		}
		return null;
	}
	
    /**
     * Obtains the offer with id equals to parameter <code>id</code>.
     * 
     * @param id the id
     * 
     * @return the offer
     */
	@SuppressWarnings("unchecked")
    private Offer obtainOffer(Integer offerId) {
        if (offerId != null) {
            try {
                IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
                Criteria criteria = new Criteria();
                criteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_ID), offerId);
                Iterator iter = offerBean.getList(criteria).iterator();
                if(iter.hasNext()){
                    return (Offer)iter.next();
                }
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error obtaining Offer with id= " + offerId, e);
            }
        }
        return null;
    }

	/**
	 * Close statuses. Sets to finished the status of the supportOrder and to processed the status of the 
	 * related offer if it exists.
	 * 
	 * @param supportOrder the support order
	 */
    @SuppressWarnings("unchecked")
    private void closeStatuses(TasDelivery tasDelivery) {
		try {
			IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
			if(tasDelivery.getOffer()== null){
				tasDelivery.getSupportOrder().setStatus(SupportOrderStatus.ACTIVE);
			}else{
				tasDelivery.getSupportOrder().setStatus(SupportOrderStatus.FINISHED);
			}
			supportOrderBean.update(tasDelivery.getSupportOrder());

            IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
            if(tasDelivery.getOffer() != null) {
            	tasDelivery.getOffer().setStatus(OfferStatus.PROCESSED);
				offerBean.update(tasDelivery.getOffer());
			}

            IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_SUPPORT_ORDER_ID), tasDelivery.getSupportOrder().getId());
            if (tasDelivery.getOffer() != null) {
                Expression offerExpr = ExpressionUtilities.getNotEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_OFFER_ID), tasDelivery.getOffer().getId());
                criteria.addExpression(offerExpr);
            }
            Iterator iterator = tasOfferBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                TasOffer tasOffer = (TasOffer)iterator.next();
                tasOffer.getOffer().setStatus(OfferStatus.DENIED);
                offerBean.update(tasOffer.getOffer());
            }
        } catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating statuses for SupportOrder with id= " + tasDelivery.getSupportOrder().getId(), e);
		}
	}
	
	/**
	 * Updates the employee of the supportOrder
	 * 
	 * @param supportOrder the support order
	 * @param employeeId the employee id
	 */
	private void updateSupportOrderEmployee(SupportOrder supportOrder, Integer employeeId) {
		try {
			IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
			supportOrder.setEmployee(obtainEmployee(employeeId));
			supportOrderBean.update(supportOrder);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating employee with id= " + employeeId + " for SupportOrder with id= " + supportOrder.getId(), e);
		}
	}
	
	/**
	 * Obtains the employee with id equals to parameter <code>employeeId</code>.
	 * 
	 * @param employeeId the employee id
	 * 
	 * @return the employee
	 */
	@SuppressWarnings("unchecked")
	private Employee obtainEmployee(Integer employeeId) {
		try {
			IManagerBean employeeBean = BeanManager.getManagerBean(Employee.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(employeeBean.getFieldName(ICompanyAlias.EMPLOYEE_ID), employeeId);
			Iterator iter = employeeBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Employee)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Employee with id= " + employeeId, e);
		}
		return null;
	}

	/**
	 * Sets to pending the status of the supportOrder of the 
	 * relatedd offer if it exists.
	 * 
	 * @param supportOrder the support order
	 */
	@SuppressWarnings("unchecked")
	private void setStatusesPending(SupportOrder supportOrder) {
		try {
			IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
            supportOrder.setStatus(SupportOrderStatus.PENDING);
            supportOrder.setEmployee(null);
            supportOrderBean.update(supportOrder);

            IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
            IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_SUPPORT_ORDER_ID), supportOrder.getId());
			Iterator iter = tasOfferBean.getList(criteria).iterator();
			while(iter.hasNext()) {
				TasOffer tasOffer = (TasOffer)iter.next();
				tasOffer.getOffer().setStatus(OfferStatus.PENDING);
				offerBean.update(tasOffer.getOffer());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating statuses for SupportOrder with id= " + supportOrder.getId(), e);
		}
	}
}