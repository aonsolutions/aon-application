package com.esferalia.aon.gwt.payroll.jooq;

import org.jooq.conf.Settings;

public class JooqCalendar {
	
	private static Settings SETTINGS = null;
	
	private static final String STATAL_HOLIDAY = "Fiestas ESTATALES";
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	

}
