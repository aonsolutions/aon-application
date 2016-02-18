package com.esferalia.aon.gwt.fiscal.client.mod115;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model115Bizkaia extends Model115Base {

	public Model115Bizkaia(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod115 mod115) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod115,"Impreso Rellenable." 
			,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=B94F009DF0D4D51239E48B11839D1182B932756BDF215C1045AFB7BEE1BA0E2C&Tam=377&Ext=application/pdf&Tem_Codigo=2093"));
		panel.add(getAnchorPanel(mod115 ,"Instrucciones"
			,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/115EurBilInst.pdf"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 3.797/2001, de 27 de noviembre"
			,"http://www.bizkaia.eus/Lehendakaritza/Bao_bob/2001/12/20011224A245.PDF"));
		panel.add(getAnchorPanel(mod115,"Enlace al programa de ayuda"
			,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=5336&Idioma=CA"));
		panel.add(getAnchorPanel(mod115,"Enlace a las fechas de vencimiento en el a\u00F1o vigente"
			,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=247&Age_Codigo=18/02/2016&Tem_Codigo=5346"));
		panel.add(getAnchorPanel(mod115,"Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
			,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
		return panel;
	}

}
