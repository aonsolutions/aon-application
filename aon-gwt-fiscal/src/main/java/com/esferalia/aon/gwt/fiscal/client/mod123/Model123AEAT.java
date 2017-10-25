package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.Pair;

public class Model123AEAT extends Model123Base {
	
	
	public Model123AEAT(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientoini/GH04.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"http://www.agenciatributaria.es/AEAT.internet/GH04/informacion.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH04.shtml"));
		return list;
	}


}
