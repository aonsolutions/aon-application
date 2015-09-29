package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class FeeUtils {
	
	public static final String FEE_CLIENT = "Cliente";
	public static final String FEE_PRODUCT = "Producto";
	public static final String FEE_QUANTITY = "Cantidad";
	public static final String FEE_PRICE = "Precio";
	public static final String FEE_DISCOUNT = "Descuento";
	public static final String FEE_START_DATE = "Fecha Inicio";
	public static final String FEE_END_DATE = "Fecha Fin";
	public static final String FEE_BILLING_DATE = "Fecha Facturaci\u00f3n";
	public static final String FEE_PERIOD = "Periodo";
	public static final String FEE_COMERCIAL = "Comercial";
	public static final String FEE_WORKPLACE = "Centro Trabajo";
	public static final String FEE_BILLING_GROUP = "Grupo Facturaci\u00f3n";
	public static final String FEE_CONFIDENTIAL = "Confidencial";
	public static final String FEE_DESCRIPTION = "Descripci\u00f3n";
	public static final String FEE_RECORD = "Expediente";
	public static final String FEE_DETAIL1 = "Detalle1";
	public static final String FEE_DETAIL2 = "Detalle2";
	public static final String FEE_DETAIL3 = "Detalle3";
	public static final String FEE_LINE = "Linea";
	
	
	public static Vector<String> feeList(){
		Vector<String> v = new Vector<String>();
		v.add(FEE_CLIENT);
		v.add(FEE_PRODUCT);
		v.add(FEE_QUANTITY);
		v.add(FEE_PRICE);
		v.add(FEE_DISCOUNT);
		v.add(FEE_START_DATE);
		v.add(FEE_END_DATE);
		v.add(FEE_BILLING_DATE);
		v.add(FEE_PERIOD);
		v.add(FEE_COMERCIAL);
		v.add(FEE_WORKPLACE);
		v.add(FEE_BILLING_GROUP);
		v.add(FEE_CONFIDENTIAL);
		v.add(FEE_DESCRIPTION);
		v.add(FEE_RECORD);
		v.add(FEE_DETAIL1);
		v.add(FEE_DETAIL2);
		v.add(FEE_DETAIL3);
		v.add(FEE_LINE);
		return v;
	}
	
	public static Vector<String> feeOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(FEE_END_DATE);
		v.add(FEE_PERIOD);
		v.add(FEE_COMERCIAL);
		v.add(FEE_WORKPLACE);
		v.add(FEE_BILLING_GROUP);
		v.add(FEE_CONFIDENTIAL);
		v.add(FEE_DESCRIPTION);
		v.add(FEE_RECORD);
		v.add(FEE_DETAIL1);
		v.add(FEE_DETAIL2);
		v.add(FEE_DETAIL3);
		v.add(FEE_LINE);
		return v;
	}
	
	public static Boolean feeCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		
		Integer num = 0;
		
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(estaFee(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 8;
	}
	
	public static Boolean estaFee(String s) {
		switch (s) {
		case FEE_CLIENT: return true;
		case FEE_PRODUCT: return true;
		case FEE_QUANTITY: return true;
		case FEE_PRICE: return true;
		case FEE_DISCOUNT: return true;
		case FEE_START_DATE: return true;
		case FEE_BILLING_DATE: return true;
		case FEE_WORKPLACE: return true;
		}
		return false;
	}
}
