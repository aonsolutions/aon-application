package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.SegSocialOutOfService;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.object.SituacionEmpresa;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

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
		WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType);
		String authorized = "127770";

		try {

			HtmlPage page = AonSegSocialJuanma.onlineSettlement(webClient, authorized);
//
//			AonSegSocialJuanma.onlineSettlementOption1(webClient, page, ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType );
//			AonSegSocialJuanma.onlineSettlementOption2(webClient, page, liquidationNumber, rnt);

		} catch (SegSocialOutOfService e) {
			e.printStackTrace();
			e.getCause();
		}

	}

	public static void setCnoCertificate(final InputStream certificateInputStream, final String certificatePassword,
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
			form.getInputByName("txt_SDFPROAFI").setValueAttribute(nss.substring(0, 2));
			form.getInputByName("txt_SDFCODAFI").setValueAttribute(nss.substring(2));

			form.getInputByName("txt_SDFTIPPFI_ayuda").setValueAttribute(ident.substring(0, 1));
			form.getInputByName("txt_SDFNUMPFI").setValueAttribute(ident.substring(1));

			form.getInputByName("txt_SDFREGAFI").setValueAttribute(regimen);

			form.getInputByName("txt_SDFTESCTACOT").setValueAttribute(cc.substring(0, 2));
			form.getInputByName("txt_SDFCTACOT").setValueAttribute(cc.substring(2));
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

			form2.getInputByName("txt_SDFCNOCUP_ayuda").setValueAttribute(cno);
			formSubmit.click();

			System.out.println(htmlPage.asXml());

		}

	}

	//

	// Metodo para navegar en la pagina
	private static HtmlPage onlineSettlement(WebClient webClient, String authorized) throws Exception {
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setDownloadImages(true);
		webClient.setJavaScriptTimeout(10000);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());

		HtmlPage htmlPage = webClient.getPage(
				"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y600");
		HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("autorizado" + authorized);
		radioButton.setChecked(true);
		htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
		HtmlRadioButtonInput radioButton2 = (HtmlRadioButtonInput) htmlPage.getElementById("idOPCION1");
		radioButton2.setChecked(true);
		htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Aceptar]")).click();
		System.out.println(htmlPage.asXml());

		return htmlPage;

	}


	public static void onlineSettlementOption1(WebClient webClient, HtmlPage htmlPage, String ccc, String regimen,
			String startMonth, String startYear, String endMonth, String endYear, String liquidationType)
			throws Exception {

		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setDownloadImages(true);
		webClient.setJavaScriptTimeout(10000);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());

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
		HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("idOpcion1");
		radioButton.setChecked(true);
		// Codigo de cuenta de cotizacion
		form.getInputByName("CCC").setValueAttribute(ccc);

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
		formSubmit.click();
		System.out.println(htmlPage.asXml());
	}

	public static void onlineSettlementOption2(WebClient webClient, HtmlPage htmlPage, String liquidationNumber,
			String rnt) throws Exception {

		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setDownloadImages(true);
		webClient.setJavaScriptTimeout(10000);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());

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

		HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) htmlPage.getElementById("idOpcion2");
		radioButton.setChecked(true);

		form.getInputByName("numeroLiquidacion").setValueAttribute(liquidationNumber);

		HtmlSelect select = (HtmlSelect) htmlPage.getElementById("solicitudRNT");
		HtmlOption selectOption = select.getOptionByValue(rnt);
		select.setSelectedAttribute(selectOption, true);

		formSubmit.click();

		System.out.println(htmlPage.asXml());

	}

}
