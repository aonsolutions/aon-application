package com.esferalia.aon.gwt.fiscal.shared.mod202;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public class Model202ScriptProvider {

	public static IModelScript<Mod202Key>[] obtainScript(Mod202 mod202) {
		IModelScript<Mod202Key>[] ms = null;
		if (mod202.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model202AEATScript.values();
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod202.getAdministration().toString() + " "
							+ mod202.getYear() + " " + mod202.getPeriod().getDescription());
		}
		return ms;
	}
}
