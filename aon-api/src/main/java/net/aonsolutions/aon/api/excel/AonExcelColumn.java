package net.aonsolutions.aon.api.excel;

public class AonExcelColumn {
	String value;
	Integer width;
	
	public AonExcelColumn(String value, Integer width) {
		this.value = value;
		this.width = width;
	}
	
	public String getValue() {
		return value;
	}
	
	public AonExcelColumn setValue(String value) {
		this.value = value;
		return this;
	}
	
	public Integer getWidth() {
		return width;
	}
	
	public AonExcelColumn setWidth(Integer width) {
		this.width = width;
		return this;
	}
}