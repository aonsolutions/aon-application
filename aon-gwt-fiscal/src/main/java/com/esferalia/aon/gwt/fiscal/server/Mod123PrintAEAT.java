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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer;

@WebServlet(name = "Mod123 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Model123PrintAEAT" })
public class Mod123PrintAEAT extends HttpServlet {

	private static final long serialVersionUID = -1855166921054314939L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			ModPrintAEAT print = new ModPrintAEAT(req);
			Mod123 mod123 = FISCAL.getMod123(print.getDomainName(), print.getDomainId(), print.getUser(), print.getId());

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod123Writer.fillWriter(mod123, writer);
			
			String fileName = AonFiscalFileUtils.getFileName(mod123); 
			downloadPDF(req, resp, mod123, fileName, output.toByteArray(), print);
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			Mod123 mod123,String fileName, byte[] content, ModPrintAEAT print) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException {
		String urlParameters = print.isCert()
				? getCertUrlParameters(mod123, print.getEncodedFile(content), print.getName(), print.getDocument())
				: getUrlParameters(mod123, print.getEncodedFile(content));
		
		String request = print.isCert() 
				? "https://www7.aeat.es/wlpl/PFTW-PICW/PresBasica"
				// REAL "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica"
				: "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";	
		print.download(resp, request, urlParameters);
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
