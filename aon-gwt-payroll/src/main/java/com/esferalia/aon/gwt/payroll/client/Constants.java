package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.i18n.client.NumberFormat;

public interface Constants {

	static final int MIN_ZOOM = 50;
	static final int MAX_ZOOM = 200;
	static final int DEFAULT_ZOOM = 135;
	static final int ZOOM_STEP = 20;
	public static final NumberFormat PERCENT_FORMAT = NumberFormat
	.getPercentFormat();

	static final int NAME_MAX_LENGTH = 32;
	static final int DESCRIPTION_MAX_LENGTH = 64;
	static final int EXPRESSION_MAX_LENGTH = 1024;
	
}
