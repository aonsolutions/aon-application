package solutions.aon.seg.social;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFieldSet;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import solutions.aon.seg.social.SistemaREDI;
import solutions.aon.seg.social.SistemaREDI.LiquidationType;
import solutions.aon.seg.social.SistemaREDI.Regime;
import solutions.aon.seg.social.exceptions.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.StatusCodeException;
import solutions.aon.seg.social.exceptions.invaliddata.InvalidDataException;
import solutions.aon.seg.social.exceptions.invaliddata.LiquidationDoesNotExist;
import solutions.aon.seg.social.exceptions.invaliddata.NotExistingYetException;
import solutions.aon.seg.social.exceptions.invaliddata.PendingProcessesException;
import solutions.aon.seg.social.exceptions.invaliddata.UnfilledMandatory;
import solutions.aon.seg.social.exceptions.invaliddata.invalidCccException;
import solutions.aon.seg.social.exceptions.invaliddata.outOfTimeException;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;
import solutions.aon.seg.social.objects.*;
import solutions.aon.seg.social.objects.BankData.BankDataBuilder;
public class SistemaREDRemesas {

	public static void draftRequest(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc, final SistemaREDI.Regime regime, 
			Date dateFrom, Date dateTo, final SistemaREDI.LiquidationType liqType, final boolean recoverPreviousMonthBases, final boolean receptionQuery) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arr_fields= {ccc, regime, dateFrom, dateTo, liqType, recoverPreviousMonthBases};
		Toolkit.verifyData(arr_fields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");
			htmlPage=htmlPage.getAnchorByHref("/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y300").click();
			htmlPage=htmlPage.getElementById("idOpcion1").click();
			htmlPage=htmlPage.getElementById("SPM.ACC.ACEPTAR").click();
			HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
			//ccc
			formDatos.getInputByName("CCC").setValueAttribute(ccc);
			//regime
			HtmlSelect selectRegime=formDatos.getSelectByName("REGIMEN");
			selectRegime.getOptionByValue(regime.getValue());
			//datefrom month
			HtmlSelect selectMonthFrom=formDatos.getSelectByName("MES_DESDE");
			Calendar c=Calendar.getInstance();
			c.setTime(dateFrom);
			String monthValue=""+(c.get(Calendar.MONTH)+1);
			if((c.get(Calendar.MONTH)+1)<10) {
				monthValue="0"+monthValue;
			}
			selectMonthFrom.getOptionByValue(monthValue).setSelected(true);
			//datefrom year
			HtmlSelect selectYearFrom=formDatos.getSelectByName("ANNIO_DESDE");
			String yearValue=""+c.get(Calendar.YEAR);
			try {
			selectYearFrom.getOptionByValue(yearValue).setSelected(true);
			} catch (ElementNotFoundException e) {
				throw new NotExistingYetException("the selected year does not exist in the select (too soon or too late)");
			}
			//dateTo month
			HtmlSelect selectMonthTo=formDatos.getSelectByName("MES_HASTA");
			c.setTime(dateTo);
			monthValue=""+(c.get(Calendar.MONTH)+1);
			if((c.get(Calendar.MONTH)+1)<10) {
				monthValue="0"+monthValue;
			}
			selectMonthTo.getOptionByValue(monthValue).setSelected(true);
			//dateTo year
			HtmlSelect selectYearTo=formDatos.getSelectByName("ANNIO_HASTA");
			try {
				selectYearTo.getOptionByValue(""+c.get(Calendar.YEAR)).setSelected(true);
			} catch (ElementNotFoundException e) {
				throw new NotExistingYetException("the selected year does not exist in the select (too soon or too late)");
			}
			//liq type
			HtmlSelect selectLiqType=formDatos.getSelectByName("TIPO_LIQUIDACION");
			selectLiqType.getOptionByValue(liqType.getValue());
			//checking "Solicito la recuperación de las bases del mes anterior de los trabajadores (sólo para liquidaciones normales L00)"
			if(recoverPreviousMonthBases) {
				formDatos.getInputByName("BASE_MES_ANTERIOR").setChecked(true);
			}
			HtmlSelect receptionSelect=formDatos.getSelectByName("SOLICITUD_RNT");
			if(receptionQuery) {
				receptionSelect.getOptionByValue("S").setSelected(true);
			}
			else {
				receptionSelect.getOptionByValue("N").setSelected(true);
			}
			htmlPage=formDatos.getInputByName("SPM.ACC.ACEPTAR").click();
			handleRemesasExceptions(htmlPage);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
	}
	
	public static void confirmationRequest(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc, final SistemaREDI.Regime regime, 
			Date dateFrom, Date dateTo, final SistemaREDI.LiquidationType liqType, final boolean receptionQuery) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arr_fields= {ccc, regime, dateFrom, dateTo, liqType};
		Toolkit.verifyData(arr_fields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");
			htmlPage=htmlPage.getAnchorByHref("/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y600").click();
			htmlPage=htmlPage.getElementById("idOPCION1").click();
			htmlPage=htmlPage.getElementById("SPM.ACC.ACEPTAR").click();
			HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
			//ccc
			formDatos.getInputByName("CCC").setValueAttribute(ccc);
			//regime
			HtmlSelect selectRegime=formDatos.getSelectByName("REGIMEN");
			selectRegime.getOptionByValue(regime.getValue());
			//datefrom month
			HtmlSelect selectMonthFrom=formDatos.getSelectByName("MES_DESDE");
			Calendar c=Calendar.getInstance();
			c.setTime(dateFrom);
			String monthValue=""+(c.get(Calendar.MONTH)+1);
			if((c.get(Calendar.MONTH)+1)<10) {
				monthValue="0"+monthValue;
			}
			selectMonthFrom.getOptionByValue(monthValue).setSelected(true);
			//datefrom year
			HtmlSelect selectYearFrom=formDatos.getSelectByName("ANNIO_DESDE");
			String yearValue=""+c.get(Calendar.YEAR);
			try {
			selectYearFrom.getOptionByValue(yearValue).setSelected(true);
			} catch (ElementNotFoundException e) {
				throw new NotExistingYetException("the selected year does not exist in the select (too soon or too late)");
			}
			//dateTo month
			HtmlSelect selectMonthTo=formDatos.getSelectByName("MES_HASTA");
			c.setTime(dateTo);
			monthValue=""+(c.get(Calendar.MONTH)+1);
			if((c.get(Calendar.MONTH)+1)<10) {
				monthValue="0"+monthValue;
			}
			selectMonthTo.getOptionByValue(monthValue).setSelected(true);
			//dateTo year
			HtmlSelect selectYearTo=formDatos.getSelectByName("ANNIO_HASTA");
			try {
				selectYearTo.getOptionByValue(""+c.get(Calendar.YEAR)).setSelected(true);
			} catch (ElementNotFoundException e) {
				throw new NotExistingYetException("the selected year does not exist in the select (too soon or too late)");
			}
			//liq type
			HtmlSelect selectLiqType=formDatos.getSelectByName("TIPO_LIQUIDACION");
			selectLiqType.getOptionByValue(liqType.getValue());
			//selection receptio type
			HtmlSelect receptionSelect=formDatos.getSelectByName("SOLICITUD_RNT");
			if(receptionQuery) {
				receptionSelect.getOptionByValue("S").setSelected(true);
			}
			else {
				receptionSelect.getOptionByValue("N").setSelected(true);
			}
			htmlPage=formDatos.getInputByName("SPM.ACC.ACEPTAR").click();
			handleRemesasExceptions(htmlPage);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
	}
	
	
	
	public static void confirmationRequest(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc, final SistemaREDI.Regime regime) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arr_fields= {ccc, regime};
		Toolkit.verifyData(arr_fields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");
			htmlPage=htmlPage.getAnchorByHref("/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y800").click();
			HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
			formDatos.getInputByName("CCC").setValueAttribute(ccc);
			HtmlSelect regimeSelect=formDatos.getSelectByName("REGIMEN");
			regimeSelect.getOptionByValue(regime.getValue());
			htmlPage=formDatos.getInputByName("SPM.ACC.ACEPTAR").click();
			handleRemesasExceptions(htmlPage);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
	}
	
	
	public static Collection<BankData> getBankDataByCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc, final SistemaREDI.Regime regime) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		Object[] arr_fields= {ccc, regime};
		Toolkit.verifyData(arr_fields);
		try(WebClient webClient=HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/M/menuSLD-REMESAS.html");
			htmlPage=htmlPage.getAnchorByHref("/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y800").click();
			HtmlForm formDatos=(HtmlForm)htmlPage.getElementById("formDatos");
			formDatos.getInputByName("CCC").setValueAttribute(ccc);
			HtmlSelect regimeSelect=formDatos.getSelectByName("REGIMEN");
			regimeSelect.getOptionByValue(regime.getValue());
			htmlPage=formDatos.getInputByName("SPM.ACC.ACEPTAR").click();
			handleRemesasExceptions(htmlPage);
			DomNodeList<DomNode> bankNodes=htmlPage.querySelectorAll("fieldset[class='ancho95']");
			for (DomNode bankNode : bankNodes) {
				HtmlFieldSet fieldSet=(HtmlFieldSet)bankNode;
				BankDataBuilder bdb=new BankDataBuilder();
				bdb.setDataType(fieldSet.getAttribute("title"));
				DomNodeList<HtmlElement> divs=fieldSet.getElementsByTagName("div");
				HtmlDivision divIban=(HtmlDivision) divs.get(0);
				HtmlSpan spanIban=(HtmlSpan) divIban.getElementsByTagName("span").get(0);
				spanIban.removeAllChildren();

			}
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		}
		return null;
	}
	
	
	
	
	private static void handleRemesasExceptions(HtmlPage htmlPage) throws SegSocialException{
		try {
			String error=htmlPage.querySelector("#ARQContenMensaje>ul >.mensajeError").getVisibleText();
			if(error.toUpperCase().contains("FUERA DE PLAZO")) {
				throw new outOfTimeException(error);
			}
			else if(error.toUpperCase().contains("DEBE TENER CONTENIDO")) {
				throw new UnfilledMandatory(error);
			}
			else if(error.toUpperCase().contains("DOCUMENTO ADELANTADO")) {
				throw new NotExistingYetException(error);
			}
			else if(error.toUpperCase().contains("C.C.C. ERRÓNEO")) {
				throw new invalidCccException(error);
			}
			else if(error.toUpperCase().contains("NO EXISTE LIQUIDACIÓN")) {
				throw new LiquidationDoesNotExist(error);
			}
			else if(error.toUpperCase().contains("EXISTEN PROCESOS PENDIENTES SOBRE ESTA LIQUIDACIÓN")) {
				throw new PendingProcessesException(error);
			}
			else if (error.toUpperCase().contains("NO VIABLE")) {
				throw new LiquidationDoesNotExist(error);
			}
			else {
				throw new InvalidDataException(error);
			}
		} catch (NullPointerException e) {}
	}
}
