package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
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
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.server.fiscal.format.Mod3902015Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390 2015 Print", urlPatterns = { "/aon_gwt_fiscal/Model3902015Print" })
public class Mod3902015Print extends HttpServlet {
	// "IVAXML.sh"
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod390"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Mod3902015 mod390 = FISCAL.getMod3902015(domainName, domainId, AonServletUtils.getLoggedUser(), id);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod3902015Writer.fillWriter(mod390, writer);
			
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

			String fileName = "Mod390" + "_" + mod390.getYear() + "_" + sb.toString();

			if(req.getParameter("cert") != null && !req.getParameter("cert").isEmpty()) {
				Integer cert = Integer.parseInt(req.getParameter("cert"));
				Attach attach = AON.getAttach(domainName, domainId, user, f -> f.getDomainProperty().eq(domainId)
						.and(f.getIdProperty().eq(cert))
						.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())), AttachType.REGISTRY, true);
				if(attach.getData() == null){
					DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, user);
					Drive drive = AonDrive.getInstace().serviceInitialize(g);
					attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
				}
				String pass = req.getParameter("pass");
				String name = req.getParameter("name");
				String document = req.getParameter("document");
				downloadPDF(req, resp, mod390, fileName, output.toByteArray(), attach.getData(), pass, name, document);
			} else downloadPDF(req, resp, fileName, output.toByteArray(), mod390.getYear());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			String fileName, byte[] content, int year) throws IOException,
			KeyManagementException, NoSuchAlgorithmException, ServletException {
		String fileString = new String(content, "ISO-8859-1");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = null;
		String request = null;
		if (year == 2015) {
			request = "https://www6.aeat.es/es13/l/zi21zilk0021";
			urlParameters = "HID=INF5390A" 
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+ "&F01=" + encodedFile 
				+ "&ANA=" + "CAP" 
				+ "&FIN=" 
				+ "&MOD=390" 
				+ "&PRG=PTLINK9T"
				+ "&EJF=2015";
		} else if (year == 2016) {
			request = "https://www6.aeat.es/es13/l/zi21zilk0021";
			urlParameters = "HID=INF6390A" 
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+ "&F01=" + encodedFile 
				+ "&ANA=" + "CAP" 
				+ "&FIN=" 
				+ "&MOD=390" 
				+ "&PRG=PTLINKF3"
				+ "&EJF=2016";
		} else if (year == 2017) {
			request = "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
			urlParameters = "HID=INF7390A" 
				+ "&IDI=ES"
				+ "&LEV=000000000000"
				+ "&FIC=" + encodedFile 
				+ "&RUT="  
				+ "&PRG=PTLINKN3"
				+ "&FIN=" 
				+ "&EJF=2017"
				+ "&MOD=390";
		} else {
			throw new ServletException("Ejercicio no soportado.");
		}
		
		// PRODUCCION String request = "https://www2.agenciatributaria.gob.es/es13/l/zi21zilk0021" PRUEBAS 
		// String request = "https://www2.agenciatributaria.gob.es/es13/l/zi21zilk0021";

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

		// resp.setContentType(MimeType.MIME_PDF.getName()); //
		// resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\";");
		AonIOUtils.copy(input, resp.getOutputStream());
		resp.flushBuffer();
		connection.disconnect();
	}

	private void downloadPDF(HttpServletRequest req, HttpServletResponse resp,
			Mod390 mod390,String fileName, byte[] content, byte[] cert,
			String pass, String name, String document) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
		String fileString = new String(content, "ISO-8859-1");
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", " ");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = "HID=INF7390A"
				+ "&FIRNIF=" + name
				+ "&FIRNOMBRE=" + document
				+ "&SOP=" + "" // TODO Vacío o código de entidad de la EEFF (4 caracteres). Nota: este código es el que se utiliza en las estadísticas, por lo que es importante que sea correcto.
				+ "&NAV=" + ""
				+ "&NDC=" + mod390.getDocument()
				+ "&NAP=" + mod390.getName()
				+ "&NOM=" + "" // TODO Nombre del sujeto pasivo
				+ "&TEL=" + ""
				+ "&ADM=" + ""
				+ "&EJF=" + mod390.getYear()
				+ "&P01=" + "" // TODO Vacío o resultado de la liquidación [86]
				+ "&P01=" + "" // TODO Vacío o resultado de la liquidación anual atribuible al territorio común [94].
				+ "&TXT=" + ""
				+ "&FIR=" + "FirmaBasica"
				+ "&CRL=" + "|"
				+ "&LEV=" + "000000000000"
				+ "&F01=" + encodedFile
				+ "&PUN=" + "00000000"
				+ "&CMN=" + ""
				+ "&FIN=" + "F";
		
		// Validacion e impresion
		// PRUEBAS
		String request = "https://www6.aeat.es/wlpl/PFTW-PICW/PresBasica";	
		
		// REAL
		//String request = "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/resBasica";

		URL url = new URL(request);
		 
		ByteArrayInputStream key = new ByteArrayInputStream(cert);
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
	    keyStore.load(key, pass.toCharArray());
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   		kmf.init(keyStore, pass.toCharArray());
   		
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(kmf.getKeyManagers(),
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
