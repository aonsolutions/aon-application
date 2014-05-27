package com.esferalia.aon.payroll.calculator.jooq;

import org.jooq.conf.Settings;

public class JooqCommon {

	private static Settings SETTINGS = null;

	static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}


}
