package solutions.aon.seg.social;

import static java.lang.Long.parseLong;
import static solutions.aon.seg.social.toolkit.Toolkit.parseDate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.object.SecondaryUser.SecondaryUserBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

public class ServicioREDSecondaryUser extends ServicioREDRegeXML {
	
	
	public static List<SecondaryUser> getSecondaryUsers(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws SegSocialException, IOException {
		SSLContext sslContext = null;
		List<SecondaryUser> users = new LinkedList<>();
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=NRW67&E=I&AP=AUT");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			if (Toolkit.getElementByAttribute(body, "id", "Sub0700410072_2_0") != null) {
				
				HttpPost httpPost = new HttpPost(link);
				
				List<NameValuePair> params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "NREMCUS1"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				params.add(new BasicNameValuePair("txt_CommandEdit", "EN"));
				params.add(new BasicNameValuePair("btn_FkeyButton", "+"));
				params.add(new BasicNameValuePair("tbl_cbo_Sub0700410072_0_0", "Select"));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				checkOldSsError(body);
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);
			}
			
			
			String btnName = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "value", "Continuar"), "name");
			emptyUserSecundary(body);
			// La autorización XXXXX de la que es usted principal 
			// no tiene asociado ningún secundario
			if(Toolkit.getElementByAttributeFirstTag(body, "id", "chkgrupo1_1") == null)
				return Collections.emptyList();
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "NREMCUS2"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_1", "1"));
			params.add(new BasicNameValuePair("chk_chkgrupo1_2", "1"));
			params.add(new BasicNameValuePair(btnName, IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			checkOldSsError(body);
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			boolean endOfData = false;
			
			while (!endOfData) {
				
				httpPost = new HttpPost(link);
				params = new ArrayList<>();
				params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
				params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "NREMCUS3"));
				params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
				for (int i=0; i<=9; i++) {
					params.add(new BasicNameValuePair("tbl_Sub0800010080_0_" + i + "_chk_", "1"));
					params.add(new BasicNameValuePair("tbl_Sub0800010080_0_" + i + "_chk_", "1"));
				}
				params.add(new BasicNameValuePair("btn_Sub2205301004_72", "Consultar+Usuario"));
				
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				checkOldSsError(body);
				link = Toolkit.getLink(body);
				sessionId = Toolkit.getSessionId(body);
				
				boolean queryEnd = false;
				
				//Texto para comprobar que ha entrado en un usuario secundario
				String affNum = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNAFAUT"), "innerText");
				
				queryEnd = affNum == null;
				
				while (!queryEnd) {
					
					users.add(getSecondaryUserInfo(body));
					
					btnName = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "value", "Continuar"), "name");
					
					httpPost = new HttpPost(link);
					params = new ArrayList<>();
					params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
					params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "NREMCUS4"));
					params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
					params.add(new BasicNameValuePair(btnName, IServicioRedConstants.CONTINUE));
					httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
					body = Toolkit.getBodyPOST(httpClient, httpPost);
					checkOldSsError(body);
					link = Toolkit.getLink(body);
					sessionId = Toolkit.getSessionId(body);
					
					affNum = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNAFAUT"), "innerText");
					queryEnd = affNum == null;
					
				}
				String nextPageName = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "value", "Pág. Sig."), "name");
				endOfData = nextPageName == null;
				
				if (!endOfData) {
					httpPost = new HttpPost(link);
					params = new ArrayList<>();
					params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, "SGIRED"));
					params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "NREMCUS3"));
					params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
					for (int i=0; i<=9; i++) {
						params.add(new BasicNameValuePair("tbl_Sub0800010080_0_" + i + "_chk_", "1"));
					}
					params.add(new BasicNameValuePair(nextPageName, "Pág.+Sig."));
					httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
					body = Toolkit.getBodyPOST(httpClient, httpPost);
					checkOldSsError(body);
					link = Toolkit.getLink(body);
					sessionId = Toolkit.getSessionId(body);
				}
				
			}
		}
		
		
		return users;
	}
	
	public static SecondaryUser getSecondaryUserInfo(String body) {
		Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFAUTORIZ"), "innerText");
		// OBJECT CREATE
		String authoritation = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFAUTORIZ"), "innerText");
		String authoritationEntity = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFRAZSOCIALAUT"), "innerText");

		String mainUserName = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNOMBREAUT"), "innerText");
		String mainUserIpf = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFIPFAUT"), "innerText");
		String mainUserNaf = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNAFAUT"), "innerText");

		String name = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNOMBRESEC"), "innerText");
		String province = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFPROVSEC"), "innerText");
		String naf = getRealNAF(Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFNAFSEC"), "innerText"));
		String situation = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFSITSEC"), "innerText");
		Date situationDate = parseDate(Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFECSITSEC"), "innerText"), "dd/MM/yyyy");
		String telephone = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFTELEFONOSEC"), "innerText");
		String fax = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFFAXSEC"), "innerText");
		String mobile = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "SDFMOVILSEC"), "innerText");
		String mail = Toolkit.getCleanAttribute(Toolkit.getElementByAttribute(body, "id", "txtconcat1_2"), "innerText");

		SecondaryUserBuilder builder = new SecondaryUserBuilder();

		builder.setAuthoritation(authoritation).setAuthoritationEntity(authoritationEntity)
				.setMainUserName(mainUserName).setMainUserIpf(mainUserIpf).setMainUserNaf(mainUserNaf).setName(name)
				.setProvince(province).setNaf(naf).setSituation(situation).setSituationDate(situationDate)
				.setTelephone(telephone).setFax(fax).setMobile(mobile).setMail(mail);

		return builder.build();
	}
	
	private static String getRealNAF(String naf) {
		if (naf == null || naf.isEmpty())
			return null;
		
		if (naf.length() == 9)
			naf = "0" + naf;

		String province = naf.substring(0, 2);
		String nafCenter = naf.substring(2, naf.length());

		String completeNAF = "";

		if (nafCenter.substring(0, 1).equals("0")) {
			Long nafD = parseLong(province + nafCenter.substring(1, nafCenter.length()));
			long mod = (nafD % 97);
			completeNAF = nafD + "" + (mod < 10 ? "0" + mod : mod);
		} else {
			Long nafD = parseLong(province + nafCenter);
			long mod = (nafD % 97);
			completeNAF = nafD + "" + (mod < 10 ? "0" + mod : mod);
		}

		if (completeNAF.length() == 11)
			completeNAF = "0" + completeNAF;

		return completeNAF;
	}
	
	//EXPERIMENTAL/UNPROVED
	private static void emptyUserSecundary(String body) throws NoQueryData{
		String msgOne = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "Sub1000401071"), "innerText");
		if(msgOne!=null) {
			String msgTwo = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "Sub1100401071"), "innerText");
			String msgError = msgOne;
			if(msgTwo!=null) {
				msgError += " ";
				msgError = msgError.concat(msgTwo);
			}
			throw new NoQueryData(msgError);
		}
	}
	
}