package com.esferalia.aon.gwt.fiscal.shared.mod131;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod131Key;

public class Model131ScriptProvider {

	public static IModelScript<Mod131Key>[] obtainScript(Mod131 mod131) {
		IModelScript<Mod131Key>[] ms = null;
		if (mod131.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model131AEATScript.values();
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod131.getAdministration().toString() + " "
							+ mod131.getYear() + " " + mod131.getPeriod().getDescription());
		}
		return ms;
	}
}
