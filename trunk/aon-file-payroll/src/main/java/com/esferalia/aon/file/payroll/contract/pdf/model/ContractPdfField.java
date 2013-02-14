package com.esferalia.aon.file.payroll.contract.pdf.model;

import org.apache.commons.lang.ObjectUtils;

import com.lowagie.text.pdf.AcroFields;

public class ContractPdfField {
	
	private static final double FACTOR_1X = 1.2;
	private static final double FACTOR_2X = 1.4;
	private static final double FACTOR_3X = 1.6;
	private static final double FACTOR_4X = 1.8;
	
	private Integer type;
	private Integer page;
	private String label;
	private String value;
	private String bottomCoordinates;
	private String leftCoordinates;
	private String width;
	private String height;
	private Integer maxLength;
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
		return ObjectUtils.equals(Boolean.TRUE.toString(),value);
	}
	public void setBooleanValue(boolean value) {
		setValue( value?Boolean.TRUE.toString():Boolean.FALSE.toString() );
	}
	
	public String getBottomCoordinates() {
		return bottomCoordinates;
	}
	public String getScaledBottomCoordinates() {
		return String.valueOf(getFactorizedValue(Double.parseDouble(bottomCoordinates)));
	}
	public void setBottomCoordinates(String bottomCoordinates) {
		this.bottomCoordinates = bottomCoordinates;
	}
	public String getLeftCoordinates() {
		return leftCoordinates;
	}
	public String getScaledLeftCoordinates() {
		return String.valueOf(getFactorizedValue(Double.parseDouble(leftCoordinates)));
	}
	public void setLeftCoordinates(String leftCoordinates) {
		this.leftCoordinates = leftCoordinates;
	}
	public String getWidth() {
		return width;
	}
	public String getScaledWidth() {
		return String.valueOf(getFactorizedValue(Double.parseDouble(width)));
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
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
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
	
	private int getFactorizedValue(double value){
		if(getZoomFactor()==0){
			return (int)value;
		} else if(getZoomFactor()==1){
			return (int)(value*FACTOR_1X);
		} else if(getZoomFactor()==2){
			return (int)(value*FACTOR_2X);
		} else if(getZoomFactor()==3){
			return (int)(value*FACTOR_3X);
		} else if(getZoomFactor()==4){
			return (int)(value*FACTOR_4X);
		}
		return (int)value;
	}
	
}
