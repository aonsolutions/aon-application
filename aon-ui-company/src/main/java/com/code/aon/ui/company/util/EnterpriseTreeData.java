package com.code.aon.ui.company.util;

public class EnterpriseTreeData {

	private Integer id;
	
	private String label;
	
	private EnterpriseTreeType type;

	public EnterpriseTreeData(Integer id, String label, EnterpriseTreeType type) {
		this.id = id;
		this.label = label;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public EnterpriseTreeType getType() {
		return type;
	}

	public String getTypeName() {
		return type.toString();
	}
	
}
