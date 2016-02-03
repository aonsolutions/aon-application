package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model111Araba extends Model111Base {
	 
	public Model111Araba(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(mod111,
				 "Formulario Papel." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D110.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668896&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Orden Foral 54 de 31 de enero de 2007."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+54+de+31+de+enero+de+2007.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668897&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668898&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Resoluci\u00F3n 135 de 27 de enero de 2015."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DResoluci%C3%B3n+135+de+27+de+enero+de+2015.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668899&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Orden Foral 104 de 17 de febrero de 2014."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668900&ssbinary=true"));
		return panel;
	}
	
}
