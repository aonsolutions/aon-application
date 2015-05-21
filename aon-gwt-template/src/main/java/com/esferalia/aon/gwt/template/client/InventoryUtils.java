package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class InventoryUtils {

	public static Vector<String> inventoryList(){
		Vector<String> v = new Vector<String>();
		v.add("Producto");
		v.add("Nombre");
		v.add("Categor\u00eda");
		v.add("Inventario");
		v.add("Coste");
		v.add("Total");
		v.add("Detalle 1");
		v.add("Detalle 2");
		v.add("Detalle 3");
		
		return v;
	}
	
	public static Vector<String> inventoryCloseList(){
		Vector<String> v = new Vector<String>();
		v.add("Producto");
		v.add("Nombre");
		v.add("Categor\u00eda");
		v.add("Inventario");
		v.add("Recuento");
		v.add("Detalle 1");
		v.add("Detalle 2");
		v.add("Detalle 3");
		
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
		case "Producto": return true;
		case "Categor\u00eda": return true;
		case "Inventario": return true;
		case "Recuento": return true;
		}
		return false;
	}
	public static Boolean isInventoryValued(String s) {
		switch (s) {
		case "Producto": return true;
		case "Categor\u00eda": return true;
		case "Inventario": return true;
		case "Coste": return true;
		case "Total": return true;
		}
		return false;
	}
	
}
