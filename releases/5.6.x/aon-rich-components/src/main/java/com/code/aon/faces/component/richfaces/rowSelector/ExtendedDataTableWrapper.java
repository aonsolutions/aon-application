package com.code.aon.faces.component.richfaces.rowSelector;

import org.richfaces.component.html.HtmlExtendedDataTable;

public class ExtendedDataTableWrapper implements IDataTable {
	
	private HtmlExtendedDataTable component;
	
	public ExtendedDataTableWrapper(HtmlExtendedDataTable component) {
		this.component = component;
	}

	public String getOnRowMouseOut() {
		return component.getOnRowMouseOut();
	}

	public void setOnRowMouseOut(String onRowMouseOut) {
		component.setOnRowMouseOut( onRowMouseOut );
	}

	public String getOnRowMouseOver() {
		return component.getOnRowMouseOver();
	}

	public void setOnRowMouseOver(String onRowMouseOver) {
		component.setOnRowMouseOver( onRowMouseOver );
	}
	
}
