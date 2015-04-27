package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class CatalogueUtils {
	public static Vector<String> catalogueList(){
		Vector<String> v = new Vector<String>();
		v.add("Centro de Trabajo");
		v.add("Departamento");
		v.add("Producto");
		v.add("Nombre");
		v.add("Detalle 1");
		v.add("Detalle 2");
		v.add("Detalle 3");

		return v;
	}
	
	public static Boolean catalogueCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		
		Integer num = 0;
		
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(estaCatalogue(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 2;
	}
	public static Boolean estaCatalogue(String s) {
		switch (s) {
		case "Producto": return true;
		case "Cantidad" : return true;
		}
		return false;
	}
}
