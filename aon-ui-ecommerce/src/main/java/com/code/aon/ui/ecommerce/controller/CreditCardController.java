package com.code.aon.ui.ecommerce.controller;



public class CreditCardController {

	private final String TESTING_URL = "https://tpv2.4b.es/simulador/teargral.exe";
	private final String PRODUCTION_URL = "https://tpv2.4b.es/simulador/teargral.exe";
	
	
	public String getQbTestingUrl(){
		return TESTING_URL;
	}

	public String getQbProductionUrl(){
		return PRODUCTION_URL;
	}
	
//	public String getQbData(){
//		ShoppingCartController scc = (ShoppingCartController) AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER);
//		final String LINE_BREAK = "\r\n";
//		final String EURO_CODE = "M978";
//		
////		Importe total de la compra  -->  M978 para euro
////		Numero de registros (ítems) de la cesta de la compra
////		Registros de la cesta de la compra
////		-	Referencia
////		-	Descripción
////		-	Unidades
////		-	Precio
//		
//		
////		M978900\r\n
////		1\r\n
//
////		1\r\n
////		desc_1\r\n
////		1\r\n
////		300\r\n
//		
////		2\r\n
////		desc_2\r\n
////		2\r\n
////		600\r\n
//		
//		String data=null;
//		
//		data += EURO_CODE+(scc.getCart().getTotal()*100)+LINE_BREAK;
//		data += scc.getCart().getQuantity()+LINE_BREAK;
//		
//		for(CartItem ci: scc.getCart().getList()){
//			data += ci.getItem().getId()+LINE_BREAK;
//			data += ci.getItem().getDescription()+LINE_BREAK;
//			data += ci.getQuantity()+LINE_BREAK;
//			data += ci.getTotal()+LINE_BREAK;
//		}
//		return data;
//	}
}
