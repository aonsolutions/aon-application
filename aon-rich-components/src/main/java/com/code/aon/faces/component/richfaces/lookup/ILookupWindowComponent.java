package com.code.aon.faces.component.richfaces.lookup;

public interface ILookupWindowComponent extends ILookupComponent {
	
	void setWindowCloseFocus(String windowCloseFocus);
	
	String getWindowCloseFocus();
	
	String getSelectReRender();

	void setSelectReRender(String selectReRender);	
	
	String getWindowTitle();
	
	String getMinWidth();
	
	String getMinHeight();

}
