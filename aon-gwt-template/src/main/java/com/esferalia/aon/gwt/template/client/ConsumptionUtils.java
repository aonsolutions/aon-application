package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class ConsumptionUtils {

	public static Vector<String> consumptionList(){
		Vector<String> v = new Vector<String>();
		v.add("Producto");
		v.add("Nombre");
		v.add("Inicial");
		v.add("Compras");
		v.add("Ventas");
		v.add("Final");
		v.add("Traspaso");
		v.add("Precio"); // precio del producto
		v.add("Valor Consumo");// precio total del consumo
		v.add("Consumo");
		//v.add("Texto Libre");
		
		
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
		case "Producto": return true;
		case "Inicial": return true;
		case "Compras": return true;
		case "Ventas": return true;
		case "Final": return true;
		case "Traspaso": return true;
		case "Precio": return true; 
		case "Valor Consumo": return true;
		case "Consumo": return true;
		}
		return false;
	}
}
