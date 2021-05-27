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
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer;

@WebServlet(name = "Mod115 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model115PrintAEAT" })
public class Mod115PrintAEAT extends ModPrintAEAT {

	private static final long serialVersionUID = 2116042075371392118L;
	
	public Mod115PrintAEAT() {
		super();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST Mod115 Print AEAT");
		try {
			JSONObject json = getRequestJSON(req);
			init(json);
			
			Mod115 mod115 = FISCAL.getMod115(getDomainName(), getDomainId(), getUser(), getId());
			Boolean isI = FiscalModelDeclarationType.DEPOSIT.equals(mod115.getDeclarationType());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod115Writer.fillWriter(mod115, writer);
			
			send(req, resp, mod115, output.toByteArray(), isI);		
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}
	}
	
	private void send(HttpServletRequest req, HttpServletResponse resp, Mod115 mod115, byte[] content, Boolean isI) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod115, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod115, getEncodedFile(content));

		String certUrl = isTest() 
			? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica" 
			: "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica";

		String request = isCert() 
			? certUrl
			: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
	
		send(req, resp, request, urlParameters, isI);
	}
		
	private String getUrlParameters(Mod115 mod115, String encodedFile) {
		return "HID=IE71150A" 
				+"&IDI=ES"
				+"&LEV=000000000000"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&PRG="
				+"&FIN=" 
				+"&EJF=" + mod115.getYear() 
				+"&MOD=115";
	}
	
	private String getCertUrlParameters(Mod115 mod115, String encodedFile, String name, String document) {
		return "HID=IE71150A"
				+ "&FIRNIF=" + document
				+ "&FIRNOMBRE=" + name
				+ "&TIA=" + mod115.getDeclarationType().getValue()
				+ "&NDC=" + mod115.getDocument()
				+ "&NRC=" + ("I".equals(mod115.getDeclarationType().getValue()) ? getNrc() : "") // TODO Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" + ("I".equals(mod115.getDeclarationType().getValue()) ? mod115.getAmount(Mod115Key.CT_C05) : "") // Importe ingresado correspondiente al NRC para el tipo I,  en resto de tipos vacío
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
		return DataResponseSource.MOD303;
	}

	@Override
	protected DataAttachSource getDataAttachSource() {
		return DataAttachSource.MOD303;
	}

	@Override
	protected void updateMod(JSONObject json) throws JSONException {
		Mod115 mod115 = FISCAL.getMod115(getDomainName(), getDomainId(), getUser(), getId());
		mod115.setNumber(json.getString("JUS"));
		FISCAL.markAsSent(getDomainName(), getUser(), mod115);
	}
}
