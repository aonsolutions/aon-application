package com.code.aon.ui.company.util;

import org.apache.commons.lang.builder.EqualsBuilder;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseTreeData o = (EnterpriseTreeData) obj;
		return new EqualsBuilder()
			.append(this.id, o.id)
			.append(this.type, o.type)
			.isEquals();
	}
	
}
