package solutions.aon.seg.social;

import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;
import static solutions.aon.seg.social.toolkit.Toolkit.getDateArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.UnexpectedPage;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSpan;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.NoMoreDataException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.Liquidation;
import solutions.aon.seg.social.object.Liquidation.LiquidationBuilder;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.object.SituacionEmpresa;
import solutions.aon.seg.social.object.SituacionEmpresa.SituacionEmpresaBuilder;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.object.WorkerLiquidation;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;
/**
 * Class to obtain resources from Social Security just sending POST/GET requests
 *
 */
public class ServicioRED extends ServicioREDRegeXML {
	/**
	 * Pattern to obtain the action link and session id from forms
	 */
	private static final Pattern FORM_PATTERN = Pattern.compile("\\<form.*action=\"(?<link>.+?SessionId=(?<session>[^\\&]+?)\\&.*?)\"");
	private static final DateFormat PDF_DATE_FORMAT = new SimpleDateFormat("dd MM yyyy");
	/**
	 * INFORME DE DATOS DE COTIZACIÓN (IDC)
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param affiliationNumber
	 * @param regime
	 * @param ccc
	 * @param date
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getIDCPOST (final byte[] certificateData, final String certificatePassword,
			final String certificateType, String regime,String ccc, String affiliationNumber, Date date) throws SegSocialException {
		return getIDCPOST(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, regime, ccc, affiliationNumber, date);
	}
	public static byte[] getIDCPOST (final InputStream certificateInputStream,final String certificatePassword,
		final String certificateType, String regime,String ccc, String affiliationNumber, Date date) throws SegSocialException {
		
		Date today = new Date(); 
		date = date.after(today) ? today : date;
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage document = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");

			// Número de Afiliación
			HtmlInput nssInput = document.querySelector("#SDFTESNAF");
			HtmlInput nssInput1 = document.querySelector("#SDFNAF");

			nssInput.setValue(affiliationNumber.substring(0, 2));
			nssInput.setValueAttribute(affiliationNumber.substring(0, 2));
			nssInput1.setValue(affiliationNumber.substring(2));
			nssInput1.setValueAttribute(affiliationNumber.substring(2));
			
			// Régimen / CCC
			HtmlInput regimeInput = document.querySelector("#SDFREGCTA");
			HtmlInput cccCodeInput = document.querySelector("#SDFTESCTA");
			HtmlInput cccInput = document.querySelector("#SDFCUENTA");

			regimeInput.setValue(regime);
			regimeInput.setValueAttribute(regime);
			cccCodeInput.setValue(ccc.substring(0, 2));
			cccCodeInput.setValueAttribute(ccc.substring(0, 2));
			cccInput.setValue(ccc.substring(2));
			cccInput.setValueAttribute(ccc.substring(2));
			
			// Fecha
			Integer[] fromArray = getDateArray(date);
			
			HtmlInput fromDayInput = document.querySelector("#SDFDIA");
			HtmlInput fromMonthInput = document.querySelector("#SDFMES");
			HtmlInput fromYearInput = document.querySelector("#SDFAO");
			
			fromDayInput.setValue(String.valueOf(fromArray[0]));
			fromDayInput.setValueAttribute(String.valueOf(fromArray[0]));
			
			fromMonthInput.setValue(String.valueOf(fromArray[1]));
			fromMonthInput.setValueAttribute(String.valueOf(fromArray[1]));
			
			fromYearInput.setValue(String.valueOf(fromArray[2]));
			fromYearInput.setValueAttribute(String.valueOf(fromArray[2]));
			
			// Tipo impresion
			HtmlSelect onlineSelect = document.querySelector("#ListaTipoImpresion");
			onlineSelect.getOption(1).setSelected(true);

			HtmlSubmitInput continueButton = document.querySelector("#Sub2207601004");

			// Check if we have more than one CCC for this person
			try {
				document = continueButton.click();
				
				// Check table
				HtmlTable table = document.querySelector("#Sub0900112078");
				
				if(null != table) {
					for(int row=1; row < table.getRowCount(); row++) {
						HtmlTableCell startDateCell = table.getCellAt(row, 1);
						
						String startDate = startDateCell.getTextContent().trim();
						
						if(startDate.length() > 0) {
							HtmlSpan span = (HtmlSpan) startDateCell.getChildNodes().get(1);
							HtmlLabel label = (HtmlLabel) span.getChildNodes().get(1);
							
							return getPDFDocument(document, label);
						}
					}
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			// DESCOMENTAR LINEAS
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] getPDFDocument(HtmlPage page, HtmlElement linkElement) throws IllegalArgumentException {
		try {
			UnexpectedPage docPage = linkElement.dblClick();
			return docPage.getWebResponse().getContentAsStream().readAllBytes();
		} catch (Exception e) {
			System.err.println("UnexpectedPage FIND");
			return null;
			// Exception
//			throw new IllegalArgumentException(e.getMessage());
		}
	}

	
	/**
	 * INFORME DATOS COTIZACION/PERIODO LIQUIDACION-CCC
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param regime
	 * @param ccc
	 * @param date
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getIDCCccPOST(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date date) throws SegSocialException {
		
		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es//Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			HttpPost httpPost = new HttpPost(link);

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3800"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair("chk_SDFSELEC", "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
			
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
		
	}

	/**
	 * INFORME DATOS COTIZACION/PERIODO LIQUIDACION
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param affiliationNumber
	 * @param regime
	 * @param ccc
	 * @param date
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getIDCNAfPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String affiliationNumber, String regime, String ccc, Date date) throws SegSocialException {
		
		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);

			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3900"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA_AYUDA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
			
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
			
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
	}

	/**
	 * DUPLICADOS DE DOCUMENTOS TA
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param ccc
	 * @param regime
	 * @param affiliationNumber
	 * @param date
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getTADuplicatePOST(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String ccc, String regime, String affiliationNumber, Date date) throws SegSocialException{

		SSLContext sslContext = null;

		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6500"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair("txt_SDFREGCTA_NH", regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFDIA, String.format("%td", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("chk_SDFINFTA1", "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, ""));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			httpPost = new HttpPost(link);
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6501"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "tbl_cbo_Sub0900112079_0_0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "EN"));
			params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
			params.add(new BasicNameValuePair("tbl_cbo_Sub0900112079_0_0", "Select"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
		
	}
	
	/**
	 * DUPLICADOS DE DOCUMENTOS TA
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param ccc
	 * @param regime
	 * @param affiliationNumber
	 * @param date
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getTADuplicatePOST(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String ccc, String regime, SituationType situationType, String affiliationNumber, Date date) throws SegSocialException{

		SSLContext sslContext = Toolkit.getTrustedSSLContext(certificateInputStream, certificatePassword, certificateType);

		String link = "";
		String sessionId = "";		
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6500"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair("txt_SDFREGCTA_NH", regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFDIA, String.format("%td", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("chk_SDFINFTA1", "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, ""));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			httpPost = new HttpPost(link);
			
			String select = "tbl_cbo_Sub0900112079_0_0";
			
			Optional<String> element = Toolkit.getElementsContainingAttribute(body, "name", "_1_").stream().filter(el-> el.contains(situationType.getName())).findAny();
			if(element.isPresent()) 
				select = "tbl_cbo_Sub0900112079_0_"+Toolkit.getAttribute(element.get(), "name").substring(3);
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6501"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, select));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "EN"));
			params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
			params.add(new BasicNameValuePair(select, "Select"));
			
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
		
	}
	/**
	 * CERTI. ESTAR AL CORRIENTE EN OBLIGAC. DE S.S.
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param regime
	 * @param ccc
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getObligationAwarenessCertificatePOST (final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws SegSocialException {
		
		SSLContext sslContext = null;

		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		String focusedControl = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
			focusedControl = Toolkit.getButtonNameByValue(body, IServicioRedConstants.CONTINUE);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "RCCM5201"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, focusedControl));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair("txt_SDFWMIDENT", ccc));
			params.add(new BasicNameValuePair("txt_SDFWMRESU", regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(focusedControl, IServicioRedConstants.CONTINUE));
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			focusedControl = Toolkit.getButtonNameByValue(body, "Confirmar");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			httpPost = new HttpPost(link);
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "RCCM5201"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, focusedControl));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_3", "1"));
			params.add(new BasicNameValuePair("chk_detalle_deuda", "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(focusedControl, "Confirmar"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
	}
	
	/**
	 * INFORME DATOS COTIZACION/PERIODO LIQUIDACION
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param affiliationNumber
	 * @param regime
	 * @param ccc
	 * @param date
	 * @return a PDF file
	 * @throws SegSocialException
	 */
	public static byte[] getContributionSettlementReportPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc, final Date date) throws SegSocialException {

		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3900"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA_AYUDA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			httpPost = Toolkit.reportGenerationForm(body);

			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
	}
	
	public static List<byte[]> getTACertificatePDFsPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc, final Date date) throws SegSocialException, IOException {
		List<byte[]> pdfList = new LinkedList<>();
		SSLContext sslContext = null;
		try {				
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		String dateToMatch = PDF_DATE_FORMAT.format(date);
		
		
		String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
		String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

		String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
		String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {		
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6500"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "Sub2207601004"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair("txt_SDFREGCTA_NH", regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFDIA, String.format("%td", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("chk_SDFINFTA1", "1"));
			params.add(new BasicNameValuePair("btn_Sub2207601004", "Continuar"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			
			String labelRegex = "\\<label[^<>]*id=[\"'](?<id>Sub0900112079_(?<column>\\d+)_(?<row>\\d+))[\"'][^<>]*\\>" +dateToMatch + "[^<>]*" + "\\<\\/label\\>";
			Pattern labelPattern = Pattern.compile(labelRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher labelMatcher = labelPattern.matcher(body);
			HashSet<String> btnFields = new HashSet<>();
			
			while (labelMatcher.find()) {
				String btnField = "tbl_cbo_Sub0900112079_0_" + labelMatcher.group("row");
				btnFields.add(btnField);
			}
			
			for (String btnField : btnFields) {
				httpPost = new HttpPost(link);
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6501"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "Archivo_SalirALTF4_101"));
				params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, "Atr65"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "EN"));
				params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
				params.add(new BasicNameValuePair(btnField, "Select"));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				ServicioREDRegeXML.checkOldSsError(body);
				link = Toolkit.getLink(body);
				httpPost = Toolkit.reportGenerationForm(body);
				try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
					Toolkit.checkResponseStatus(resp);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					resp.getEntity().writeTo(baos);
					pdfList.add(baos.toByteArray());
				}
				
				body = Toolkit.goBackPdf(httpClient, link, sessionId);
				ServicioREDRegeXML.checkOldSsError(body);
				link = Toolkit.getLink(body);
			}
		}
		
		return pdfList;
		
	}
	
	public static List<byte[]> getContributionPDFsPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc, final Date date) throws SegSocialException, IOException {
		List<byte[]> pdfList = new LinkedList<>();
		SSLContext sslContext = null;
		try {				
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		String dateToMatch = PDF_DATE_FORMAT.format(date);
		
		
		String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
		String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";
		
		String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
		String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {		
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3700"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "Sub2207601004"));
			params.add(new BasicNameValuePair("txt_SDFTESNAF", txtSDFTESNAF));
			params.add(new BasicNameValuePair("txt_SDFNAF", txtSDFNAF));
			params.add(new BasicNameValuePair("txt_SDFREGCTA", regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFDIA, String.format("%td", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, String.format("%tm", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, String.format("%tY", date)));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("btn_Sub2207601004", "Continuar"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			
			String labelRegex = "\\<label[^<>]*id=[\"'](?<id>Sub0900112078_1_(?<row>\\d+))[\"'][^<>]*\\>" + dateToMatch + "[^<>]*" + "\\<\\/label\\>";
			Pattern labelPattern = Pattern.compile(labelRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher labelMatcher = labelPattern.matcher(body);
			HashSet<String> btnFields = new HashSet<>();
			
			while (labelMatcher.find()) {
				String btnField = "tbl_cbo_Sub0900112078_0_" + labelMatcher.group("row");
				btnFields.add(btnField);
			}
			
			for (String btnField : btnFields) {
				httpPost = new HttpPost(link);
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3701"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, "Atr65"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
				params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "EN"));
				params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
				for(String paramName : btnFields) {
					if (paramName != null && paramName.equals(btnField)) {
						params.add(new BasicNameValuePair(paramName, "Select"));
					} else {
						params.add(new BasicNameValuePair(paramName, ""));
					}
				}
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				ServicioREDRegeXML.checkOldSsError(body);
				link = Toolkit.getLink(body);
				httpPost = Toolkit.reportGenerationForm(body);
				try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
					Toolkit.checkResponseStatus(resp);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					resp.getEntity().writeTo(baos);
					pdfList.add(baos.toByteArray());
				}
				
				body = Toolkit.goBackPdf(httpClient, link, sessionId);
				ServicioREDRegeXML.checkOldSsError(body);
				link = Toolkit.getLink(body);
			}
		}
		
		return pdfList;
		
	}
	
	
	

	/**
	 * CONSULTA DE SITUACIÓN DE LA EMPRESA
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param regime
	 * @param ccc
	 * @return A SituacionEmpresa object
	 * @throws SegSocialException
	 */
	public static SituacionEmpresa getSituacionEmpresaPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws SegSocialException {
		SituacionEmpresaBuilder seb = new SituacionEmpresaBuilder();
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFNUMCTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ACRM6901"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, IServicioRedConstants.ACR69));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA_AYUDA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair("txt_SDFNUMCTA", txtSDFNUMCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			Map<String, String> values = Toolkit.getEverythingWithId(body);
            fillManagementData(seb, values);
            link = Toolkit.getLink(body);
            sessionId = Toolkit.getSessionId(body);
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ACRM6905"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "Sub2205801003"));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, IServicioRedConstants.ACR69));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, IServicioRedConstants.ACR69));
			params.add(new BasicNameValuePair("chk_SDFCONSCOLE", "1"));
			params.add(new BasicNameValuePair("chk_SDFCONSSALDOS", "1"));
			params.add(new BasicNameValuePair("chk_SDFCONSSII", "1"));
			params.add(new BasicNameValuePair("btn_Sub2205801003", "Datos+Iden."));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			values = Toolkit.getEverythingWithId(body);
			fillIdentifyingData(seb, values);
			
			return seb.build();
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
	}
	
	/**
	 * @deprecated use getSituacionEmpresaPost instead
	 * CONSULTA DE SITUACIÓN DE LA EMPRESA (DEPRECATED due to it's low reliability, use getSituacionEmpresaPOST instead)
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param regime
	 * @param ccc
	 * @return A SituacionEmpresa object
	 * @throws SegSocialException
	 */
	@Deprecated
	public static SituacionEmpresa getSituacionEmpresaSAX(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc)
			throws SegSocialException {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		SituacionEmpresaBuilder seb = new SituacionEmpresaBuilder();
		
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			try (CloseableHttpResponse resp = httpClient.execute(new HttpGet("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR"))) {
				Toolkit.checkResponseStatus(resp);
				String body = EntityUtils.toString(resp.getEntity(), ServicioREDRegeXML.DEFAULT_ENCODING);
				Matcher matcher = FORM_PATTERN.matcher(body);
				if (matcher.find()) {
					
					String params = matcher.group("link").replace(IServicioRedConstants.AMPERSAND, "&");
					link = IServicioRedConstants.SEG_SOCIAL_DOMAIN + params;
					sessionId = matcher.group(IServicioRedConstants.SESSION);
				}
			}
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFNUMCTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ACRM6901"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, IServicioRedConstants.ACR69));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA_AYUDA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair("txt_SDFNUMCTA", txtSDFNUMCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
			
				if (entity != null) {
					String body = EntityUtils.toString(entity, ServicioREDRegeXML.DEFAULT_ENCODING);
					ServicioREDRegeXML.checkOldSsError(body);
					
					Map<String, String> values = extractSituacionEmpresaInfo(body); 
		            fillManagementData(seb, values);
		            Matcher matcher = FORM_PATTERN.matcher(body);
					if (matcher.find()) {
						String parameters = matcher.group("link").replace(IServicioRedConstants.AMPERSAND, "&");
						link = IServicioRedConstants.SEG_SOCIAL_DOMAIN + parameters;
						sessionId = matcher.group(IServicioRedConstants.SESSION);
					}
				}
			}
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ACRM6905"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, "Sub2205801003"));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, IServicioRedConstants.ACR69));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, IServicioRedConstants.ACR69));
			params.add(new BasicNameValuePair("chk_SDFCONSCOLE", "1"));
			params.add(new BasicNameValuePair("chk_SDFCONSSALDOS", "1"));
			params.add(new BasicNameValuePair("chk_SDFCONSSII", "1"));
			params.add(new BasicNameValuePair("btn_Sub2205801003", "Datos+Iden."));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
			
				if (entity != null) {
					String body = EntityUtils.toString(entity, ServicioREDRegeXML.DEFAULT_ENCODING);
					ServicioREDRegeXML.checkOldSsError(body);
					
					Map<String, String> values = extractSituacionEmpresaInfo(body);
					fillIdentifyingData(seb, values);
				}
				
				
			}
			
			
			
			return seb.build();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		} catch (SAXException | ParserConfigurationException e) {
			throw new SegSocialException(e.getMessage());
		}
	}
	
	/**
	 * INFORME DE DATOS DE COTIZACIÓN (IDC)
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param affiliationNumber
	 * @param regime
	 * @param ccc
	 * @return A collection with the IDCs
	 * @throws SegSocialException
	 */
	public static Collection<Idc> getIDCDatesPOST(final byte[] certificateData,
			final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String affiliationNumber) throws SegSocialException {
		return getIDCDatesPOST(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, regime, ccc, affiliationNumber);
	}
	public static Collection<Idc> getIDCDatesPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String affiliationNumber) throws SegSocialException {
//		SSLContext sslContext = null;
//		try {
//			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
//		} catch (Exception e1) {
//			throw new InvalidCertificateException();
//		}
		
		SSLContext sslContext = Toolkit.getTrustedSSLContext(certificateInputStream, certificatePassword, certificateType);
		
		String link = "";
		String sessionId = "";
		
		List<Idc> collects = new ArrayList<>();
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3700"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, "Atr37"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "Atr37"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFDIA, ""));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, ""));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, ""));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
	
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			
			boolean end = false;
			
			if(body != null && !body.isEmpty()) {
				Collection<Idc> collect = Toolkit.getIDCDatesByRegex(body);
				collects.addAll(collect);
				end = collect.size() < 12;
			}
			
			while(!end) {
				try {
					body = nextPage(body, httpClient);
					if(body != null && !body.isEmpty()) {
						Collection<Idc> collect = Toolkit.getIDCDatesByRegex(body);
						collects.addAll(collect);
						end = collect.size() < 12;
					}
					ServicioREDRegeXML.checkOldSsError(body);
				} catch (NoMoreDataException e) {
					end = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
		Collections.sort(collects, (i1,i2) -> i1.getFecha().compareTo(i2.getFecha()));
		return collects;
	}
	
	/**
	 * ULTIMO INFORME DE DATOS DE COTIZACIÓN (IDC)
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param affiliationNumber
	 * @param regime
	 * @param ccc
	 * @return A collection with the IDCs
	 * @throws SegSocialException
	 */
	public static Collection<Idc> getIDCLatest(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc) throws SegSocialException {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		Set<Idc> collects = new HashSet<>();
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3700"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair(IServicioRedConstants.FOCUSED_CONTROL, IServicioRedConstants.SUB2207601004));
			params.add(new BasicNameValuePair(IServicioRedConstants.DEFAULT_NULL, "1"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_ENTORNO_PR, "0"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_TRANSAC, "Atr37"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_PRACTICE_MENU, "I"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_COMMAND_EDIT, "Atr37"));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESNAF, txtSDFTESNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFNAF, txtSDFNAF));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFREGCTA, regime));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFTESCTA, txtSDFTESCTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFCUENTA, txtSDFCUENTA));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFDIA, ""));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFMES, ""));
			params.add(new BasicNameValuePair(IServicioRedConstants.TXT_SDFAO, ""));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair(IServicioRedConstants.BTN_SUB2207601004, IServicioRedConstants.CONTINUE));
	
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			
			boolean end = false;
			
			if(body != null && !body.isEmpty()) {
				Collection<Idc> collect = Toolkit.getIDCDatesByRegex(body);
				collects.addAll(collect);
				end = collect.size() < 12;
			}
			

			while(!end) {
				try {
					body = nextPage(body, httpClient);
					if(body != null && !body.isEmpty()) {
						Collection<Idc> collect = Toolkit.getIDCDatesByRegex(body);
						collects.addAll(collect);
						end = collect.size() < 12;
					}
					ServicioREDRegeXML.checkOldSsError(body);
				} catch (NoMoreDataException e) {
					end = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}

		return collects;
	}
	
	
	/**
	 * INFORME DE DATOS DE COTIZACIÓN (IDC) - Fechas de alta
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param affiliationNumber
	 * @param regime
	 * @param ccc
	 * @return A collection with all the discharge dates
	 * @throws SegSocialException
	 */
	public static Collection<Date> getDischargeDatesPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String ccc) throws SegSocialException {
		
		try {
				return getIDCDatesPOST(certificateInputStream, certificatePassword, certificateType, regime, ccc, affiliationNumber)
					.stream()
					.filter(idc -> idc.getDescripcion().equals("ALTA"))
					.map(Idc::getFecha)
					.collect(Collectors.toCollection(LinkedList::new));
		} catch (NullPointerException e) {
			return Collections.emptyList();
		}
		
	}
	
	/**
	 * Servicio Consulta de Cálculos
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param ccc
	 * @param regime
	 * @param dateFrom
	 * @param dateTo
	 * @param liqType
	 * @param liqOrigin
	 * @return A collection with the requested liquidations
	 * @throws SegSocialException
	 */
	public static Collection<Liquidation> calculationByCCCPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		SSLContext sslContext = null;

		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		int liqs = 0;
		ArrayList<Liquidation> liquidations = new ArrayList<>();
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			link = Toolkit.getLinkPROSA(body);
			ticket = Toolkit.getTicket(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SEARCH_MODE, "CCC"));
			params.add(new BasicNameValuePair("CCC", ccc));
			params.add(new BasicNameValuePair(IServicioRedConstants.REGIME, regime.getValue()));
			params.add(new BasicNameValuePair(IServicioRedConstants.MONTH_FROM, String.format("%tm", dateFrom)));
			params.add(new BasicNameValuePair(IServicioRedConstants.YEAR_FROM, String.format("%tY", dateFrom)));
			params.add(new BasicNameValuePair(IServicioRedConstants.MONTH_TO, String.format("%tm", dateTo)));
			params.add(new BasicNameValuePair(IServicioRedConstants.YEAR_TO, String.format("%tY", dateTo)));
			params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION_TYPE, liqType.getValue()));
			params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION_ORIGIN, liqOrigin.getValue()));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_ACCEPT, IServicioRedConstants.ACCEPT));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			Toolkit.checkProsaError(body);
			link = Toolkit.getLinkPROSA(body);
			ticket = Toolkit.getTicket(body);
			liqs = Toolkit.getNumberOfLiquidations(body);	
			
			for (int liq=0; liq<liqs; liq++) {
				
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
				params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION, String.valueOf(liq)));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTINUE, IServicioRedConstants.CONTINUE));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				Toolkit.checkProsaError(body);
				String table = Toolkit.getCalculationTable(body);
				Collection<String> trs = Toolkit.getTrs(table);
				LiquidationBuilder lb = new LiquidationBuilder();				
				liquidationDataType(lb, trs);
				liquidations.add(lb.build());
				if (liq < liqs-1)
					Toolkit.goBack(httpClient, link, ticket);
			}
			
			return liquidations;
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
		
	}
	
	public static Map<String,Map<String, WorkerLiquidation>> workersCalculationByCCCPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		
		SSLContext sslContext = null;

		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		int liqs = 0;
		Map<String,Map<String, WorkerLiquidation>> liquidations = new HashMap<>();
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
			link = Toolkit.getLinkPROSA(body);
			ticket = Toolkit.getTicket(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SEARCH_MODE, "CCC"));
			params.add(new BasicNameValuePair("CCC", ccc));
			params.add(new BasicNameValuePair(IServicioRedConstants.REGIME, regime.getValue()));
			params.add(new BasicNameValuePair(IServicioRedConstants.MONTH_FROM, String.format("%tm", dateFrom)));
			params.add(new BasicNameValuePair(IServicioRedConstants.YEAR_FROM, String.format("%tY", dateFrom)));
			params.add(new BasicNameValuePair(IServicioRedConstants.MONTH_TO, String.format("%tm", dateTo)));
			params.add(new BasicNameValuePair(IServicioRedConstants.YEAR_TO, String.format("%tY", dateTo)));
			params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION_TYPE, liqType.getValue()));
			params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION_ORIGIN, liqOrigin.getValue()));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_ACCEPT, IServicioRedConstants.ACCEPT));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			Toolkit.checkProsaError(body);
			link = Toolkit.getLinkPROSA(body);
			ticket = Toolkit.getTicket(body);
			liqs = Toolkit.getNumberOfLiquidations(body);
			
			for (int liq=0; liq<liqs; liq++) {
				String type = null;
				httpPost = new HttpPost(link);
				
				String inLink = link;
				
				int nafs = 0;
				
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
				params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION, String.valueOf(liq)));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTINUE, IServicioRedConstants.CONTINUE));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				Toolkit.checkProsaError(body);
				inLink = Toolkit.getLinkPROSA(body);
				ticket = Toolkit.getTicket(body);
				type = Toolkit.getLiqType(body);
				
				httpPost = new HttpPost(inLink);
				
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
				params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
				params.add(new BasicNameValuePair("SPM.ACC.CONSULTA_TRABAJADORES", "Consulta+de+Trabajadores"));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				Toolkit.checkProsaError(body);
				inLink = Toolkit.getLinkPROSA(body);
				ticket = Toolkit.getTicket(body);
				nafs = Toolkit.howManyNafs(body);
				
				Map<String, WorkerLiquidation> nafLiq = new HashMap<>(); 
				for (int naf=0; naf<nafs; naf++) {
					httpPost = new HttpPost(inLink);
					
					params = new ArrayList<>();
					params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
					params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
					params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
					params.add(new BasicNameValuePair("NAF", String.valueOf(naf)));
					params.add(new BasicNameValuePair("SPM.ACC.CONSULTAR", "Consultar"));
					
					httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
					
					body = Toolkit.getBodyPOST(httpClient, httpPost);
					Toolkit.checkProsaError(body);
					inLink = Toolkit.getLinkPROSA(body);
					ticket = Toolkit.getTicket(body);
					WorkerLiquidation wl = Toolkit.getWorkerLiquidation(body);
					nafLiq.put(wl.getNss(), wl);
					
					Toolkit.goBack(httpClient, inLink, ticket);
					liquidations.put(type, nafLiq);
				}
				
				if (liq < liqs-1)
					Toolkit.goBack(httpClient, link, ticket);
			}
			
			return liquidations;
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidCertificateException();
		}
		
	}
	
	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> workersCalculationByCCCandNAFsPOST(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException, IOException{
		
				SSLContext sslContext = null;

				try {
					sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
				} catch (Exception e1) {
					throw new InvalidCertificateException();
				}
				String link = "";
				String ticket = "";
				int liqs = 0;
				
				Map<String, Map<String,Map<Period, Map<String, Calc>>>> ret= new LinkedHashMap<>();
				
				try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
					boolean manyAuths = false;
					
					String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV21Y200");
					link = Toolkit.getLinkPROSA(body);
					ticket = Toolkit.getTicket(body);
					
					String probableMatch = "\\<legend\\>Seleccione\\s*un\\s*N.mero\\s*de\\s*Autorizaci.n\\<\\/legend\\>";
					Pattern pattern = Pattern.compile(probableMatch, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher matcher = pattern.matcher(body);
					if (matcher.find()) {
						manyAuths = true;
					}
					
					
					HttpPost httpPost;
					
					if (manyAuths) {
						
						httpPost = new HttpPost(link);
						List<NameValuePair> params = new ArrayList<>();
						params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
						params.add(new BasicNameValuePair("NUM_AUTORIZADO", "0"));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_ACCEPT, IServicioRedConstants.ACCEPT));
						
						httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
						
						body = Toolkit.getBodyPOST(httpClient, httpPost);
						Toolkit.checkProsaError(body);
						link = Toolkit.getLinkPROSA(body);
						ticket = Toolkit.getTicket(body);
					}
					
					httpPost = new HttpPost(link);
					List<NameValuePair> params = new ArrayList<>();
					params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
					params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
					params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
					params.add(new BasicNameValuePair(IServicioRedConstants.SEARCH_MODE, "CCC"));
					params.add(new BasicNameValuePair("CCC", ccc));
					params.add(new BasicNameValuePair(IServicioRedConstants.REGIME, regime.getValue()));
					params.add(new BasicNameValuePair(IServicioRedConstants.MONTH_FROM, String.format("%tm", dateFrom)));
					params.add(new BasicNameValuePair(IServicioRedConstants.YEAR_FROM, String.format("%tY", dateFrom)));
					params.add(new BasicNameValuePair(IServicioRedConstants.MONTH_TO, String.format("%tm", dateTo)));
					params.add(new BasicNameValuePair(IServicioRedConstants.YEAR_TO, String.format("%tY", dateTo)));
					params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION_TYPE, liqType.getValue()));
					params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION_ORIGIN, liqOrigin.getValue()));
					params.add(new BasicNameValuePair(IServicioRedConstants.SPM_ACCEPT, IServicioRedConstants.ACCEPT));
					
					httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
					
					body = Toolkit.getBodyPOST(httpClient, httpPost);
					Toolkit.checkProsaError(body);
					link = Toolkit.getLinkPROSA(body);
					ticket = Toolkit.getTicket(body);
					liqs = Toolkit.getNumberOfLiquidations(body);
					
					for (int liq=0; liq<liqs; liq++) {
						
						String type = null;
						httpPost = new HttpPost(link);
						
						String inLink = link;
						
						params = new ArrayList<>();
						params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
						params.add(new BasicNameValuePair(IServicioRedConstants.LIQUIDATION, String.valueOf(liq)));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTINUE, IServicioRedConstants.CONTINUE));
						
						httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
						
						
						body = Toolkit.getBodyPOST(httpClient, httpPost);
						Toolkit.checkProsaError(body);
						inLink = Toolkit.getLinkPROSA(body);
						ticket = Toolkit.getTicket(body);
						type = Toolkit.getLiqType(body);
						
						
						httpPost = new HttpPost(inLink);
						
						params = new ArrayList<>();
						params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
						params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
						params.add(new BasicNameValuePair("SPM.ACC.CONSULTA_TRABAJADORES", "Consulta+de+Trabajadores"));
						
						httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
						
						boolean inputText = false;
						Map<String, String> nafValues = null;
						
						
						body = Toolkit.getBodyPOST(httpClient, httpPost);
						inputText = Toolkit.idExists("NAF_TRABAJADOR", body);
						
						if (!inputText) {
							nafValues = Toolkit.getNafValues(body);
						}
						
						Toolkit.checkProsaError(body);
						inLink = Toolkit.getLinkPROSA(body);
						ticket = Toolkit.getTicket(body);
						
						Map<String,Map<Period, Map<String, Calc>>> nafMap = new LinkedHashMap<>();
						
						for (String naf : nafs) {
							Map<Period, Map<String, Calc>> periods = new LinkedHashMap<>();
							httpPost = new HttpPost(inLink);
							
							params = new ArrayList<>();
							params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
							params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
							params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
							
							if (inputText)
								params.add(new BasicNameValuePair("NAF_TRABAJADOR", naf));
							else if (nafValues.containsKey(naf))
								params.add(new BasicNameValuePair("NAF", nafValues.get(naf)));
							
							params.add(new BasicNameValuePair("SPM.ACC.CONSULTAR", "Consultar"));
							
							httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
							
							
							body = Toolkit.getBodyPOST(httpClient, httpPost);
							inLink = Toolkit.getLinkPROSA(body);
							ticket = Toolkit.getTicket(body);
							
							
							boolean err = Toolkit.isThereProsaError(body);
							
							if (!err) {
								String mainTable = Toolkit.getTable(body);
								Collection<String> mainTrs = Toolkit.getTrs(mainTable);
								LinkedHashMap<String, Calc> shiranai = new LinkedHashMap<>();
								for (String tr : mainTrs) {
									
									List<String> tds = Toolkit.getTdsTexts(tr);
									
									if (tds.size() == 5) {
										String description = tds.get(0);
										Double base = Toolkit.strToDouble(tds.get(1));
										Double enterprise = Toolkit.strToDouble(tds.get(2));
										Double employee = Toolkit.strToDouble(tds.get(3));
										Double total = Toolkit.strToDouble(tds.get(4));
										
										Calc calc = new Calc()
												.setBase(base)
												.setTotal(total)
												.setEmployee(employee)
												.setEnterprise(enterprise)
												;
										
										shiranai.put(description, calc);
										
									}
									
									
								}
								periods.put(null, shiranai);
								
								
								httpPost = new HttpPost(inLink);
								
								params = new ArrayList<>();
								params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
								params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
								params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
								params.add(new BasicNameValuePair("SPM.ACC.RELACION_TRAMOS", "Relaci%F3n+de+Tramos"));
								
								httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
								
								
								body = Toolkit.getBodyPOST(httpClient, httpPost);
								inLink = Toolkit.getLinkPROSA(body);
								ticket = Toolkit.getTicket(body);
								List<String> relations = Toolkit.getRelations(body);

								int i = 0;
								for (String relation : relations) {
									
									List<String> periodTds = Toolkit.getTdsTexts(relation);
									Date fromDate = Toolkit.parseDate(periodTds.get(1), "dd/MM/yyyy");
									Date toDate = Toolkit.parseDate(periodTds.get(2), "dd/MM/yyyy");
									Double quoteDays = Toolkit.strToDouble(periodTds.get(3));
									Double hours = Toolkit.strToDouble(periodTds.get(4));
									Double baseCC = Toolkit.strToDouble(periodTds.get(5));
									Double baseAT = Toolkit.strToDouble(periodTds.get(6));
									
									
									
									
									Period period = 
											new Period(fromDate, toDate)
											.setHours(hours)
											.setBaseAT(baseAT)
											.setBaseCC(baseCC)
											.setQuoteDays(quoteDays)
											;
									
									params = new ArrayList<>();
									params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
									params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
									params.add(new BasicNameValuePair(IServicioRedConstants.SPM_PORTALTYPE, "HTML"));
									params.add(new BasicNameValuePair("TRAMO", String.valueOf(i++)));
									params.add(new BasicNameValuePair("SPM.ACC.CALCULOS_TRAMO", "C%E1lculos+del+Tramo"));
									
									httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
									
									body = Toolkit.getBodyPOST(httpClient, httpPost);
									inLink = Toolkit.getLinkPROSA(body);
									ticket = Toolkit.getTicket(body);
									
									String table = Toolkit.getTable(body);
									Collection<String> trs = Toolkit.getTrs(table);
									
									LinkedHashMap<String, Calc> calcs = new LinkedHashMap<>();
									
									for (String tr : trs) {
										List<String> tds = Toolkit.getTdsTexts(tr);
										
										String description = Toolkit.removeWeirdCharacters(tds.get(0));
										
										Double base = Toolkit.strToDouble(tds.get(1));
										Double enterprisePercent = Toolkit.strToDouble(tds.get(2));
										Double enterprise = Toolkit.strToDouble(tds.get(3));
										Double employeePercent = Toolkit.strToDouble(tds.get(4));
										Double employee = Toolkit.strToDouble(tds.get(5));
										Double total = Toolkit.strToDouble(tds.get(6));
										
										Calc calc = new Calc()
												.setBase(base)
												.setTotal(total)
												.setEmployee(employee)
												.setEnterprise(enterprise)
												.setEmployeePercent(employeePercent)
												.setEnterprisePercent(enterprisePercent)
												;
										calcs.put(description, calc);

									}
									
									Toolkit.goBack(httpClient, inLink, ticket);										
									periods.put(period, calcs);
									
								}
								
								nafMap.put(naf, periods);
								Toolkit.goBack(httpClient, inLink, ticket);
								Toolkit.goBack(httpClient, inLink, ticket);
							}
							
							
						}
						
						Toolkit.goBack(httpClient, inLink, ticket);
						Toolkit.goBack(httpClient, inLink, ticket);
						ret.put(type, nafMap);
					}
				}
				
				return ret;
	}
	
	private static String nextPage(String body, CloseableHttpClient httpClient) throws SegSocialException, IOException {

		String link = Toolkit.getLink(body);
		String sessionId = Toolkit.getSessionId(body);
		
		HttpPost httpPost = new HttpPost(link);
		
		String btnValue = "P\u00e1g. Sig.";
		String btnNext = Toolkit.getElementByAttributeFirstTag(body, "value", btnValue);
		String btnName = Toolkit.getAttribute(btnNext, "name");

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
		params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM3701"));
		params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
		params.add(new BasicNameValuePair(btnName, btnValue));

		httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
		return Toolkit.getBodyPOST(httpClient, httpPost);
	}
}
