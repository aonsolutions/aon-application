package solutions.aon.seg.social;

import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.clickAndCheckCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.doubleClickAndCheckCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


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


import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.SegSocialOutOfService;

import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

public class AonSegSocialJuanma extends SegSocialException {

	public static void main(String[] args)
			throws SegSocialException, FileNotFoundException, FailingHttpStatusCodeException, SegSocialOutOfService {
		String nss = "461052988590";
		String ident = "173578385M";
		String regimen = "0111";
		String cc = "11122534302";
		String cno = "0020";
		String ccc = "03130332870";  //03130332870 //04102275092 //03142913972  //04118623131
		String certificate = "/tmp/AyudaTFNMT.p12";
		String startMonth = "02";
		String startYear = "2023";
		String endMonth = "02";
		String endYear = "2023";
		String liquidationType = "";
		String liquidationNumber = "01202300203626577";
		String rnt = "S";
		String user = "aon";
		String password = "t3st";
		File file = new File(certificate);
		final InputStream certificateInputStream = new FileInputStream(file);
		final String certificatePassword = "123456";
		final String certificateType = "pkcs12";
		String authorized = "127770";
		String href = "/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24J001";
		 
	
		try {

//			AonSegSocialJuanma.liquidationStateAuthorized(certificateInputStream, certificatePassword, certificateType,
//					href);

//			AonSegSocialJuanma.liquidationStateCcCode(certificateInputStream, certificatePassword, certificateType,
//					href, ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType);

//			AonSegSocialJuanma.getPdfCcc(certificateInputStream, certificatePassword, certificateType, href, ccc, regimen,
//					startMonth, startYear, endMonth, endYear, liquidationType);

//			AonSegSocialJuanma.liquidationNumberLiquidation(certificateInputStream, certificatePassword,
//					certificateType, href, liquidationNumber);
			
			AonSegSocialJuanma.getItExcel(certificateInputStream, certificatePassword, certificateType, href, ccc, regimen, startMonth, startYear, endMonth, endYear);

		} catch (Exception e) {
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
			final String certificatePassword, final String certificateType, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType,
			String authorized) throws Exception {

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
			final String certificatePassword, final String certificateType, String authorized, String liquidationNumber,
			String rnt) throws Exception {
		try {
			AonSegSocialJuanma.onlineSettlementOption2(certificateInputStream, certificatePassword, certificateType,
					authorized, liquidationNumber, rnt);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	public static void aonLogin(final InputStream certificateInputStream, final String certificatePassword,
//			final String certificateType, String user, String password) throws Exception {
//
//		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
//				certificateType)) {
//
//			webClient.getOptions().setCssEnabled(false);
//			webClient.getOptions().setDownloadImages(false);
//			webClient.setJavaScriptTimeout(10000);
//			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//			HtmlPage htmlPage = webClient.getPage("Https://ayudat.aonsolutions.net ");
//
//			HtmlForm form = htmlPage.getHtmlElementById("login");
//			form.getInputByName("j_username").setValueAttribute(user);
//			form.getInputByName("j_password").setValueAttribute(password);
//
//			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Login]")).click();
//
//			// Pruebas de navegacion
//
//			System.out.println(htmlPage.asXml() + "prueba menu logeado");
//
//			// Seleccion de empresa y seleccion de años
//
//			HtmlElement elementA = (HtmlElement) htmlPage
//					.getElementById("aonContent:mainForm:domainSwitcherData:3:j_id282");
//			htmlPage = (HtmlPage) elementA.click();
//
//			System.out.println(htmlPage.asXml() + "prueba menu seleccion empresa");
//
//			HtmlSelect select = (HtmlSelect) htmlPage.getElementById("aonContent:mainForm:fiscalYearSelect");
//			HtmlOption option = select.getOptionByValue("2022");
//			select.setSelectedAttribute(option, true);
//
//			select = (HtmlSelect) htmlPage.getElementById("aonContent:mainForm:salaryYearSelect");
//			option = select.getOptionByValue("2022");
//			select.setSelectedAttribute(option, true);
//
//			select = (HtmlSelect) htmlPage.getElementById("aonContent:mainForm:accountPeriodSelect");
//			option = select.getOptionByValue(
//					"rO0ABXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAADAE");
//			select.setSelectedAttribute(option, true);
//
//			select = (HtmlSelect) htmlPage.getElementById("aonContent:mainForm:contractYearSelect");
//			option = select.getOptionByValue("2022");
////			select.setSelectedAttribute(option, true);
//
//			htmlPage = (HtmlPage) select.setSelectedAttribute(option, true);
//			System.out.println(htmlPage.asText());
//
////			HtmlElement elementA = (HtmlElement) htmlPage.getElementById("aonContent:mainMenuForm:menu_accounting");
////			htmlPage = (HtmlPage) elementA.click();
////
////			System.out.println(htmlPage.asXml() + "prueba menu accounting");
////
////			elementA = (HtmlElement) htmlPage.getElementById("aonContent:mainMenuForm:menu_enterprise");
////			htmlPage = (HtmlPage) elementA.click();
////
////			System.out.println(htmlPage.asXml() + "prueba menu enterprise");
////
////			elementA = (HtmlElement) htmlPage.getElementById("aonContent:mainMenuForm:menu_fiscal");
////			htmlPage = (HtmlPage) elementA.click();
////
////			System.out.println(htmlPage.asXml() + "prueba menu fiscal");
////
////			elementA = (HtmlElement) htmlPage.getElementById("aonContent:mainMenuForm:menu_payroll");
////			htmlPage = (HtmlPage) elementA.click();
////
////			System.out.println(htmlPage.asXml() + "prueba menu laboral");
////
////			elementA = (HtmlElement) htmlPage.getElementById("aonContent:mainMenuForm:menu_configuration");
////			htmlPage = (HtmlPage) elementA.click();
////
////			System.out.println(htmlPage.asXml() + "prueba menu configuracion");
////
////			elementA = (HtmlElement) htmlPage.getElementById("headerOptionsForm:index");
////			htmlPage = (HtmlPage) elementA.click();
////
////			System.out.println(htmlPage.asXml() + "prueba vuelta al index");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	public static void liquidationStateAuthorized(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String href) throws Exception {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");

			HtmlAnchor anchor = (HtmlAnchor) htmlPage.getAnchorByHref(href);
			htmlPage = anchor.click();

			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("autorizacion5");
			radioButton.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
			// Hasta aqui navegacion pantalla opciones
			// Opcion 1 seleccion de liquidaciones mediante Autorizacion/autorizado

			System.out.println(htmlPage.asXml());

			radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("OPCION1");
			radioButton.setChecked(true);
			radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("OP1");
			radioButton.setChecked(true);

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Confirmar]")).click();

		}

	}

	private static AonSegSocialTableResult liquidationStateCcCode(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String href, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType)
			throws Exception {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");

			HtmlAnchor anchor = (HtmlAnchor) htmlPage.getAnchorByHref(href);
			htmlPage = anchor.click();

			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("autorizacion0");
			radioButton.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();

			// Hasta aqui navegacion pantalla opciones

			radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("OPCION2");
			radioButton.setChecked(true);

			HtmlForm form = htmlPage.getHtmlElementById("formDatos");
			form.getInputByName("CCC").setValueAttribute(ccc);

			HtmlSelect select = (HtmlSelect) htmlPage.getElementById("Regimen");
			HtmlOption option = select.getOptionByValue(regimen);
			select.setSelectedAttribute(option, true);

			// Fecha de inicio
			select = (HtmlSelect) htmlPage.getElementById("PerLiqMesDesde");
			option = select.getOptionByValue(startMonth);
			select.setSelectedAttribute(option, true);
			select = (HtmlSelect) htmlPage.getElementById("ANNIO_DESDE");
			option = select.getOptionByValue(startYear);
			select.setSelectedAttribute(option, true);
			// Fecha de fin
			select = (HtmlSelect) htmlPage.getElementById("PerLiqMesHasta");
			option = select.getOptionByValue(endMonth);
			select.setSelectedAttribute(option, true);
			select = (HtmlSelect) htmlPage.getElementById("ANNIO_HASTA");
			option = select.getOptionByValue(endYear);
			select.setSelectedAttribute(option, true);
			// Tipo de liquidacion
			select = (HtmlSelect) htmlPage.getElementById("tipoLiq");
			option = select.getOptionByValue(liquidationType);
			select.setSelectedAttribute(option, true);

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

			System.out.println(htmlPage.asXml());

			List<HtmlDefinitionDescription> dd = htmlPage.getByXPath("//dd[@class='margenInf8 der']");

			AonSegSocialTableResult example = new AonSegSocialTableResult();

			example.setTotalCccs(dd.get(0).asText());
			example.setTotalLiquidations(dd.get(1).asText());
			example.setAmount(dd.get(2).asText());
			example.setStretches(dd.get(3).asText());
			example.setCalculatedStretches(dd.get(4).asText());
			example.setNoCalculatedStretches(dd.get(5).asText());

			System.out.println(example.getTotalCccs());
			System.out.println(example.getTotalLiquidations());
			System.out.println(example.getAmount());
			System.out.println(example.getStretches());
			System.out.println(example.getCalculatedStretches());
			System.out.println(example.getNoCalculatedStretches());

			return example;

		}
	}

	public static void getTableResultLiquidationStateCcCode(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String href, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType)
			throws Exception {
		try {
			AonSegSocialJuanma.liquidationStateCcCode(certificateInputStream, certificatePassword, certificateType,
					href, ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static AonSegSocialTableResult liquidationNumberLiquidation(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String href, String liquidationNumber)
			throws Exception {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");

			HtmlAnchor anchor = (HtmlAnchor) htmlPage.getAnchorByHref(href);
			htmlPage = anchor.click();

			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("autorizacion0");
			radioButton.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();

			// Hasta aqui navegacion pantalla opciones

			radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("OPCION3");
			radioButton.setChecked(true);

			HtmlForm form = htmlPage.getHtmlElementById("formDatos");
			form.getInputByName("NUMLIQ").setValueAttribute(liquidationNumber);

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

			System.out.println(htmlPage.asXml());

			List<HtmlDefinitionDescription> dd = htmlPage.getByXPath("//dd[@class='margenInf8 der']");

			AonSegSocialTableResult example = new AonSegSocialTableResult();

			example.setTotalCccs(dd.get(0).asText());
			example.setTotalLiquidations(dd.get(1).asText());
			example.setAmount(dd.get(2).asText());
			example.setStretches(dd.get(3).asText());
			example.setCalculatedStretches(dd.get(4).asText());
			example.setNoCalculatedStretches(dd.get(5).asText());

			System.out.println(example.getTotalCccs());
			System.out.println(example.getTotalLiquidations());
			System.out.println(example.getAmount());
			System.out.println(example.getStretches());
			System.out.println(example.getCalculatedStretches());
			System.out.println(example.getNoCalculatedStretches());

			return example;

		}

	}

	public static void getTableResultLiquidationNumberLiquidation(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String href, String liquidationNumber)
			throws Exception {

		try {
			AonSegSocialJuanma.liquidationNumberLiquidation(certificateInputStream, certificatePassword,
					certificateType, href, liquidationNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static byte[] getPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String href, String ccc, String regimen, String startMonth, String startYear,
			String endMonth, String endYear, String liquidationType) throws Exception {

		InvalidCertificateException.checkCertificate(certificateInputStream);

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");

			HtmlAnchor anchor = (HtmlAnchor) htmlPage.getAnchorByHref(href);
			htmlPage = anchor.click();

			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("autorizacion0");
			radioButton.setChecked(true);
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();

			radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("OPCION2");
			radioButton.setChecked(true);

			HtmlForm form = htmlPage.getHtmlElementById("formDatos");
			form.getInputByName("CCC").setValueAttribute(ccc);

			HtmlSelect select = (HtmlSelect) htmlPage.getElementById("Regimen");
			HtmlOption option = select.getOptionByValue(regimen);
			select.setSelectedAttribute(option, true);

			// Fecha de inicio
			select = (HtmlSelect) htmlPage.getElementById("PerLiqMesDesde");
			option = select.getOptionByValue(startMonth);
			select.setSelectedAttribute(option, true);
			select = (HtmlSelect) htmlPage.getElementById("ANNIO_DESDE");
			option = select.getOptionByValue(startYear);
			select.setSelectedAttribute(option, true);
			// Fecha de fin
			select = (HtmlSelect) htmlPage.getElementById("PerLiqMesHasta");
			option = select.getOptionByValue(endMonth);
			select.setSelectedAttribute(option, true);
			select = (HtmlSelect) htmlPage.getElementById("ANNIO_HASTA");
			option = select.getOptionByValue(endYear);
			select.setSelectedAttribute(option, true);
			// Tipo de liquidacion
			select = (HtmlSelect) htmlPage.getElementById("tipoLiq");
			option = select.getOptionByValue(liquidationType);
			select.setSelectedAttribute(option, true);

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

			DomElement formSubmit = null;
			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("SPM.ACC.GENERAR_INFORME");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}

			htmlPage = formSubmit.click();

			HtmlButton docuButton = (HtmlButton) htmlPage
					.getFirstByXPath("//button[@class='botonDesplegable desplegar']");

			docuButton.click();

			List<HtmlListItem> list = htmlPage.getByXPath("//li[@class='relleno5  listaEnlaces']");
			Page page = doubleClickAndCheckCode(list.get(0));
			byte[] ret = null;
			if (page.isHtmlPage()) {
				htmlPage = (HtmlPage) page;
				HtmlUnitToolkit.manageStatusCode(htmlPage);

				HtmlAnchor aPdf = (HtmlAnchor) htmlPage
						.getAnchorByText("Informe:Informe del Estado de las Liquidaciones (se abre en ventana nueva).");
				System.out.println(aPdf.asXml());
				if (aPdf != null) {
					page = clickAndCheckCode(aPdf);
				}
			}

			if (!page.isHtmlPage()) {

				WebResponse response = HtmlUnitToolkit.wait4(page, p -> p.getWebResponse()).orElseGet(null);
				InputStream is = response.getContentAsStream();
				ret = is.readAllBytes();
				is.close();
				FileUtils.writeByteArrayToFile(new File("/home/jmortega/pruebaPdf/prueba2.pdf"), ret);

				return ret;
			}

			return null;

		}

	}
	
	public static void getPdfCcc(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String href, String ccc, String regimen, String startMonth, String startYear,
			String endMonth, String endYear, String liquidationType) throws Exception{
		
		try {
			AonSegSocialJuanma.getPdf(certificateInputStream, certificatePassword, certificateType, href, ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static byte [] getItExcel(final InputStream certificateInputStream, final String certificatePassword,
            final String certificateType, String href, String ccc, String regimen, String startMonth, String startYear,
            String endMonth, String endYear) throws Exception{

        InvalidCertificateException.checkCertificate(certificateInputStream);
        try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
                certificateType)){
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setDownloadImages(false);
            webClient.setJavaScriptTimeout(10000);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            HtmlPage htmlPage = webClient.getPage("https://www.seg-social.es/wps/portal/wss/internet/Inicio");

            HtmlAnchor anchor = (HtmlAnchor) htmlPage.getAnchorByHref("https://w2.seg-social.es/fs/indexframes.html");
            htmlPage = anchor.click();
            anchor = (HtmlAnchor) htmlPage.getAnchorByHref("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV26L100");
            htmlPage = anchor.click();
            HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("OPCIONES_BUSQUEDA_3");
            radioButton.setChecked(true);
            HtmlForm form = htmlPage.getHtmlElementById("FORMULARIO_1");
            form.getInputByName("regimen").setValueAttribute("0111");
            form.getInputByName("provincia").setValueAttribute("11");
            form.getInputByName("cccnum").setValueAttribute("122534302");

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = formatter.parse("16/05/2022");
            Date endDate = formatter.parse("30/05/2022");

            form.getInputByName("fechaIniEmpresa").setValueAttribute(formatter.format(startDate));
            form.getInputByName("fechaFinEmpresa").setValueAttribute(formatter.format(endDate));
            
            HtmlElement button = (HtmlElement) htmlPage.getElementById("ENVIO_5");
//            htmlPage = button.click();
            XmlPage rPage = (XmlPage) button.click();

//            System.out.println(rPage.asXml());
          String redirectUrl = rPage.getWebResponse().getWebRequest().getUrl().toString();
          htmlPage = webClient.getPage(redirectUrl);
			HtmlUnitToolkit.manageStatusCode(htmlPage);

          System.out.println(htmlPage.asXml());
          
          
          
          
          if ( !rPage.isHtmlPage() ){
                WebResponse response = HtmlUnitToolkit.wait4(rPage, p -> p.getWebResponse()).orElseGet(null);
                InputStream is = response.getContentAsStream();
                byte[] ret = is.readAllBytes();
                is.close();
                return ret;
                
            }
            
            
            System.out.println(htmlPage);
////            System.out.println(rPage.asText());

//            System.out.println(htmlPage.asXml());
            
            
           



        }
        return null;
    }


}
