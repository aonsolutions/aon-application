package com.esferalia.aon.gwt.fiscal.client.mod123;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model123Araba extends Model123Base {

	public Model123Araba(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod123 mod123) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod123,"Formulario Papel." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader"
				+ "=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2"
				+ "=pragma&blobheadervalue1=attachment%3B+filename%3D123.pdf&blobheadervalue2"
				+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511587&ssbinary=true"));
			panel.add(getAnchorPanel(mod123,"Decreto Foral 14 de 29 de febrero de 2000."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
				+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
				+ "attachment%3B+filename%3DDecreto+Foral+14+de+29+de+febrero+de+2000.pdf&"
				+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511588&ssbinary=true"));
			panel.add(getAnchorPanel(mod123,"Orden Foral 673 de 18 de octubre de 2001."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
				+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
				+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
				+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511589&ssbinary=true"));
		return panel;
	}
}
