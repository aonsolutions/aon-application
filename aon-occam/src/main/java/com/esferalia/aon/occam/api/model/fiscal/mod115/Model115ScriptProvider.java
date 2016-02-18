package com.esferalia.aon.occam.api.model.fiscal.mod115;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Administration;

public class Model115ScriptProvider {

	public static IModelScript[] obtainScript(Mod115 mod115) {
		IModelScript[] ms = null;
		if (mod115.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model115AEATScript.values();
		} else if (mod115.getAdministration() == Administration.GIPUZKOA) {
			ms = Model115GipuzkoaScript.values();
		} else if (mod115.getAdministration() == Administration.BIZKAIA) {
			ms = Model115BizkaiaScript.values();
		} else if (mod115.getAdministration() == Administration.NAVARRA) {
			ms = Model760NavarraScript.values();
		} else if (mod115.getAdministration() == Administration.ALAVA) {
			if (mod115.getYear() > 2015) {
				ms = Model115Araba2016Script.values();
			} else {
				ms = Model115ArabaScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod115.getAdministration().toString() + " "
							+ mod115.getYear() + " " + mod115.getPeriod().getDescription());
		}
		return ms;
	}
}
