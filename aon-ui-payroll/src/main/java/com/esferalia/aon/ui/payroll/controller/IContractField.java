package com.esferalia.aon.ui.payroll.controller;

import com.lowagie.text.pdf.AcroFields;

public interface IContractField {
	
	public AcroFields getType();
	public void setType(AcroFields type);
	
	public String getLabel();
	public void setLabel(String label);
	
	public String getValue();
	public void setValue(String value);
	
	public String getBottomCoordinates();
	public void setBottomCoordinates(String bottomCoordinates);
	
	public String getLeftCoordinates();
	public void setLeftCoordinates(String leftCoordinates);
	
	public String getWidth();
	public void setWidth(String fieldWidth);
	
	public String getHeight();
	public void setHeight(String fieldHeight);

}
