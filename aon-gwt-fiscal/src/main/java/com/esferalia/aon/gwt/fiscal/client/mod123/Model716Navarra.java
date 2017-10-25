package com.esferalia.aon.gwt.fiscal.client.mod123;


import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.Pair;

public class Model716Navarra extends Model123Base {

	
	public Model716Navarra(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria"
				,"http://www.navarra.es/home_es/servicios/ficha/1824/Retenciones"));
		return list;
	}

}
