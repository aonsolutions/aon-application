package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.util.Pair;

public class Model111AEAT extends Model111Base {
	
	
	public Model111AEAT(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GH01.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"http://www.agenciatributaria.es/AEAT.internet/GH01/informacion.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH01.shtml"));
		return list;
	}

}
