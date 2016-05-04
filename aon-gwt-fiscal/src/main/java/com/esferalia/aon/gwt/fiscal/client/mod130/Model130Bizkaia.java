package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model130Bizkaia extends Model130Base {
	
	
	public Model130Bizkaia(IFiscalModelCallback<Mod130> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod130 mod130) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod130
				,"Impreso (Rellenable)"
				,"http://www.bizkaia.eus/ogasuna/ereduak/info_descarga.asp?Idioma=CA&val1=8AA405AF81D"
				+ "76CC992B77CD9B7C7112FCA1B1651B529D2961D5EFE269326793A&Tam=572&Ext=application/pdf&"
				+ "Tem_Codigo=2093"));
		panel.add(getAnchorPanel( mod130
				,"Instrucciones" 
				,"http://www.bizkaia.eus/fitxategiak/05/ogasuna/ereduak/Argitaratu/130EurBilInst.pdf"));
		panel.add(getAnchorPanel(mod130
				,"ORDEN FORAL 636/2014, de 17 de marzo"
				,"http://www.bizkaia.eus/lehendakaritza/Bao_bob/2014/03/20140324a057.pdf#page=40"));
		panel.add(getAnchorPanel(mod130
				,"Enlace al programa de ayuda"
				,"http://www.bizkaia.eus/home2/Temas/DetalleTema.asp?Tem_Codigo=1937"));
		panel.add(getAnchorPanel(mod130
				,"Enlace a las fechas de vencimiento en el a\u00F1o vigente"
				,"http://www.bizkaia.eus/ogasuna/egutegia/egutegia_anual.asp?id=0&Modelos=145&Age_Codigo"
				+ "=04/05/2016&Tem_Codigo=5346"));
		panel.add(getAnchorPanel(mod130
				,"Enlace a la gu\u00EDa de informaci\u00F3n tributaria GURE GIDA"
				,"http://www.bizkaia.eus/ogasuna/guregida/fitxabisorea_loturak.asp?Idioma=CA&Tem_Codigo"
				+ "=7884&bnetmobile=0&dpto_biz=5&codpath_biz=5|3405|7884&IdPublicoMostrar=810&IdColumna=3556"));
		return panel;
	    
	    
	}
	
	@Override
	protected void paintParticularyRow(final IFiscalModelCallback<Mod130> callback, IModelScript<Mod130Key> script) {
	}
	
}
