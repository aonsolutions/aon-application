package com.esferalia.aon.occam.api.model.fiscal.mod131;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;

public class Model131ScriptProvider {
	
	private Model131ScriptProvider() {
		
	}

	private enum Model131Script {
		AEAT_SCRIPT {
			@Override
			boolean accept(Mod131 mod131) {
				return mod131.isAEAT();
			}
	
			@Override
			IModelScript<Mod131Key>[] getScript() {
				return Model131AEATScript.values();
			}
		};
		
		abstract boolean accept(Mod131 mod131);
		abstract IModelScript<Mod131Key>[] getScript();
	}
	
	public static IModelScript<Mod131Key>[] obtainScript(Mod131 mod131) {
		IModelScript<Mod131Key>[] ms = null;
		for ( Model131Script script : Model131Script.values()) {
			if (script.accept(mod131)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod131.getAdministration().getDescription()
					+ " - " 
					+ mod131.getModelFullName());
		}
		return ms;
	}
	
}
