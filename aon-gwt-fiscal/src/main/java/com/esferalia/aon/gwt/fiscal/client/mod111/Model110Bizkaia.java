package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model110Bizkaia extends Model111Base {

	public Model110Bizkaia(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(mod111
				 ,"Impreso Rellenable." 
				,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=0E681A22B56829AFDBC3ED6DE62B26D17C12C3911BAF83BAC7253A40FDAC1106&Tam=171&Ext=application/pdf&Tem_Codigo=2093"));
		panel.add(getAnchorPanel(mod111
				,"ORDEN FORAL 1452/2007, de 23 de mayo."
				,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2007/06/20070604a109.pdf#page=34"));
		panel.add(getAnchorPanel(mod111
				,"Enlace al programa de ayuda"
				,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1992&idioma=CA&dpto_biz=5&codpath_biz=5|3587|1933|1948|1992"));
		panel.add(getAnchorPanel(mod111
				,"Enlace a las fechas de vencimiento en el a\u00F1o vigente"
				,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=245&Age_Codigo=29/01/2016&Tem_Codigo=5346"));
		panel.add(getAnchorPanel(mod111
				,"Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
				,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea.asp?Idioma=CA&Tem_Codigo=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdPublicoMostrarAnterior=804"));
		return panel;
	}


}
