package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class InventoryUtils {

	public static final String INVENTORY_PRODUCT = "Producto";
	public static final String INVENTORY_NAME = "Nombre";
	public static final String INVENTORY_CATEGORY = "Categor\u00eda";
	public static final String INVENTORY_INVENTORY = "Inventario";
	public static final String INVENTORY_COST = "Coste";
	public static final String INVENTORY_TOTAL = "Total";
	public static final String INVENTORY_DETAIL1 = "Detalle 1";
	public static final String INVENTORY_DETAIL2 = "Detalle 2";
	public static final String INVENTORY_DETAIL3 = "Detalle 3";
	public static final String INVENTORY_COUNT = "Recuento";

	public static Vector<String> inventoryList(){
		Vector<String> v = new Vector<String>();
		v.add(INVENTORY_PRODUCT);
		v.add(INVENTORY_NAME);
		v.add(INVENTORY_CATEGORY);
		v.add(INVENTORY_INVENTORY);
		v.add(INVENTORY_COST);
		v.add(INVENTORY_TOTAL);
		v.add(INVENTORY_DETAIL1);
		v.add(INVENTORY_DETAIL2);
		v.add(INVENTORY_DETAIL3);
		return v;
	}
	
	public static Vector<String> inventoryOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(INVENTORY_NAME);
		v.add(INVENTORY_DETAIL1);
		v.add(INVENTORY_DETAIL2);
		v.add(INVENTORY_DETAIL3);
		return v;
	}
	
	public static Vector<String> inventoryCloseList(){
		Vector<String> v = new Vector<String>();
		v.add(INVENTORY_PRODUCT);
		v.add(INVENTORY_NAME);
		v.add(INVENTORY_CATEGORY);
		v.add(INVENTORY_INVENTORY);
		v.add(INVENTORY_COUNT);
		v.add(INVENTORY_DETAIL1);
		v.add(INVENTORY_DETAIL2);
		v.add(INVENTORY_DETAIL3);
		
		return v;
	}

	public static Vector<String> inventoryCloseOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(INVENTORY_NAME);
		v.add(INVENTORY_DETAIL1);
		v.add(INVENTORY_DETAIL2);
		v.add(INVENTORY_DETAIL3);
		return v;
	}
	
	public static Boolean inventoryCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		
		Integer num = 0;
		
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(isInventoryClosed(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		Integer num2 = 0;
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(isInventoryValued(l.getItemText(l.getSelectedIndex()))){
				num2++;
			}
		}
		return num2 == 5 || num == 4;
	}
	public static Boolean isInventoryClosed(String s) {
		switch (s) {
		case INVENTORY_PRODUCT: return true;
		case INVENTORY_CATEGORY: return true;
		case INVENTORY_INVENTORY: return true;
		case INVENTORY_COUNT: return true;
		}
		return false;
	}
	public static Boolean isInventoryValued(String s) {
		switch (s) {
		case INVENTORY_PRODUCT: return true;
		case INVENTORY_CATEGORY: return true;
		case INVENTORY_INVENTORY: return true;
		case INVENTORY_COST: return true;
		case INVENTORY_TOTAL: return true;
		}
		return false;
	}
	
}
