package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.Sales;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * A listener for DeliveryController
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-jun-2006
 * @since 1.0
 * 
 */
public class DeliveryControllerListener extends LinesControllerListener {
	
	/**
	 * The logger of the class
	 */
	private static final Logger LOGGER = Logger.getLogger(DeliveryControllerListener.class.getName());
	
	/**
	 * DeliveryDetail controller name
	 */
	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";
	
	/**
	 * Delivery controller name
	 */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	/**
	 * Fills the criteria  
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		Criteria criteria;
		try {
			criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME));
			criteria.addOrder(event.getController().getFieldName(IWarehouseAlias.DELIVERY_SERIES));
			criteria.addOrder(event.getController().getFieldName(IWarehouseAlias.DELIVERY_NUMBER));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e.getCause());
		}
	}

	/** 
	 * Sets DeliveryDetail controller new flag to false
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCanceled(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController) AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		deliveryDetailController.setNew(false);
	}

	/**
	 * Set default status to pending and default address
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((Delivery) ((DeliveryController) event.getController()).getTo()).setStatus(DeliveryStatus.PENDING);
		((Delivery) ((DeliveryController) event.getController()).getTo()).setRaddress(obtainRegistryAddress(((Delivery) ((DeliveryController) event.getController()).getTo()).getCustomer().getRegistry().getId()));
		super.beforeBeanAdded(event);
	}

	/**
	 * Default address asigned
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		((Delivery) ((DeliveryController) event.getController()).getTo()).setRaddress(obtainRegistryAddress(((Delivery) ((DeliveryController) event.getController()).getTo()).getCustomer().getRegistry().getId()));
		super.beforeBeanUpdated(event);
	}
	
	/**
	 * Assigns default warehouse id from delivery controller
	 * 
	 * @see com.code.aon.ui.form.listener.LinesControllerListener#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)event.getController();
        deliveryController.setWarehouseId(obtainWarehouseId((Delivery)deliveryController.getTo()));
		//deliveryController.setPosId(obtainPosId((Delivery)deliveryController.getTo()));
		super.afterBeanSelected(event);
	}
	
	/** 
	 * Assigns warehouse ident to null at the controller
	 * 
	 * @see com.code.aon.ui.form.listener.LinesControllerListener#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((DeliveryController)event.getController()).setPosId(null);
		((DeliveryController)event.getController()).setWarehouseId(null);
		super.afterBeanCreated(event);
	}

	/**
	 * Updates Sales data of the SalesDetail of this DeliveryDetail 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@SuppressWarnings("unchecked")
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
			Delivery delivery = (Delivery) event.getController().getTo();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID),delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				DeliveryDetail deliveryDetail = (DeliveryDetail) iter.next();
				/*
				Sales sales = deliveryDetail.getSalesDetail().getSales();
				DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
				sales.setPos(obtainPos(deliveryController.getPosId()));
				sales.setDiscountExpression(new DiscountExpression("0.0"));
				sales.setIssueDate(deliveryDetail.getDelivery().getIssueTime());
				sales.setNumber(deliveryDetail.getDelivery().getNumber());
				sales.setPayMethod(null);
				sales.setSecurityLevel(deliveryDetail.getDelivery().getSecurityLevel());
				sales.setSeries(deliveryDetail.getDelivery().getSeries());
				sales.setCustomer(deliveryDetail.getDelivery().getCustomer());
				sales.setShippingAddress(delivery.getRaddress());
				salesBean.update(sales);
				*/
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the first registry address of the registry of this ident
	 * 
	 * @param id ident of the registry
	 * @return the first address
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
	 * Returns the Warehouse ident of the first DeliveryDetail of this Delivery
	 * 
	 * @param delivery the Delivery
	 * @return the Ident of the Warehouse
	 */
	@SuppressWarnings("unchecked")
	private Integer obtainWarehouseId(Delivery delivery) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((DeliveryDetail)iter.next()).getWarehouse().getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining warehouse for delivery with id= " + delivery.getId(), e);
		}
		return null;
	}
	
	/**
	 * Returns the id of the PointOfSale related with the given Delivery
	 * 
	 * @param delivery the Delivery
	 * @return the Ident of the Warehouse
	 */
	@SuppressWarnings("unchecked")
	private Integer obtainPosId(Delivery delivery) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				return((DeliveryDetail)iter.next()).getSalesDetail().getSales().getPos().getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining pos for delivery with id= " + delivery.getId(), e);
		}
		return null;
	}
	
	/**
	 * Returns the PointOfSale with the given id
	 * 
	 * @param delivery the Delivery
	 * @return the Ident of the Warehouse
	 */
	@SuppressWarnings("unchecked")
	private PointOfSale obtainPos(Integer posId) {
		try {
			IManagerBean posBean = BeanManager.getManagerBean(PointOfSale.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posBean.getFieldName(ISalesAlias.POINT_OF_SALE_ID), posId);
			Iterator iter = posBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (PointOfSale)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining pos with id= " + posId, e);
		}
		return null;
	}
}