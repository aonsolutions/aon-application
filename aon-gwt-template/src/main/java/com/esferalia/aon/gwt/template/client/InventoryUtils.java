package com.esferalia.aon.gwt.template.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class InventoryUtils {
	
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);

	public static LinkedList<String> inventoryList(){
		LinkedList<String> v = new LinkedList<String>();
		v.add(MSG.product());
		v.add(MSG.name());
		v.add(MSG.category());
		v.add(MSG.inventory());
		v.add(MSG.cost());
		v.add(MSG.total());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.serialNumber());
		return v;
	}
	
	public static LinkedList<String> inventoryOptionalList(){
		LinkedList<String> v = new LinkedList<String>();
		v.add(MSG.name());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.serialNumber());
		return v;
	}
	
	public static LinkedList<String> inventoryCloseList(){
		LinkedList<String> v = new LinkedList<String>();
		v.add(MSG.product());
		v.add(MSG.name());
		v.add(MSG.category());
		v.add(MSG.inventory());
		v.add(MSG.count());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.serialNumber());
		return v;
	}

	public static LinkedList<String> inventoryCloseOptionalList(){
		LinkedList<String> v = new LinkedList<String>();
		v.add(MSG.name());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
		v.add(MSG.serialNumber());
		return v;
	}
	
	public static LinkedList<String> requiredList(Boolean closed){
		LinkedList<String> list = new LinkedList<String>();
		list.add(MSG.product());
		list.add(MSG.category());
		list.add(MSG.inventory());
		if(closed) list.add(MSG.count());
		else{
			list.add(MSG.cost());
			list.add(MSG.total());
		}
		return list;
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
		return s.equalsIgnoreCase(MSG.product())
				|| s.equalsIgnoreCase(MSG.category())
				|| s.equalsIgnoreCase(MSG.inventory())
				|| s.equalsIgnoreCase(MSG.count());
	}
	
	public static Boolean isInventoryValued(String s) {
		return s.equalsIgnoreCase(MSG.product())
				|| s.equalsIgnoreCase(MSG.category())
				|| s.equalsIgnoreCase(MSG.inventory())
				|| s.equalsIgnoreCase(MSG.cost())
				|| s.equalsIgnoreCase(MSG.total());
	}
	
}
