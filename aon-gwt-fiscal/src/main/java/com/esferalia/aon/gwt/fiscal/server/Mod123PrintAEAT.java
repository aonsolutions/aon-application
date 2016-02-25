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
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod123.Mod123Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod123 Print AEAT", urlPatterns = { "/aon_gwt_fiscal/Model123PrintAEAT" })
public class Mod123PrintAEAT extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod123"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = AonServletUtils.getLoggedUser();
			Mod123 mod123 = FISCAL.getMod123(domainName, domainId, user,id);

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
			downloadPDF(req, resp, mod123, fileName, output.toByteArray());

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			Mod123 mod123,String fileName, byte[] content) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String fileString = new String(content);
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", " ");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");
		
		String urlParameters = "HID=IE51230A" 
				+"&IDI=ES"
				+"&LEV=000000000000"
				+"&FIC=" + encodedFile
				+"&RUT="
				+"&PRG="
				+"&FIN=" 
				+"&EJF=" + mod123.getYear() 
				+"&MOD=123";
		// Validacion e impresion
		String request = "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
				
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
