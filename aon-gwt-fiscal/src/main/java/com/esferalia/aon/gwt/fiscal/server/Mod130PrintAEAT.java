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
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer;

@WebServlet(name = "Mod130 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model130PrintAEAT" })
public class Mod130PrintAEAT extends ModPrintAEAT {

	private static final long serialVersionUID = 1384243773471289795L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			JSONObject json = getRequestJSON(req);
			init(json);

			Mod130 mod130 = FISCAL.getMod130(domainName, domainId, user,id);
			Boolean isI = FiscalModelDeclarationType.DEPOSIT.equals(mod130.getDeclarationType());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod130Writer.fillWriter(mod130, writer);
			
			send(req, resp, mod130, output.toByteArray(), isI);

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private void send(HttpServletRequest req, HttpServletResponse resp, Mod130 mod130, byte[] content, Boolean isI) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod130, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod130, getEncodedFile(content));
	
		String request = isCert() 
			? // PRUEBAS "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
			  // REAL 
			  "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
			: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
	
		send(req, resp, request, urlParameters, isI);
	}

	private String getUrlParameters(Mod130 mod130, String encodedFile) {		
		return "HID=IE51300A" 
				+"&IDI=ES"
				+"&LEV=000000000000"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&PRG="
				+"&FIN=" 
				+"&EJF=" + mod130.getYear() 
				+"&MOD=130";
	}
	
	private String getCertUrlParameters(Mod130 mod130, String encodedFile, String name, String document) {
		return "HID=IE51300A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&TIA=" + mod130.getDeclarationType().getValue()
				+ "&NDC=" + mod130.getDocument()
				+ "&NRC=" + ("I".equals(mod130.getDeclarationType().getValue()) ? getNrc() : "") // TODO Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" + ("I".equals(mod130.getDeclarationType().getValue()) ? mod130.getAmount(Mod130Key.C28) : "") // Importe ingresado correspondiente al NRC para el tipo I,  en resto de tipos vacío
				+ "&NRR=" + ""
				+ "&ICO=" + ""
				+ "&NR1=" + ""
				+ "&IN1=" + ""
				+ "&NR2=" + ""
				+ "&IN2=" + ""
				+ "&NR3=" + ""
				+ "&IN3=" + ""
				+ "&NR4=" + ""
				+ "&IN4=" + ""
				+ "&NR5=" + ""
				+ "&IN5=" + ""
				+ "&NR6=" + ""
				+ "&IN6=" + ""
				+ "&NR7=" + ""
				+ "&IN7=" + ""
				+ "&CMN=" + ""
				+ "&LOT=" + "0"
				+ "&IDI=" + "ES"
				+ "&LEV=" + "000000000000"
				+ "&F01=" + encodedFile
				+ "&PUN=" + "00000000"
				+ "&TXT=" + ""
				+ "&FIR=" + "FirmaBasica"
				+ "&FIN=" + "F";
	}
	

	@Override
	protected DataResponseSource getDataResponseSource() {
		return DataResponseSource.MOD130;
	}

	@Override
	protected DataAttachSource getDataAttachSource() {
		return DataAttachSource.MOD130;
	}

	@Override
	protected void updateMod(JSONObject json) throws JSONException {
		Mod130 mod130 = FISCAL.getMod130(getDomainName(), getDomainId(), getUser(), getId());
		mod130.setNumber(json.getString("JUS"));
		FISCAL.markAsSent(getDomainName(), getUser(), mod130);
	}
}
