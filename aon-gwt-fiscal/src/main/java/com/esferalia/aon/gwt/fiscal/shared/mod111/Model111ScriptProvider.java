package com.esferalia.aon.gwt.fiscal.shared.mod111;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.AonError;

public class Model111ScriptProvider {
	private Model111ScriptProvider() {
		
	}

	public static IModelScript<Mod111Key>[] obtainScript(Mod111 mod111) {
		IModelScript<Mod111Key>[] ms = mod111.getAdministration().visit(new IAdministrationVisitor<IModelScript<Mod111Key>[]>() {

			@Override
			public IModelScript<Mod111Key>[] visitCommonTerritory() {
				return (mod111.getYear() > 2021)
					?Model111AEAT2022Script.values()
					:Model111AEATScript.values();
			}


			@Override
			public IModelScript<Mod111Key>[] visitAlava() {
				if ( (mod111.getYear() > 2021) ) {
					return  Model111Araba2022Script.values();
				} else if (mod111.getYear() > 2015) {
					return  Model111Araba2016Script.values();
				} 
				return Model111ArabaScript.values();
			}

			@Override
			public IModelScript<Mod111Key>[] visitBizkaia() {
				if ( (mod111.getYear() > 2021) ) {
					return (mod111.getPeriod().isQuarterPeriod())
						?Model110Bizkaia2022Script.values()
						:Model111Bizkaia2022Script.values();
				} 
				return (mod111.getPeriod().isQuarterPeriod())
						?Model110BizkaiaScript.values()
						:Model111BizkaiaScript.values();
			}

			@Override
			public IModelScript<Mod111Key>[] visitGipuzkoa() {
				return Model110GipuzkoaScript.values();
			}

			@Override
			public IModelScript<Mod111Key>[] visitNavarra() {
				return Model715NavarraScript.values();
			}

			@Override
			public IModelScript<Mod111Key>[] visitUnknown() {
				return null;
			}
		});
		if (ms == null) {
			throw new IllegalStateException("No hay declaración disponible para: "
					+ mod111.getAdministration().toString()
					+ mod111.getYear()
					+mod111.getPeriod().getDescription());
		}
		return ms;
	}
}
