package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.watson.util.Pair;

public class Model115AEAT extends Model115Base {
	
	
	public Model115AEAT(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GH02.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"http://www.agenciatributaria.es/AEAT.internet/GH02/informacion.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH02.shtml"));
		return list;
	}


}
