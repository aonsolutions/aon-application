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
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer;

@WebServlet(name = "Mod111 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model111PrintAEAT" })
public class Mod111PrintAEAT extends ModPrintAEAT {

	private static final long serialVersionUID = 1340640917105582917L;

	public Mod111PrintAEAT() {
		super();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("POST Mod111 Print AEAT");
		try {
			JSONObject json = getRequestJSON(req);
			init(json);
			
			Mod111 mod111 = FISCAL.getMod111(getDomainName(), getDomainId(), getUser(),getId());	
			Boolean isI = FiscalModelDeclarationType.DEPOSIT.equals(mod111.getDeclarationType());
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod111Writer.fillWriter(mod111, writer);

			send(req, resp, mod111, output.toByteArray(), isI);
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}
	}
	

	private void send(HttpServletRequest req, HttpServletResponse resp, Mod111 mod111, byte[] content, Boolean isI) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod111, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod111, getEncodedFile(content));
	
		String request = isCert() 
			? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
			// REAL "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
			: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
		
		send(req, resp, request, urlParameters, isI);
	}

	private String getUrlParameters(Mod111 mod111, String encodedFile) {
		return "HID=IE7111VA" 
				+"&IDI=ES"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&PRG="
				+"&FIN=F" 
				+"&EJF=" + mod111.getYear() 
				+"&MOD=111";
	}
	
	private String getCertUrlParameters(Mod111 mod111, String encodedFile, String name, String document) {
		return "HID=IE71110A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&TIA=" + mod111.getDeclarationType().getValue()
				+ "&NDC=" + mod111.getDocument()
				+ "&NRC=" + ("I".equals(mod111.getDeclarationType().getValue()) ? getNrc() : "") // TODO Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" + ("I".equals(mod111.getDeclarationType().getValue()) ? mod111.getAmount(Mod111Key.CT_C30) : "") // Importe ingresado correspondiente al NRC para el tipo I,  en resto de tipos vacío
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
		return DataResponseSource.MOD111;
	}

	@Override
	protected DataAttachSource getDataAttachSource() {
		return DataAttachSource.MOD111;
	}

	@Override
	protected void updateMod(JSONObject json) throws JSONException {
		Mod111 mod111 = FISCAL.getMod111(getDomainName(), getDomainId(), getUser(), getId());
		mod111.setNumber(json.getString("JUS"));
		FISCAL.markAsSent(getDomainName(), getUser(), mod111);
	}
}
