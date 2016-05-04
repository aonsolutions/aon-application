package com.esferalia.aon.occam.api.model.fiscal.mod130;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod130Key;

public class Model130ScriptProvider {

	public static IModelScript<Mod130Key>[] obtainScript(Mod130 mod130) {
		IModelScript<Mod130Key>[] ms = null;
		if (mod130.getAdministration() == Administration.COMMON_TERRITORY) {
			ms = Model130AEATScript.values();
		} else if (mod130.getAdministration() == Administration.BIZKAIA) {
			ms = Model130BizkaiaScript.values();
			
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod130.getAdministration().toString() + " "
							+ mod130.getYear() + " " + mod130.getPeriod().getDescription());
		}
		return ms;
	}
}
