package com.code.aon.ui.report.export;


public class ReportColumnMetadata {
	
	private String name;
	private int type;
	private String label;
	private int displaySize;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public int getDisplaySize() {
		return displaySize;
	}
	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	@Override
	public String toString() {
		return getName() + "\t" + getType() + "\t" + getLabel() + "\t" + getDisplaySize();
	}
}
