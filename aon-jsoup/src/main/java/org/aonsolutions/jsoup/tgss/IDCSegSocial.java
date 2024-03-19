package org.aonsolutions.jsoup.tgss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Evaluator;

class IDCSegSocial {

	private static final String IDC_URL = "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR";

	public static void main(String[] args) throws Exception {

		String password = "Alma1981";
		try (FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12")) {
			byte[] certificateData = certificateIs.readAllBytes();
			Collection<Date> dates = SistemaRed.getIDCDates(certificateData, password, "011005185924", "0111", "01105360062",
					null);
			FileOutputStream pdfIDC = null;
			if (dates.size() > 0)
				pdfIDC = new FileOutputStream(
						"/home/ndiaz/eclipse-workspace/aon.parent/aon-jsoup/src/main/java/idc.pdf");
			pdfIDC.write(SistemaRed.getIDC(certificateData, password, "011005185924", "0111", "01105360062",
					dates.stream().findFirst().get()));
		}

	}

	public static Collection<Date> getIDCDates(InputStream certificateIs, String password, String naf, String regime,
			String ccc, Date date)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {

		Document document = IDCSegSocial.firstForm(certificateIs, password, naf, regime, ccc, null);

		Element sub0900112078 = document.getElementById("Sub0900112078");

		Collection<Element> cells = sub0900112078.select(new Evaluator() {
			@Override
			public boolean matches(Element root, Element element) {
				return element.attr("id").startsWith("Sub0900112078_1_") && element.hasText();
			}
		});

		ArrayList<Date> dates = new ArrayList<>(cells.size());

		for (Element cell : cells) {
			Date newdate = Utils.parse(cell.text());
			dates.add(newdate);
		}

		return dates;

	}

	public static byte[] getIDC(InputStream certificateIs, String password, String naf, String regime, String ccc,
			Date date)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException {

		Document document = IDCSegSocial.firstForm(certificateIs, password, naf, regime, ccc, date);

		FormElement jacadaForm = (FormElement) document.getElementById("jacadaform");

		jacadaForm.getElementById("Sub0900112078_0_0").selectXpath("option").forEach(option -> {
			option.val(option.text());
			option.attr("selected", "Select".equalsIgnoreCase(option.text()));
		});

//		jacadaForm.getElementById("Ayuda").remove();
//		jacadaForm.getElementById("Sub2205001006").remove();
//		jacadaForm.getElementsByAttributeValue("name", "sequenceNumber").remove();
//		jacadaForm.getElementsByAttributeValueStarting("name", "client").remove();
//		jacadaForm.getElementById("focusedControl").val("tbl_cbo_Sub0900112078_0_0");
//		jacadaForm.getElementById("FkeyButton").val("+");
		
		jacadaForm.getElementById("Sub2206101001").remove();
		jacadaForm.getElementById("Sub2206301003").remove();
		jacadaForm.getElementsByAttributeValueStarting("name", "Scroll").remove();
		jacadaForm.getElementsByAttributeValueStarting("name", "keep").remove();
		jacadaForm.getElementsByAttributeValueStarting("name", "btn_j").remove();
		jacadaForm.getElementsByAttributeValueStarting("name", "acc").remove();
		jacadaForm.getElementsByAttributeValue("name", "defaultbtn_null").remove();
		jacadaForm.getElementById("CommandEdit").val("EN");


		document = jacadaForm.submit().timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute().parse();

		jacadaForm = (FormElement) document.getElementById("jacadaform");

		FormElement invocaFormularioImpRED = (FormElement) document
				.getElementsByAttributeValue("name", "InvocaFormularioImpRED").getFirst();

		invocaFormularioImpRED.attr("action", "https://w2.seg-social.es/ImprPDF/InSeNaCoder");

		invocaFormularioImpRED.getElementsByAttributeValue("name", "param").first()
				.val(jacadaForm.getElementById("SDFFICHERO").val());
		invocaFormularioImpRED.getElementsByAttributeValue("name", "trans").first()
				.val(jacadaForm.getElementById("SDFINFORMEA601").val()
						+ jacadaForm.getElementById("SDFINFORMEA602").val()
						+ jacadaForm.getElementById("SDFINFORMEA603").val()
						+ jacadaForm.getElementById("SDFINFORMEA604").val());
		invocaFormularioImpRED.getElementsByAttributeValue("name", "aplicacion").first()
				.val(jacadaForm.getElementById("SDFAPLICACION").val());
		invocaFormularioImpRED.getElementsByAttributeValue("name", "usuario").first()
				.val(jacadaForm.getElementById("SDFUSUARIO").val());
		invocaFormularioImpRED.getElementsByAttributeValue("name", "idioma").first()
				.val(jacadaForm.getElementById("SDFIDIOMA").val());
		invocaFormularioImpRED.getElementsByAttributeValue("name", "fecha").first()
				.val(jacadaForm.getElementById("SDFFECHA").val() + ' ' + jacadaForm.getElementById("SDFHORA").val());
		invocaFormularioImpRED.getElementsByAttributeValue("name", "tipo").first()
				.val(jacadaForm.getElementById("SDFTIPO").val());

		Response pdf = invocaFormularioImpRED.submit().timeout(0).ignoreHttpErrors(true).followRedirects(true)
				.execute();

		return pdf.bodyAsBytes();

	}

	public static Document firstForm(InputStream certificateIs, String password, String naf, String regime, String ccc,
			Date date) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		File jksFile = Utils.setSSLCertificate(certificateIs, password);

		Document document = Jsoup.connect(IDC_URL).timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute()
				.parse();

		FormElement jacadaForm = (FormElement) document.getElementById("jacadaform");

		jacadaForm.getElementById("SDFTESNAF").val(naf.substring(0, 2));
		jacadaForm.getElementById("SDFNAF").val(naf.substring(2));

		jacadaForm.getElementById("SDFTESCTA").val(ccc.substring(0, 2));
		jacadaForm.getElementById("SDFCUENTA").val(ccc.substring(2));

		jacadaForm.getElementById("SDFREGCTA").val(regime);

		if (date != null) {
			String dateStr = Utils.FORMATTER.format(date);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH) + 1;
			int year = calendar.get(Calendar.YEAR);
		}

		jacadaForm.getElementById("ListaTipoImpresion").selectXpath("option").forEach(option -> {
			option.val(option.text());
			option.attr("selected", "OnLine".equalsIgnoreCase(option.text()));
		});

//		jacadaForm.getElementById("Ayuda").remove();
//		jacadaForm.getElementById("Sub2205901006").remove();

		document = jacadaForm.submit().timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute().parse();

		jksFile.delete();

		return document;
	}
}
