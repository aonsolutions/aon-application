package com.esferalia.aon.gwt.stat.client.util;

import com.google.gwt.ajaxloader.client.Properties;

public class StatUtils {
	public static final String RESULT_COLOR = "#36ED15";
	
	public static final String SALES_COLOR = "#3A85C3";
	public static final String PURCH_COLOR = "#ED2618";
	public static final String EXPEN_COLOR = "#ED7B18";
	public static final String UNDED_COLOR = "#863BF5";
	
	public static final String[] COMBO_CHART_SERIES_COLORS 		  = {SALES_COLOR, PURCH_COLOR, EXPEN_COLOR, UNDED_COLOR};
	public static final String[] COMBO_CHART_SERIES_COLORS_RESULT = {RESULT_COLOR, SALES_COLOR, PURCH_COLOR, EXPEN_COLOR, UNDED_COLOR};
	
	public static final Properties ANIMATION = Properties.create();
	static {
		ANIMATION.set("durtion", 1000.0);
		ANIMATION.set("easing", "out");
		ANIMATION.set("startup", true);
	}
			 

	
}
