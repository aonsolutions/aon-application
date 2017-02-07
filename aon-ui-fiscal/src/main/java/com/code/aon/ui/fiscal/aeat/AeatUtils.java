package com.code.aon.ui.fiscal.aeat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

import com.code.aon.common.AonException;
import com.code.aon.fiscal.enumeration.Period;

public class AeatUtils {
	
	public static synchronized void printMod347(int year,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String urlParameters =
					"HID=INV4347A" 
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&RUT="
					+ "&PRG=PTLINK6F"
					+ "&FIN="
					+ "&EJF="+year
					+ "&MOD=347";
			String location= "https://www6.aeat.es/es13/l/zi22zilk0022";

			URL url = new URL(location);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
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

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}

	public static synchronized void printMod111(int year, Period period,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String urlParameters =
					"HID=IE1111VA" 
					+ "&IDI=ES"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
//					+ "&RUT="
					+ "&PRG=EWLINKQN"
					+ "&FIN=F"
					+ "&EJF="+year
					+ "&MOD=111";
			// String location= "https://www2.agenciatributaria.gob.es/es13/l/zi22zilk0022";
			String location= "https://www6.aeat.es/es13/l/zi22zilk0022 ";
			URL url = new URL(location);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
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

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}

	public static synchronized void printMod115(int year, Period period,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String urlParameters =
					"HID=INV5115A" 
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&RUT="
					+ "&PRG=PTLINK6F"
					+ "&FIN="
					+ "&EJF="+year
					+ "&MOD=115";
			String location= "https://www6.aeat.es/es13/l/zi22zilk0022";

			URL url = new URL(location);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
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

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}

	public static synchronized void printMod123(int year, Period period,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String urlParameters =
					"HID=IE51230A" 
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&RUT="
					+ "&PRG="
					+ "&FIN="
					+ "&EJF="+year
					+ "&MOD=123";
			String location= "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";

			URL url = new URL(location);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
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

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}
	
	public static synchronized void printMod130(int year, Period period,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String urlParameters =
					"HID=IE51300A" 
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&RUT="
					+ "&PRG="
					+ "&FIN="
					+ "&EJF="+year
					+ "&MOD=130";
			String location= "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";

			URL url = new URL(location);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
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

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}
	
	public static synchronized void printMod131(int year, Period period,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String urlParameters =
					"HID=IE51310A" 
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&RUT="
					+ "&PRG="
					+ "&FIN="
					+ "&EJF="+year
					+ "&MOD=131";
//			String location= "https://www6.aeat.es/es13/l/zi22zilk0022";
			String location="https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";

			URL url = new URL(location);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
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

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}

	public static synchronized void printMod303(int year, Period period,InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			String hid = "";
			if (year == 2017) {
				hid = "IE73030A"; 	
			} else if (year == 2016){
				hid = "INV5303A";
			} else if (year < 2016) {
				hid = "INV4303A";
			}
			
			String prg = "";
			if (year == 2017) {
				hid = ""; 	
			} else if (year == 2016){
				prg = "PTLINK6F";
			} else if (year < 2016) {
				prg = "PTLINK1T";
			}
			
			String urlParameters =
					"HID="+hid
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&FIC="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&RUT="
					+ "&PRG="+prg
					+ "&FIN="
					+ "&EJF="+year
					+ "&MOD=303";
			String location= "";
			if (year == 2017) {
				location= "https://www6.aeat.es/wlpl/PFTW-PICW/ServVali";
			} else {
				location= "https://www6.aeat.es/es13/l/zi22zilk0022";
			}
			URL url = new URL(location);

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
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "ISO-8859-1");
			connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (NoSuchAlgorithmException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (KeyManagementException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
	}
	
	public static enum Mod303Type {
		I, // A ingresar
		C, // A compensar
		D, // A devolver
		N  // Resultado cero / Sin actividad
	}
	
	public static synchronized void sendMod303(Integer year, Period period,
			Mod303Type type, String deponentDocument,
			InputStream input, OutputStream output) throws AonException {
		try {
			InputStreamReader fis = new InputStreamReader(input,"ISO-8859-1");
			byte[] o = IOUtils.toByteArray(fis, "ISO-8859-1");
			String fileString = new String(o);
			fileString = fileString.replace("\n", "");
			fileString = fileString.replace("\r", "");
			
			// TODO
			String urlParameters =
					"HID=IE43030B"
					+ "&TIA="+type
					+ "&NDC="+deponentDocument
					+ "&NRC="
					+ "&ING="
					+ "&NRR="
					+ "&ICO="
					+ "&NR1="
					+ "&IN1="
					+ "&NR2="
					+ "&IN2="
					+ "&NR3="
					+ "&IN3="
					+ "&NR4="
					+ "&IN4="
					+ "&NR5="
					+ "&IN5="
					+ "&NR6="
					+ "&IN6="
					+ "&NR7="
					+ "&IN7="
					+ "&CMN="
					+ "&LOT=0"
					+ "&IDI=ES"
					+ "&LEV=000000000000"
					+ "&F01="+URLEncoder.encode(fileString, "ISO-8859-1")
					+ "&PUN=00000000"
					+ "&TXT="
					+ "&FIR="
					+ "&FIN=F"
					+ "&EJF="+year
					+ "&MOD=303"
					+ "&PRG=EWLINKZU";
			//String location= "https://www2.agenciatributaria.gob.es/es13/l/zi21zilk0021";
			String location= "https://www6.aeat.es/es13/l/zi21zilk0021";

			URL url = new URL(location);

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
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "ISO-8859-1");
			connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			DataInputStream in = new DataInputStream(connection.getInputStream());
			IOUtils.copy(in, output);
			output.flush();
			connection.disconnect();
			output.flush();
		} catch (FileNotFoundException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (UnsupportedEncodingException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (IOException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (NoSuchAlgorithmException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		} catch (KeyManagementException e) {
			throw new AonException("No se pudo realizar la impresión. (" + e.getMessage() + ")",e);
		}
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
