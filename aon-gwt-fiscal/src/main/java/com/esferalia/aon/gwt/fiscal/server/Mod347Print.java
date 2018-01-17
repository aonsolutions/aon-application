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
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod347Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod347 Print", urlPatterns = { "/aon_gwt_fiscal/Model347Print" })
public class Mod347Print extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod347"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod347 mod347 = FISCAL.getMod347(domainName, domainId,AonServletUtils.getLoggedUser(), id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod347Writer.fillWriter(mod347, writer);
			byte[] content = output.toByteArray();
			
			String s = mod347.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = AonFiscalFileUtils.getFileName(mod347); 

			downloadPDF(req, resp, fileName, content, Integer.toString(mod347.getYear()));

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp, 
			String fileName, byte[] content, String year) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		String fileString = new String(content);
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		// Solo a partir del 2014
		String prg = "";
		if (year == "2014")
			prg = "PTLINK5L";
		else if (year == "2015")
			prg = "PTLINK9V";
		else if (year == "2016")
			prg = "PTLINKG2";
		
		String urlParameters = 
				"HID=IE"+year.substring(3)+"347A" +  // Cambia para cada ejercicio
				"&IDI=ES" +
				"&LEV=000000000000" +						
				"&FIC="	+ encodedFile + 
				"&RUT=" + 
				"&PRG=" + prg +
				"&FIN=" + 
				"&EJF=" + year +
				"&MOD=347";
		
		String request = "";
		if (year == "2014" || year == "2015")
			request = "https://www6.aeat.es/es13/l/zi22zilk0022";
		else request = "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
		
		URL url = new URL(request);

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0],
				new TrustManager[] { new DefaultTrustManager() },
				new SecureRandom());
		SSLContext.setDefault(ctx);

		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "ISO-8859-1");
		connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
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
