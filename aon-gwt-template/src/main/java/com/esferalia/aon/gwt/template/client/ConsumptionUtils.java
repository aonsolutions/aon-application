package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class ConsumptionUtils {

	public static final String CONSUMPTION_PRODUCT = "Producto";
	public static final String CONSUMPTION_NAME = "Nombre";
	public static final String CONSUMPTION_INITIAL = "Inicial";
	public static final String CONSUMPTION_PURCHASES = "Compras";
	public static final String CONSUMPTION_SALES = "Ventas";
	public static final String CONSUMPTION_FINAL = "Final";
	public static final String CONSUMPTION_TRANSFER = "Traspaso";
	public static final String CONSUMPTION_PRICE = "Precio";
	public static final String CONSUMPTION_CONSUMPTION_VALUE = "Valor Consumo";
	public static final String CONSUMPTION_CONSUMPTION = "Consumo";
	
	public static Vector<String> consumptionList(){
		Vector<String> v = new Vector<String>();
		v.add(CONSUMPTION_PRODUCT);
		v.add(CONSUMPTION_NAME);
		v.add(CONSUMPTION_INITIAL);
		v.add(CONSUMPTION_PURCHASES);
		v.add(CONSUMPTION_SALES);
		v.add(CONSUMPTION_FINAL);
		v.add(CONSUMPTION_TRANSFER);
		v.add(CONSUMPTION_PRICE); // precio del producto
		v.add(CONSUMPTION_CONSUMPTION_VALUE);// precio total del consumo
		v.add(CONSUMPTION_CONSUMPTION);
		return v;
	}
	
	public static Vector<String> consumptionOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(CONSUMPTION_NAME);
		return v;
	}
	
	public static Boolean consumptionCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		
		Integer num = 0;
		
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(isConsumption(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 9;
	}
	public static Boolean isConsumption(String s) {
		switch (s) {
		case CONSUMPTION_PRODUCT: return true;
		case CONSUMPTION_INITIAL: return true;
		case CONSUMPTION_PURCHASES: return true;
		case CONSUMPTION_SALES: return true;
		case CONSUMPTION_FINAL: return true;
		case CONSUMPTION_TRANSFER: return true;
		case CONSUMPTION_PRICE: return true; 
		case CONSUMPTION_CONSUMPTION_VALUE: return true;
		case CONSUMPTION_CONSUMPTION: return true;
		}
		return false;
	}
}
