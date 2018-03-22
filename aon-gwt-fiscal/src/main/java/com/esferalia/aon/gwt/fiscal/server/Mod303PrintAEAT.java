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
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer;

@WebServlet(name = "Mod303 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model303PrintAEAT" })
public class Mod303PrintAEAT extends ModPrintAEAT {
	
	private static final long serialVersionUID = -8391437522744646639L;

 	public Mod303PrintAEAT() {
		super();
	}
 	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject json = new JSONObject();
			if(req.getParameter("mod") == null || req.getParameter("mod").isEmpty())
				json = getRequestJSON(req);
			init(req, json);
			
			Mod303 mod303 = FISCAL.getMod303(getDomainName(), getDomainId(), getUser(), getId());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod303Writer.fillWriter(mod303, writer);
						
			send(req, resp, mod303, output.toByteArray());
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}
	}

	private void send(HttpServletRequest req, HttpServletResponse resp, Mod303 mod303, byte[] content) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod303, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod303, getEncodedFile(content));
	
		String request = isCert() 
			? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
			// REAL "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
			: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
		
		send(req, resp, request, urlParameters);
	}
	
	public String getUrlParameters(Mod303 mod303, String encodedFile){
		return "HID=IE83030A"
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&FIN="
				+"&EJF=" + mod303.getYear()
				+"&MOD=303";
	}
	
	public String getCertUrlParameters(Mod303 mod303, String encodedFile, String name, String document){
		return "HID=IE83030A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&TIA=" + mod303.getDeclarationType().getValue()
				+ "&NDC=" + mod303.getDocument()
				+ "&NRC=" + ("I".equals(mod303.getDeclarationType().getValue()) ? "" : "") // TODO Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" +  ("I".equals(mod303.getDeclarationType().getValue())
							|| "U".equals(mod303.getDeclarationType().getValue())
								? mod303.getAmount(Mod303Key.CT_C71) : "")
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
	
	private String getCheckUrlParameters(Mod303 mod303) {
		return "HID=COIN1303"
				+ "&NIF=" + mod303.getDocument()
				+ "&EJF=" + mod303.getYear() 
				+ "&PER=" + mod303.getPeriod()
				+ "&CEL=" + ""
				+ "&EXP=" + ""
				+ "&NIU=" + ""
				+ "&IDI=" + "ES"
				+ "&VIA=" + ""
				+ "&FIN=" + "";
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
		Mod303 mod303 = FISCAL.getMod303(getDomainName(), getDomainId(), getUser(), getId());
		mod303.setNumber(json.getString("JUS"));
		FISCAL.markAsSent(getDomainName(), mod303, getUser());
	}
}
