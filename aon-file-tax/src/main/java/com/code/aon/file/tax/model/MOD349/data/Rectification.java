package com.code.aon.file.tax.model.MOD349.data;

/**
 * The correction
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 * 
 */
public class Rectification extends Operator {

	private Integer rectifiedYear;
	private String rectifiedPeriod;
	private Double rectifiedAmount;
	
	public Integer getRectifiedYear() {
		return rectifiedYear;
	}
	public void setRectifiedYear(Integer rectifiedYear) {
		this.rectifiedYear = rectifiedYear;
	}
	
	public String getRectifiedPeriod() {
		return rectifiedPeriod;
	}
	public void setRectifiedPeriod(String rectifiedPeriod) {
		this.rectifiedPeriod = rectifiedPeriod;
	}

	public Double getRectifiedAmount() {
		return rectifiedAmount;
	}
	public void setRectifiedAmount(Double rectifiedAmount) {
		this.rectifiedAmount = rectifiedAmount;
	}
	
}
