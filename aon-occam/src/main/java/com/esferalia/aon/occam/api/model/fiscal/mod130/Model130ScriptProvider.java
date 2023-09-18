package com.esferalia.aon.occam.api.model.fiscal.mod130;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;

public class Model130ScriptProvider {

	private enum Model130Script {
		AEAT_SCRIPT {
			@Override
			boolean accept(Mod130 mod130) {
				return mod130.isAEAT();
			}
	
			@Override
			IModelScript<Mod130Key>[] getScript() {
				return Model130AEATScript.values();
			}
		}
		,BIZKAIA_SCRIPT {
			@Override
			boolean accept(Mod130 mod130) {
				return mod130.isBizkaia();
			}
	
			@Override
			IModelScript<Mod130Key>[] getScript() {
				return Model130BizkaiaScript.values();
			}
		}
		;
		abstract boolean accept(Mod130 mod130);
		abstract IModelScript<Mod130Key>[] getScript();
	}
	
	public static IModelScript<Mod130Key>[] obtainScript(Mod130 mod130) {
		IModelScript<Mod130Key>[] ms = null;
		for ( Model130Script script : Model130Script.values()) {
			if (script.accept(mod130)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod130.getAdministration().getDescription()
					+ " - " 
					+ mod130.getModelFullName());
		}
		return ms;
	}
}
