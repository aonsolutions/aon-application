package com.esferalia.aon.occam.api.model.fiscal.mod421;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public class Model421PrintScriptProvider {

	private enum Model421Script {
		ATC_2026 {
			@Override
			boolean accept(Mod421 mod421) {
				return mod421.isCanarias() && mod421.getYear() >= 2026;
			}
			@Override
			IModelScript<Mod421Key>[] getScriptT1() {
				return Model4212026ATCPrintT1Script.values();
			}
			@Override
			IModelScript<Mod421Key>[] getScriptT4() {
				return Model4212026ATCPrintT4Script.values();
			}
		}
		;
		
		abstract boolean accept(Mod421 mod421);
		abstract IModelScript<Mod421Key>[] getScriptT1();
		abstract IModelScript<Mod421Key>[] getScriptT4();
	}

	public static IModelScript<Mod421Key>[] obtainScript(Mod421 mod421) {
		IModelScript<Mod421Key>[] ms = null;
		for (Model421Script script : Model421Script.values()) {
			if (script.accept(mod421)) {
				ms = mod421.isLastPeriod() ? script.getScriptT4() : script.getScriptT1();
				break;
			}
		}
		if (ms == null) {
			throw new IllegalStateException("No hay declaraci\u00F3n disponible para: "
					+ mod421.getAdministration().getDescription()
					+ " - " 
					+ mod421.getModelFullName());
		}
		return ms;
	}
}
