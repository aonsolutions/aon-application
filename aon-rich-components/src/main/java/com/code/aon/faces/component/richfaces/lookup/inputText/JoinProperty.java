/**
 * 
 */
package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.io.Serializable;

import jakarta.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.code.aon.AonVersion;


public class JoinProperty implements Serializable { 
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String alias;
	
	private ValueExpression ve;
	
	private boolean orExpression;

	public JoinProperty(String alias, ValueExpression ve, boolean orExpression) {
		this.alias = alias;
		this.ve = ve;
		this.orExpression = orExpression;
	}

	public JoinProperty(String alias, ValueExpression ve) {
		this(alias, ve, false);
	}
	
	public String getAlias() {
		return alias;
	}

	public ValueExpression getVe() {
		return ve;
	}

	public boolean isOrExpression() {
		return orExpression;
	}
	
	public Object getValue( FacesContext ctx ) {
		return ve.getValue(ctx.getELContext());
	}
	
}