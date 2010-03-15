package com.code.aon.ui.tasDelivery.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Class used by the tasDeliveryImportController.
 * 
 */
public class TasDeliveryImport {

	/** DELIVERY_CONTROLLER_NAME. */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";

	/**
	 * Creates a <code>DeliveryWrapper</code> using a <code>TasOffer</code> if it exist or a 
	 * <code>SupportOrder</code> if not
	 * 
	 * @param supportOrder_Id the support order id
	 * 
	 * @return the delivery wrapper
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public static DeliveryWrapper onImport(Integer supportOrder_Id, Integer offer_Id, Delivery delivery) throws ManagerBeanException {
		DeliveryWrapper deliveryWrapper = null;
		SupportOrder supportOrder = getSupportOrder(supportOrder_Id);
        Offer offer = getOffer(offer_Id);
		if (offer != null){
			deliveryWrapper = createDeliveryWrapper(offer, delivery);
		}else{
			deliveryWrapper = createDeliveryWrapper(supportOrder, delivery);
		}
		return deliveryWrapper;
	}

	/**
	 * Gets the support order with id equals to the parameter supportOrder_Id.
	 * 
	 * @param supportOrder_Id the support order_ id
	 * 
	 * @return the support order
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private static SupportOrder getSupportOrder(Integer supportOrder_Id) throws ManagerBeanException{
		SupportOrder supportOrder = null;
		IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
		Criteria supportOrderCriteria = new Criteria();
		supportOrderCriteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), supportOrder_Id);
		Iterator iter = supportOrderBean.getList(supportOrderCriteria).iterator();
		if(iter.hasNext()){
			supportOrder = (SupportOrder) iter.next();
		}
		return supportOrder;
	}
	
	/**
	 * Gets the tas offer with supportOrder equals to the parameter supportOrder.
	 * 
	 * @param supportOrder the support order
	 * 
	 * @return the tas offer
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private static Offer getOffer(Integer offer_Id) throws ManagerBeanException{
		Offer offer = null;
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria offerCriteria = new Criteria();
		offerCriteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_ID), offer_Id);
		Iterator iter  = offerBean.getList(offerCriteria).iterator();
		if(iter.hasNext()){
			offer = (Offer) iter.next();
		}
		return offer;
	}

	/**
	 * Creates the delivery wrapper using the tasOffer passed as parameter.
	 * 
	 * @param tasOffer the tas offer
	 * 
	 * @return the delivery wrapper
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private static DeliveryWrapper createDeliveryWrapper(Offer offer, Delivery delivery)throws ManagerBeanException{
		List<ITransferObject> offerLines = getOfferLines(offer);
		
		DeliveryWrapper deliveryWrapper = new DeliveryWrapper();
		
		Customer customer = new Customer();
		customer.setId(offer.getTarget().getRegistry().getId());
        customer.setRegistry(offer.getTarget().getRegistry());
		delivery.setCustomer(customer);
		delivery.setIssueTime((delivery.getIssueTime()==null?offer.getIssueDate():delivery.getIssueTime()));
		delivery.setSeries((delivery.getSeries()!=null?delivery.getSeries():offer.getSeries()));
		delivery.setNumber((delivery.getNumber() > 0?delivery.getNumber():0));
		delivery.setSecurityLevel(SecurityLevel.OFFICIAL);
		delivery.setStatus(DeliveryStatus.PENDING);
		
		deliveryWrapper.setDelivery(delivery);
		
		List<DeliveryDetail> lines = new ArrayList<DeliveryDetail>();
		Iterator iter = offerLines.iterator();
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		while (iter.hasNext()){
			OfferDetail offerDetail = (OfferDetail)iter.next();
			
			DeliveryDetail deliveryDetail = new DeliveryDetail();
			deliveryDetail.setDelivery(delivery);
			deliveryDetail.setDiscountExpression(offerDetail.getDiscountExpression());
			deliveryDetail.setItem(offerDetail.getItem());
			deliveryDetail.setDescription(offerDetail.getDescription());
			deliveryDetail.setPrice(offerDetail.getPrice());
			deliveryDetail.setQuantity(offerDetail.getQuantity());
			deliveryDetail.setWarehouse(new Warehouse());
			deliveryDetail.getWarehouse().setId(deliveryController.getWarehouseId());
			
			lines.add(deliveryDetail);
		}
		deliveryWrapper.setLines(lines);

		return deliveryWrapper;
	}
	
	/**
	 * Gets the offer lines related with the offer passed as paramerter.
	 * 
	 * @param offer the offer
	 * 
	 * @return the offer lines
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	private static List<ITransferObject> getOfferLines(Offer offer) throws ManagerBeanException{
		List<ITransferObject> lines = null;
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria offerDetailCriteria = new Criteria();
		offerDetailCriteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
		lines = offerDetailBean.getList(offerDetailCriteria);
		return lines;
	}
	
	/**
	 * Creates the delivery wrapper using the supportOrder passed as parameter.
	 * 
	 * @param supportOrder the support order
	 * 
	 * @return the delivery wrapper
	 */
	private static DeliveryWrapper createDeliveryWrapper(SupportOrder supportOrder, Delivery delivery){
		DeliveryWrapper deliveryWrapper = new DeliveryWrapper();
		
//		Delivery delivery = new Delivery();
		Customer customer = new Customer();
        customer.setId(supportOrder.getTarget().getRegistry().getId());
		customer.setRegistry(supportOrder.getTarget().getRegistry());
		delivery.setCustomer(customer);
		delivery.setIssueTime((delivery.getIssueTime()==null?supportOrder.getStartDate():delivery.getIssueTime()));
		delivery.setSeries((delivery.getSeries()!=null?delivery.getSeries():null));
		delivery.setNumber((delivery.getNumber() > 0?delivery.getNumber():0));
		delivery.setSecurityLevel(SecurityLevel.OFFICIAL);
		delivery.setStatus(DeliveryStatus.PENDING);
		
		deliveryWrapper.setDelivery(delivery);
		
		return deliveryWrapper;
	}
}