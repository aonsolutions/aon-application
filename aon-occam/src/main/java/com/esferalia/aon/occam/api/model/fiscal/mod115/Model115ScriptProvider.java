package com.esferalia.aon.occam.api.model.fiscal.mod115;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public class Model115ScriptProvider {
	
	private Model115ScriptProvider() {
		
	}
	
	private enum Model115Script {
		AEAT_2022_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isAEAT() && mod115.getYear() > 2021;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115AEAT2022Script.values();
			}
		}
		,AEAT_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isAEAT() && mod115.getYear() < 2022;
			}

			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115AEATScript.values();
			}
		}
		,ARABA_2022_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isAraba() && mod115.getYear() > 2021;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115Araba2022Script.values();
			}
		}
		,ARABA_2016_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isAraba() && mod115.getYear() > 2015 && mod115.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115Araba2016Script.values();
			}
		}
		,ARABA_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isAraba() && mod115.getYear() < 2016;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115ArabaScript.values();
			}
		}
		,BIZKAIA_2022_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isBizkaia() && mod115.getYear() > 2021;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115Bizkaia2022Script.values();
			}
		}
		,BIZKAIA_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isBizkaia() && mod115.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115BizkaiaScript.values();
			}
		}
		,GIPUZKOA_2022_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isGipuzkoa() && mod115.getYear() > 2021;
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115Gipuzkoa2022Script.values();
			}
		}
		,GIPUZKOA_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isGipuzkoa();
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model115GipuzkoaScript.values();
			}
		}
		,NAFARROA_SCRIPT {
			@Override
			boolean accept(Mod115 mod115) {
				return mod115.isNavarra();
			}
	
			@Override
			IModelScript<Mod115Key>[] getScript() {
				return Model760NavarraScript.values();
			}
		}
		;
		abstract boolean accept(Mod115 mod115);
		abstract IModelScript<Mod115Key>[] getScript();
	}
	
	public static IModelScript<Mod115Key>[] obtainScript(Mod115 mod115) {
		IModelScript<Mod115Key>[] ms = null;
		for ( Model115Script script : Model115Script.values()) {
			if (script.accept(mod115)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod115.getAdministration().getDescription()
					+ " - " 
					+ mod115.getModelFullName());
		}
		return ms;
	}
}
