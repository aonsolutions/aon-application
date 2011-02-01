package com.code.aon.ui.ecommerce.paypal;

/**
 * Created by IntelliJ IDEA.
 * User: lhuynh
 * Date: Dec 6, 2007
 * Time: 5:06:52 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.EcPaymethod;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.controller.ConfigController;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class Paypalfunctions {

	private String gv_APIUserName;
	private String gv_APIPassword;
	private String gv_APISignature;

	private String gv_APIEndpoint;
	private String gv_BNCode;

	private String gv_Version;
	private String gv_nvpHeader;
	private String gv_ProxyServer;
	private String gv_ProxyServerPort;
	private int gv_Proxy;
	private boolean gv_UseProxy;
	private String PAYPAL_URL;

	public Paypalfunctions() {
		// lhuynh - Actions to be Done on init of this
		// class

		// BN Code is only applicable for partners
		gv_BNCode = "PP-ECWizard";
		// Replace <API_USERNAME> with your API Username
		// Replace <API_PASSWORD> with your API Password
		// Replace <API_SIGNATURE> with your Signature
//		gv_APIUserName = "<API_USERNAME>";
//		gv_APIPassword = "<API_PASSWORD>";
//		gv_APISignature = "<API_SIGNATURE>";
			
//		gv_APIUserName = "prueba_1255511730_biz_api1.esferalia.com";
//		gv_APIPassword = "1255511739";
//		gv_APISignature = "AiPC9BjkCyDFQXbSkoZcgqH3hpacAZa3zOITxD2n9C.JM5ji-BNZB2UK";
		
		Integer paypalId = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getActiveConfig().getPaypal().getId();
		Criteria criteria = new Criteria();
		EcPaymethod ecp = null;
		try {
			criteria.addEqualExpression(BeanManager.getManagerBean(EcPaymethod.class).getFieldName(IEbackofficeAlias.EC_PAYMETHOD_PAYMETHOD_ID), paypalId);
			ecp = (EcPaymethod)BeanManager.getManagerBean(EcPaymethod.class).getList(criteria).get(0);
		} catch (ManagerBeanException e) {
			String message = "Hubo un error de comunicacion con el servidor de Paypal";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException();
		}
		
		gv_APIUserName = ecp.getUserName();
		gv_APIPassword = ecp.getPassword();
		gv_APISignature = ecp.getSignature();

		boolean bSandbox = true;

		/*
		 * Servers for NVP API Sandbox: https://api-3t.sandbox.paypal.com/nvp
		 * Live: https://api-3t.paypal.com/nvp
		 */

		/*
		 * Redirect URLs for PayPal Login Screen Sandbox:
		 * https://www.sandbox.paypal
		 * .com/webscr&cmd=_express-checkout&token=XXXX Live:
		 * https://www.paypal.
		 * com/cgi-bin/webscr?cmd=_express-checkout&token=XXXX
		 */

		if (bSandbox == true) {
			gv_APIEndpoint = "https://api-3t.sandbox.paypal.com/nvp";
			PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
		} else {
			gv_APIEndpoint = "https://api-3t.paypal.com/nvp";
			PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		}

		String HTTPREQUEST_PROXYSETTING_SERVER = "";
		String HTTPREQUEST_PROXYSETTING_PORT = "";
		boolean USE_PROXY = false;

		gv_Version = "59.0";
//		gv_Version = "50.0";

		// WinObjHttp Request proxy settings.
		gv_ProxyServer = HTTPREQUEST_PROXYSETTING_SERVER;
		gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;
		gv_Proxy = 2; // 'setting for proxy activation
		gv_UseProxy = USE_PROXY;

	}

	/*********************************************************************************
	 * CallShortcutExpressCheckout: Function to perform the SetExpressCheckout
	 * API call
	 * 
	 * Inputs: paymentAmount: Total value of the shopping cart currencyCodeType:
	 * Currency code value the PayPal API paymentType: paymentType has to be one
	 * of the following values: Sale or Order or Authorization returnURL: the
	 * page where buyers return to after they are done with the payment review
	 * on PayPal cancelURL: the page where buyers return to when they cancel the
	 * payment review on PayPal
	 * 
	 * Output: Returns a HashMap object containing the response from the server.
	 *********************************************************************************/
	public HashMap CallShortcutExpressCheckout(String paymentAmount,
			String returnURL, String cancelURL) {

		/*
		 * '------------------------------------ ' 
		 * The currencyCodeType and
		 * paymentType ' are set to the selections made on the Integration
		 * Assistant 
		 * '------------------------------------
		 */

		String currencyCodeType = "EUR";
		String paymentType = "Sale";

		/*
		 * Construct the parameter string that describes the PayPal payment the
		 * varialbes were set in the web form, and the resulting string is
		 * stored in $nvpstr
		 */
		String nvpstr = "&Amt=" + paymentAmount + "&PAYMENTACTION="
				+ paymentType + "&ReturnUrl=" + URLEncoder.encode(returnURL)
				+ "&CANCELURL=" + URLEncoder.encode(cancelURL)
				+ "&CURRENCYCODE=" + currencyCodeType;

		
		
		
		
		
		
		
//		String nvpstr =
//		"&METHOD=SetExpressCheckout"
//		+"&RETURNURL=" + URLEncoder.encode(returnURL)
//		+"&CANCELURL=" + URLEncoder.encode(cancelURL)
//		
//		//encoder.add("AMT",request.getParameter("paymentAmount"));
//		+"&PAYMENTACTION"+ paymentType
//		+"&CURRENCYCODE="+ currencyCodeType
//		
////		encoder.add("NAME",request.getParameter("NAME"));
////		encoder.add("SHIPTOSTREET",request.getParameter("SHIPTOSTREET"));
////		encoder.add("SHIPTOCITY",request.getParameter("SHIPTOCITY"));
////		encoder.add("SHIPTOSTATE",request.getParameter("SHIPTOSTATE"));
////		encoder.add("SHIPTOCOUNTRYCODE",request.getParameter("SHIPTOCOUNTRYCODE"));
////		encoder.add("SHIPTOZIP",request.getParameter("SHIPTOZIP"));
//		+"&L_NAME0=L_NAME0"
//		+"&L_NUMBER0=1000"
//		+"&L_DESC0=Size: 8.8-oz"
//		+"&L_AMT0=L_AMT0"
//		+"&L_QTY0=L_QTY0"
//		+"&L_NAME1=L_NAME1"
//		+"&L_NUMBER1=10001"
//		+"&L_DESC1=Size: Two 24-piece boxes"
//		+"&L_AMT1=L_AMT1"
//		+"&L_QTY1=L_QTY1"
//		+"&L_ITEMWEIGHTVALUE1=0.5"
//		+"&L_ITEMWEIGHTUNIT1=lbs";
////		// add up all line amount, L_AMTns 
////		float ft = Float.valueOf(request.getParameter("L_QTY0").trim()).floatValue()*Float.valueOf(request.getParameter("L_AMT0").trim()).floatValue()+Float.valueOf(request.getParameter("L_QTY1").trim()).floatValue()*Float.valueOf(request.getParameter("L_AMT1").trim()).floatValue();
////		
////		encoder.add("ITEMAMT",String.valueOf(ft));
////		encoder.add("TAXAMT","2.59");
////		//amount = itemamount+ shippingamt+shippingdisc+taxamt+insuranceamount;
////		float amt = Util.round(ft + 5.00f+ 2.59f+1.00f,2);	
////		float maxamt = Util.round(amt+25.00f,2);
////		encoder.add("SHIPDISCAMT","-3.00");
////		encoder.add("AMT",String.valueOf(amt));
////		encoder.add("SHIPPINGAMT","8.00");
////		encoder.add("MAXAMT",String.valueOf(maxamt));
////		encoder.add("CALLBACK","https://www.ppcallback.com/callback.pl");
////		encoder.add("INSURANCEOPTIONOFFERED","true");
////		encoder.add("INSURANCEAMT","1.00");
////		encoder.add("L_SHIPPINGOPTIONISDEFAULT0","false");
////		encoder.add("L_SHIPPINGOPTIONNAME0","Ground");
////		encoder.add("L_SHIPPINGOPTIONLABEL0","UPS Ground 7 Days");
////		encoder.add("L_SHIPPINGOPTIONAMOUNT0","3.50");
////		encoder.add("L_SHIPPINGOPTIONISDEFAULT1","true");
////		encoder.add("L_SHIPPINGOPTIONNAME1","UPS Air");
////		encoder.add("L_SHIPPINGOPTIONlABEL1","UPS Next Day Air");
////		encoder.add("L_SHIPPINGOPTIONAMOUNT1","8.00");
////		encoder.add("CALLBACKTIMEOUT","4");
////		
////		
//		
		
		
		
		
		
		
		/*
		 * Make the call to PayPal to get the Express Checkout token If the API
		 * call succeded, then redirect the buyer to PayPal to begin to
		 * authorize payment. If an error occured, show the resulting errors
		 */

		HashMap nvp = httpcall("SetExpressCheckout", nvpstr);
//		HashMap nvp = httpcall("DoExpressCheckoutPayment ", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase("Success")) {
			return nvp;
		}

		return null;
	}

	/*********************************************************************************
	 * CallMarkExpressCheckout: Function to perform the SetExpressCheckout API
	 * call
	 * 
	 * Inputs: paymentAmount: Total value of the shopping cart currencyCodeType:
	 * Currency code value the PayPal API paymentType: paymentType has to be one
	 * of the following values: Sale or Order or Authorization returnURL: the
	 * page where buyers return to after they are done with the payment review
	 * on PayPal cancelURL: the page where buyers return to when they cancel the
	 * payment review on PayPal shipToName: the Ship to name entered on the
	 * merchant's site shipToStreet: the Ship to Street entered on the
	 * merchant's site shipToCity: the Ship to City entered on the merchant's
	 * site shipToState: the Ship to State entered on the merchant's site
	 * shipToCountryCode: the Code for Ship to Country entered on the merchant's
	 * site shipToZip: the Ship to ZipCode entered on the merchant's site
	 * shipToStreet2: the Ship to Street2 entered on the merchant's site
	 * phoneNum: the phoneNum entered on the merchant's site
	 * 
	 * Output: Returns a HashMap object containing the response from the server.
	 *********************************************************************************/
	public HashMap CallMarkExpressCheckout(String paymentAmount,
			String returnURL, String cancelURL, String shipToName,
			String shipToStreet, String shipToCity, String shipToState,
			String shipToCountryCode, String shipToZip, String shipToStreet2,
			String phoneNum) {
		/*
		 * '------------------------------------ ' The currencyCodeType and
		 * paymentType ' are set to the selections made on the Integration
		 * Assistant '------------------------------------
		 */
		String currencyCodeType = "EUR";
		String paymentType = "Sale";

		/*
		 * Construct the parameter string that describes the PayPal payment the
		 * varialbes were set in the web form, and the resulting string is
		 * stored in $nvpstr
		 */
		String nvpstr = "ADDROVERRIDE=1&Amt=" + paymentAmount
				+ "&PAYMENTACTION=" + paymentType;
		nvpstr = nvpstr.concat("&CURRENCYCODE=" + currencyCodeType
				+ "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL="
				+ URLEncoder.encode(cancelURL));

		nvpstr = nvpstr.concat("&SHIPTONAME=" + shipToName + "&SHIPTOSTREET="
				+ shipToStreet + "&SHIPTOSTREET2=" + shipToStreet2);
		nvpstr = nvpstr.concat("&SHIPTOCITY=" + shipToCity + "&SHIPTOSTATE="
				+ shipToState + "&SHIPTOCOUNTRYCODE=" + shipToCountryCode);
		nvpstr = nvpstr.concat("&SHIPTOZIP=" + shipToZip + "&PHONENUM"
				+ phoneNum);

		/*
		 * Make the call to PayPal to set the Express Checkout token If the API
		 * call succeded, then redirect the buyer to PayPal to begin to
		 * authorize payment. If an error occured, show the resulting errors
		 */

		HashMap nvp = httpcall("SetExpressCheckout", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null
				&& !(strAck.equalsIgnoreCase("Success") || strAck
						.equalsIgnoreCase("SuccessWithWarning"))) {
			return nvp;
		}

		return null;
	}

	/*********************************************************************************
	 * GetShippingDetails: Function to perform the GetExpressCheckoutDetails API
	 * call
	 * 
	 * Inputs: None
	 * 
	 * Output: Returns a HashMap object containing the response from the server.
	 *********************************************************************************/
	public HashMap GetShippingDetails(String token) {
		/*
		 * Build a second API request to PayPal, using the token as the ID to
		 * get the details on the payment authorization
		 */

		String nvpstr = "&TOKEN=" + token;

		/*
		 * Make the API call and store the results in an array. If the call was
		 * a success, show the authorization details, and provide an action to
		 * complete the payment. If failed, show the error
		 */

		HashMap nvp = httpcall("GetExpressCheckoutDetails", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null
				&& !(strAck.equalsIgnoreCase("Success") || strAck
						.equalsIgnoreCase("SuccessWithWarning"))) {
			return nvp;
		}
		return null;
	}

	/*********************************************************************************
	 * GetShippingDetails: Function to perform the DoExpressCheckoutPayment API
	 * call
	 * 
	 * Inputs: None
	 * 
	 * Output: Returns a HashMap object containing the response from the server.
	 *********************************************************************************/
	public HashMap ConfirmPayment(String token, String payerID,
			String finalPaymentAmount, String serverName) {

		/*
		 * '------------------------------------ ' The currencyCodeType and
		 * paymentType ' are set to the selections made on the Integration
		 * Assistant '------------------------------------
		 */
		String currencyCodeType = "EUR";
		String paymentType = "Sale";

		/*
		 * '----------------------------------------------------------------------------
		 * '---- Use the values stored in the session from the previous SetEC
		 * call
		 * '----------------------------------------------------------------------------
		 */
		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerID
				+ "&PAYMENTACTION=" + paymentType + "&AMT="
				+ finalPaymentAmount;

		nvpstr = nvpstr + "&CURRENCYCODE=" + currencyCodeType + "&IPADDRESS="
				+ serverName;

		/*
		 * Make the call to PayPal to finalize payment If an error occured, show
		 * the resulting errors
		 */
		HashMap nvp = httpcall("DoExpressCheckoutPayment", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null
				&& (strAck.equalsIgnoreCase("Success") || strAck
						.equalsIgnoreCase("SuccessWithWarning"))) {
			
			return nvp;
		}
		return null;

	}

	/*********************************************************************************
	 * httpcall: Function to perform the API call to PayPal using API signature @
	 * methodName is name of API method. @ nvpStr is nvp string. returns a NVP
	 * string containing the response from the server.
	 *********************************************************************************/
	public HashMap httpcall(String methodName, String nvpStr) {

		String version = "59.0";
		String agent = "Mozilla/4.0";
		String respText = "";
		HashMap nvp = null; // lhuynh not used?

		// deformatNVP( nvpStr );
		String encodedData = "METHOD=" + methodName + "&VERSION=" + gv_Version
				+ "&PWD=" + gv_APIPassword + "&USER=" + gv_APIUserName
				+ "&SIGNATURE=" + gv_APISignature + nvpStr + "&BUTTONSOURCE="
				+ gv_BNCode;

		try {
			URL postURL = new URL(gv_APIEndpoint);
			HttpURLConnection conn = (HttpURLConnection) postURL
					.openConnection();

			// Set connection parameters. We need to perform input and output,
			// so set both as true.
			conn.setDoInput(true);
			conn.setDoOutput(true);

			// Set the content type we are POSTing. We impersonate it as
			// encoded form data
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", agent);

			// conn.setRequestProperty( "Content-Type", type );
			conn.setRequestProperty("Content-Length", String
					.valueOf(encodedData.length()));
			conn.setRequestMethod("POST");

			// get the output stream to POST to.
			DataOutputStream output = new DataOutputStream(conn
					.getOutputStream());
			output.writeBytes(encodedData);
			output.flush();
			output.close();

			// Read input from the input stream.
			DataInputStream in = new DataInputStream(conn.getInputStream());
			int rc = conn.getResponseCode();
			if (rc != -1) {
				BufferedReader is = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String _line = null;
				while (((_line = is.readLine()) != null)) {
					respText = respText + _line;
				}
				nvp = deformatNVP(respText);
			}
			return nvp;
		} catch (IOException e) {
			// handle the error here
			return null;
		}
	}

	/*********************************************************************************
	 * deformatNVP: Function to break the NVP string into a HashMap pPayLoad is
	 * the NVP string. returns a HashMap object containing all the name value
	 * pairs of the string.
	 *********************************************************************************/
	public HashMap deformatNVP(String pPayload) {
		HashMap nvp = new HashMap();
		StringTokenizer stTok = new StringTokenizer(pPayload, "&");
		while (stTok.hasMoreTokens()) {
			StringTokenizer stInternalTokenizer = new StringTokenizer(stTok
					.nextToken(), "=");
			if (stInternalTokenizer.countTokens() == 2) {
				String key = URLDecoder.decode(stInternalTokenizer.nextToken());
				String value = URLDecoder.decode(stInternalTokenizer
						.nextToken());
				nvp.put(key.toUpperCase(), value);
			}
		}
		return nvp;
	}

	/*********************************************************************************
	 * RedirectURL: Function to redirect the user to the PayPal site token is
	 * the parameter that was returned by PayPal returns a HashMap object
	 * containing all the name value pairs of the string.
	 *********************************************************************************/
	public void RedirectURL(HttpServletResponse response, String token) {
		String payPalURL = PAYPAL_URL + token;

//		response.sendRedirect( payPalURL );
//		response.setStatus(302);
//		response.setHeader("Location", response.encodeRedirectURL(payPalURL));
//		response.setHeader("Connection", "close");
//		try {
//			response.sendRedirect( response.encodeRedirectURL(payPalURL) );
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(payPalURL);
		} catch (IOException e) {
			String message = "Paypal server is down.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}

	// end class
}
