package com.code.aon.ui.ecommerce.controller;


import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

/**
* Controller used in the cart offer maintenance.  
* 
* @author Esferalia Networks. Ekain Agirrezabal- 15-sep-2009
* @since 1.0
*  
*/
public class CartOfferController {

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
	 */
	public void insertOffer() {
		IManagerBean offerBean;
		try {
//			OfferController o = (OfferController)FormUtil.getController("offer");
//			Offer offer = (Offer)o.getTo();
			
			offerBean = BeanManager.getManagerBean(Offer.class);
			offerBean.insert(getOffer());
		} catch (ManagerBeanException e) {
			AonUtil.addInfoMessage("Fallo al recuperar el offer.");
			throw new AbortProcessingException(e);
		}
	}

	/**
	 * Insert current offer's item list
	 * @param offer
	 */
	public void insertOfferDetail() {
		IManagerBean offerDetailBean;
		try {
			offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		
			ShoppingCartController sc = (ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER);
			List<CartItem> list = sc.getList();
			Iterator<CartItem> it = list.iterator();
			while(it.hasNext()){
				CartItem ci = it.next();
				OfferDetail od = new OfferDetail();
				od.setOffer(getOffer());
				od.setItem(ci.getItem().getItem());
				// line - not null
				od.setLine(list.indexOf(ci)+1);
				od.setDescription(ci.getItem().getProduct().getName());
				od.setQuantity(ci.getQuantity());
				od.setPrice(ci.getItem().getPrice());
				od.setDiscountExpression(new DiscountExpression());
				od.setStatus(OfferDetailStatus.PENDING);
				
				offerDetailBean.insert(od);
			}
			
		} catch (ManagerBeanException e) {
			AonUtil.addInfoMessage("Fallo al recuperar el offerDetail.");
			throw new AbortProcessingException(e);
		}
	}
	
	/**
	 * Adds a new item to the offer detail list
	 */
	public void addItem(){
		
	}
	
	
	public void onAccept(ActionEvent event) {
		Target target = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCartTarget().getEcTarget().getTarget();
		getOffer().setTarget(target);
		try {
			getOffer().setAddress(target.getRegistry().getDefaultAddress());
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insertOffer();
		insertOfferDetail();
//		try {
//		} catch (ManagerBeanException e) {
//			AonUtil.addInfoMessage("Fallo al recuperar el offer.");
//		}
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
		
//		try {
//			//offer.setWorkPlace((WorkPlace)((CompanyCollectionsController)AonUtil.getRegisteredBean("companyCollections")).getWorkPlaces().get(0).getValue());
//			//offer.setWorkPlace((WorkPlace)(WorkPlace)ECommerceUtil.getWorkPlaces().get(0));
//		} catch (ManagerBeanException e) {
//			//offer.setWorkPlace(new WorkPlace());
//			AonUtil.addInfoMessage("Fallo al recuperar el workplace.");
//		}
		
		
		String series = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getActiveConfig().getSeries();

		// target
		offer.setSeries(series);
		offer.setNumber(SeriesNumberUtil.obtainNumber(series, "Offer"));
		//RegistryAddress - not null
		offer.setAddress(new RegistryAddress());
		offer.setTariff(null);
		offer.setSeller(null);
		offer.setDiscountExpression(new DiscountExpression());
		offer.setIssueDate(Calendar.getInstance().getTime());
		offer.setPayMethod(null);
		offer.setSecurityLevel(SecurityLevel.OFFICIAL);
		offer.setStatus(OfferStatus.PENDING);
		offer.setType(OfferType.INTERNET);
		// workplace - not null
		//offer.setWorkPlace(new WorkPlace());
		try {
			offer.setWorkPlace((WorkPlace)(WorkPlace)ECommerceUtil.getWorkPlaces().get(0));
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// scope - not null
		Scope scope = new Scope();
		IManagerBean scopeBean;
		try {
			scopeBean = BeanManager.getManagerBean(Scope.class);
			//Criteria criteria = new Criteria();
			//criteria.addOrder(scopeBean.getFieldName(IConfigAlias.SCOPE_DESCRIPTION));
			scope = (Scope)scopeBean.getList(null).get(0);
			//#{configCollections.currentUserScopes}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//scope.setId(0);
		offer.setScope(scope);
		offer.setNumberOfPayments(1);
		offer.setDaysToFirstPayment(0);
		offer.setDaysBetweenPayments(0);
		offer.setPaymentDays("");
		offer.setBank(null);
		offer.setBankAccount(null);
		
		
	}
	
	
}
