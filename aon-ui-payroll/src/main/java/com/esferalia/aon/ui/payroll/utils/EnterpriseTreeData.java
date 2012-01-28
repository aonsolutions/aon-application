package com.esferalia.aon.ui.payroll.utils;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.builder.EqualsBuilder;

public class EnterpriseTreeData {

	public static final String ENTERPRISE_ICON = "/images/aon-icon/aon-icon-tree-root.png";
	
	public static final String WORKPLACE_ICON = "/images/aon-icon/aon-icon-tree.png";
	
	public static final String CONTRACT_ICON = "/images/aon-icon/aon-icon-contact.png";
	
	public static final String END_CONTRACT_ICON = "/images/aon-icon/aon-icon-removed.png";
	
	public static final String ACTIVITY_ICON = "/images/aon-icon/aon-icon-menu-top-item.png";
	
	public static final String MAIN_ICON = "/images/aon-icon/aon-icon-pay.png";
	
	public static final String PAYMENT_ICON = "/images/aon-icon/aon-icon-fraction.png";
	
	public static final String DEDUCTION_ICON = "/images/aon-icon/aon-icon-settle.png";
	
	public static final String BONUS_ICON = "/images/aon-icon/aon-icon-fraction.png";
	
	public static final String EMBARGO_ICON = "/images/aon-icon/aon-icon-settle.png";
	
	public static final String SALARY_ICON = "/images/aon-icon/aon-icon-menu-top-item.png";
	
	public static final String SALARY_DRAFT_ICON = "/images/aon-icon/aon-icon-edit.png";
	
	public static final String DOCUMENT_ICON = "/images/aon-icon/aon-icon-file.png";
	
	public static final String AEAT_ICON = "/images/aon-icon/aon-aeat.png";

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
