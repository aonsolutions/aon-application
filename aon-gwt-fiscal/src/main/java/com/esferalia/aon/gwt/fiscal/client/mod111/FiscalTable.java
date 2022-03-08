package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlexTable;

public class FiscalTable extends FlexTable {
	private static final String WIDTH_140PX = "140px";
	
	public void defineTable() {
		setWidth("100%");
		addStyleName(AON.CSS.aonMarginBottom());
		
		getColumnFormatter().setWidth(0, "auto");
		getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		getColumnFormatter().setWidth(1, "40px");
		getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		getColumnFormatter().setWidth(2, WIDTH_140PX);
		
		getColumnFormatter().setWidth(3, "40px");
		getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		getColumnFormatter().setWidth(4, WIDTH_140PX);
		
		getColumnFormatter().setWidth(5, "40px");
		getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		getColumnFormatter().setWidth(6, WIDTH_140PX);
		
		getColumnFormatter().setWidth(7, "50px");
	}

}
