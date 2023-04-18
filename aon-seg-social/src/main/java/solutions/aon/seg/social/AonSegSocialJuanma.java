package solutions.aon.seg.social;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.SegSocialOutOfService;

import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

public class AonSegSocialJuanma extends SegSocialException {

	public static void main(String[] args) throws Exception {
		String nss = "461052988590";
		String ident = "173578385M";
		String regimen = "0111";
		String cc = "11122534302";
		String cno = "0020";
		String ccc = "78123456799";
		String certificate = "/tmp/AyudaTFNMT.p12";
		String startMonth = "05";
		String startYear = "2019";
		String endMonth = "05";
		String endYear = "2022";
		String liquidationType = "L90";
		String liquidationNumber = "12345";
		String rnt = "S";
		File file = new File(certificate);
		final InputStream certificateInputStream = new FileInputStream(file);
		final String certificatePassword = "123456";
		final String certificateType = "pkcs12";
		String authorized = "127770";

		try {
			

			
			

			AonSegSocialJuanma.onlineSettlementOptionCCC(certificateInputStream, certificatePassword, certificateType, authorized, ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType);

			AonSegSocialJuanma.OnlineSettlementOptionLiquidationNumber(certificateInputStream, certificatePassword, certificateType, authorized, liquidationNumber, rnt);

		} catch (SegSocialOutOfService e) {
			e.printStackTrace();
			e.getCause();
		}

	}

	private static void setCnoCertificateP(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nss, String ident, String regimen, String cc, String cno)
			throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR55&E=I&AP=AFIR");

			DomElement formSubmit = null;
			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("Sub2207001004_42");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFPROAFI").setValue(nss.substring(0, 2));
			form.getInputByName("txt_SDFCODAFI").setValue(nss.substring(2));

			form.getInputByName("txt_SDFTIPPFI_ayuda").setValue(ident.substring(0, 1));
			form.getInputByName("txt_SDFNUMPFI").setValue(ident.substring(1));

			form.getInputByName("txt_SDFREGAFI").setValue(regimen);

			form.getInputByName("txt_SDFTESCTACOT").setValue(cc.substring(0, 2));
			form.getInputByName("txt_SDFCTACOT").setValue(cc.substring(2));
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			System.out.println(htmlPage.asXml());

			Page pageaux = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

			String pageAux = pageaux.toString();

			htmlPage = webClient.getPage(pageAux.substring(9));
			HtmlForm form2 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("Sub2207001004_85");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}

			form2.getInputByName("txt_SDFCNOCUP_ayuda").setValue(cno);
			formSubmit.click();

			System.out.println(htmlPage.asXml());

		}

	}

	public static void setCnoCertificate(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nss, String ident, String regimen, String cc, String cno)
			throws Exception {
		try {
			AonSegSocialJuanma.setCnoCertificateP(certificateInputStream, certificatePassword, certificateType, nss,
					ident, regimen, cc, cno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//

	// Metodo para navegar en la pagina
//	public static HtmlPage onlineSettlement(final InputStream certificateInputStream, final String certificatePassword,
//			final String certificateType, String authorized) throws Exception {
//
//		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
//				certificateType)) {
//
//			webClient.getOptions().setCssEnabled(true);
//			webClient.getOptions().setDownloadImages(true);
//			webClient.setJavaScriptTimeout(10000);
//			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//
//			HtmlPage htmlPage = webClient.getPage(
//					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y600");
//			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage
//					.getElementById("autorizado" + authorized);
//			radioButton.setChecked(true);
//			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
//			HtmlRadioButtonInput radioButton2 = (HtmlRadioButtonInput) htmlPage.getElementById("idOPCION1");
//			radioButton2.setChecked(true);
//			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
//			System.out.println(htmlPage.asXml());
//
//			return htmlPage;
//		}
//
//	}

	private static void onlineSettlementOption1(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String authorized, String ccc,
			String regimen, String startMonth, String startYear, String endMonth, String endYear,
			String liquidationType) throws Exception {
		
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setDownloadImages(true);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y600");
			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage
					.getElementById("autorizado" + authorized);
			radioButton.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
			HtmlRadioButtonInput radioButton2 = (HtmlRadioButtonInput) htmlPage.getElementById("idOPCION1");
			radioButton2.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();

			DomElement formSubmit = null;
			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("SPM.ACC.ACEPTAR");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}

			HtmlForm form = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getHtmlElementById("formDatos"))
					.orElseThrow();
			// Seleccionar opcion1
			HtmlRadioButtonInput radioButton3 = (HtmlRadioButtonInput) htmlPage.getElementById("idOpcion1");
			radioButton3.setChecked(true);
			// Codigo de cuenta de cotizacion
			form.getInputByName("CCC").setValue(ccc);

			// Regimen
			HtmlSelect select = (HtmlSelect) htmlPage.getElementById("idRegimen");
			HtmlOption option = select.getOptionByValue(regimen);
			select.setSelectedAttribute(option, true);

			// Periodo de liquidacion
			// Fecha de inicio
			HtmlSelect selectMesInicio = (HtmlSelect) htmlPage.getElementById("idMesDesde");
			HtmlOption optionMesInicio = selectMesInicio.getOptionByValue(startMonth);
			selectMesInicio.setSelectedAttribute(optionMesInicio, true);
			HtmlSelect selectAnioInicio = (HtmlSelect) htmlPage.getElementById("idAnioDesde");
			HtmlOption optionAnioInicio = selectAnioInicio.getOptionByValue(startYear);
			selectAnioInicio.setSelectedAttribute(optionAnioInicio, true);
			// Fecha de fin
			HtmlSelect selectMesFin = (HtmlSelect) htmlPage.getElementById("idMesHasta");
			HtmlOption optionMesFin = selectMesFin.getOptionByValue(endMonth);
			selectMesFin.setSelectedAttribute(optionMesFin, true);
			HtmlSelect selectAnioFin = (HtmlSelect) htmlPage.getElementById("idAnioHasta");
			HtmlOption optionAnioFin = selectAnioFin.getOptionByValue(endYear);
			selectAnioFin.setSelectedAttribute(optionAnioFin, true);
			HtmlSelect liquidacion = (HtmlSelect) htmlPage.getElementById("idTipoLiquidacion");
			HtmlOption optionLiquidacion = liquidacion.getOptionByValue(liquidationType);
			liquidacion.setSelectedAttribute(optionLiquidacion, true);
//			formSubmit.click();
			System.out.println(htmlPage.asXml());
		}
	}

	public static void onlineSettlementOptionCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType,  String ccc,
			String regimen, String startMonth, String startYear, String endMonth, String endYear,
			String liquidationType, String authorized) throws Exception {

		try {		
			AonSegSocialJuanma.onlineSettlementOption1(certificateInputStream, certificatePassword, certificateType,
					 ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType, authorized);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void onlineSettlementOption2(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String authorized, String liquidationNumber,
			String rnt) throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setDownloadImages(true);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y600");
			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage
					.getElementById("autorizado" + authorized);
			radioButton.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
			HtmlRadioButtonInput radioButton2 = (HtmlRadioButtonInput) htmlPage.getElementById("idOPCION1");
			radioButton2.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();

			DomElement formSubmit = null;
			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("SPM.ACC.ACEPTAR");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			HtmlForm form = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getHtmlElementById("formDatos"))
					.orElseThrow();

			HtmlRadioButtonInput radioButton3 = (HtmlRadioButtonInput) htmlPage.getElementById("idOpcion2");
			radioButton3.setChecked(true);

			form.getInputByName("numeroLiquidacion").setValue(liquidationNumber);

			HtmlSelect select = (HtmlSelect) htmlPage.getElementById("solicitudRNT");
			HtmlOption selectOption = select.getOptionByValue(rnt);
			select.setSelectedAttribute(selectOption, true);

			formSubmit.click();

			System.out.println(htmlPage.asXml());
		}

	}

	public static void OnlineSettlementOptionLiquidationNumber(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String authorized,  String liquidationNumber,
			String rnt) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption2(certificateInputStream, certificatePassword, certificateType, authorized,
					liquidationNumber, rnt);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
