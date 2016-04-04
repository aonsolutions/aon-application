package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;
import java.util.Vector;

import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class FeeUtils {
	
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);
	
	public static Vector<String> feeList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.customer());
		v.add(MSG.product());
		v.add(MSG.quantity());
		v.add(MSG.price());
		v.add(MSG.discount());
		v.add(MSG.startDate());
		v.add(MSG.endDate());
		v.add(MSG.billingDate());
		v.add(MSG.period());
		v.add(MSG.comercial());
		v.add(MSG.workplace());
		v.add(MSG.billingGroup());
		v.add(MSG.confidential());
		v.add(MSG.description());
		v.add(MSG.record());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.line());
		return v;
	}
	
	public static Vector<String> feeOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.endDate());
		v.add(MSG.period());
		v.add(MSG.comercial());
		v.add(MSG.workplace());
		v.add(MSG.billingGroup());
		v.add(MSG.confidential());
		v.add(MSG.description());
		v.add(MSG.record());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.line());
		return v;
	}
	
	public static LinkedList<String> requiredList(){
		LinkedList<String> list = new LinkedList<String>(); 
		list.add(MSG.customer());
		list.add(MSG.product());
		list.add(MSG.quantity());
		list.add(MSG.price());
		list.add(MSG.discount());
		list.add(MSG.startDate());
		list.add(MSG.billingDate());
		return list;
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
		return s.equalsIgnoreCase(MSG.customer())
				|| s.equalsIgnoreCase(MSG.product())
				|| s.equalsIgnoreCase(MSG.quantity())
				|| s.equalsIgnoreCase(MSG.price())
				|| s.equalsIgnoreCase(MSG.discount())
				|| s.equalsIgnoreCase(MSG.startDate())
				|| s.equalsIgnoreCase(MSG.billingDate());
	}
}
