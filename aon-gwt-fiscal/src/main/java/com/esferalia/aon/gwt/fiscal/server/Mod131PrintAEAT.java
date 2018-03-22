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
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod131Writer;

@WebServlet(name = "Mod131 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model131PrintAEAT" })
public class Mod131PrintAEAT extends ModPrintAEAT {

	private static final long serialVersionUID = -6382863064129841608L;

	public Mod131PrintAEAT() {

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			JSONObject json = new JSONObject();
			if(req.getParameter("mod") == null || req.getParameter("mod").isEmpty())
				json = getRequestJSON(req);
			init(req, json);

			Mod131 mod131 = FISCAL.getMod131(domainName, domainId, user,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod131Writer.fillWriter(mod131, writer);
			send(req, resp, mod131, output.toByteArray());
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}

	}
	
	private void send(HttpServletRequest req, HttpServletResponse resp, Mod131 mod131, byte[] content) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod131, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod131, getEncodedFile(content));
	
		String request = isCert() 
			? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
			// REAL "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
			: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
	
		send(req, resp, request, urlParameters);
	}
	
	private String getUrlParameters(Mod131 mod131, String encodedFile) {		
		return "HID=IE61310A" 
				+"&IDI=ES"
				+"&LEV=000000000000"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&PRG="
				+"&FIN=" 
				+"&EJF=" + mod131.getYear() 
				+"&MOD=131";
	}
	
	private String getCertUrlParameters(Mod131 mod131, String encodedFile, String name, String document) {
		return "HID=IE51310A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&TIA=" + mod131.getDeclarationType().getValue()
				+ "&NDC=" + mod131.getDocument()
				+ "&NRC=" + ("I".equals(mod131.getDeclarationType().getValue()) ? "" : "") // TODO Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" + ("I".equals(mod131.getDeclarationType().getValue()) ? mod131.getAmount(Mod131Key.C15) : "") // Importe ingresado correspondiente al NRC para el tipo I,  en resto de tipos vacío
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
		return DataResponseSource.MOD131;
	}

	@Override
	protected DataAttachSource getDataAttachSource() {
		return DataAttachSource.MOD131;
	}

	@Override
	protected void updateMod(JSONObject json) throws JSONException {
		Mod131 mod131 = FISCAL.getMod131(getDomainName(), getDomainId(), getUser(), getId());
		mod131.setNumber(json.getString("JUS"));
		FISCAL.markAsSent(getDomainName(), getUser(), mod131);
	}
}
