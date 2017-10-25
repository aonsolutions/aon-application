package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.Pair;

public class Model123Bizkaia extends Model123Base {

	public Model123Bizkaia(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Impreso Rellenable." 
			,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=0E427541D20570EC345E56908844474CEB6AAFD583BF29E10CEFB85F5E99D0BA&Tam=374&Ext=application/pdf&Tem_Codigo=2093"));
		list.add(new Pair<String, String>("Instrucciones"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/123EurBilInst.pdf"));
		list.add(new Pair<String, String>("Orden Foral 1.330/2000, de 27 de abril"
			,"http://www.bizkaia.eus/Lehendakaritza/Bao_Bob/2000/06/20000601a104.pdf"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=249&Age_Codigo=25/02/2016&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
		return list;
	}

}
