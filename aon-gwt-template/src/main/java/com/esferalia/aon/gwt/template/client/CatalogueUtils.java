package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class CatalogueUtils {
	public static final String CATALOGUE_WORKPLACE = "Centro de Trabajo";
	public static final String CATALOGUE_DEPARTMENT = "Departamento";
	public static final String CATALOGUE_PRODUCT = "Producto";
	public static final String CATALOGUE_NAME = "Nombre";
	public static final String CATALOGUE_DETAIL1 = "Detalle 1";
	public static final String CATALOGUE_DETAIL2 = "Detalle 2";
	public static final String CATALOGUE_DETAIL3 = "Detalle 3";
	public static final String CATALOGUE_QUANTITY = "Cantidad";

	public static Vector<String> catalogueList(){
		Vector<String> v = new Vector<String>();
		v.add(CATALOGUE_WORKPLACE);
		v.add(CATALOGUE_DEPARTMENT);
		v.add(CATALOGUE_PRODUCT);
		v.add(CATALOGUE_NAME);
		v.add(CATALOGUE_DETAIL1);
		v.add(CATALOGUE_DETAIL2);
		v.add(CATALOGUE_DETAIL3);
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
		case CATALOGUE_PRODUCT: return true;
		case CATALOGUE_QUANTITY : return true;
		}
		return false;
	}
}
