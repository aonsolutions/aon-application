package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class StockUtils {

	public static final String STOCK_PRODUCT = "Producto";
	public static final String STOCK_QUANTITY = "Cantidad";
	public static final String STOCK_DETAIL1 = "Detalle 1";
	public static final String STOCK_DETAIL2 = "Detalle 2";
	public static final String STOCK_DETAIL3 = "Detalle 3";
	public static final String STOCK_NAME = "Nombre";
	
	public static Vector<String> stockList(){
		Vector<String> v = new Vector<String>();
		v.add(STOCK_PRODUCT);
		v.add(STOCK_QUANTITY);
		v.add(STOCK_DETAIL1);
		v.add(STOCK_DETAIL2);
		v.add(STOCK_DETAIL3);
		v.add(STOCK_NAME);
		return v;
	}
	
	public static Vector<String> stockOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(STOCK_DETAIL1);
		v.add(STOCK_DETAIL2);
		v.add(STOCK_DETAIL3);
		v.add(STOCK_NAME);
		return v;
	}
	
	public static Boolean stockCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		
		Integer num = 0;
		
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(estaStock(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 2;
	}
	public static Boolean estaStock(String s) {
		switch (s) {
		case STOCK_PRODUCT: return true;
		case STOCK_QUANTITY : return true;
		}
		return false;
	}
}
