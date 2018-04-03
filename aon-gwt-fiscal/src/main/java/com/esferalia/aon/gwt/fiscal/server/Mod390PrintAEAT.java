package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.server.fiscal.format.Mod3902015Writer;

@WebServlet(name = "Mod390 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model390PrintAEAT" })
public class Mod390PrintAEAT extends ModPrintAEAT {
	
	private static final long serialVersionUID = -8391437522744646639L;

 	public Mod390PrintAEAT() {
		super();
	}
 	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject json = getRequestJSON(req);
			init(json);
			
			Mod3902015 mod390 = FISCAL.getMod3902015(getDomainName(), getDomainId(), getUser(), getId());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod3902015Writer.fillWriter(mod390, writer);
						
			send(req, resp, mod390, output.toByteArray());
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}
	}

	private void send(HttpServletRequest req, HttpServletResponse resp, Mod3902015 mod390, byte[] content) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod390, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod390, getEncodedFile(content));

		String request = getUrl(mod390.getYear());	
		
		send(req, resp, request, urlParameters, false);
	}
	
	public String getUrl(Integer year) {
		if(isCert()) 
			return // PRUEBAS "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica";
				   // REAL
					"https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica";
		else if(year == 2015)
			return "https://www6.aeat.es/es13/l/zi21zilk0021";
		else if(year == 2016)
			return "https://www6.aeat.es/es13/l/zi21zilk0021";
		else if(year >= 2017)
			return "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
		return "";
	}
	
	public String getUrlParameters(Mod3902015 mod390, String encodedFile){
		if (mod390.getYear() == 2015) {
			return "HID=INF5390A" 
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+ "&F01=" + encodedFile 
				+ "&ANA=" + "CAP" 
				+ "&FIN=" 
				+ "&MOD=390" 
				+ "&PRG=PTLINK9T"
				+ "&EJF=2015";
		} else if (mod390.getYear() == 2016) {
			return "HID=INF6390A" 
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+ "&F01=" + encodedFile 
				+ "&ANA=" + "CAP" 
				+ "&FIN=" 
				+ "&MOD=390" 
				+ "&PRG=PTLINKF3"
				+ "&EJF=2016";
		} else if (mod390.getYear() == 2017) {
			return "HID=INF7390A" 
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+ "&FIC=" + encodedFile 
				+ "&RUT="  
				+ "&PRG=PTLINKN3"
				+ "&FIN=" 
				+ "&EJF=2017"
				+ "&MOD=390";
		}
		return "";
	}
	
	public String getCertUrlParameters(Mod3902015 mod390, String encodedFile, String name, String document){
		
		return "HID=INF7390A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&SOP=" + "" // TODO Vacío o código de entidad de la EEFF (4 caracteres). Nota: este código es el que se utiliza en las estadísticas, por lo que es importante que sea correcto.
				+ "&NAV=" + ""
				+ "&NDC=" + mod390.getDocument()
				+ "&NAP=" + mod390.getName()
				+ "&NOM=" + "" // TODO Nombre del sujeto pasivo
				+ "&TEL=" + ""
				+ "&ADM=" + ""
				+ "&EJF=" + mod390.getYear()
				+ "&P01=" + mod390.getBox86() // TODO Vacío o resultado de la liquidación [86]
				+ "&P01=" + mod390.getBox94() // TODO Vacío o resultado de la liquidación anual atribuible al territorio común [94].
				+ "&TXT=" + ""
				+ "&FIR=" + "FirmaBasica"
				+ "&CRL=" + "|"
				+ "&LEV=" + "000000000000"
				+ "&F01=" + encodedFile
				+ "&PUN=" + "00000000"
				+ "&CMN=" + ""
				+ "&FIN=" + "F";
	}
	
	@Override
	protected DataResponseSource getDataResponseSource() {
		return DataResponseSource.MOD390;
	}

	@Override
	protected DataAttachSource getDataAttachSource() {
		return DataAttachSource.MOD390;
	}

	@Override
	protected void updateMod(JSONObject json) throws JSONException {
		Mod3902015 mod390 = FISCAL.getMod3902015(getDomainName(), getDomainId(), getUser(), getId());
		FISCAL.changeStatusMod3902015(getDomainName(), getUser(), mod390, FiscalStatus.SENT);
	}
}
