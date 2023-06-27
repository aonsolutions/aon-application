package com.esferalia.aon.gwt.mod200.server.e2013;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@WebServlet(name = "Mod200 - 2013 Print", urlPatterns = { "/aon_gwt_mod200/Model2002013Print" })
public class Mod2002013Print extends HttpServlet {

	private static final long serialVersionUID = 6413086920045017220L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		try {
//			int id = Integer.parseInt(req.getParameter("modId"));
//			String domainName = req.getParameter("domainName");
//			int domainId = Integer.parseInt(req.getParameter("domainId"));
//			Mod2002013 mod200 = AON.getMod2002013ById(domainName,domainId,id);
//			MOD2002013Writer writer = new MOD2002013Writer();
//			FileOutput fileoutput = writer.createMOD200(mod200);
//			String s = mod200.getEnterpriseName();
//			StringBuilder sb = new StringBuilder();
//			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
//				sb.append("_");
//			}
//			for (char c : s.toCharArray()) {
//				if (Character.isJavaIdentifierPart(c)) {
//					sb.append(c);
//				}
//			}
//			String fileName = "Mod200" + "_" + mod200.getYear() + "_" + sb.toString();
//			downloadPDF(req, resp, fileName, fileoutput.getContent());
//		} catch (Throwable e) {
//			throw new ServletException(e);
//		}
//
	}
/*
	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			String fileName, byte[] content) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String fileString = new String(content);
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = "HID=SOV3200A"  
				+"&IDI=ES" 
				+"&LEV=000000000000"
				+"&FIC=" + encodedFile  
				+"&RUT="  
				+"&PRG=PTLINK3X"  
				+"&FIN="  
				+"&EJF=2013"  
				+"&MOD=200";
		
		String request = "https://www2.agenciatributaria.gob.es/l/zi22zilk0022";

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
		
//		resp.setContentType(MimeType.MIME_PDF.getName());
//		resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
		AonIOUtils.copy(input, resp.getOutputStream());
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
*/
}
