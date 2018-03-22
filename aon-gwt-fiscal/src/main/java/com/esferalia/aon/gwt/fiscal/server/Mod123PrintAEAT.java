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
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer;

@WebServlet(name = "Mod123 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model123PrintAEAT" })
public class Mod123PrintAEAT extends ModPrintAEAT {

	private static final long serialVersionUID = -1855166921054314939L;
	
	public Mod123PrintAEAT() {

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			JSONObject json = new JSONObject();
			if(req.getParameter("mod") == null || req.getParameter("mod").isEmpty())
				json = getRequestJSON(req);
			init(req, json);
			
			Mod123 mod123 = FISCAL.getMod123(getDomainName(), getDomainId(), getUser(), getId());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod123Writer.fillWriter(mod123, writer);
			send(req, resp, mod123, output.toByteArray());
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}
	}

	private void send(HttpServletRequest req, HttpServletResponse resp, Mod123 mod123, byte[] content) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod123, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod123, getEncodedFile(content));
	
		String request = isCert() 
			? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
			// REAL "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
			: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
		
		send(req, resp, request, urlParameters);
	}
	
	private String getUrlParameters(Mod123 mod123, String encodedFile) {
		return "HID=IE51230A" 
				+"&IDI=ES"
				+"&LEV=000000000000"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&PRG="
				+"&FIN=" 
				+"&EJF=" + mod123.getYear() 
				+"&MOD=123";
	}
	
	private String getCertUrlParameters(Mod123 mod123, String encodedFile, String name, String document) {
		return "HID=IE51230A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&TIA=" + mod123.getDeclarationType().getValue()
				+ "&NDC=" + mod123.getDocument()
				+ "&NRC=" + ("I".equals(mod123.getDeclarationType().getValue()) ? "" : "") // TODO Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" + ("I".equals(mod123.getDeclarationType().getValue()) ? mod123.getAmount(Mod123Key.CT_C08) : "") // Importe ingresado correspondiente al NRC para el tipo I,  en resto de tipos vacío
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
	
	private String getCheckUrlParameters(Mod123 mod123) {
		return "HID=COINX123"
				+ "&NIF=" + mod123.getDocument()
				+ "&EJF=" + mod123.getYear() 
				+ "&PER=" + mod123.getPeriod()
				+ "&CEL=" + ""
				+ "&EXP=" + ""
				+ "&NIU=" + ""
				+ "&IDI=" + "ES"
				+ "&VIA=" + ""
				+ "&FIN=" + "";
	}
	
	@Override
	protected DataResponseSource getDataResponseSource() {
		return DataResponseSource.MOD123;
	}

	@Override
	protected DataAttachSource getDataAttachSource() {
		return DataAttachSource.MOD123;
	}

	@Override
	protected void updateMod(JSONObject json) throws JSONException {
		Mod123 mod123 = FISCAL.getMod123(getDomainName(), getDomainId(), getUser(), getId());
		mod123.setNumber(json.getString("JUS"));
		FISCAL.markAsSent(getDomainName(), getUser(), mod123);
	}
}
