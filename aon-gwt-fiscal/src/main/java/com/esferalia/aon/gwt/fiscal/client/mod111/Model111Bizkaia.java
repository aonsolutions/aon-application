package com.esferalia.aon.gwt.fiscal.client.mod111;


import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.util.Pair;

public class Model111Bizkaia extends Model111Base {

	
	public Model111Bizkaia(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("ORDEN FORAL 1452/2007, de 23 de mayo."
				,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2007/06/20070604a109.pdf#page=34"));
		list.add(new Pair<String, String>("Enlace al programa de ayuda"
				,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1992"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
				,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=246&Age_Codigo=29/01/2016&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
				,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
		return list;
	}
}
