package com.code.aon.ui.ecommerce.controller;

import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.code.aon.ui.ecommerce.paypal.Paypalfunctions;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;



public class PaypalController {
	
	private String payerId;
	private String token;
	
	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	
	
	/**
	 * 
	 */
	public void initialize(ActionEvent event){

		String paymentAmount = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCart().getTotal().toString();


        /*
        '------------------------------------
        ' The returnURL is the location where buyers return to when a
        ' payment has been succesfully authorized.
        '
        ' This is set to the value entered on the Integration Assistant
        '------------------------------------
        */

        String returnURL = "http://localhost:8080/aon-ecommerce/";

        /*
        '------------------------------------
        ' The cancelURL is the location buyers are sent to when they hit the
        ' cancel button during authorization of payment during the PayPal flow
        '
        ' This is set to the value entered on the Integration Assistant
        '------------------------------------
        */
        String cancelURL = "http://localhost:8080/aon-ecommerce/";

        /*
        '------------------------------------
        ' Calls the SetExpressCheckout API call
        '
        ' The CallShortcutExpressCheckout function is defined in the file PayPalFunctions.asp,
        ' it is included at the top of this file.
        '-------------------------------------------------
        */
        Paypalfunctions ppf = new Paypalfunctions();
        HashMap nvp = ppf.CallShortcutExpressCheckout (paymentAmount, returnURL, cancelURL);
        String strAck = nvp.get("ACK").toString();
        if(strAck !=null && strAck.equalsIgnoreCase("Success"))
        {
            //session.setAttribute("token", nvp.get("TOKEN").toString());
            //' Redirect to paypal.com
            //response.sendRedirect(response.encodeRedirectURL( nvp.get("TOKEN").toString() ));
            //Paypalfunctions ppf = new Paypalfunctions();
            ppf.RedirectURL(null, nvp.get("TOKEN").toString() );
        }
        else
        {
            // Display a user friendly Error on the page using any of the following error information returned by PayPal

            String ErrorCode = nvp.get("L_ERRORCODE0").toString();
            String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
            String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
            String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
        }
	}
	
	/**
	 * PayPal Express Checkout - MARK FLOW
	 * 
	 */
	public void addPayPalToBillingPage(){
		/*
		 ==================================================================
		 PayPal Express Checkout - MARK FLOW : START SNIPPET
		 ===================================================================
		*/
		//IMPORTANT NOTE: Please import Class paypalfunctions if not in the same package level.
		// import paypalfunctions;


//		if (PaymentOption == "PayPal")
//		{
//			/*
//			'------------------------------------
//			' The paymentAmount is the total value of 
//			' the shopping cart, that was set 
//			' earlier in a session variable 
//			' by the shopping cart page
//			'------------------------------------
//			*/
//
//			String paymentAmount = (String)session.getAttribute("Payment_Amount");
//
//			/*
//			'------------------------------------
//			' The currencyCodeType and paymentType 
//			' are set to the selections made on the Integration Assistant 
//			'------------------------------------
//			*/
//
//			String currencyCodeType = "EUR";
//			String paymentType = "Sale";
//
//			/*
//			'------------------------------------
//			' The returnURL is the location where buyers return to when a
//			' payment has been succesfully authorized.
//			'
//			' This is set to the value entered on the Integration Assistant 
//			'------------------------------------
//			*/
//
//			String returnURL = "http://localhost:8080/aon-ecommerce/";
//
//			/*
//			'------------------------------------
//			' The cancelURL is the location buyers are sent to when they hit the
//			' cancel button during authorization of payment during the PayPal flow
//			'
//			' This is set to the value entered on the Integration Assistant 
//			'------------------------------------
//			*/
//			String cancelURL = "http://localhost:8080/aon-ecommerce/";
//
//			/*
//			'------------------------------------
//			' When you integrate this code 
//			' set the variables below with 
//			' shipping address details 
//			' entered by the user on the 
//			' Shipping page.
//			'------------------------------------
//			*/
//
//			String shipToName 			= "<<ShiptoName>>";
//			String shipToStreet 		= "<<ShipToStreet>>";
//			String shipToStreet2 		= "<<ShipToStreet2>>"; //'Leave it blank if there is no value
//			String shipToCity 			= "<<ShipToCity>>";
//			String shipToState 			= "<<ShipToState>>";
//			String shipToCountryCode 	= "<<ShipToCountryCode>>"; //' Please refer to the PayPal country codes in the API documentation
//			String shipToZip 			= "<<ShipToZip>>";
//			String phoneNum 			= "<<PhoneNumber>>";
//
//			/*
//			'------------------------------------
//			' Calls the SetExpressCheckout API call
//			'
//			' The CallMarkExpressCheckout function is defined in the file PayPalFunctions.asp,
//			' it is included at the top of this file.
//			'-------------------------------------------------
//			*/
//			Paypalfunctions ppf = new Paypalfunctions();
//			HashMap nvp = ppf.CallMarkExpressCheckout (paymentAmount, returnURL, cancelURL,
//											shipToName, shipToStreet, shipToCity, shipToState,
//											shipToCountryCode, shipToZip, shipToStreet2, phoneNum );
//											
//			String strAck = nvp.get("ACK").toString();
//			if(strAck !=null && !(strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning")))
//			{
//				session.setAttribute("token", nvp.get("TOKEN").toString());
//				//' Redirect to paypal.com
//				ppf.ReDirectURL( nvp.get("TOKEN").toString());
//			}
//			else
//			{  
//				// Display a user friendly Error on the page using any of the following error information returned by PayPal
//				
//				String ErrorCode = nvp.get("L_ERRORCODE0").toString();
//				String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
//				String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
//				String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
//			}
//		}
//		else
//		{
//		 if (((PaymentOption == "Visa") || (PaymentOption == "MasterCard") || (PaymentOption == "Amex") || (PaymentOption = "Discover"))
//					and ( PaymentProcessorSelected == "PayPal Direct Payment"))
//
//			/*		
//			'------------------------------------
//			' The paymentAmount is the total value of 
//			' the shopping cart, that was set 
//			' earlier in a session variable 
//			' by the shopping cart page
//			'------------------------------------
//			*/
//			String paymentAmount = (String)session.getAttribute("Payment_Amount");
//
//			/*
//			'------------------------------------
//			' The paymentType that was selected earlier 
//			'------------------------------------
//			*/
//			String paymentType = "Sale";
//			
//			/*
//			' Set these values based on what was selected by the user on the Billing page Html form
//			*/
//			
//			String creditCardType 		= "<<Visa/MasterCard/Amex/Discover>>"; //' Set this to one of the acceptable values (Visa/MasterCard/Amex/Discover) match it to what was selected on your Billing page
//			String creditCardNumber 	= "<<CC number>>"; // ' Set this to the string entered as the credit card number on the Billing page
//			String expDate 				= "<<Expiry Date>>"; // ' Set this to the credit card expiry date entered on the Billing page
//			String cvv2 				= "<<cvv2>>"; // ' Set this to the CVV2 string entered on the Billing page 
//			String firstName 			= "<<firstName>>"; // ' Set this to the customer's first name that was entered on the Billing page 
//			String lastName 			= "<<lastName>>"; // ' Set this to the customer's last name that was entered on the Billing page 
//			String street 				= "<<street>>"; // ' Set this to the customer's street address that was entered on the Billing page 
//			String city 				= "<<city>>"; // ' Set this to the customer's city that was entered on the Billing page 
//			String state 				= "<<state>>"; // ' Set this to the customer's state that was entered on the Billing page 
//			String zip 					= "<<zip>>"; // ' Set this to the zip code of the customer's address that was entered on the Billing page 
//			String countryCode 			= "<<PayPal Country Code>>"; // ' Set this to the PayPal code for the Country of the customer's address that was entered on the Billing page 
//			String currencyCode 		= "<<PayPal Currency Code>>"; // ' Set this to the PayPal code for the Currency used by the customer 
//			
//			/*	
//			'------------------------------------------------
//			' Calls the DoDirectPayment API call
//			'
//			' The DirectPayment function is defined in PayPalFunctions.jsp 
//			' included at the top of this file.
//			'-------------------------------------------------
//			*/
//			nvp = ppf.DirectPayment ( paymentType, paymentAmount, creditCardType, creditCardNumber,
//									expDate, cvv2, firstName, lastName, street, city, state, zip, 
//									countryCode, currencyCode ); 
//
//			String strAck = nvp.get("ACK").toString();
//			if(strAck ==null || strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning") )
//			{
//				// Display a user friendly Error on the page using any of the following error information returned by PayPal
//				String ErrorCode = nvp.get("L_ERRORCODE0").toString();
//				String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
//				String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
//				String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
//			}
//		}
		/*
		 ==================================================================
		 PayPal Express Checkout - MARK FLOW : END SNIPPET
		 ===================================================================
		*/
	}
	
	
	/**
	 * PayPal Express Checkout - ORDER REVIEW
	 * 
	 */
	public void getShippingAddressFromPayPal(){
		/*
		 ==================================================================
		 PayPal Express Checkout - ORDER REVIEW : START SNIPPET
		 ===================================================================
		*/
		/* 
			This step indicates whether the user was sent here by PayPal 
			if this value is null then it is part of the regular checkout flow in the cart
		*/

//		String token = request.getAttribute("token");
//		if ( token != null)
//		{
//
//		//IMPORTANT NOTE: Please import Class paypalfunctions if not in the same package level.
//		// import paypalfunctions;
//
//			/*
//			'------------------------------------
//			' Calls the GetExpressCheckoutDetails API call
//			'
//			' The GetShippingDetails function is defined in PayPalFunctions.jsp
//			' included at the top of this file.
//			'-------------------------------------------------
//			*/
//			
//			String token = (String) session.getAttribute("token");
//			
//			Paypalfunctions ppf = new Paypalfunctions();
//			HashMap nvp = ppf.GetShippingDetails( token );
//			
//			if (nvp == null)
//				return;
//				
//			String strAck = nvp.get("ACK").toString();
//			if(strAck != null && !(strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning")))
//			{
//				String email 			= nvp.get("EMAIL").toString(); // ' Email address of payer.
//				String payerId 			= nvp.get("PAYERID").toString(); // ' Unique PayPal customer account identification number.
//				
//				session.setAttribute("PAYERID", payerId);
//				
//				String payerStatus		= nvp.get("PAYERSTATUS").toString(); // ' Status of payer. Character length and limitations: 10 single-byte alphabetic characters.
//				String salutation		= nvp.get("SALUTATION").toString(); // ' Payer's salutation.
//				String firstName		= nvp.get("FIRSTNAME").toString(); // ' Payer's first name.
//				String middleName		= nvp.get("MIDDLENAME").toString(); // ' Payer's middle name.
//				String lastName			= nvp.get("LASTNAME").toString(); // ' Payer's last name.
//				String suffix			= nvp.get("SUFFIX").toString(); // ' Payer's suffix.
//				String cntryCode		= nvp.get("COUNTRYCODE").toString(); // ' Payer's country of residence in the form of ISO standard 3166 two-character country codes.
//				String business			= nvp.get("BUSINESS").toString(); // ' Payer's business name.
//				String shipToName		= nvp.get("SHIPTONAME").toString(); // ' Person's name associated with this address.
//				String shipToStreet		= nvp.get("SHIPTOSTREET").toString(); // ' First street address.
//				String shipToStreet2	= nvp.get("SHIPTOSTREET2").toString(); // ' Second street address.
//				String shipToCity		= nvp.get("SHIPTOCITY").toString(); // ' Name of city.
//				String shipToState		= nvp.get("SHIPTOSTATE").toString(); // ' State or province
//				String shipToCntryCode	= nvp.get("SHIPTOCOUNTRYCODE").toString(); // ' Country code. 
//				String shipToZip		= nvp.get("SHIPTOZIP").toString(); // ' U.S. Zip code or other country-specific postal code.
//				String addressStatus 	= nvp.get("ADDRESSSTATUS").toString(); // ' Status of street address on file with PayPal   
//				String invoiceNumber	= nvp.get("INVNUM").toString(); // ' Your own invoice or tracking number, as set by you in the element of the same name in SetExpressCheckout request .
//				String phonNumber		= nvp.get("PHONENUM").toString(); // ' Payer's contact telephone number. Note:  PayPal returns a contact telephone number only if your Merchant account profile settings require that the buyer enter one. 
//
//				/*
//				' The information that is returned by the GetExpressCheckoutDetails call should be integrated by the partner into his Order Review 
//				' page		
//				*/
//			}
//			else
//			{  
//				// Display a user friendly Error on the page using any of the following error information returned by PayPal
//				
//				String ErrorCode = nvp.get("L_ERRORCODE0").toString();
//				String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
//				String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
//				String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
//			}
//		}
				
		/*
		 ==================================================================
		 PayPal Express Checkout - ORDER REVIEW : END SNIPPET
		 ===================================================================
		*/
	}
	
	
	
	
	/**
	 * PayPal Express Checkout - ORDER CONFIRM 
	 * 
	 */
	public void confirmPaymentFromPayPal(ActionEvent event){
		//String token = HttpServlet.this.ssion.this.getAttribute("token");
//		Map<String, String> session = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		 
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpSession session2 = request.getSession(true);
		
		 
//		String token = session.get("token");
//		String token = (String) session2.getAttribute("token");
		String token = getToken();
		
		if ( token != null)
		{

		//IMPORTANT NOTE: Please import Class paypalfunctions if not in the same package level.
		// import paypalfunctions;

		    /*
			'------------------------------------
			' Get the token parameter value stored in the session 
			' from the previous SetExpressCheckout call
			'------------------------------------
			*/
			//String token =  session.getAttribute("TOKEN");

			/*
			'------------------------------------
			' The paymentAmount is the total value of 
			' the shopping cart, that was set 
			' earlier in a session variable 
			' by the shopping cart page
			'------------------------------------
			*/
			
//			ServletRequest request = (ServletRequest) FacesContext
//					.getCurrentInstance().getExternalContext().getRequest();
//
//			if (request instanceof HttpServletRequest) {
//				HttpServletRequest httpRequest = (HttpServletRequest) request;
//
//				//String serverName = httpRequest.getServerName();
//				//int serverPort = httpRequest.getServerPort();
//			}

			
			
//			request.getRemoteAddr();
//			request.getAttribute("token");
//			request.getAttribute("PayerID");
//			request.getAttributeNames();
			
			//String serverName =  request.getServerName();
			String serverName =  request.getRemoteAddr();
			
			//String payerId = (String) session2.getAttribute("payerId");
//			String payerId =  session.get("PayerID");
			String payerId;
//			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("PayerID");
//			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("token");
			//String token =  session.getAttribute("token");
//			payerId = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getPayerID();
			payerId = getPayerId();
//			String finalPaymentAmount =  session.get("Payment_Amount");
			String finalPaymentAmount;
			finalPaymentAmount = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCart().getTotal().toString();
			/*
			'------------------------------------
			' Calls the DoExpressCheckoutPayment API call
			'
			' The ConfirmPayment function is defined in the file PayPalFunctions.jsp,
			' that is included at the top of this file.
			'-------------------------------------------------
			*/

			Paypalfunctions ppf = new Paypalfunctions();
			HashMap nvp = ppf.ConfirmPayment ( token, payerId, finalPaymentAmount, serverName );
			String strAck = nvp.get("ACK").toString();
			if(strAck !=null && (strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning")))
			{
				/*
				'********************************************************************************************************************
				'
				' THE PARTNER SHOULD SAVE THE KEY TRANSACTION RELATED INFORMATION LIKE 
				'                    transactionId & orderTime 
				'  IN THEIR OWN  DATABASE
				' AND THE REST OF THE INFORMATION CAN BE USED TO UNDERSTAND THE STATUS OF THE PAYMENT 
				'
				'********************************************************************************************************************
				*/

				String transactionId	= nvp.get("TRANSACTIONID").toString(); // ' Unique transaction ID of the payment. Note:  If the PaymentAction of the request was Authorization or Order, this value is your AuthorizationID for use with the Authorization & Capture APIs. 
				String transactionType 	= nvp.get("TRANSACTIONTYPE").toString(); //' The type of transaction Possible values: l  cart l  express-checkout 
				String paymentType		= nvp.get("PAYMENTTYPE").toString();  //' Indicates whether the payment is instant or delayed. Possible values: l  none l  echeck l  instant 
				String orderTime 		= nvp.get("ORDERTIME").toString();  //' Time/date stamp of payment
				String amt				= nvp.get("AMT").toString();  //' The final amount charged, including any shipping and taxes from your Merchant Profile.
				String currencyCode		= nvp.get("CURRENCYCODE").toString();  //' A three-character currency code for one of the currencies listed in PayPay-Supported Transactional Currencies. Default: USD. 
				String feeAmt			= nvp.get("FEEAMT").toString();  //' PayPal fee amount charged for the transaction
				//String settleAmt		= nvp.get("SETTLEAMT").toString();  //' Amount deposited in your PayPal account after a currency conversion.
				String taxAmt			= nvp.get("TAXAMT").toString();  //' Tax charged on the transaction.
				//String exchangeRate		= nvp.get("EXCHANGERATE").toString();  //' Exchange rate if a currency conversion occurred. Relevant only if your are billing in their non-primary currency. If the customer chooses to pay with a currency other than the non-primary currency, the conversion occurs in the customer’s account.
				
				/*
				' Status of the payment: 
						'Completed: The payment has been completed, and the funds have been added successfully to your account balance.
						'Pending: The payment is pending. See the PendingReason element for more information. 
				*/
				
				String paymentStatus	= nvp.get("PAYMENTSTATUS").toString(); 

				/*
				'The reason the payment is pending:
				'  none: No pending reason 
				'  address: The payment is pending because your customer did not include a confirmed shipping address and your Payment Receiving Preferences is set such that you want to manually accept or deny each of these payments. To change your preference, go to the Preferences section of your Profile. 
				'  echeck: The payment is pending because it was made by an eCheck that has not yet cleared. 
				'  intl: The payment is pending because you hold a non-U.S. account and do not have a withdrawal mechanism. You must manually accept or deny this payment from your Account Overview. 		
				'  multi-currency: You do not have a balance in the currency sent, and you do not have your Payment Receiving Preferences set to automatically convert and accept this payment. You must manually accept or deny this payment. 
				'  verify: The payment is pending because you are not yet verified. You must verify your account before you can accept this payment. 
				'  other: The payment is pending for a reason other than those listed above. For more information, contact PayPal customer service. 
				*/
				
				String pendingReason	= nvp.get("PENDINGREASON").toString();  

				/*
				'The reason for a reversal if TransactionType is reversal:
				'  none: No reason code 
				'  chargeback: A reversal has occurred on this transaction due to a chargeback by your customer. 
				'  guarantee: A reversal has occurred on this transaction due to your customer triggering a money-back guarantee. 
				'  buyer-complaint: A reversal has occurred on this transaction due to a complaint about the transaction from your customer. 
				'  refund: A reversal has occurred on this transaction because you have given the customer a refund. 
				'  other: A reversal has occurred on this transaction due to a reason not listed above. 
				*/
				
				String reasonCode		= nvp.get("REASONCODE").toString();   
				
								
				AonUtil.addErrorMessage(transactionId +	transactionType + paymentType + orderTime + amt + currencyCode + feeAmt + taxAmt + paymentStatus + pendingReason + reasonCode);
			}
			else
			{  
				// Display a user friendly Error on the page using any of the following error information returned by PayPal
				
				String ErrorCode = nvp.get("L_ERRORCODE0").toString();
				String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
				String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
				String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
//				StringBuffer sb = new StringBuffer();
//				sb.append(ErrorCode).append(ErrorShortMsg).append(ErrorLongMsg).append(ErrorSeverityCode);
//				AonUtil.addErrorMessage(ErrorCode +"\n"+ ErrorShortMsg +"\n"+ ErrorLongMsg +"\n"+ ErrorSeverityCode);
				AonUtil.addErrorMessage(ErrorShortMsg + ErrorLongMsg);
			}
		}		
	}
	
	
//	import com.paypal.sdk.services.NVPCallerServices;
//	import com.paypal.sdk.util.*;
//	import com.paypal.sdk.core.nvp.NVPEncoder;
//
//	import com.paypal.sdk.core.nvp.NVPDecoder;
	
	
//	static final String testEnv = "sandbox";
//	static final String devCentral = "developer";
//	static final String DEFAULT_USER_NAME = "sdk-three_api1.sdk.com";
//	static final String DEFAULT_PASSWORD = "QFZCWN5HZM8VBG7Q";
//	static final String DEFAULT_SIGNATURE = "A.d9eRKfd1yVkRrtmMfCFLTqa6M9AyodL0SJkhYztxUi8W9pCXF6.4NI";
//	
//	public void a(){
//		FacesContext context = FacesContext.getCurrentInstance();
//		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//
//		
//		
//		//NVPCallerServiced object is taken from the session
////		NVPCallerServices caller = (NVPCallerServices) session.getValue("caller");
//	    
//		StringBuffer url = new StringBuffer();
//		url.append("http://");
//		url.append(request.getServerName());
//		url.append(":");
//		url.append(request.getServerPort());
//		url.append(request.getContextPath());
//		
//		String returnURL = url.toString() + "/nvp/GetExpressCheckoutDetails.jsp?=" + "&currencyCodeType=" + request.getParameter("currencyCodeType");
//		String cancelURL = url.toString() + "/nvp/SetExpressCheckout.jsp?"+"paymentType=" + request.getParameter("paymentType") ;
//		
//		String strNVPRequest = "";
//		StringBuffer sbErrorMessages= new StringBuffer("");
//
//		//NVPEncoder object is created and all the name value pairs are loaded into it.
//		NVPEncoder encoder = new NVPEncoder();
//
////		encoder.add("METHOD","SetExpressCheckout");
//		encoder.add("RETURNURL",returnURL + "?paymentAmount=" + request.getParameter("paymentAmount") + "&currencyCodeType=" + request.getParameter("currencyCodeType"));
//		encoder.add("CANCELURL",cancelURL);
//		
//		//encoder.add("AMT",request.getParameter("paymentAmount"));
//		encoder.add("PAYMENTACTION",request.getParameter("paymentType"));
//		encoder.add("CURRENCYCODE",request.getParameter("currencyCodeType"));	
//		
//		
//		encoder.add("NAME",request.getParameter("NAME"));
//		encoder.add("SHIPTOSTREET",request.getParameter("SHIPTOSTREET"));
//		encoder.add("SHIPTOCITY",request.getParameter("SHIPTOCITY"));
//		encoder.add("SHIPTOSTATE",request.getParameter("SHIPTOSTATE"));
//		encoder.add("SHIPTOCOUNTRYCODE",request.getParameter("SHIPTOCOUNTRYCODE"));
//		encoder.add("SHIPTOZIP",request.getParameter("SHIPTOZIP"));
//		encoder.add("L_NAME0",request.getParameter("L_NAME0"));
//		encoder.add("L_NUMBER0","1000");
//		encoder.add("L_DESC0","Size: 8.8-oz");
//		encoder.add("L_AMT0",request.getParameter("L_AMT0"));
//		encoder.add("L_QTY0",request.getParameter("L_QTY0"));
//		encoder.add("L_NAME1",request.getParameter("L_NAME1"));
//		encoder.add("L_NUMBER1","10001");
//		encoder.add("L_DESC1","Size: Two 24-piece boxes");
//		encoder.add("L_AMT1",request.getParameter("L_AMT1"));
//		encoder.add("L_QTY1",request.getParameter("L_QTY1"));
//		encoder.add("L_ITEMWEIGHTVALUE1","0.5");
//		encoder.add("L_ITEMWEIGHTUNIT1","lbs");
//		// add up all line amount, L_AMTns 
//		float ft = Float.valueOf(request.getParameter("L_QTY0").trim()).floatValue()*Float.valueOf(request.getParameter("L_AMT0").trim()).floatValue()+Float.valueOf(request.getParameter("L_QTY1").trim()).floatValue()*Float.valueOf(request.getParameter("L_AMT1").trim()).floatValue();
//		
//		encoder.add("ITEMAMT",String.valueOf(ft));
//		encoder.add("TAXAMT","2.59");
//		//amount = itemamount+ shippingamt+shippingdisc+taxamt+insuranceamount;
////		float amt = Util.round(ft + 5.00f+ 2.59f+1.00f,2);	
//		float amt = Math.round(ft + 5.00f+ 2.59f+1.00f);	
////		float maxamt = Util.round(amt+25.00f,2);
//		float maxamt = Math.round(amt+25.00f);
//		encoder.add("SHIPDISCAMT","-3.00");
//		encoder.add("AMT",String.valueOf(amt));
//		encoder.add("SHIPPINGAMT","8.00");
//		encoder.add("MAXAMT",String.valueOf(maxamt));
//		encoder.add("CALLBACK","https://www.ppcallback.com/callback.pl");
//		encoder.add("INSURANCEOPTIONOFFERED","true");
//		encoder.add("INSURANCEAMT","1.00");
//		encoder.add("L_SHIPPINGOPTIONISDEFAULT0","false");
//		encoder.add("L_SHIPPINGOPTIONNAME0","Ground");
//		encoder.add("L_SHIPPINGOPTIONLABEL0","UPS Ground 7 Days");
//		encoder.add("L_SHIPPINGOPTIONAMOUNT0","3.50");
//		encoder.add("L_SHIPPINGOPTIONISDEFAULT1","true");
//		encoder.add("L_SHIPPINGOPTIONNAME1","UPS Air");
//		encoder.add("L_SHIPPINGOPTIONlABEL1","UPS Next Day Air");
//		encoder.add("L_SHIPPINGOPTIONAMOUNT1","8.00");
//		encoder.add("CALLBACKTIMEOUT","4");
//		
////		session.setAttribute("paymentType", request.getParameter("paymentType"));
////		session.setAttribute("currencyCodeType", request.getParameter("currencyCodeType"));
////		String testEnv = (String)session.getAttribute("environment");
//		Paypalfunctions ppf = new Paypalfunctions();
//		try {
//			//encode method will encode the name and value and form NVP string for the request	
//			strNVPRequest = encoder.encode(); 
//
//			//call method will send the request to the server and return the response NVPString
////			String ppresponse =
////				(String) caller.call( strNVPRequest);
//			HashMap nvp = ppf.httpcall("SetExpressCheckout", strNVPRequest);
//			String ppresponse = nvp.toString();
//
//			//NVPDecoder object is created
//			NVPDecoder resultValues = new NVPDecoder();
//			
//			//decode method of NVPDecoder will parse the request and decode the name and value pair			
//			resultValues.decode(ppresponse);
//			
//				//checks for Acknowledgement and redirects accordingly to display error messages		
//			String strAck = resultValues.get("ACK"); 
//			if(strAck !=null && !(strAck.equals("Success") || strAck.equals("SuccessWithWarning")))
//			{
////				session.setAttribute("response",resultValues);
////				response.sendRedirect("APIError.jsp");
//				return;
//			}else {
//				
//				
//				ppf.RedirectURL(null,resultValues.get("TOKEN"));
////				response.sendRedirect("https://www."+testEnv+".paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+resultValues.get("TOKEN"));
//						
//			}
//		} catch (Exception e) {
////			session.setAttribute("exception", e);
////			response.sendRedirect("Error.jsp");
//			return;
//		}
//	}
	
}
