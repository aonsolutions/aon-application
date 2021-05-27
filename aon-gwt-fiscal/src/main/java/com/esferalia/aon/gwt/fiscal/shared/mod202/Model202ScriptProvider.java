package com.esferalia.aon.gwt.fiscal.shared.mod202;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;

public class Model202ScriptProvider {

	public static IModelScript<Mod202Key>[] obtainScript(Mod202 mod202) {
		IModelScript<Mod202Key>[] ms = null;
		if (mod202.getAdministration() == Administration.COMMON_TERRITORY) {
			if (mod202.getYear() < 2018 || (mod202.getYear() == 2018 && mod202.getPeriod().ordinal() < Period.T2.ordinal())) {
				ms = Model202AEATScript.values();
			} else {
				ms = Model2022018AEATScript.values();
			}
		}
		if (ms == null) {
			throw new IllegalStateException(
					"No hay declaración disponible para: " + mod202.getAdministration().toString() + " "
							+ mod202.getYear() + " " + mod202.getPeriod().getDescription());
		}
		return ms;
	}
}
