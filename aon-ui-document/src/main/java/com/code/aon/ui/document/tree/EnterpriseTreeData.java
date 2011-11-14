package com.code.aon.ui.document.tree;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.builder.EqualsBuilder;

public class EnterpriseTreeData {

	public static final String ENTERPRISE_ICON = "/images/aon-icon/aon-icon-tree-root.png";
	
	public static final String CATEGORY_ICON = "/images/aon-icon/aon-icon-tree.png";
	
	public static final String DOCUMENT_ICON = "/images/aon-icon/aon-icon-contact.png";

	private Serializable id;
	
	private String label;
	
	private EnterpriseTreeType type;
	
	private int count;

	public EnterpriseTreeData(Serializable id, String label, EnterpriseTreeType type) {
		this.id = id;
		this.label = label;
		this.type = type;
	}

	public Serializable getId() {
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
	
	public void actionListener( ActionEvent event ) {
		if ( this.type.getActionListener() != null ) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			this.type.getActionListener().invoke(ctx.getELContext(), new Object[]{event});			
		}
	}
	
	public String getKey() {
		return getType().toString() + getId();
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseTreeData o = (EnterpriseTreeData) obj;
		return new EqualsBuilder()
			.append(this.id, o.id)
			.append(this.label, o.label)
			.append(this.type, o.type)
			.isEquals();
	}
	
}
