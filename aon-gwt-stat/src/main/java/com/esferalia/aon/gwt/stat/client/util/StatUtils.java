package com.esferalia.aon.gwt.stat.client.util;

import com.google.gwt.ajaxloader.client.Properties;

public class StatUtils {

	public static final String[] COMBO_CHART_SERIES_COLORS = {"#cc0000", "#0059b3", "#e6b800", "#009900", "#3e0099"};
	
	public static final Properties ANIMATION = Properties.create();
	static {
		ANIMATION.set("durtion", 1000.0);
		ANIMATION.set("easing", "out");
		ANIMATION.set("startup", true);
	}
			 

	
}
