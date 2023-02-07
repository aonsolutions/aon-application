package com.esferalia.aon.occam.api.model.fiscal.mod111;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public class Model111ScriptProvider {
	
	private Model111ScriptProvider() {
		
	}
	
	private enum Model111Script {
		AEAT_2023_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAEAT() && mod111.getYear() >= 2023;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111AEAT2023Script.values();
			}
		}
		,BIZKAIA_2023_110_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getYear() >= 2023 && mod111.isQuarterPeriod();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model110Bizkaia2023Script.values();
			}
		}
		,BIZKAIA_2023_111_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getYear() >= 2023 && mod111.isMonthPeriod();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111Bizkaia2022Script.values();
			}
		}
		,GIPUZKOA_2023_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isGipuzkoa() && mod111.getYear() >= 2023;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model110Gipuzkoa2023Script.values();
			}
		}
		// ************************************ OLDERS
		,ARABA_2023_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAraba() && mod111.getYear() >= 2023;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111Araba2023Script.values();
			}
		}
		
		
		,AEAT_2022_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAEAT() && mod111.getYear() == 2022;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111AEAT2022Script.values();
			}
		}
		,AEAT_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAEAT() && mod111.getYear() < 2022;
			}

			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111AEATScript.values();
			}
		}
		,ARABA_2022_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAraba() && mod111.getYear() > 2021 && mod111.getYear() > 2023;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111Araba2022Script.values();
			}
		}
		,ARABA_2016_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAraba() && mod111.getYear() > 2015 && mod111.getYear() < 2022;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111Araba2016Script.values();
			}
		}
		,ARABA_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isAraba() && mod111.getYear() < 2016;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111ArabaScript.values();
			}
		}
		,BIZKAIA_2022_110_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getYear() > 2021 && mod111.getYear() < 2023 && mod111.isQuarterPeriod();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model110Bizkaia2022Script.values();
			}
		}
		,BIZKAIA_110_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getYear() < 2022 && mod111.isQuarterPeriod();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model110BizkaiaScript.values();
			}
		}
		,BIZKAIA_2022_111_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getYear() > 2021 && mod111.getYear() < 2023 && mod111.isMonthPeriod();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111Bizkaia2022Script.values();
			}
		}
		,BIZKAIA_111_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isBizkaia() && mod111.getYear() < 2022 && mod111.isMonthPeriod();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model111BizkaiaScript.values();
			}
		}
		,GIPUZKOA_2021_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isGipuzkoa() && mod111.getYear() < 2023;
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model110GipuzkoaScript.values();
			}
		}
		,NAFARROA_SCRIPT {
			@Override
			boolean accept(Mod111 mod111) {
				return mod111.isNavarra();
			}
	
			@Override
			IModelScript<Mod111Key>[] getScript() {
				return Model715NavarraScript.values();
			}
		}
		;
		abstract boolean accept(Mod111 mod111);
		abstract IModelScript<Mod111Key>[] getScript();
	}
	

	public static IModelScript<Mod111Key>[] obtainScript(Mod111 mod111) {
		IModelScript<Mod111Key>[] ms = null;
		for ( Model111Script script : Model111Script.values()) {
			if (script.accept(mod111)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod111.getAdministration().getDescription()
					+ " - " 
					+ mod111.getModelFullName());
		}
		return ms;
	}
	
}
