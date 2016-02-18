package com.esferalia.aon.gwt.fiscal.client.mod115;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model115Araba extends Model115Base {

	public Model115Araba(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod115 mod115) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(mod115,"Formulario Papel. [pdf]" 
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3D115-A.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770466&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 402 de 26 de marzo de 1998. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1"
			+ "=attachment%3B+filename%3DOrden+Foral+402+de+26+de+marzo+de+1998.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770467&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 673 de 18 de octubre de 2001. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770468&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 39 de 3 de febrero de 2010 que regula la "
			+ "obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma "
			+ "telem\u00E1tica por Internet."
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+"
			+ "obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma"
			+ "+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770469&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Resoluci\u00F3n 21 de 12 de enero de 2016 . [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DResoluci%C3%B3n+21+de+12+de+enero+de+2016+.pdf&"
			+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770470"
			+ "&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 104 de 17 de febrero de 2014. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770471&ssbinary=true"));
		return panel;
	}
}
