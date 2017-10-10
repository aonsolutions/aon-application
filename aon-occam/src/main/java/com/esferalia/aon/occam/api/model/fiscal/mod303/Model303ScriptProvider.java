package com.esferalia.aon.occam.api.model.fiscal.mod303;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public class Model303ScriptProvider {

	public static IModelScript<Mod303Key>[] obtainScript(Mod303 mod303) {
		IModelScript<Mod303Key>[] ms = null;
		if (mod303.getYear() > 2014) {
			if (mod303.isAEAT()) {
				ms = Model3032017PrintAEATScript.values();
			} else if (mod303.isAraba()) {
				ms = Model3032017PrintARABAScript.values();
			} else if (mod303.isGipuzkoa()) {
				ms = Model3032017PrintGIPUZKOAScript.values();
			} else if (mod303.isBizkaia()) {
				ms = Model3032017PrintBIZKAIAScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod303.getAdministration().toString() + " "
							+ mod303.getYear() + " " + mod303.getPeriod().getDescription());
		}
		return ms;
	}
}
