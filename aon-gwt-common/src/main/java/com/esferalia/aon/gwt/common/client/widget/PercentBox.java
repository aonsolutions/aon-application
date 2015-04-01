package com.esferalia.aon.gwt.common.client.widget;

public class PercentBox extends DoubleBox {

	private static final int VISIBLE_LENGTH = 6;
	private static final int MAX_LENGTH = 6;

	
	public PercentBox() {
		super();
		setVisibleLength(VISIBLE_LENGTH);
		setMaxLength(MAX_LENGTH);
	}
}
