package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.Pair;

public class Model123Gipuzkoa extends Model123Base {
	
	public Model123Gipuzkoa(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria"
			, "https://w390w.gipuzkoa.net/WAS/CORP/LIATramitesWEB/"
			+ "cambiarLocale.do?cambiarLocale=true&idioma=es&tipoBusq="
			+ "busq_mat&ms=1421096256948&ver=1648"));
		return list;
	}
}
