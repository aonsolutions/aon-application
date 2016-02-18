package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod111.Mod111Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod111 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/Model111PrintAEAT" })
public class Mod111PrintAEAT extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod111"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod111 mod111 = AON.getMod111(domainName, domainId, user,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod111Writer.fillWriter(mod111, writer);
			
			String fileName = AonFiscalFileUtils.getFileName(mod111); 
			downloadPDF(req, resp, mod111, fileName, output.toByteArray());

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			Mod111 mod111,String fileName, byte[] content) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String fileString = new String(content);
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", " ");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");
		
		String urlParameters = "HID=IE6111VA" 
				+"&IDI=ES"
				+"&FIC=" + encodedFile
				//+"&PRG=PTLINK9Y"
				+"&PRG="
				+"&FIN=F" 
				+"&EJF=" + mod111.getYear() 
				+"&MOD=111";
		// Validacion e impresion
		String request = "https://www2.agenciatributaria.gob.es/es13/l/zi21zilk0022";
//		String request = "https://www6.aeat.es/es13/l/zi22zilk0022";
				
//		String urlParameters = "HID=IE61110B" 
//				+"&TIA" + mod111.getDeclarationType()
//				+"&NDC=" + mod111.getDocument()
//				+"&NRC=" 
//				+"&ING=" + mod111.getResult()
//				+"&NRR="
//				+"&ICO="
//				+"&NR1="
//				+"&IN1=" 
//				+"&NR2="
//				+"&IN2="
//				+"&NR3="
//				+"&IN3=" 
//				+"&NR4="
//				+"&IN4="
//				+"&NR5="
//				+"&IN5="
//				+"&NR6="
//				+"&IN6="
//				+"&NR7="
//				+"&IN7="
//				+"&CMN="
//				+"&LOT=0"
//				+"&IDI=ES"
//				+"&LEV=000000000000"
//				+"&F01=" + encodedFile
//				+"&PUN=00000000"
//				+"&TXT="
//				+"&FIR="
//				+"&FIN=F" 
//				+"&EJF=" + mod111.getYear() 
//				+"&MOD=111"
//				+"&PRG=PTLINK9X";
//		
//		// Predeclaracion
//		String request = "https://www2.agenciatributaria.gob.es/es13/l/zi21zilk0021";

		URL url = new URL(request);

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0],
				new TrustManager[] { new DefaultTrustManager() },
				new SecureRandom());
		SSLContext.setDefault(ctx);

		HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();
		connection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "ISO-8859-1");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		DataInputStream input = new DataInputStream(connection.getInputStream());
		
		AonIOUtils.copy(input, resp.getOutputStream());
		resp.flushBuffer();
		connection.disconnect();
	}
	
}
