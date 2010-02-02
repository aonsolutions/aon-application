package com.code.aon.ui.tasDelivery.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.tasDelivery.util.DeliveryWrapper;
import com.code.aon.ui.tasDelivery.util.TasDeliveryImport;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

/**
 * Controller used to import a supportOrder to create a delivery.
 * 
 */
public class TasDeliveryImportController extends BasicController {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TasDeliveryImportController.class.getName());

	/** DELIVERY_CONTROLLER_NAME. */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	/** DELIVERY_DETAIL_CONTROLLER_NAME. */
	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";
	
	/**
	 * Imports a supportOrder and the related offer if it exists, to create a delivery
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public void importDelivery(ActionEvent event) throws ManagerBeanException{
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		if(deliveryController.getSupportOrderId() == null){
			addMessage("SupportOrder_id required");
			throw new AbortProcessingException("SupportOrder_id required");
		}
		Delivery delivery = (Delivery)deliveryController.getTo();
		DeliveryWrapper deliveryWrapper = TasDeliveryImport.onImport(deliveryController.getSupportOrderId(), deliveryController.getOfferId(), delivery);
		if(deliveryController.isNew()){ //Se esta importando orden de reparacion y (puede que) presupuesto
			importSupportOrder(deliveryWrapper);
			if(deliveryWrapper.getLines() != null){
				importOffer(deliveryWrapper);
			}
		}else{ //se está importando sólo presupuesto
			importOffer(deliveryWrapper);
			deliveryController.accept((ActionEvent)null);
		}
	}
	
	private void importSupportOrder(DeliveryWrapper deliveryWrapper) {
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Delivery delivery = (Delivery)deliveryController.getTo();
		delivery.setCustomer(deliveryWrapper.getDelivery().getCustomer());
		delivery.setRaddress(obtainAddress(deliveryWrapper.getDelivery().getCustomer().getRegistry()));
		delivery.setIssueTime(deliveryWrapper.getDelivery().getIssueTime());
		delivery.setSecurityLevel(deliveryWrapper.getDelivery().getSecurityLevel());
		delivery.setStatus(deliveryWrapper.getDelivery().getStatus());
		deliveryController.accept((ActionEvent)null);
	}

	@SuppressWarnings("unchecked")
    private void importOffer(DeliveryWrapper deliveryWrapper) {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		deliveryDetailController.setImportingOffer(true);
		
		List<DeliveryDetail> lines = deliveryWrapper.getLines();
		if(lines != null){
			Iterator iter = lines.iterator();
			while (iter.hasNext()){
				deliveryDetailController.onReset((ActionEvent)null);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();
				
				DeliveryDetail deliveryDetailWrapped = (DeliveryDetail)iter.next();
				
				deliveryDetail.setDelivery(deliveryDetailWrapped.getDelivery());
				deliveryDetail.setDiscountExpression(deliveryDetailWrapped.getDiscountExpression());
				deliveryDetail.setItem(deliveryDetailWrapped.getItem());
				deliveryDetail.setDescription(deliveryDetailWrapped.getDescription());
				deliveryDetail.setPrice(deliveryDetailWrapped.getPrice());
				deliveryDetail.setQuantity(deliveryDetailWrapped.getQuantity());
				deliveryDetail.setWarehouse(deliveryDetailWrapped.getWarehouse());

				deliveryDetailController.onAccept((ActionEvent)null);
			}
		}
		deliveryDetailController.setImportingOffer(false);
	}
	
	/**
     * Gets the data of the current supportOrder.
     * 
     * @return the support order data
     * 
     * @throws ManagerBeanException the manager bean exception
     */
	@SuppressWarnings("unchecked")
    public String getSupportOrderData() throws ManagerBeanException{
        DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
        IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), deliveryController.getSupportOrderId());
        Iterator iter = supportOrderBean.getList(criteria).iterator();
        String data = new String();
        if(iter.hasNext()){
            SupportOrder supportOrder = (SupportOrder)iter.next();
            data = supportOrder.getTasItem().getPublicCode() + " / " + supportOrder.getTasItem().getModel().getMake().getName()+ " " + supportOrder.getTasItem().getModel().getName();
            deliveryController.setDescription(supportOrder.getDescription());
        }
        return data;
    }
    
    /**
     * Gets the data of the current offer.
     * 
     * @return the offer data
     * 
     * @throws ManagerBeanException the manager bean exception
     */
	@SuppressWarnings("unchecked")
    public String getOfferData() throws ManagerBeanException{
        DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
        IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_ID), deliveryController.getOfferId());
        Iterator iter = offerBean.getList(criteria).iterator();
        String data = new String();
        if(iter.hasNext()){
            Offer offer = (Offer)iter.next();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            data = (offer.getSeries()==null?"":offer.getSeries()+"/")+offer.getNumber()+ " - " +formatter.format(offer.getIssueDate());
        }
        return data;
    }
    
	/**
	 * Obtain the registryAddress related with the registry passed as parameter.
	 * 
	 * @param registry the registry
	 * 
	 * @return the registry address
	 */
	@SuppressWarnings("unchecked")
	private RegistryAddress obtainAddress(Registry registry) {
		try {
			IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),registry.getId());
			Iterator iter = registryAddressBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (RegistryAddress) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining RegistryAddress for registry with id: " + registry.getId(), e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isScopeNeeded() throws ManagerBeanException{
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		if(deliveryController.getSupportOrderId() != null){
			IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), deliveryController.getSupportOrderId());
			Iterator iter = supportOrderBean.getList(criteria,0,1).iterator();
			if(iter.hasNext()){
				SupportOrder supportOrder = (SupportOrder)iter.next();
				Customer customer = obtainCustomer(supportOrder.getTarget().getId());
				if(customer != null){
					deliveryController.setScopeId(customer.getScope().getId());
					return false;
				}else{
					return true;
				}
			}
		}else{
			return false;
		}
		List<Scope> scopes = UserUtils.getInstance().getCurrentUserScopes();
		if( scopes.size() > 1){
			return true;
		}else if(scopes.size() == 1){
			deliveryController.setScopeId(scopes.get(0).getId());
			return false;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Customer obtainCustomer(Integer id) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), id);
		Iterator iter = customerBean.getList(criteria,0,1).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}
}