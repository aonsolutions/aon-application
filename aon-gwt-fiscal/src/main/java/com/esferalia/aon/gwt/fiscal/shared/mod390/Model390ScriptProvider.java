package com.esferalia.aon.gwt.fiscal.shared.mod390;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public class Model390ScriptProvider {

	public static IModelScript<Mod390Key>[] obtainScript(Mod390HF mod390) {
		IModelScript<Mod390Key>[] ms = null;
		if (mod390.getYear() >= 2017) {
			if (mod390.isAraba()) {
				ms = Model3902017PrintARABAScript.values();
			} else if (mod390.isGipuzkoa()) {
				ms = Model3902017PrintGIPUZKOAScript.values();
			} else if (mod390.isBizkaia()) {
				ms = Model3902017PrintBIZKAIAScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay impresión disponible para: " + mod390.getAdministration().toString() + " "
							+ mod390.getYear() + " " + mod390.getPeriod().getDescription());
		}
		return ms;
	}
}
