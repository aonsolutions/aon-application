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
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.server.fiscal.format.Mod190Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod190 Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model190Print" })
public class Mod190Print extends HttpServlet {

	private static final long serialVersionUID = -898176379168968783L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod190"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Mod190 mod190 = FISCAL.getMod190(domainName, domainId, user,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod190Writer.fillWriter(mod190, writer);
			
			String s = mod190.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod190" + "_" + mod190.getYear() + "_"
					+ sb.toString();

			downloadPDF(req, resp, fileName, output.toByteArray(), mod190);

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			String fileName, byte[] content, Mod190 mod190) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String fileString = new String(content, "ISO-8859-1");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = "";
		String request = "";
		
		if (mod190.getYear() < 2016) {
			urlParameters = 
					"HID=INV5190A" + 
					"&IDI=ES" + 
					"&FIC="	+ encodedFile + 
					"&RUT=" + 
					"&PRG=" + 
					"&FIN=" + 
					"&EJF=2015" + 
					"&MOD=190";
			request = "https://www2.agenciatributaria.gob.es/l/zi22zilk0022";
		} if (mod190.getYear() == 2016) {
			urlParameters = 
					"HID=INV6190A" + 
					"&IDI=ES" +
					"&LEV=000000000000" +
					"&FIC="	+ encodedFile + 
					"&RUT=" + 
					"&PRG=PTLINKG2" + 
					"&FIN=" + 
					"&EJF=2016" + 
					"&MOD=190";
//			request = "https://www2.agenciatributaria.gob.es/l/zi22zilk0022";
			request = "https://www6.aeat.es/es13/l/zi22zilk0022";
		} else {
			urlParameters = 
					"HID=IE7190AA" + 
					"&IDI=ES" +
					"&LEV=000000000000" +
					"&FIC="	+ encodedFile + 
					"&RUT=" + 
					"&PRG=" + 
					"&FIN=" + 
					"&EJF=2017" + 
					"&MOD=190";
			request = "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
//			request = "https://www2.agenciatributaria.gob.es/wlpl/OVCT-IPDF/ovweb/vistaprevia";		
		}

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
