package com.code.aon.ui.ecommerce.controller;


import java.util.Calendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

/**
* Controller used in the cart offer maintenance.  
* 
* @author Esferalia Networks. Ekain Agirrezabal- 15-sep-2009
* @since 1.0
*  
*/
public class OfferController {

	private Offer offer;
	private List<OfferDetail> offerDetail;

	public Offer getOffer() {
		return offer;
	}
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	public List<OfferDetail> getOfferDetail() {
		return offerDetail;
	}
	public void setOfferDetail(List<OfferDetail> offerDetail) {
		this.offerDetail = offerDetail;
	}
	
	/**
	 * Insert current offer
	 * @param offer
	 * @throws ManagerBeanException 
	 */
	public void insert() throws ManagerBeanException{
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		offerBean.insert(getOffer());
	}

	/**
	 * Adds a new item to the offer detail list
	 */
	public void addItem(){
		
	}
	
	
	public void onAccept(ActionEvent event) {
		try {
			insert();
		} catch (ManagerBeanException e) {
			AonUtil.addInfoMessage("Fallo al recuperar el offer.");
		}
	}
	
	/**
	 * creacion del presupuesto 
	 */
	public void initialize() {
		
//		OfferController offerController = (OfferController)FormUtil.getController(IECommerceConstants.OFFER_CONTROLLER);
//		offerController.setOffer(new Offer());
		
		setOffer(new Offer());
		
//		Offer offer = offerController.getOffer();
		//Target target = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCartTarget().getEcTarget().getTarget();
		//offer.setTarget(target);
		offer.setWorkPlace(new WorkPlace());
//		try {
//			//offer.setWorkPlace((WorkPlace)((CompanyCollectionsController)AonUtil.getRegisteredBean("companyCollections")).getWorkPlaces().get(0).getValue());
//			//offer.setWorkPlace((WorkPlace)(WorkPlace)ECommerceUtil.getWorkPlaces().get(0));
//		} catch (ManagerBeanException e) {
//			//offer.setWorkPlace(new WorkPlace());
//			AonUtil.addInfoMessage("Fallo al recuperar el workplace.");
//		}
		
		String series = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getActiveConfig().getSeries();

		offer.setNumber(0);
		// El type debe ser INTERNET
		offer.setType(OfferType.INTERNET);
		offer.setStatus(OfferStatus.PENDING);
		offer.setIssueDate(Calendar.getInstance().getTime());
		offer.setSeries(series);
		//
		offer.setScope(new Scope());
		//offer.set
		
//		try {
//			offerController.insert();
//		} catch (ManagerBeanException e) {
//			AonUtil.addInfoMessage("Fallo al recuperar el offer.");
//		}
		
		
		/*
		-private String series;
	    ?-private int number;
	    private Target target;
	    private RegistryAddress address;
	    private Tariff tariff;
	    private Seller seller;
	    private DiscountExpression discountExpression;
	    -private Date issueDate;
	    private PayMethod payMethod;
	    private SecurityLevel securityLevel;
		?-status;
		?-type;
		?-workPlace;
		  
		 */
		
		
		
		
		
		
		
		
//		OfferController offerController = (OfferController)FormUtil.getController(IECommerceConstants.OFFER_CONTROLLER);
//		//OfferDetailController offerDetailController = (OfferDetailController)FormUtil.getController(IECommerceConstants.OFFER_DETAIL_CONTROLLER);
//		if(offerController.exist(getCartTarget().getTarget())){
//			// ********************************************************
//			// ********************************************************
//			//  EN CASO DE EXISTIR AINADIR EL TARGET CORRESPONDIENTE AL OFFER
//			// ********************************************************
//			// ********************************************************
//			 
//		} else {
//			offerController.onReset(null);
//			Offer to = (Offer)offerController.getTo();
//			getCartTarget().getTarget().setId(738);
//			to.setTarget(getCartTarget().getTarget());
//			to.setStatus(OfferStatus.PENDING);
//			try {
//				// ********************************************************
//				// ********************************************************
//				//  REPASAR EL WORKPLACE QUE HAY QUE AINADIR POR DEFECTO
//				// ********************************************************
//				// ********************************************************
//				to.setWorkPlace((WorkPlace)((CompanyCollectionsController)AonUtil.getRegisteredBean("companyCollections")).getWorkPlaces().get(0).getValue());
//				//to.setWorkPlace((WorkPlace)ECommerceUtil.getWorkPlaces().get(0));
//			} catch (ManagerBeanException e) {
//				e.printStackTrace();
//			}
//			offerController.accept(null);
//			for(CartItem ci:getList()){
//				offerDetailController.onReset(null);
//				OfferDetail tod = (OfferDetail)offerDetailController.getTo();
//				tod.setOffer(to);
//				tod.setItem(ci.getItem());
//				tod.setDescription(ci.getItem().getProduct().getName());
//				tod.setQuantity(ci.getQuantity());
//				tod.setPrice(ci.getItem().getPrice());
//				tod.setDiscountExpression(null);
//				offerDetailController.onAccept(null);
//			}
//		}
//		
	}
	
	
}
