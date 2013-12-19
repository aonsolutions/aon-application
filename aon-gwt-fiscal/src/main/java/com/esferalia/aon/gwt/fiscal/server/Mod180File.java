package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.fiscal.server.AonServletUtils.rollback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.gwt.fiscal.server.file.MOD180Writer;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod180;
import com.esferalia.aon.gwt.fiscal.sql.SQLUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod180 File download", urlPatterns = { "/aon_gwt_fiscal/Model180File" })
public class Mod180File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			MOD180Writer writer = new MOD180Writer();
			int id = Integer.parseInt(req.getParameter("mod180"));
			Mod180 mod180 = SQLMod180.getById(id, conn);

			FileOutput fileoutput = writer.createMOD180(conn, id,
					mod180.getYear(), mod180.getAdministration());
			commit(conn);

			String s = mod180.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod180" + "_" + mod180.getYear() + "_"
					+ sb.toString();

			downloadPDF(req, resp, fileName, fileoutput.getContent());
			// ByteArrayInputStream in = new
			// ByteArrayInputStream(fileoutput.getContent());
			// resp.setContentType(MimeType.MIME_TXT.getName() +
			// "; charset=ISO-8859-1");
			// resp.setHeader("Content-disposition", "attachment; filename=\"" +
			// fileName + ".txt\";");
			// IOUtils.copy(in, resp.getOutputStream());
			// resp.flushBuffer();

		} catch (AonSQLException e) {
			rollback(conn);
			throw new ServletException(e);
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}

	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			String fileName, byte[] content) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String fileString = new String(content);
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = 
				"HID=INV3180A" + 
				"&IDI=ES" + 
				"&FIC="	+ encodedFile + 
				"&RUT=" + 
				"&PRG=" + 
				"&FIN=" + 
				"&EJF=2013" + 
				"&MOD=180";
		String request = "https://www6.aeat.es/l/zi22zilk0022";

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
		
		resp.setContentType(MimeType.MIME_PDF.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
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
