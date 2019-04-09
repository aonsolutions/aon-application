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
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.server.fiscal.format.Mod3902018Writer;

@WebServlet(name = "Mod390 2018 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model3902018PrintAEAT" })
public class Mod3902018PrintAEAT extends ModPrintAEAT {
	
	private static final long serialVersionUID = -8391437522744646639L;

 	public Mod3902018PrintAEAT() {
		super();
	}
 	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject json = getRequestJSON(req);
			init(json);
			
			Mod3902018 mod390 = FISCAL.getMod3902018(getDomainName(), getDomainId(), getUser(), getId());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod3902018Writer.fillWriter(mod390, writer);
						
			send(req, resp, mod390, output.toByteArray());
		} catch (Throwable e) {
			if(!isPrint()) {
				exceptionErrors(req, resp, e.getMessage());
			}
			throw new ServletException(e);
		}
	}

	private void send(HttpServletRequest req, HttpServletResponse resp, Mod3902018 mod390, byte[] content) throws JSONException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ScriptException {
		String urlParameters = isCert() 
			? getCertUrlParameters(mod390, getEncodedFile(content), getName(), getDocument())
			: getUrlParameters(mod390, getEncodedFile(content));

		String request = getUrl(mod390.getYear());	
		
		send(req, resp, request, urlParameters, false);
	}
	
	public String getUrl(Integer year) {
		if(isCert()) 
			return isTest() 
				? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica" 
				: "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica";
		else if(year >= 2017)
			return "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
		return "";
	}
	
	public String getUrlParameters(Mod3902018 mod390, String encodedFile){
		if (mod390.getYear() == 2018) {
			return "&IDI=ES"
				+ "&FIC=" + encodedFile 
				+ "&RUT=" 
				+ "&PRG=T"
				+ "&FIN=" 
				+ "&EJF=2018"
				+ "&MOD=390" 
				;
		}
		return "";
	}
	
	public String getCertUrlParameters(Mod3902018 mod390, String encodedFile, String name, String document){
		return "&FIRNIF=" + document
			+ "&FIRNOMBRE=" + name
			+ "&IDI=ES" 
			+ "&F01=" + encodedFile
			+ "&FIR=" + "FirmaBasica"
			;
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
		Mod3902018 mod390 = FISCAL.getMod3902018(getDomainName(), getDomainId(), getUser(), getId());
		FISCAL.changeStatusMod3902018(getDomainName(), getUser(), mod390, FiscalStatus.SENT);
	}
}
