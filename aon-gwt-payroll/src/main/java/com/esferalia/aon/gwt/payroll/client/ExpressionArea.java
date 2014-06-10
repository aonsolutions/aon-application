package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.UserExpresion.getNoLabelExpression;

import com.esferalia.aon.gwt.payroll.shared.UserExpresion;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.TextArea;

public class ExpressionArea extends TextArea {
	
	private UserExpresion userExpresion; 
	
	public ExpressionArea() {
	}

	public ExpressionArea(Element element) {
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
		try {
			userExpresion = parse(expression);
			super.setText(userExpresion.getInput());
		} catch (IllegalArgumentException e) {
			userExpresion = new UserExpresion() {
				public String replace(String replacement) {return null;}
			};// Null UserExpression
		}
	}

	private String getExpression() {
		return userExpresion.replace(super.getText());
	}

	// ------------------------------------------------------------------------

	public static boolean accept(String expression) {
		try {
			getNoLabelExpression(expression);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static UserExpresion parse(final String expression)
			throws IllegalArgumentException {
		return getNoLabelExpression(expression);
	}
	
	
}
