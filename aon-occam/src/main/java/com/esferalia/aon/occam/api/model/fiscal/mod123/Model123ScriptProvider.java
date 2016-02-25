package com.esferalia.aon.occam.api.model.fiscal.mod123;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public class Model123ScriptProvider {

	public static IModelScript<Mod123Key>[] obtainScript(Mod123 mod123) {
		IModelScript<Mod123Key>[] ms = null;
		if (mod123.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model123AEATScript.values();
		} else if (mod123.getAdministration() == Administration.GIPUZKOA) {
			ms = Model123GipuzkoaScript.values();
		} else if (mod123.getAdministration() == Administration.BIZKAIA) {
			ms = Model123BizkaiaScript.values();
		} else if (mod123.getAdministration() == Administration.NAVARRA) {
			ms = Model716NavarraScript.values();
		} else if (mod123.getAdministration() == Administration.ALAVA) {
			if (mod123.getYear() > 2015) {
				ms = Model123Araba2016Script.values();
			} else {
				ms = Model123ArabaScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod123.getAdministration().toString() + " "
							+ mod123.getYear() + " " + mod123.getPeriod().getDescription());
		}
		return ms;
	}
}
