package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class CatalogueUtils {
	
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);

	public static Vector<String> catalogueList(){
		Vector<String> v = new Vector<String>();
		v.add(MSG.workplace());
		v.add(MSG.department());
		v.add(MSG.product());
		v.add(MSG.name());
		v.add(MSG.detail1());
		v.add(MSG.detail2());
		v.add(MSG.detail3());
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
		return s.equalsIgnoreCase(MSG.product())
				|| s.equalsIgnoreCase(MSG.quantity());
	}
}
