package com.code.aon.ui.ecommerce.controller;

import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.code.aon.ui.ecommerce.paypal.Paypalfunctions;

import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;



public class PaypalController {
	
	private String payerId;
	private String token;
	private boolean payment;
	
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

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	
	
	
	/**
	 * 
	 */
	public void initialize(ActionEvent event){

		String paymentAmount = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCart().getTotal().toString();
        String returnURL = ECommerceUtil.getUrl();
        String cancelURL = ECommerceUtil.getUrl();

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
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpSession session2 = request.getSession(true);
		
		String token = request.getParameter("token");
		
		if ( token != null)
		{

			/*
			'------------------------------------
			' The paymentAmount is the total value of 
			' the shopping cart, that was set 
			' earlier in a session variable 
			' by the shopping cart page
			'------------------------------------
			*/

			String serverName =  request.getRemoteAddr();
			String payerId = getPayerId();
			String finalPaymentAmount = ((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCart().getTotal().toString();
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
				
								
				AonUtil.addInfoMessage(transactionId +	transactionType + paymentType + orderTime + amt + currencyCode + feeAmt + taxAmt + paymentStatus + pendingReason + reasonCode);
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
	
}
