package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.util.Pair;

public class Model130Bizkaia extends Model130Base {
	
	
	public Model130Bizkaia(IFiscalModelCallback<Mod130> callback) {
		super(callback);
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Impreso (Rellenable)"
			,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=8AA405AF81D"
			+ "76CC992B77CD9B7C7112FCA1B1651B529D2961D5EFE269326793A&Tam=572&Ext=application/pdf&"
			+ "Tem_Codigo=2093"));
		list.add(new Pair<String, String>("Instrucciones" 
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/130EurBilInst.pdf"));
		list.add(new Pair<String, String>("ORDEN FORAL 636/2014, de 17 de marzo"
			,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2014/03/20140324a057.pdf#page=40"));
		list.add(new Pair<String, String>("Enlace al programa de ayuda"
			,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1937"));
		list.add(new Pair<String, String>("Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=145&Age_Codigo"
			+ "=04/05/2016&Tem_Codigo=5346"));
		list.add(new Pair<String, String>("Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea_loturak.asp?Idioma=CA&Tem_Codigo"
			+ "=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdColumna=3556"));
		return list;
	}
	
	@Override
	protected void paintParticularyRow(final IFiscalModelCallback<Mod130> callback, IModelScript<Mod130Key> script) {
	}
	
}
