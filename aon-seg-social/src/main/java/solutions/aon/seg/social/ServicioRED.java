package solutions.aon.seg.social;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

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

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.toolkit.Toolkit;
/**
 * Class to obtain resources from Social Security just sending POST/GET requests
 *
 */
public class ServicioRED {
	/**
	 * Pattern to obtain the action link and session id from forms
	 */
	private static final Pattern FORM_PATTERN = Pattern.compile("\\<form.*action=\"(?<link>.+?SessionId=(?<session>[^\\&]+?)\\&.*?)\"");

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
	public static byte[] getIDCPost (final InputStream certificateInputStream,final String certificatePassword,
		final String certificateType, String affiliationNumber, String regime,String ccc, Date date) throws SegSocialException {
			
		SSLContext sslContext = null;
			try {
				sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
			} catch (Exception e1) {
				throw new InvalidCertificateException();
			}
			String link = "";
			String sessionId = "";
			
			
			try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
				try (CloseableHttpResponse resp = httpClient.execute(new HttpGet("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR"))) {
					Toolkit.checkResponseStatus(resp);
					String body = EntityUtils.toString(resp.getEntity(), "UTF-8");
					Matcher matcher = FORM_PATTERN.matcher(body);
					if (matcher.find()) {
						
						String params = matcher.group("link").replaceAll("&amp;", "&");
						link = "https://w2.seg-social.es/" + params;
						sessionId = matcher.group("session");
					}
				}
				
				HttpPost httpPost = new HttpPost(link);
				
				String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
				String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

				String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
				String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("txt_SDFTESNAF", txtSDFTESNAF));
				params.add(new BasicNameValuePair("txt_SDFNAF", txtSDFNAF));
				params.add(new BasicNameValuePair("txt_SDFREGCTA", regime));
				params.add(new BasicNameValuePair("txt_SDFTESCTA", txtSDFTESCTA));
				params.add(new BasicNameValuePair("txt_SDFCUENTA", txtSDFCUENTA));
				params.add(new BasicNameValuePair("txt_SDFDIA", String.format("%td", date)));
				params.add(new BasicNameValuePair("txt_SDFMES", String.format("%tm", date)));
				params.add(new BasicNameValuePair("txt_SDFAO", String.format("%tY", date)));
				params.add(new BasicNameValuePair("cbo_ListaTipoImpresion", "OnLine"));
				
				params.add(new BasicNameValuePair("Applname", "LIBAFCON"));
				params.add(new BasicNameValuePair("Formname", "ATRM3700"));
				params.add(new BasicNameValuePair("sessionId", sessionId));
				params.add(new BasicNameValuePair("focusedControl", "Sub2207601004"));
//				params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//				params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//				params.add(new BasicNameValuePair("clientDebugLevel", "0"));
				params.add(new BasicNameValuePair("default_null", "1"));
//				params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//				params.add(new BasicNameValuePair("txt_Transac", "Atr37"));
//				params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
//				params.add(new BasicNameValuePair("txt_CommandEdit", "Atr37"));
//				params.add(new BasicNameValuePair("txt_SDFTEXTO1", ""));
//				params.add(new BasicNameValuePair("txt_SDFTEXTO2", ""));
//				params.add(new BasicNameValuePair("txt_SDFTEXTO3", ""));
//				params.add(new BasicNameValuePair("txt_SDFTEXTO4", ""));
//				params.add(new BasicNameValuePair("txt_SDFAUTORIZORIGI", ""));
//				params.add(new BasicNameValuePair("txt_SDFTEXTO5", ""));
//				params.add(new BasicNameValuePair("txt_SDFSILCONORIGI", ""));
//				params.add(new BasicNameValuePair("txt_SDFTEXTO6", ""));
//				params.add(new BasicNameValuePair("txt_SDFNIFORIGI", ""));
				params.add(new BasicNameValuePair("btn_Sub2207601004", "Continuar"));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				
				try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
					Toolkit.checkResponseStatus(resp);
					HttpEntity entity = resp.getEntity();
				
					if (entity != null) {
						String body = EntityUtils.toString(entity, "UTF-8");
						String error = Toolkit.getDIL(body);
						
						if (Toolkit.getErrCode(error) != null)
							InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
						
						Matcher matcher = FORM_PATTERN.matcher(body);
						if (matcher.find()) {
							link = "https://w2.seg-social.es/" + matcher.group("link").replaceAll("&amp;", "&");
							sessionId = matcher.group("session");
						}
					}
				}
				
				httpPost = new HttpPost(link);
				
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("Applname", "LIBAFCON"));
				params.add(new BasicNameValuePair("Formname", "ATRM3701"));
				params.add(new BasicNameValuePair("sessionId", sessionId));
				params.add(new BasicNameValuePair("focusedControl", "tbl_cbo_Sub0900112078_0_0"));
//				params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//				params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//				params.add(new BasicNameValuePair("clientDebugLevel", "0"));
				params.add(new BasicNameValuePair("default_null", "1"));
//				params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//				params.add(new BasicNameValuePair("txt_Transac", "Atr37"));
//				params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
				params.add(new BasicNameValuePair("txt_CommandEdit", "EN"));
				params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
				params.add(new BasicNameValuePair("tbl_cbo_Sub0900112078_0_0", "Select"));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				
				try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
					Toolkit.checkResponseStatus(resp);
					HttpEntity entity = resp.getEntity();
				
					if (entity != null) {
						String body = EntityUtils.toString(entity, "UTF-8");
						httpPost = Toolkit.reportGenerationForm(body);
					}
				}
				
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
			
			try (CloseableHttpResponse resp = httpClient.execute(new HttpGet("https://w2.seg-social.es//Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR"))) {
				Toolkit.checkResponseStatus(resp);
				String body = EntityUtils.toString(resp.getEntity(), "UTF-8");
				
				Matcher matcher = FORM_PATTERN.matcher(body);
				if (matcher.find()) {
					
					String params = matcher.group("link").replaceAll("&amp;", "&");
					
					link = "https://w2.seg-social.es/" + params;
					sessionId = matcher.group("session");
				}
			}
			
			HttpPost httpPost = new HttpPost(link);

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Applname", "LIBAFCON"));
			params.add(new BasicNameValuePair("Formname", "ATRM3800"));
			params.add(new BasicNameValuePair("sessionId", sessionId));
			params.add(new BasicNameValuePair("focusedControl", "Sub2207601004"));
//			params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//			params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//			params.add(new BasicNameValuePair("clientDebugLevel", "0"));
			params.add(new BasicNameValuePair("default_null", "1"));
//			params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//			params.add(new BasicNameValuePair("txt_Transac", "Atr38"));
//			params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
//			params.add(new BasicNameValuePair("txt_CommandEdit", "Atr38"));
			params.add(new BasicNameValuePair("txt_SDFREGCTA", regime));
			params.add(new BasicNameValuePair("txt_SDFTESCTA", txtSDFTESCTA));
			params.add(new BasicNameValuePair("txt_SDFCUENTA", txtSDFCUENTA));
			params.add(new BasicNameValuePair("cbo_ListaTipoImpresion", "OnLine"));
//			params.add(new BasicNameValuePair("txt_SDFREGCTAP", ""));
//			params.add(new BasicNameValuePair("txt_SDFTESCTAP", ""));
//			params.add(new BasicNameValuePair("txt_SDFCUENTAP", ""));
//			params.add(new BasicNameValuePair("txt_SDFNUMAUT", ""));
			params.add(new BasicNameValuePair("txt_SDFMES", String.format("%tm", date)));
			params.add(new BasicNameValuePair("txt_SDFAO", String.format("%tY", date)));
			params.add(new BasicNameValuePair("chk_SDFSELEC", "1"));
			params.add(new BasicNameValuePair("btn_Sub2207601004", "Continuar"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
				
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
					String error = Toolkit.getDIL(body);
					
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					httpPost = Toolkit.reportGenerationForm(body);
				}
			}
			
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
			
			try (CloseableHttpResponse resp = httpClient.execute(new HttpGet("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR"))) {
				Toolkit.checkResponseStatus(resp);
				String body = EntityUtils.toString(resp.getEntity(), "UTF-8");
				
				Matcher matcher = FORM_PATTERN.matcher(body);
				if (matcher.find()) {
					
					String params = matcher.group("link").replaceAll("&amp;", "&");
					
					link = "https://w2.seg-social.es/" + params;
					sessionId = matcher.group("session");
				}
			}
			
			HttpPost httpPost = new HttpPost(link);

			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Applname", "LIBAFCON"));
			params.add(new BasicNameValuePair("Formname", "ATRM3900"));
			params.add(new BasicNameValuePair("sessionId", sessionId));
			params.add(new BasicNameValuePair("focusedControl", "Sub2207601004"));
//			params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//			params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//			params.add(new BasicNameValuePair("clientDebugLevel", "0"));
			params.add(new BasicNameValuePair("default_null", "1"));
//			params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//			params.add(new BasicNameValuePair("txt_Transac", "Atr39"));
//			params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
//			params.add(new BasicNameValuePair("txt_CommandEdit", "Atr39"));
			params.add(new BasicNameValuePair("txt_SDFTESNAF", txtSDFTESNAF));
			params.add(new BasicNameValuePair("txt_SDFNAF", txtSDFNAF));
			params.add(new BasicNameValuePair("txt_SDFREGCTA_ayuda", regime));
			params.add(new BasicNameValuePair("txt_SDFTESCTA", txtSDFTESCTA));
			params.add(new BasicNameValuePair("txt_SDFCUENTA", txtSDFCUENTA));
			params.add(new BasicNameValuePair("txt_SDFMES", String.format("%tm", date)));
			params.add(new BasicNameValuePair("txt_SDFAO", String.format("%tY", date)));
			params.add(new BasicNameValuePair("cbo_ListaTipoImpresion", "OnLine"));
			params.add(new BasicNameValuePair("btn_Sub2207601004", "Continuar"));
			
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
				
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
					
					String error = Toolkit.getDIL(body);
					
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					httpPost = Toolkit.reportGenerationForm(body);
				}
			}
			
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
			try (CloseableHttpResponse resp = httpClient.execute(new HttpGet("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR"))) {
				Toolkit.checkResponseStatus(resp);
				String body = EntityUtils.toString(resp.getEntity(), "UTF-8");
				Matcher matcher = FORM_PATTERN.matcher(body);
				if (matcher.find()) {
					
					String params = matcher.group("link").replaceAll("&amp;", "&");
					link = "https://w2.seg-social.es/" + params;
					sessionId = matcher.group("session");
				}
			}
			
			HttpPost httpPost = new HttpPost(link);
			
			String txtSDFTESNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(0, 2) : "";
			String txtSDFNAF = affiliationNumber.length() > 2 ? affiliationNumber.substring(2) : "";

			String txtSDFTESCTA = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCUENTA = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Applname", "LIBAFCON"));
			params.add(new BasicNameValuePair("Formname", "ATRM6500"));
			params.add(new BasicNameValuePair("sessionId", sessionId));
			params.add(new BasicNameValuePair("focusedControl", "Sub2207601004"));
//			params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//			params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//			params.add(new BasicNameValuePair("clientDebugLevel", "0"));
			params.add(new BasicNameValuePair("default_null", "1"));
//			params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//			params.add(new BasicNameValuePair("txt_Transac", "Atr65"));
//			params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
//			params.add(new BasicNameValuePair("txt_CommandEdit", "Atr65"));
			params.add(new BasicNameValuePair("txt_SDFTESNAF", txtSDFTESNAF));
			params.add(new BasicNameValuePair("txt_SDFNAF", txtSDFNAF));
			params.add(new BasicNameValuePair("txt_SDFREGCTA_NH", regime));
			params.add(new BasicNameValuePair("txt_SDFTESCTA", txtSDFTESCTA));
			params.add(new BasicNameValuePair("txt_SDFCUENTA", txtSDFCUENTA));
			params.add(new BasicNameValuePair("txt_SDFDIA", String.format("%td", date)));
			params.add(new BasicNameValuePair("txt_SDFMES", String.format("%tm", date)));
			params.add(new BasicNameValuePair("txt_SDFAO", String.format("%tY", date)));
			params.add(new BasicNameValuePair("cbo_ListaTipoImpresion", "OnLine"));
			params.add(new BasicNameValuePair("chk_SDFINFTA1", "1"));
//			params.add(new BasicNameValuePair("txt_SDFTEXTO1", ""));
//			params.add(new BasicNameValuePair("txt_SDFTEXTO2", ""));
//			params.add(new BasicNameValuePair("txt_SDFTEXTO3", ""));
//			params.add(new BasicNameValuePair("txt_SDFTEXTO4", ""));
//			params.add(new BasicNameValuePair("txt_SDFAUTORIZORIGI", ""));
//			params.add(new BasicNameValuePair("txt_SDFTEXTO5", ""));
//			params.add(new BasicNameValuePair("txt_SDFSILCONORIGI", ""));
//			params.add(new BasicNameValuePair("txt_SDFTEXTO6", ""));
//			params.add(new BasicNameValuePair("txt_SDFNIFORIGI", ""));
			params.add(new BasicNameValuePair("btn_Sub2207601004", ""));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
			
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
					String error = Toolkit.getDIL(body);
					
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					Matcher matcher = FORM_PATTERN.matcher(body);
					if (matcher.find()) {
						link = "https://w2.seg-social.es/" + matcher.group("link").replaceAll("&amp;", "&");
						sessionId = matcher.group("session");
					}
				}
			}
			
			httpPost = new HttpPost(link);
			
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Applname", "LIBAFCON"));
			params.add(new BasicNameValuePair("Formname", "ATRM6501"));
			params.add(new BasicNameValuePair("sessionId", sessionId));
			params.add(new BasicNameValuePair("focusedControl", "tbl_cbo_Sub0900112079_0_0"));
//			params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//			params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//			params.add(new BasicNameValuePair("clientDebugLevel", "0"));
			params.add(new BasicNameValuePair("default_null", "1"));
//			params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//			params.add(new BasicNameValuePair("txt_Transac", "Atr65"));
//			params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
			params.add(new BasicNameValuePair("txt_CommandEdit", "EN"));
			params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
			params.add(new BasicNameValuePair("tbl_cbo_Sub0900112079_0_0", "Select"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
				
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
					
					String error = Toolkit.getDIL(body);
					
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					httpPost = Toolkit.reportGenerationForm(body);
				}
			}
			
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
			try (CloseableHttpResponse resp = httpClient.execute(new HttpGet("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR"))) {
				Toolkit.checkResponseStatus(resp);
				String body = EntityUtils.toString(resp.getEntity(), "UTF-8");
				focusedControl = Toolkit.getButtonNameByValue(body, "Continuar");
				Matcher matcher = FORM_PATTERN.matcher(body);
				if (matcher.find()) {
					
					String params = matcher.group("link").replaceAll("&amp;", "&");
					link = "https://w2.seg-social.es/" + params;
					sessionId = matcher.group("session");
				}
			}
			
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Applname", "SGIRED"));
			params.add(new BasicNameValuePair("Formname", "RCCM5201"));
			params.add(new BasicNameValuePair("sessionId", sessionId));
			params.add(new BasicNameValuePair("focusedControl", focusedControl));
//			params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
//			params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
//			params.add(new BasicNameValuePair("clientDebugLevel", "0"));
			params.add(new BasicNameValuePair("default_null", "1"));
//			params.add(new BasicNameValuePair("txt_EntornoPr", "0"));
//			params.add(new BasicNameValuePair("txt_Transac", "Rcr92"));
//			params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
//			params.add(new BasicNameValuePair("txt_CommandEdit", "Rcr92"));
			params.add(new BasicNameValuePair("txt_SDFWMIDENT", ccc));
			params.add(new BasicNameValuePair("txt_SDFWMRESU", regime));
//			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
//			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
//			params.add(new BasicNameValuePair("chk_chkgrupo1_3", "1"));
//			params.add(new BasicNameValuePair("chk_detalle_deuda", "1"));
			params.add(new BasicNameValuePair("cbo_ListaTipoImpresion", "OnLine"));
			params.add(new BasicNameValuePair(focusedControl, "Continuar"));
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
			
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
//					System.out.println(body);
					focusedControl = Toolkit.getButtonNameByValue(body, "Confirmar");
					
					String error = Toolkit.getDIL(body);
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					Matcher matcher = FORM_PATTERN.matcher(body);
					if (matcher.find()) {
						link = "https://w2.seg-social.es/" + matcher.group("link").replaceAll("&amp;", "&");
						sessionId = matcher.group("session");
					}
				}
			}
			
			httpPost = new HttpPost(link);
			
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Applname", "SGIRED"));
			params.add(new BasicNameValuePair("Formname", "RCCM5201"));
			params.add(new BasicNameValuePair("sessionId", sessionId));
			params.add(new BasicNameValuePair("focusedControl", focusedControl));
			params.add(new BasicNameValuePair("keepAliveURL", "/KeepAlive?"));
			params.add(new BasicNameValuePair("keepAliveInterval", "180000"));
			params.add(new BasicNameValuePair("clientDebugLevel", "0"));
			params.add(new BasicNameValuePair("default_null", "1"));
			params.add(new BasicNameValuePair("txt_EntornoPr", ""));
			params.add(new BasicNameValuePair("txt_Transac", "Rcr92"));
			params.add(new BasicNameValuePair("txt_MenuPracticas", "I"));
			params.add(new BasicNameValuePair("txt_CommandEdit", "Rcr92"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_3", "1"));
			params.add(new BasicNameValuePair("txt_inputgrupo1_3_1", ""));
			params.add(new BasicNameValuePair("txt_inputgrupo1_3_2", ""));
			params.add(new BasicNameValuePair("txt_inputgrupo1_3_3", ""));
			params.add(new BasicNameValuePair("txt_inputgrupo1_3_4", ""));
			params.add(new BasicNameValuePair("txt_inputgrupo1_3_5", ""));
			params.add(new BasicNameValuePair("chk_detalle_deuda", "1"));
			params.add(new BasicNameValuePair("cbo_ListaTipoImpresion", "OnLine"));
			params.add(new BasicNameValuePair(focusedControl, "Confirmar"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
			
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
					String error = Toolkit.getDIL(body);
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					Matcher matcher = FORM_PATTERN.matcher(body);
					if (matcher.find()) {
						link = "https://w2.seg-social.es/" + matcher.group("link").replaceAll("&amp;", "&");
						sessionId = matcher.group("session");
					}
				}
			}
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				HttpEntity entity = resp.getEntity();
				
				if (entity != null) {
					String body = EntityUtils.toString(entity, "UTF-8");
					
					String error = Toolkit.getDIL(body);
					
					if (Toolkit.getErrCode(error) != null)
						InvalidDataException.checkCode(Toolkit.getErrCode(error), Toolkit.getErrMsg(error));
					
					httpPost = Toolkit.reportGenerationForm(body);
				}
			}
			
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
}
