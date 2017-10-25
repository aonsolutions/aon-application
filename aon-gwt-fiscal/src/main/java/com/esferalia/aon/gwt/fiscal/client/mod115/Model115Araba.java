package com.esferalia.aon.gwt.fiscal.client.mod115;


import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.watson.util.Pair;

public class Model115Araba extends Model115Base {

	public Model115Araba(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}


	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Formulario Papel. [pdf]" 
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3D115-A.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770466&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 402 de 26 de marzo de 1998. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1"
			+ "=attachment%3B+filename%3DOrden+Foral+402+de+26+de+marzo+de+1998.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770467&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 673 de 18 de octubre de 2001. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770468&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 39 de 3 de febrero de 2010 que regula la "
			+ "obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma "
			+ "telem\u00E1tica por Internet."
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+"
			+ "obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma"
			+ "+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770469&ssbinary=true"));
		list.add(new Pair<String, String>("Resoluci\u00F3n 21 de 12 de enero de 2016 . [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DResoluci%C3%B3n+21+de+12+de+enero+de+2016+.pdf&"
			+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770470"
			+ "&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 104 de 17 de febrero de 2014. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770471&ssbinary=true"));
		return list;
	}
}
