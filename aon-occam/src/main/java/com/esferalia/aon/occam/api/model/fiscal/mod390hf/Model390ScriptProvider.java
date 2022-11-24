package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public class Model390ScriptProvider {
	
	private Model390ScriptProvider() {
		
	}
	private enum Model390Script {
		BIZKAIA_2022_SCRIPT {
			@Override
			boolean accept(Mod390HF mod) {
				return mod.isBizkaia() && mod.getYear() >= 2022;
			}
	
			@Override
			IModelScript<Mod390Key>[] getScript() {
				return Model3902022PrintBIZKAIAScript.values();
			}
		},
		BIZKAIA_SCRIPT {
			@Override
			boolean accept(Mod390HF mod) {
				return mod.isBizkaia() && mod.getYear() >= 2017;
			}
	
			@Override
			IModelScript<Mod390Key>[] getScript() {
				return Model3902017PrintBIZKAIAScript.values();
			}
		},
		ARABA_SCRIPT {
			@Override
			boolean accept(Mod390HF mod) {
				return mod.isAraba() && mod.getYear() >= 2017;
			}
	
			@Override
			IModelScript<Mod390Key>[] getScript() {
				return Model3902017PrintARABAScript.values();
			}
		},
		GIPUZKOA_2021_SCRIPT {
			@Override
			boolean accept(Mod390HF mod) {
				return mod.isGipuzkoa() && mod.getYear() >= 2017 && mod.getYear() >= 2021;
			}
	
			@Override
			IModelScript<Mod390Key>[] getScript() {
				return Model3902021PrintGIPUZKOAScript.values();
			}
		},
		GIPUZKOA_2017_SCRIPT {
			@Override
			boolean accept(Mod390HF mod) {
				return mod.isGipuzkoa() && mod.getYear() >= 2017 && mod.getYear() < 2021;
			}
	
			@Override
			IModelScript<Mod390Key>[] getScript() {
				return Model3902017PrintGIPUZKOAScript.values();
			}
		},
		
		;		
		abstract boolean accept(Mod390HF mod);
		abstract IModelScript<Mod390Key>[] getScript();
	}

	public static IModelScript<Mod390Key>[] obtainScript(Mod390HF mod) {
		IModelScript<Mod390Key>[] ms = null;
		for ( Model390Script script : Model390Script.values()) {
			if (script.accept(mod)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod.getAdministration().getDescription()
					+ " - " 
					+ mod.getModelFullName());
		}
		return ms;
	}

}
