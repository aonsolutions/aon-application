package com.esferalia.aon.ui.payroll.controller.agreement;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.builder.EqualsBuilder;

public class AgreementTreeData {

	public static final String AGREEMENT_ICON = "/images/aon-icon/aon-icon-file.png";
	public static final String AGREEMENT_LEVEL_ICON = "/images/aon-icon/aon-icon-index.png";
	public static final String AGREEMENT_LEVEL_CATEGORY_ICON = "/images/aon-icon/aon-icon-category.png";
	public static final String AGREEMENT_LEVEL_PAYMENT_ICON = "/images/aon-icon/aon-icon-payment.png";

	private Serializable id;
	private String label;
	private AgreementTreeType type;
	
	public AgreementTreeData(Serializable id, String label, AgreementTreeType type) {
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
	public void setLabel( String label ) {
		this.label = label;
	}

	public AgreementTreeType getType() {
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AgreementTreeData o = (AgreementTreeData) obj;
		return new EqualsBuilder()
			.append(this.id, o.id)
			.append(this.label, o.label)
			.append(this.type, o.type)
			.isEquals();
	}

}
