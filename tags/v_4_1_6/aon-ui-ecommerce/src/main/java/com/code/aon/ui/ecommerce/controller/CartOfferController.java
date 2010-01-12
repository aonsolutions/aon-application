package com.code.aon.ui.ecommerce.controller;


import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.SystemUtils;

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
public class CartOfferController extends EmailParentController {
	
	private static final String ECOMMERCE_BUNDLE = "ecommerceBundle";
	private static final String BUNDLE = "bundle";

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
			List<CartItem> list = (List)sc.getModel().getWrappedData();//Cart().getList();
			Iterator<CartItem> it = list.iterator();
			while(it.hasNext()){
				CartItem ci = it.next();
				OfferDetail od = new OfferDetail();
				od.setOffer(getOffer());
				od.setItem(ci.getItem().getItem());
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
	
	public void onAccept(ActionEvent event) {
		Target target = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCartTarget().getEcTarget().getTarget();
		getOffer().setTarget(target);
		try {
			getOffer().setAddress(target.getRegistry().getDefaultAddress());
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PaypalController paypalBean = (PaypalController) AonUtil
				.getRegisteredBean(IECommerceConstants.PAYPAL_CONTROLLER);
		CreditCardController qbBean = (CreditCardController) AonUtil
				.getRegisteredBean(IECommerceConstants.CREDIT_CARD_CONTROLLER);
		
		if(paypalBean.isPayment()){
			paypalBean.confirmPaymentFromPayPal(null);
			paypalBean.setPayment(false);
			getOffer().setStatus(OfferStatus.INVOICED);
		}
		if(qbBean.isPayment()){
//			paypal.confirmPaymentFromPayPal(null);
			qbBean.setPayment(false);
			getOffer().setStatus(OfferStatus.INVOICED);
		}
		
		insertOffer();
		insertOfferDetail();
		((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).setModel(null);
		((ShopController)AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER)).setContentView(ViewEnum.ITEM_LIST);
		sendEmail();
	}
	
	/**
	 * creacion del presupuesto 
	 */
	public void initialize() {
		setOffer(new Offer());
		
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
			scope = (Scope)scopeBean.getList(null).get(0);
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
	
	public void sendEmail(){
		String from=null;
		String to = null;
		try {
			from = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getCompany().getEmail().getValue();
		} catch (ManagerBeanException e1) {
			String msg = "En los Datos de la Empresa no esta indicado el email";
			AonUtil.addErrorMessage(msg);
			new AbortProcessingException(msg,e1);
		}
		String subject = "AON-ECOMMERCE - Resumen de compra.";
		StringBuffer content = new StringBuffer();
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_cart_summary")).append(SystemUtils.LINE_SEPARATOR);

		ShoppingCartController sc = (ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER);
		List<CartItem> list = sc.getCart().getList();
		Iterator<CartItem> it = list.iterator();
		while(it.hasNext()){
			CartItem ci = it.next();
			
			content.append(	AonUtil.getMessage(BUNDLE,"aon_name")).append(": ");
			content.append( ci.getItem().getProduct().getName() ).append("\t");
			content.append(	AonUtil.getMessage(BUNDLE,"aon_price")).append(": ");
			content.append( ci.getItem().getPrice() ).append("\t");
			content.append(	AonUtil.getMessage(BUNDLE,"aon_quantity")).append(": ");
			content.append( ci.getItem().getQuantity() ).append("\t");
			content.append(	AonUtil.getMessage(BUNDLE,"aon_total")).append(": ");
			content.append( ci.getItem().getTotal() ).append(SystemUtils.LINE_SEPARATOR);
		}
		content.append(	AonUtil.getMessage(BUNDLE,"aon_total")).append(": ");
		content.append( sc.getCart().getTotal() ).append(SystemUtils.LINE_SEPARATOR);
		to = sc.getCartTarget().getEcTarget().getLogin();
		
		super.email(subject, from, to, content.toString());
	}
	
	public void onCloseSession(ActionEvent event) {
		ShoppingCartController scc = ((ShoppingCartController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER));
		scc.setModel(null);
		scc.onCartClean(null);
		String message = "Operación realizada satisfactoriamente.";
		message += "\n Se procede a la desconexión.";
		AonUtil.addInfoMessage(message);
	}
	
}
