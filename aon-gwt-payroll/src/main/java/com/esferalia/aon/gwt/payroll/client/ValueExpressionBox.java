package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;


public class ValueExpressionBox extends ValueTextBox implements ValueLabel {

	private SpecialExpresion specialExpresion;

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
