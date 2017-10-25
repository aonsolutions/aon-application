package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.util.Pair;

public class Model111Araba extends Model111Base {
	 
	public Model111Araba(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria" 
			,"http://www.araba.eus/cs/Satellite?pageid=1193046566413&language=es_ES&tipomodelo=1193045445346&pagename=DiputacionAlava%2FPage%2FDPA_B_listadoModelos&tipoimpuesto=-1&nmodelo=110&anio=2017&aniodesde=2007&aniohasta=2017&btnimpu=Buscar"));
		return list;
	}
}
