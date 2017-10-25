package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.watson.util.Pair;

public class Model115Gipuzkoa extends Model115Base {
	
	public Model115Gipuzkoa(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria"
				, "https://w390w.gipuzkoa.net/WAS/CORP/LIATramitesWEB/cambiarLocale.do?"
						+ "cambiarLocale=true&idioma=es&tipoBusq=busq_mat&ms=1423832872615&ver=1645"));
		return list;
	}

}
