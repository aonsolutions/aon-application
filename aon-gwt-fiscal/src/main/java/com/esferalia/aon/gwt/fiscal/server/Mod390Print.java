package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod390;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390 Print", urlPatterns = { "/aon_gwt_fiscal/Model390Print" })
public class Mod390Print extends HttpServlet {
	// "IVAXML.sh"
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			int id = Integer.parseInt(req.getParameter("mod390"));
			Mod390 mod390 = SQLMod390.getById(id, conn);
			String content = SQLMod390.getXMLContentById(id, conn);
			commit(conn);

			String s = mod390.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod390" + "_" + mod390.getYear() + "_"
					+ sb.toString();

			downloadPDF(req, resp, fileName, content.getBytes());

		} catch (AonSQLException e) {
			rollback(conn);
			throw new ServletException(e);
		} catch (Throwable e) {
			rollback(conn);
			e.printStackTrace();
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			String fileName, byte[] content) throws IOException,
			KeyManagementException, NoSuchAlgorithmException {

		FileReader fis = new FileReader("/tmp/Mod390_2013_MARIALUISA.390");
		byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
		String encodedFile0 = URLEncoder.encode(new String(o), "ISO-8859-1");

		String fileString = new String(content);
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = "HID=INF3390A" + "&IDI=ES"
				+ "&LEV=000000000000"
				+
				// TODO MAL!!
				"&F01=" + encodedFile0 + "&ANA=INE" + "&XFI=" + encodedFile
				+ "&FIN=" + "&MOD=390" + "&PRG=PTLINK1N" + "&EJF=2013";

		// PRODUCCION
		String request = "https://www2.agenciatributaria.gob.es/es13/l/zi21zilk0021";

		// PRUEBAS String request = "https://www6.aeat.es/es13/l/zi21zilk0021";

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

		// resp.setContentType(MimeType.MIME_PDF.getName()); //
		resp.setHeader("Content-disposition", "attachment; filename=\""
				+ fileName + ".pdf\";");
		IOUtils.copy(input, resp.getOutputStream());
		resp.flushBuffer();
		connection.disconnect();
	}

	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
