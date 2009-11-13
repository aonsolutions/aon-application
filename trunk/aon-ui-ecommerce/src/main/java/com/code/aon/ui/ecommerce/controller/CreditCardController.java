package com.code.aon.ui.ecommerce.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;


public class CreditCardController {

	private final String TESTING_URL = "https://tpv2.4b.es/simulador/teargral.exe";
	private final String PRODUCTION_URL = "https://tpv2.4b.es/simulador/teargral.exe";
	
	
	public String getQbTestingUrl(){
		return TESTING_URL;
	}

	public String getQbProductionUrl(){
		return PRODUCTION_URL;
	}
	
	public String getQbData(){
		ShoppingCartController scc = (ShoppingCartController) AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER);
		final String LINE_BREAK = "\r\n";
		final String EURO_CODE = "M978";
		
//		String data = "M978900\r\n1\r\n1\r\ndesc_1\r\n1\r\n300\r\n2\r\ndesc_2\r\n2\r\n600\r\n";
		
		
		
//		Importe total de la compra  -->  M978 para euro
//		Numero de registros (ítems) de la cesta de la compra
//		Registros de la cesta de la compra
//		-	Referencia
//		-	Descripción
//		-	Unidades
//		-	Precio
		
		
//		M978900\r\n
//		1\r\n

//		1\r\n
//		desc_1\r\n
//		1\r\n
//		300\r\n
		
//		2\r\n
//		desc_2\r\n
//		2\r\n
//		600\r\n
		
		String data=null;
		
		data += EURO_CODE+(scc.getCart().getTotal()*100)+LINE_BREAK;
		data += scc.getQuantity()+LINE_BREAK;
		
		for(CartItem ci: scc.getCart().getList()){
			data += ci.getItem().getId()+LINE_BREAK;
			data += ci.getItem().getDescription()+LINE_BREAK;
			data += ci.getQuantity()+LINE_BREAK;
			data += ci.getTotal()+LINE_BREAK;
		}
		
		
		return data;
	}
	
//	public void redirectUrl(ActionEvent event) {
//		
//		try {
//			String url = "https://tpv2.4b.es/simulador/teargral.exe";
//			String version = "59.0";
//			String agent = "Mozilla/4.0";
//			URL postURL;
//			postURL = new URL(url);
//			HttpURLConnection conn = (HttpURLConnection) postURL.openConnection();
//
//			// Set connection parameters. We need to perform input and output,
//			// so set both as true.
//			conn.setDoInput(true);
//			conn.setDoOutput(true);
//
//			// Set the content type we are POSTing. We impersonate it as
//			// encoded form data
//			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//			conn.setRequestProperty("User-Agent", agent);
//
//			// conn.setRequestProperty( "Content-Type", type );
//			// conn.setRequestProperty("Content-Length",
//			// String.valueOf(encodedData.length()));
//			conn.setRequestMethod("POST");
//			
//			
//		
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			FacesContext facesContext = FacesContext.getCurrentInstance();
//			facesContext.getExternalContext().redirect(TESTING_URL);
//			facesContext.responseComplete();
//		} catch (IOException e) {
//			String message = "Paypal server is down.";
//			AonUtil.addErrorMessage(message);
//			throw new AbortProcessingException(message, e);
//		}
//	}
//	
//	public void doPostMethodCall(ActionEvent event){
////		Construct data
//        String data;
//		try {
//			data = URLEncoder.encode("order", "UTF-8") + "=" + URLEncoder.encode("COMPRA", "UTF-8");
//	        data += "&" + URLEncoder.encode("store", "UTF-8") + "=" + URLEncoder.encode("PI00012936", "UTF-8");
//	        // Send data
//	        URL url = new URL(TESTING_URL);
//			URLConnection conn = url.openConnection();
////			<input type="hidden" name="order" value="COMPRA"/> 
////			<input type="hidden" name="store" value="PI00012936"/> 
//			conn.setDoOutput(true);
//	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//	        wr.write(data);
//	        wr.flush();
//	        // Get the response
//	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//	        String line;
//	        while ((line = rd.readLine()) != null) {
//	            // Process line...
//	        }
//	        wr.close();
//	        rd.close();
//	    
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
	
	

		public static void main(String[] args) {
			Map map = new HashMap();
			Map weakMap = new WeakHashMap();
			imprime("map", map);
			imprime("weakMap", weakMap);
			for (int i = 0; i < 50; i++) {
				System.out.println("=======================n=======================");
				System.out.println("Insertando: " + i);
				System.out.println("Insertando: " + i + "_key");
				map.put("" + i, "" + i);
				map.put("" + i + "_key", "" + i);
				weakMap.put("" + i, "" + i);
				weakMap.put("" + i + "_key", "" + i);
				imprime("map", map);
				imprime("weakMap", weakMap);
				if (map.keySet().size() != weakMap.keySet().size()) {
					System.out.println("DISTINTO TAMAÑO !!!");
				}
				System.out.println("Eliminando: " + i);
				map.remove("" + i);
				weakMap.remove("" + i);
				imprime("map", map);
				imprime("weakMap", weakMap);
			}
		}        

		private static void imprime(String texto, Map map) {
			System.out.println("n-----------" + texto + "-----------");
			System.out.println("Size: " + map.keySet().size());
			System.out.println("Map:" + map);
			System.out.println("------------------------------n");
		}
	

}
