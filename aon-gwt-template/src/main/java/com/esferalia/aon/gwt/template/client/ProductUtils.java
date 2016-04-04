package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;
import java.util.Vector;

import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class ProductUtils {
	
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);
	
	public static Vector<String> productList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.name());
		v.add(MSG.code());
		v.add(MSG.priceCost());
		v.add(MSG.priceSaleBase());
		v.add(MSG.category());
		v.add(MSG.brand());
		v.add(MSG.tag());
		v.add(MSG.type());
		v.add(MSG.vat());
		v.add(MSG.irpf());
		v.add(MSG.inventoriable());
		v.add(MSG.composedProduct());
		v.add(MSG.compositionPrice());
		v.add(MSG.status());
		v.add(MSG.barcode());
		v.add(MSG.description());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.serializable());
		v.add(MSG.lotable());
		v.add(MSG.serialNumber());
		
		// Características de envasado

		v.add(MSG.packaged());
		v.add(MSG.format());
		v.add(MSG.units());
		v.add(MSG.unitsFormat());
		v.add(MSG.measurement());
		v.add(MSG.measurementFormat());
		return v;
	}
	
	public static Vector<String> productOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.category());
		v.add(MSG.brand());
		v.add(MSG.tag());
		v.add(MSG.type());
		v.add(MSG.vat());
		v.add(MSG.irpf());
		v.add(MSG.inventoriable());
		v.add(MSG.composedProduct());
		v.add(MSG.compositionPrice());
		v.add(MSG.status());
		v.add(MSG.barcode());
		v.add(MSG.description());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.serializable());
		v.add(MSG.lotable());
		v.add(MSG.serialNumber());
		
		// Características de envasado

		v.add(MSG.packaged());
		v.add(MSG.format());
		v.add(MSG.units());
		v.add(MSG.unitsFormat());
		v.add(MSG.measurement());
		v.add(MSG.measurementFormat());
		return v;
	}
	
	public static LinkedList<String> requiredList(){
		LinkedList<String> list = new LinkedList<String>();
		list.add(MSG.name());
		list.add(MSG.code());
		list.add(MSG.priceCost());
		list.add(MSG.priceSaleBase());
		return list;
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
		return s.equalsIgnoreCase(MSG.name())
				|| s.equalsIgnoreCase(MSG.code())
				|| s.equalsIgnoreCase(MSG.priceCost())
				|| s.equalsIgnoreCase(MSG.priceSaleBase());
	}
	
}
