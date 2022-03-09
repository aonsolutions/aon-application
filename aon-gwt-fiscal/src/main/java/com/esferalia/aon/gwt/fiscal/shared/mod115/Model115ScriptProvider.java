package com.esferalia.aon.gwt.fiscal.shared.mod115;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public class Model115ScriptProvider {

	public static IModelScript<Mod115Key>[] obtainScript(Mod115 mod115) {
		IModelScript<Mod115Key>[] ms = mod115.getAdministration().visit(new IAdministrationVisitor<IModelScript<Mod115Key>[]>() {

			@Override
			public IModelScript<Mod115Key>[] visitCommonTerritory() {
				return Model115AEATScript.values();
			}


			@Override
			public IModelScript<Mod115Key>[] visitAlava() {
				if (mod115.getYear() > 2015) {
					return  Model115Araba2016Script.values();
				}
				return Model115ArabaScript.values();
			}

			@Override
			public IModelScript<Mod115Key>[] visitBizkaia() {
				return Model115BizkaiaScript.values();
			}

			@Override
			public IModelScript<Mod115Key>[] visitGipuzkoa() {
				return Model115GipuzkoaScript.values();
			}

			@Override
			public IModelScript<Mod115Key>[] visitNavarra() {
				return Model760NavarraScript.values();
			}

			@Override
			public IModelScript<Mod115Key>[] visitUnknown() {
				return null;
			}
		});
		if (ms == null) {
			throw new IllegalStateException("No hay declaración disponible para: "
					+ mod115.getAdministration().toString()
					+ mod115.getYear()
					+mod115.getPeriod().getDescription());
		}
		return ms;
	}
}
