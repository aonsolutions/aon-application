package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;
import java.util.Vector;

import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class StockUtils {
	
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);

	public static Vector<String> stockList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.product());
		v.add(MSG.quantity());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.name());
		v.add(MSG.serialNumber());

		// Características de envasado

		v.add(MSG.format());
		v.add(MSG.units());
		v.add(MSG.unitsFormat());
		v.add(MSG.measurement());
		v.add(MSG.measurementFormat());
		return v;
	}
	
	public static Vector<String> stockOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.name());
		v.add(MSG.serialNumber());
		
		// Características de envasado

		v.add(MSG.format());
		v.add(MSG.units());
		v.add(MSG.unitsFormat());
		v.add(MSG.measurement());
		v.add(MSG.measurementFormat());
		return v;
	}
	
	public static LinkedList<String> requiredList(){
		LinkedList<String> list = new LinkedList<String>();
		list.add(MSG.product());
		list.add(MSG.quantity());
		return list;
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
		return s.equalsIgnoreCase(MSG.product()) 
				|| s.equalsIgnoreCase(MSG.quantity());
	}
}
