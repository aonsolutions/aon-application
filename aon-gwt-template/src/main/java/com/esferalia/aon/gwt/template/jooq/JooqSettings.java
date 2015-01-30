package com.esferalia.aon.gwt.template.jooq;

import org.jooq.conf.Settings;

public class JooqSettings {

	private static Settings SETTINGS = null;

	public static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}


}
