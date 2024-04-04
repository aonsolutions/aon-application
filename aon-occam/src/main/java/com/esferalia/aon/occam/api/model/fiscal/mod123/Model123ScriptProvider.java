package com.esferalia.aon.occam.api.model.fiscal.mod123;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public class Model123ScriptProvider {

	private Model123ScriptProvider() {
		
	}

	private enum Model123Script {
		AEAT_2024_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAEAT() && mod123.getYear() >= 2024;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123AEAT2024Script.values();
			}
		}		
		,AEAT_2022_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAEAT() && (mod123.getYear() == 2022 || mod123.getYear() == 2023);
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123AEAT2022Script.values();
			}
		}
		,AEAT_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAEAT() && mod123.getYear() < 2022;
			}

			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123AEATScript.values();
			}
		}
		,ARABA_2024_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAraba() && mod123.getYear() >= 2024;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Araba2024Script.values();
			}
		}
		,ARABA_2022_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAraba() && (mod123.getYear() == 2022 || mod123.getYear() == 2023);
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Araba2022Script.values();
			}
		}
		,ARABA_2016_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAraba() && mod123.getYear() > 2015 && mod123.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Araba2016Script.values();
			}
		}
		,ARABA_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isAraba() && mod123.getYear() < 2016;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123ArabaScript.values();
			}
		}
		,BIZKAIA_2024_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isBizkaia() && mod123.getYear() >= 2024;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Bizkaia2024Script.values();
			}
		}
		,BIZKAIA_2022_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isBizkaia() && (mod123.getYear() == 2022 || mod123.getYear() == 2023);
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Bizkaia2022Script.values();
			}
		}
		,BIZKAIA_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isBizkaia() && mod123.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123BizkaiaScript.values();
			}
		}
		,GIPUZKOA_2024_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isGipuzkoa() && mod123.getYear() >= 2024;
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Gipuzkoa2024Script.values();
			}
		}
		,GIPUZKOA_2022_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isGipuzkoa() && (mod123.getYear() == 2022 || mod123.getYear() == 2023);
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123Gipuzkoa2022Script.values();
			}
		}
		,GIPUZKOA_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isGipuzkoa();
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model123GipuzkoaScript.values();
			}
		}
		,NAFARROA_SCRIPT {
			@Override
			boolean accept(Mod123 mod123) {
				return mod123.isNavarra();
			}
	
			@Override
			IModelScript<Mod123Key>[] getScript() {
				return Model716NavarraScript.values();
			}
		}
		;
		abstract boolean accept(Mod123 mod123);
		abstract IModelScript<Mod123Key>[] getScript();
	}

	public static IModelScript<Mod123Key>[] obtainScript(Mod123 mod123) {
		IModelScript<Mod123Key>[] ms = null;
		for ( Model123Script script : Model123Script.values()) {
			if (script.accept(mod123)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod123.getAdministration().getDescription()
					+ " - " 
					+ mod123.getModelFullName());
		}
		return ms;
	}
}
