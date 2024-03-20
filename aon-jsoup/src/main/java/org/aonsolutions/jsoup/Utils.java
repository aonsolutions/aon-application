package org.aonsolutions.jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static File setSSLCertificate(InputStream is, String password, String type)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		File jksFile = File.createTempFile("certificate", "jks");
		try (OutputStream os = new FileOutputStream(jksFile)) {
			KeyStore keyStore = KeyStore.getInstance(type);
			keyStore.load(is, password.toCharArray());
			keyStore.store(os, password.toCharArray());
			System.setProperty("javax.net.ssl.keyStorePassword", password);
			System.setProperty("javax.net.ssl.keyStore", jksFile.getAbsolutePath());
			return jksFile;
		}
	}

	public static Date parse(String text) {
		try {
			return Utils.FORMATTER.parse(text);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static File setSSLCertificate(InputStream is, String password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return setSSLCertificate(is, password, KeyStore.getDefaultType());
	}

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MM yyyy");

}
