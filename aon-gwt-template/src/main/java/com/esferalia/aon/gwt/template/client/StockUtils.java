package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class StockUtils {

	public static Vector<String> stockList(){
		Vector<String> v = new Vector<String>();
		v.add("Producto");
		//v.add("Series");
		//v.add("Numero");
		//v.add("Almac\u00e9n Origen");
		//v.add("Almac\u00e9n Destino");
		v.add("Cantidad");
		v.add("Detalle 1");
		v.add("Detalle 2");
		v.add("Detalle 3");
		//v.add("Comentarios");
		v.add("Texto Libre");
		
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
		case "Producto": return true;
		case "Cantidad" : return true;
		}
		return false;
	}
}
