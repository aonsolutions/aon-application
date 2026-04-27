package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class AmortizationPeriodBox extends ListBox {

	public AmortizationPeriodBox() {
		this("------");
	}	

	public AmortizationPeriodBox(String firstItemLabel) {
		setWidth("120px");
		addItem(AonStringUtils.defaultIfBlank(firstItemLabel),"");
		for (AmortizationPeriod d : AmortizationPeriod.values()) {
			String desc = d.getDescription(); 
			addItem(desc);
		}
	}

	public void setValue(AmortizationPeriod type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public AmortizationPeriod getValue() {
		return getSelectedIndex()==0?null:AmortizationPeriod.values()[getSelectedIndex()-1];
	}
	
}

