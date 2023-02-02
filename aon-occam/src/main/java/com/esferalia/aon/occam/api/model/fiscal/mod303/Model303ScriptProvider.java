package com.esferalia.aon.occam.api.model.fiscal.mod303;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public class Model303ScriptProvider {

	private enum Model303Script {
		AEAT_2023_SCRIPT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && mod303.getYear() >= 2023;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032023AEATPrintScript.values();
			}
		}
		,ARABA_2023 {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAraba() && mod303.getYear() >= 2023;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032023ARABAPrintScript.values();
			}
		}
		// Ejercicios anteriroes
		,AEAT_2021_2_SCRIPT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && 
					(mod303.getYear() == 2022 || (mod303.getYear() == 2021 && mod303.getPeriod().isLastSemester()));
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model30320212AEATPrintScript.values();
			}
		}
		,AEAT_2021_SCRIPT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && mod303.getYear() == 2021 && mod303.getPeriod().isFirstSemester();
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032021AEATPrintScript.values();
			}
		}
		,AEAT {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAEAT() && mod303.getYear() < 2021;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017AEATPrintScript.values();
			}
		}
		,ARABA_2022 {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAraba() && mod303.getYear() == 2022;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032022ARABAPrintScript.values();
			}
		}
		,ARABA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isAraba() && mod303.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017ARABAPrintScript.values();
			}
		}
		,GIPUZKOA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isGipuzkoa();
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017GIPUZKOAPrintScript.values();
			}
		}
		,BIZKAIA_2022 {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isBizkaia() && mod303.getYear() > 2021;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032022BIZKAIAPrintScript.values();
			}
		}
		,BIZKAIA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isBizkaia() && mod303.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032017BIZKAIAPrintScript.values();
			}
		}
		,NAVARRA {
			@Override
			boolean accept(Mod303 mod303) {
				return mod303.isNavarra() && mod303.getYear() > 2021;
			}
	
			@Override
			IModelScript<Mod303Key>[] getScript() {
				return Model3032022NAVARRARGScript.values();
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
