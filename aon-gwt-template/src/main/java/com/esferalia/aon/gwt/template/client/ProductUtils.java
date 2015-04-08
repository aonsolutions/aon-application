package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class ProductUtils {

	
	public static Vector<String> productList(){
		Vector<String> v = new Vector<String>();
		v.add("Nombre");
		v.add("C\u00f3digo");
		v.add("Precio Coste");
		v.add("Precio Venta Base");
		v.add("Categor\u00eda");
		v.add("Marca");
		v.add("Etiqueta");
		v.add("Tipo");
		v.add("IVA");
		v.add("IRPF");
		v.add("Inventoriable");
		v.add("Producto Compuesto");
		v.add("Precio Composici\u00f3n");
		v.add("Estado");
		v.add("C\u00f3digo de Barras");
		v.add("Descripci\u00f3n");
		v.add("Detalle 1");
		v.add("Detalle 2");
		v.add("Detalle 3");
		v.add("Texto Libre");
		return v;
		
	}
	
	public static Boolean productCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		Integer num = 0;
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(estaProduct(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 4;
	}
	
	public static Boolean estaProduct(String s) {
		switch (s) {
		case "Nombre": return true;
		case "C\u00f3digo" : return true;
		case "Precio Coste" : return true;
		case "Precio Venta Base" : return true;
		}
		return false;
	}
	
}
