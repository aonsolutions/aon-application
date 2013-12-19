package com.esferalia.aon.gwt.fiscal.client.css;

import com.google.gwt.resources.client.CssResource;

public interface AonCSS extends  CssResource {
	
	@ClassName("aon-table-row-link")
	String aonTableRowLink();

	@ClassName("aon-editDataTable-iconColumn")
    String aonDataTableIconColumn();
	
	@ClassName("aon-editDataTable-textColumn")
    String aonDataTableTextColumn();

	@ClassName("aon-editDataTable-numberColumn")
    String aonDataTableNumberColumn();

	@ClassName("aon-text-center")
    String aonTextCenter();

	@ClassName("aon-text-left")
    String aonTextLeft();

	@ClassName("aon-text-right")
    String aonTextRight();

	@ClassName("aon-textBox-error")
    String aonTextBoxError();
	
	@ClassName("aon-icon-enterprise")
	String aonIconEnterprise();

	@ClassName("aon-list-data")
	String aonListData();

	@ClassName("aon-timer")
	String aonTimer();
	
	@ClassName("aon-icon-aeat")
	String aonIconAeat();
	
}
