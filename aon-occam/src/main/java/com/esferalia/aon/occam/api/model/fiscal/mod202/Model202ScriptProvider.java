package com.esferalia.aon.occam.api.model.fiscal.mod202;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;

public class Model202ScriptProvider {

	private enum Model202Script {
		AEAT_2025_SCRIPT {
			@Override
			boolean accept(Mod202 mod202) {
				return mod202.isAEAT() && mod202.getYear() >= 2025;
			}
	
			@Override
			List<IModelScript<Mod202Key>> getScript() {
				// A partir de 2025 se separan las casillas en 3 pestañas, por lo que no se usa este metodo 
				// para el formulario en pantalla, sino que se hace directamente en Model2022025AEAT. 
				// Hasta ahora se usaba un solo script para la pantalla y para la Excel, pero a partir
				// de 2025, este metodo solo se usa para la Excel y devuelve todos los scripts				
				List<IModelScript<Mod202Key>> scriptList = new ArrayList<IModelScript<Mod202Key>>();
				scriptList.addAll(Arrays.asList(Model2022025AddDataAEATScript.values()));
				scriptList.addAll(Arrays.asList(Model2022025LiquidationAEATScript.values()));
				scriptList.addAll(Arrays.asList(Model2022025AddInfoAEATScript.values()));
				return scriptList;
			}
			
		},
		AEAT_2023_SCRIPT {
			@Override
			boolean accept(Mod202 mod202) {
				return mod202.isAEAT() && (mod202.getYear() == 2023 || mod202.getYear() == 2024);
			}
	
			@Override
			List<IModelScript<Mod202Key>> getScript() {
				return Arrays.asList(Model2022023AEATScript.values());
			}
			
		},
		AEAT_2018_SCRIPT {
			@Override
			boolean accept(Mod202 mod202) {
				return mod202.isAEAT() 
					&& mod202.getYear() < 2023
					&& (mod202.getYear() > 2018 || 
					(mod202.getYear() == 2018 && mod202.getPeriod().ordinal() >= Period.T2.ordinal()));
			}
	
			@Override
			List<IModelScript<Mod202Key>> getScript() {
				return Arrays.asList(Model2022018AEATScript.values());
			}
			
		},
		AEAT_SCRIPT {
			@Override
			boolean accept(Mod202 mod202) {
				return mod202.isAEAT() 
					&& (mod202.getYear() < 2018 
					|| (mod202.getYear() == 2018 && mod202.getPeriod().ordinal() < Period.T2.ordinal()));
			}
	
			@Override
			List<IModelScript<Mod202Key>> getScript() {
				return Arrays.asList(Model202AEATScript.values());
			}
			
		}
		;
		abstract boolean accept(Mod202 mod202);
		abstract List<IModelScript<Mod202Key>> getScript();
	}
	
	public static List<IModelScript<Mod202Key>> obtainScript(Mod202 mod202) {
		List<IModelScript<Mod202Key>> ms = null;
		for (Model202Script script : Model202Script.values()) {
			if (script.accept(mod202)) {
				ms = script.getScript();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod202.getAdministration().getDescription()
					+ " - " 
					+ mod202.getModelFullName());
		}
		return ms;
	}
	
}
