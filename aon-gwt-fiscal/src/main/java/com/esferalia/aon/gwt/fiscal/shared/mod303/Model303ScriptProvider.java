package com.esferalia.aon.gwt.fiscal.shared.mod303;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017PrintAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021PrintAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021_2PrintAEATScript;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public class Model303ScriptProvider {

	private enum Model303Script {
		AEAT_2021_2_SCRIPT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && 
					(mod303.getYear() > 2021 || (mod303.getYear() == 2021 && mod303.getPeriod().isLastSemester()));
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032021_2PrintAEATScript.values();
			}
		}
		,AEAT_2021_SCRIPT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && mod303.getYear() == 2021 && mod303.getPeriod().isFirstSemester();
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032021PrintAEATScript.values();
			}
		}
		,AEAT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && mod303.getYear() < 2021;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017PrintAEATScript.values();
			}
		}
		,ARABA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAraba();
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017PrintARABAScript.values();
			}
		}
		,GIPUZKOA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isGipuzkoa();
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017PrintGIPUZKOAScript.values();
			}
		}
		,BIZKAIA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isBizkaia();
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017PrintBIZKAIAScript.values();
			}
		}
		;
		abstract boolean accept(Mod303 mod303);
		abstract IModelScript<Mod303Key>[] getScript();
	}

	public static IModelScript<Mod303Key>[] obtainScript(Mod303 mod303) {
		IModelScript<Mod303Key>[] ms = null;
		for ( Model303Script script : Model303Script.values()) {
			if (script.accept(mod303)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod303.getAdministration().getDescription()
					+ " - " 
					+ mod303.getModelFullName());
		}
		return ms;
	}
}
