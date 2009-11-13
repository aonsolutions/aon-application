package com.code.aon.ui.ecommerce.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;


public class ShopController {
	
	private boolean logged;
	private ViewEnum backView;
	private ViewEnum contentView;
	
	public ShopController() {
		setBackView(ViewEnum.ITEM_LIST);
		ConfigController cc = (ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
		if(cc.isWelcomeBanner()){
			setContentView(ViewEnum.WELCOME);
		} else {
			setContentView(ViewEnum.ITEM_LIST);
		}
	}

	public ViewEnum getBackView() {
		return backView;
	}
	public void setBackView(ViewEnum backView) {
		this.backView = backView;
	}
	public String backView() {
		return backView.getOutcome();
	}
	
	public ViewEnum getContentView() {
		return contentView;
	}
	public void setContentView(ViewEnum contentView) {
		this.contentView = contentView;
	}
	public String contentView() {
		return contentView.getOutcome();
	}

	public boolean isItemsView() {
		return getContentView() == ViewEnum.ITEM_LIST;
	}
	public boolean isDetailView() {
		return getContentView() == ViewEnum.ITEM_DETAIL;
	}
	public boolean isCartView() {
		return getContentView() == ViewEnum.SHOPPING_CART;
	}
	public boolean isWelcomeView() {
		return getContentView() == ViewEnum.WELCOME;
	}
	public boolean isPaymethodView() {
		return getContentView() == ViewEnum.PAYMETHOD;
	}

	public boolean isLogged() {
		return logged;
	}
	public void setLogged(boolean logged) {
		this.logged = logged;
	}
	
	public void refreshView(ActionEvent event) {
		ViewEnum newContentView = (getBackView()==null)?ViewEnum.ITEM_LIST:getBackView(); 
		setBackView( getContentView() );
		setContentView(newContentView);
	}
	
	public void onViewCart(ActionEvent event) {
		setContentView(ViewEnum.SHOPPING_CART);
		setBackView(ViewEnum.ITEM_LIST);
	}
	
	public void goList(ActionEvent event) {
		setContentView(ViewEnum.ITEM_LIST);
	}
	
	public ConfigController getConfigController(){
		return (ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
	}

	public CartOfferController getCartOfferController(){
		return (CartOfferController)AonUtil.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER);
	}

	public boolean isPaymethodPaypal() {
		if(getCartOfferController().getOffer().getPayMethod() == null){
			return false;
		}
		return getCartOfferController().getOffer().getPayMethod().equals(getConfigController().getActiveConfig().getPaypal());
	}
	
	public boolean isPaymethodBankDraft(){
		if(getCartOfferController().getOffer().getPayMethod() == null){
			return false;
		}
		return getCartOfferController().getOffer().getPayMethod().equals(getConfigController().getActiveConfig().getBankDraft());
	}
	public boolean isPaymethodBankTransfer(){
		if(getCartOfferController().getOffer().getPayMethod() == null){
			return false;
		}
		return getCartOfferController().getOffer().getPayMethod().equals(getConfigController().getActiveConfig().getBankTransfer());
	}
	public boolean isPaymethodCashOnDelivery(){
		if(getCartOfferController().getOffer().getPayMethod() == null){
			return false;
		}
		return getCartOfferController().getOffer().getPayMethod().equals(getConfigController().getActiveConfig().getCashOnDelivery());
	}
	public boolean isPaymethodVisa(){
		if(getCartOfferController().getOffer().getPayMethod() == null){
			return false;
		}
		return getCartOfferController().getOffer().getPayMethod().equals(getConfigController().getActiveConfig().getCreditCard());
	}
	
	public void paypal(){
		
	}
	
	public void paypalIpn(ActionEvent event){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Iterator<String> en = ec.getRequestParameterNames();
		String str = "cmd=_notify-validate";

		// read post from PayPal system and add 'cmd'
//		while(en.hasNext()){
//			String paramName = (String)en.next();
//			String paramValue = ec.getRequestParameterValuesMap().get(paramName).toString();
//			str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
//		}

		
//		<f:param name="cmd" value="_cart"/>
//		<f:param name="business" value="me@mybusiness.com"/>
//		<f:param name="currency_code" value="EUR"/>
//		<f:param name="item_name" value="HTML book"/>
//		<f:param name="amount" value="24.99"/>
//		<f:param name="add" value="1"/>
		
		
		
		// post back to PayPal system to validate
		// NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
		// using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
		// and configured for older versions.
		URL u = null;
		URLConnection uc = null;
		String res = null;
		try {
			u = new URL("https://www.paypal.com/cgi-bin/webscr");
			uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
			pw.println(str);
			pw.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			res = in.readLine();
			in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// assign posted variables to local variables
//		String itemName = ec.getRequestParameterValuesMap().get("item_name").toString();
//		String itemNumber = ec.getRequestParameterValuesMap().get("item_number").toString();
//		String paymentStatus = ec.getRequestParameterValuesMap().get("payment_status").toString();
//		String paymentAmount = ec.getRequestParameterValuesMap().get("mc_gross").toString();
//		String paymentCurrency = ec.getRequestParameterValuesMap().get("mc_currency").toString();
//		String txnId = ec.getRequestParameterValuesMap().get("txn_id").toString();
//		String receiverEmail = ec.getRequestParameterValuesMap().get("receiver_email").toString();
//		String payerEmail = ec.getRequestParameterValuesMap().get("payer_email").toString();

		//check notification validation
		if(res.equals("VERIFIED")) {
		// check that paymentStatus=Completed
		// check that txnId has not been previously processed
		// check that receiverEmail is your Primary PayPal email
		// check that paymentAmount/paymentCurrency are correct
		// process payment
		}
		else if(res.equals("INVALID")) {
		// log for investigation
		}
		else {
		// error
		}
	}
	
}
