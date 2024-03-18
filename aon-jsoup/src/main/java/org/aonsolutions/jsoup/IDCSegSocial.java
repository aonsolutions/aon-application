package org.aonsolutions.jsoup;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

public class IDCSegSocial {

	private static final String IDC_URL = "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR";
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MM yyyy");

	public static void main(String[] args) throws Exception {

		String password = "Alma1981";
		try (FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12")) {
			getIDCDates(certificateIs, password, "011005185924", "0111", "01105360062");
		}
	}

	/**
	 * Gets the list of IDC's dates.
	 * 
	 * @param certificateIs
	 * @param password
	 * @param naf
	 * @param regime
	 * @param ccc
	 * @return IDC Dates
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static Collection<Date> getIDCDates(InputStream certificateIs, String password, String naf, String regime,
			String ccc)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {

		File jksFile = setSSLCertificate(certificateIs, password);

		//
		// Same than code below, choose one or another it's about personal
		//
		// Connection connection = Jsoup.connect(IDC_URL);
		// connection.timeout(5000);
		// connection.ignoreHttpErrors(true);
		// connection.followRedirects(true);
		// Connection.Response response = connection.execute();

		Document document = Jsoup.connect(IDC_URL).timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute()
				.parse();

		FormElement jacadaForm = (FormElement) document.getElementById("jacadaform");

		jacadaForm.getElementById("SDFTESNAF").val(naf.substring(0, 2));
		jacadaForm.getElementById("SDFNAF").val(naf.substring(2));

		jacadaForm.getElementById("SDFTESCTA").val(ccc.substring(0, 2));
		jacadaForm.getElementById("SDFCUENTA").val(ccc.substring(2));

		jacadaForm.getElementById("SDFREGCTA").val(regime);

		//
		// For <select> 'ListaTipoImpresion'. We need
		// 1-. Select 'OnLine' option
		// 2-. Set it's value to 'Online'.
		//
		// <select id='ListaTipoImpresion' ...>
		// <option val=""></option>
		// <option selected="true" value="Online" >OnLine</option>
		// <option value="Diferido">Diferido</option>
		// </select>
		//
		jacadaForm.getElementById("ListaTipoImpresion").selectXpath("//option").forEach(option -> {
			option.val(option.text());
			option.attr("selected", "OnLine".equalsIgnoreCase(option.text()));
		});

		jacadaForm.getElementById("Ayuda").remove();
		jacadaForm.getElementById("Sub2205901006").remove();


		document = jacadaForm.submit().timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute().parse();


		// Here you need to extract dates form table.
		// Not all dates, only start dates ( first column of dates ).

		Element Sub0900112078 = document.getElementById("Sub0900112078");

		Collection<Element> cells = Sub0900112078.select(new Evaluator() {

			@Override
			public boolean matches(Element root, Element element) {

				return element.attr("id").startsWith("Sub0900112078_1_") && element.hasText();
			}
		});
		

		ArrayList<Date> dates = new ArrayList<>();

		for (Element cell : cells) {
			Date date = FORMATTER.parse(cell.text());
			dates.add(date);
		}

		Collections.sort(dates);

		return dates;
	}

	public static File setSSLCertificate(InputStream is, String password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return setSSLCertificate(is, password, KeyStore.getDefaultType());
	}

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

}
