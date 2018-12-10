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

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.server.fiscal.format.Mod184Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "Mod184 Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model184Print" })
public class Mod184Print extends HttpServlet {

	private static final long serialVersionUID = -391231488847437476L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod184"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod184 mod184 = FISCAL.getMod184(domainName, domainId, user, id);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod184Writer.fillWriter(mod184, writer);

			String s = mod184.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod184" + "_" + mod184.getYear() + "_"
					+ sb.toString();

			downloadPDF(req, resp, fileName, output.toByteArray(),
					Integer.toString(mod184.getYear()));

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp, 
			String fileName, byte[] content, String year) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String fileString = new String(content, "ISO-8859-1");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		int y = AonNumberUtils.toint(year);
		String urlParameters = null;
		if (y < 2018) {
			urlParameters = 
				"HID=IE7184A" + 
				"&IDI=ES" + 
				"&LEV=000000000000" +
				"&FIC="	+ encodedFile + 
				"&RUT=" + 
				"&PRG=" + 
				"&FIN=" + 
				"&EJF=" + year +
				"&MOD=184";
		} else {
			urlParameters = 
				"&IDI=ES" +
				"&LEV=000000000000" +
				"&FIC="	+ encodedFile + 
				"&RUT=" + 
				"&PRG=" + 
				"&FIN=" + 
				"&EJF=" + year +
				"&MOD=184";
		}
		
//		String request = "https://www2.agenciatributaria.gob.es/l/zi22zilk0022";
//		String request = "https://www6.aeat.es/es13/l/zi22zilk0022";
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
