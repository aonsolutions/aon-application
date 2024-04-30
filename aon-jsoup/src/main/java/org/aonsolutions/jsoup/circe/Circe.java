package org.aonsolutions.jsoup.circe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.aonsolutions.jsoup.Utils;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;

public class Circe {

	private static final String CIRCE_URL = "https://paeelectronico.circe.es/Account/LoginVirtual?id=10240";

	public static void main(String[] args) throws Exception {
		String password = "Alma1981";
		FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12");

		firstForm(certificateIs, password);
	}

	public static Document firstForm(InputStream certificateIs, String password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		File jksFile = Utils.setSSLCertificate(certificateIs, password);

		Document document = Jsoup.connect(CIRCE_URL).timeout(5000).ignoreHttpErrors(true).followRedirects(true)
				.execute().parse();

		FormElement sending = (FormElement) document.getElementById("sending");

		sending.elements().add(document.createElement("input").attr("type", "hidden").attr("name", "send").val(""));
				
		List<KeyVal> formData = sending.formData();
		for (KeyVal data : formData) {
			System.out.println(data);

		}

		document = sending.submit().timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute().parse();

		System.out.println(document);

		return document;
	}
}
