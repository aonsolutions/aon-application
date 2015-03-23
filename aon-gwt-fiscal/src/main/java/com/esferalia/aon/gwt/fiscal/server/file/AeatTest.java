package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AeatTest {
	private static String MODEL = "11802012B95528451RAMON HERRERA ZAPATOS, S.L       "
			+ "       D941123123MIGUEL ANGEL PANCEIRA            "
			+ "       0000000000000  0000000000000000000001 00000"
			+ "0002475000000000000519750                         "
			+ "                                                  "
			+ "21802012B95528451B48553465         ANAYER, S.L    "
			+ "                         484 000000247500021000000"
			+ "0005197500000                                     "
			+ "                                                  "
			+ "                                                  ";

	public static void main1(String[] args) throws IOException,
			NoSuchAlgorithmException, KeyManagementException {
		testPrint();
	}

	public static void testPrint() throws IOException,
			NoSuchAlgorithmException, KeyManagementException {

		String fileString = AonStringUtils.chomp(MODEL);
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = "HID=INV3180A" + "&IDI=ES" + "&FIC="
				+ encodedFile + "&RUT=" + "&PRG=" + "&FIN=" + "&EJF=2013"
				+ "&MOD=180";
		System.out.println(urlParameters);
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
		FileOutputStream out = new FileOutputStream(
				"/home/ecastellano/AEAT/output.pdf");

		IOUtils.copy(input, out);

		connection.disconnect();
	}

	public static void testSend() throws IOException,
			NoSuchAlgorithmException, KeyManagementException {

		String fileString = AonStringUtils.chomp(MODEL);
		String encodedFile = URLEncoder.encode(fileString, "ISO-8859-1");

		String urlParameters = "HID=INV3180A" + "&IDI=ES" + "&FIC="
				+ encodedFile + "&RUT=" + "&PRG=" + "&FIN=" + "&EJF=2013"
				+ "&MOD=180";
		System.out.println(urlParameters);
		String request = "https://www7.aeat.es/es13/l/ewzcewlinkzc ";

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
		FileOutputStream out = new FileOutputStream(
				"/home/ecastellano/AEAT/output.pdf");

		IOUtils.copy(input, out);

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
