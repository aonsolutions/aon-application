package com.esferalia.aon.gwt.fiscal.server.fiscal.mod349;

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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.DefaultTrustManager;
import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod349Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod349 Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model349Print" })
public class Mod349Print extends HttpServlet {

	private static final long serialVersionUID = -5574018042948328985L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod349"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod349 mod349 = MODEL349.get(occam,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod349Writer.fillWriter(mod349, writer);
			byte[] content = output.toByteArray();
			
			String s = mod349.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = AonFiscalFileUtils.getFileName(mod349); 

			downloadPDF(req, resp, fileName, content, Integer.toString(mod349.getYear()));

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

		// El servicio de validacion se añadió en el ejercicio 2014
		String prg = "";
		String request = "https://www6.aeat.es/es13/l/zi22zilk0022";
		if (year == "2014")
			prg = "PTLINK1S";
		else if (year == "2015")
			prg = "PTLINK5L";
		else if (year == "2016")
			prg = "PTLINK9V";
		else if (year == "2017")
			prg = "PTLINKG2";
		else {
			// A partir de 2018 prg es vacio y cambia la url de llamada
			request = "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
		}
		
		String urlParameters = 
				"HID=INV"+year.substring(3)+"349"+ year=="2014"?"":"A" +  // Cambia para cada ejercicio
				"&IDI=ES" +
				"&LEV=000000000000" +						
				"&FIC="	+ encodedFile + 
				"&RUT=" + 
				"&PRG=" + prg + // Cambia para cada ejercicio
				"&FIN=" + 
				"&EJF=" + year +
				"&MOD=349";
		
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
