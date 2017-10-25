package com.esferalia.aon.gwt.fiscal.shared.mod111;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public class Model111ScriptProvider {

	public static IModelScript<Mod111Key>[] obtainScript(Mod111 mod111) {
		IModelScript<Mod111Key>[] ms = null;
		if (mod111.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model111AEATScript.values();
		} else if (mod111.getAdministration() == Administration.GIPUZKOA) {
			ms = Model110GipuzkoaScript.values();
		} else if (mod111.getAdministration() == Administration.BIZKAIA) {
			if (mod111.getPeriod().isQuarterPeriod()) {
				ms = Model110BizkaiaScript.values();
			} else {
				ms = Model111BizkaiaScript.values();
			}
		} else if (mod111.getAdministration() == Administration.NAVARRA) {
			ms = Model715NavarraScript.values();
		} else if (mod111.getAdministration() == Administration.ALAVA) {
			if (mod111.getYear() > 2015) {
				ms = Model111Araba2016Script.values();
			} else {
				ms = Model111ArabaScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod111.getAdministration().toString() + " "
							+ mod111.getYear() + " " + mod111.getPeriod().getDescription());
		}
		return ms;
	}
}
