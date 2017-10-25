package com.esferalia.aon.gwt.fiscal.client.mod123;


import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.Pair;

public class Model123Araba extends Model123Base {

	public Model123Araba(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}

	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Formulario Papel." 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader"
				+ "=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2"
				+ "=pragma&blobheadervalue1=attachment%3B+filename%3D123.pdf&blobheadervalue2"
				+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511587&ssbinary=true"));
		list.add(new Pair<String, String>("Decreto Foral 14 de 29 de febrero de 2000."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
				+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
				+ "attachment%3B+filename%3DDecreto+Foral+14+de+29+de+febrero+de+2000.pdf&"
				+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511588&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 673 de 18 de octubre de 2001."
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
				+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
				+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
				+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511589&ssbinary=true"));
		return list;
	}
}
