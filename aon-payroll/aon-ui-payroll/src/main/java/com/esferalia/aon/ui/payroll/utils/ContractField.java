package com.esferalia.aon.ui.payroll.utils;

import org.apache.commons.lang.ObjectUtils;

import com.lowagie.text.pdf.AcroFields;

public class ContractField {
	
	private static final String TRUE_VALUE = "true";
	private static final String FALSE_VALUE = "false";
	private Integer type;
	private Integer page;
	private String label;
	private String value;
	private String bottomCoordinates;
	private String leftCoordinates;
	private String width;
	private String height;
	private int zoomFactor;
	
	public int getZoomFactor() {
		return zoomFactor;
	}
	
	public void setZoomFactor(int zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getValue() {
		if(value==null){
			return "";
		}
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean getBooleanValue() {
		return ObjectUtils.equals(TRUE_VALUE,value);
	}
	public void setBooleanValue(boolean value) {
		setValue( value?TRUE_VALUE:FALSE_VALUE );
	}
	
	public String getBottomCoordinates() {
		return bottomCoordinates;
	}
	public String getScaledBottomCoordinates() {
		ContractBuilder builder = ContractBuilder.getInstance();
		return String.valueOf(builder.getFactorizedValue(Double.parseDouble(bottomCoordinates)));
	}
	public void setBottomCoordinates(String bottomCoordinates) {
		this.bottomCoordinates = bottomCoordinates;
	}
	public String getLeftCoordinates() {
		return leftCoordinates;
	}
	public String getScaledLeftCoordinates() {
		ContractBuilder builder = ContractBuilder.getInstance();
		return String.valueOf(builder.getFactorizedValue(Double.parseDouble(leftCoordinates)));
	}
	public void setLeftCoordinates(String leftCoordinates) {
		this.leftCoordinates = leftCoordinates;
	}
	public String getWidth() {
		return width;
	}
	public String getScaledWidth() {
		ContractBuilder builder = ContractBuilder.getInstance();
		return String.valueOf(builder.getFactorizedValue(Double.parseDouble(width)));
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	public boolean isText(){
		if(type.equals(AcroFields.FIELD_TYPE_TEXT)){
			return true;
		}
		return false;
	}
	public boolean isCheck(){
		if(type.equals(AcroFields.FIELD_TYPE_CHECKBOX)){
			return true;
		}
		return false;
	}
	
}
