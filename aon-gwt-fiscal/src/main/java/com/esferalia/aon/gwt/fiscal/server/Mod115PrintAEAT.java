package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer;

@WebServlet(name = "Mod115 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model115PrintAEAT" })
public class Mod115PrintAEAT extends HttpServlet {

	private static final long serialVersionUID = 2116042075371392118L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			ModPrintAEAT print = new ModPrintAEAT(req);
			Mod115 mod115 = FISCAL.getMod115(print.getDomainName(), print.getDomainId(), print.getUser(),print.getId());
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod115Writer.fillWriter(mod115, writer);
			
			String fileName = AonFiscalFileUtils.getFileName(mod115); 
			downloadPDF(req, resp, mod115, fileName, output.toByteArray(), print);		
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}
	
	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			Mod115 mod115,String fileName, byte[] content, ModPrintAEAT print) throws JSONException {
		try {
			String urlParameters = print.isCert() 
				? getCertUrlParameters(mod115, print.getEncodedFile(content), print.getName(), print.getDocument())
				: getUrlParameters(mod115, print.getEncodedFile(content));
	
			String request = print.isCert() 
				? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
				// REAL "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
				: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
		
			JSONObject json = print.send(req, request, urlParameters);
			json = saveHistory(mod115, json, print);
			print.giveBack(req, resp, json, new JSONObject());
		}catch (Exception e) {
			e.printStackTrace();
			JSONObject json =  new JSONObject();
			if("keystore password was incorrect".equals(e.getMessage())) {
				json.put("E00", "La contraseña del certificado es incorrecta");
			} else {
				json.put("E00", "Ha ocurrido un error inesperado");
			}
			print.giveBack(req, resp, json, new JSONObject());
		}	
	}
	
	private JSONObject saveHistory(Mod115 mod115, JSONObject json, ModPrintAEAT print) throws IOException, JSONException {
		Boolean ok = json.opt("CEL")!= null;
		json = print.saveHistory(DataResponseSource.MOD115, DataAttachSource.MOD115, json);
		if(print.isCert() && ok) {
			mod115.setNumber(json.getString("JUS"));
			FISCAL.markAsSent(print.getDomainName(), print.getUser(), mod115);
		}
		return json;
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
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&TIA=" + mod115.getDeclarationType().getValue()
				+ "&NDC=" + mod115.getDocument()
				+ "&NRC=" + "" // Número de Referencia Completo (NRC) para el tipo I, en resto de tipos vacío. 
				+ "&ING=" + "" // Importe ingresado correspondiente al NRC para el tipo I,  en resto de tipos vacío
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
}
