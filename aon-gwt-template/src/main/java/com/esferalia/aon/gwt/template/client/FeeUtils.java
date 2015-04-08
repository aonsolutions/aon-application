package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class FeeUtils {
	public static Vector<String> feeList(){
		Vector<String> v = new Vector<String>();
		v.add("Cliente");
		v.add("Producto");
		v.add("Cantidad");
		v.add("Precio");
		v.add("Descuento");
		v.add("Fecha Inicio");
		v.add("Fecha Fin");
		v.add("Fecha Facturaci\u00f3n");
		v.add("Periodo");
		v.add("Comercial");
		v.add("Centro Trabajo");
		v.add("Grupo Facturaci\u00f3n");
		v.add("Confidencial");
		v.add("Descripci\u00f3n");
		v.add("Proyecto");
		v.add("Detalle1");
		v.add("Detalle2");
		v.add("Detalle3");
		v.add("Texto Libre");
		v.add("Linea");
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
		case "Cliente": return true;
		case "Producto" : return true;
		case "Cantidad" : return true;
		case "Precio" : return true;
		case "Descuento" : return true;
		case "Fecha Inicio" : return true;
		case "Fecha Facturaci\u00f3n" : return true;
		case "Centro Trabajo": return true;

		}
		return false;
	}
}
