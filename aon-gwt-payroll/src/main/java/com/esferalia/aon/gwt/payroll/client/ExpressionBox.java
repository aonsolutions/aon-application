/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TextBox;
import com.ibm.icu.impl.duration.impl.DataRecord.EPluralization;

/**
 * @author rtrepiana
 * 
 */
public class ExpressionBox extends TextBox {

	private SpecialExpresion specialExpresion;

	public ExpressionBox() {
	}

	public ExpressionBox(Element element) {
		super(element);
	}

	@Override
	public void setText(String text) {
		setExpression(text);
	}

	@Override
	public String getText() {
		return getExpression();
	}

	// ------------------------------------------------------------------------

	private void setExpression(String expression) {
		specialExpresion = SpecialExpresion.parse(expression);
		super.setText(specialExpresion.getInput());
		setReadOnly(specialExpresion.isReadOnly());
	}

	private String getExpression() {

		return specialExpresion.replace(super.getText());
	}

	// ------------------------------------------------------------------------


}
