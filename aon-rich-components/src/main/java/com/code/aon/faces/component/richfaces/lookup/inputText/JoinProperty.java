/**
 * 
 */
package com.code.aon.faces.component.richfaces.lookup.inputText;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;


public class JoinProperty {

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