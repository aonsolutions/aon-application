package solutions.aon.seg.social;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

public class SistemaREDINSS {

//	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//
//		String regimen = "0111";
//		String ccc = "11122534302";
//		String certificate = "/tmp/AyudaTFNMT.p12";
//		Date startDate = null;
//		Date endDate = null;
//		String sDate = "16/05/2022";
//		String eDate = "30/05/2022";
//		File file = new File(certificate);
//		final InputStream certificateInputStream = new FileInputStream(file);
//		final String certificatePassword = "123456";
//		final String certificateType = "pkcs12";
//		String authorized = "127770";
//		String href = "/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24J001";
//
//		// Cambiar datos
//		SistemaREDINSS ins = new SistemaREDINSS();
//		ins.getINSSFIE(certificateInputStream, certificatePassword, certificateType, regimen, ccc, startDate, endDate, sDate, eDate);
//
//	}
//
//
//	private static byte[] getFIE(InputStream certificateInputStream, String certificatePassword, String certificateType,
//			String regime, String ccc, Date startDate, Date endDate, String sDate, String eDate) throws InvalidCertificateException,
//			FailingHttpStatusCodeException, MalformedURLException, IOException, ParseException, InterruptedException {
//		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
//				certificateType)) {
//			HtmlPage htmlPage = webClient.getPage(
//					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV26L100");
//
//			((HtmlRadioButtonInput) htmlPage.getElementById("OPCIONES_BUSQUEDA_3")).setChecked(true);
//			HtmlForm searchForm = htmlPage.getHtmlElementById("FORMULARIO_1");
//			searchForm.getInputByName("regimen").setValueAttribute(regime);
//			searchForm.getInputByName("provincia").setValueAttribute(ccc.substring(0, 2));
//			searchForm.getInputByName("cccnum").setValueAttribute(ccc.substring(2));
//
//			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//			startDate = formatter.parse(sDate);
//			endDate = formatter.parse(eDate);
//
//			searchForm.getInputByName("fechaIniEmpresa").setValueAttribute(formatter.format(startDate));
//			searchForm.getInputByName("fechaFinEmpresa").setValueAttribute(formatter.format(endDate));
//
//			HtmlButton envio5 = (HtmlButton) htmlPage.getElementById("ENVIO_5");
//			// SPM.ACC.Accion_Buscar: Accion_Buscar
//			XmlPage xmlPage = envio5.click();
//
//			// SPM.ACC.DESCARGAR: DESCARGAR
//			envio5.setAttribute("name", "SPM.ACC.DESCARGAR");
//			envio5.setAttribute("value", "DESCARGAR");
//			xmlPage = envio5.click();
//
//			System.out.println(xmlPage.asXml());
//
//			// FICHERO FIE
//			java.util.regex.Matcher matcher = Pattern
//					.compile(";jsessionid=\\S+", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE).matcher(xmlPage.asXml());
//			matcher.find();
//			String jsessionid = matcher.group();
//			String fieUrl = String.format("/ProsaInternet/ViewDocUtf8%s?SECUENCIAL=1&TYPEVIEW=INFORME", jsessionid);
//
//			HtmlAnchor fieAnchor = (HtmlAnchor) htmlPage.createElement("a");
//			fieAnchor.setAttribute("href", fieUrl);
//			fieAnchor.setAttribute("target", "_blank");
//			searchForm.appendChild(fieAnchor);
//
//			Page page = fieAnchor.click();
//			////
//			// PRUEBA PARA VER LA DESCARGA
//			byte[] ret = null;
//			WebResponse response = HtmlUnitToolkit.wait4(page, p -> p.getWebResponse()).orElseGet(null);
//
//			InputStream is = response.getContentAsStream();
//			ret = is.readAllBytes();
//			is.close();
//			FileUtils.writeByteArrayToFile(new File("/home/jmortega/pruebaPdf/pruebaExcel2.xls"), ret);
//			// PRUEBA PARA VER LA DESCARGA
//			////
//			// https://w2.seg-social.es/ProsaInternet/ViewDocUtf8;jsessionid=0000VgpeaVfJBwsyQVfSNAl1ybY:18jagtf7j?SECUENCIAL=2&TYPEVIEW=INFORME
//			return page.getWebResponse().getContentAsStream().readAllBytes();
//		}
//	}
//	
//	
//	public static void getINSSFIE(InputStream certificateInputStream, String certificatePassword, String certificateType,
//			String regime, String ccc, Date startDate, Date endDate, String sDate, String eDate) {
//		try {
//			SistemaREDINSS.getFIE(certificateInputStream, certificatePassword, certificateType, regime, ccc, startDate, endDate, sDate, eDate);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy"); 

	public static void main(String[] args) throws Exception {
	    	
	    
	    	String regime = "0111";
		String ccc = "11122534302";
		String certificateType = "pkcs12";
		String certificatePassword = "123456";
		InputStream certificateInputStream = new FileInputStream("/home/rtrepiana/aon-workspace/aon-application/aon-seg-social/src/test/resources/solutions/aon/seg/social/AyudaTFNMT.p12");
		
		Date startDate = DATE_FORMAT.parse("16/05/2022"); 
		Date endDate = DATE_FORMAT.parse("30/05/2022"); 
		
		byte [] fie = getFIE(certificateInputStream, certificatePassword, certificateType, regime, ccc, startDate, endDate);
		
		try ( OutputStream os =  new FileOutputStream("/tmp/fie.xsl")) {
		    os.write(fie);
		}
		
		
	}
	
	public static byte[] getFIE(byte [] certifcateData, String certificatePassword,
		String certificateType, String regime, String ccc, Date startDate, Date endDate) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException {
	    try ( ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(certifcateData)){
		return getFIE(certificateInputStream, certificatePassword, certificateType, regime, ccc, startDate, endDate);
	    }
	}
	
	public static byte[] getFIE(InputStream certificateInputStream, String certificatePassword,
		String certificateType, String regime, String ccc, Date startDate, Date endDate) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
	    certificateType)){
		HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV26L100");
		
		((HtmlRadioButtonInput) htmlPage.getElementById("OPCIONES_BUSQUEDA_3")).setChecked(true);
		HtmlForm searchForm = htmlPage.getHtmlElementById("FORMULARIO_1");
		searchForm.getInputByName("regimen").setValue(regime);
		searchForm.getInputByName("provincia").setValue(ccc.substring(0, 2));
		searchForm.getInputByName("cccnum").setValue(ccc.substring(2));
		searchForm.getInputByName("fechaIniEmpresa").setValue(DATE_FORMAT.format(startDate));
		searchForm.getInputByName("fechaFinEmpresa").setValue(DATE_FORMAT.format(endDate));
		
		HtmlButton envio5Button = (HtmlButton ) htmlPage.getElementById("ENVIO_5");
		// SPM.ACC.Accion_Buscar: Accion_Buscar
		XmlPage xmlPage = envio5Button.click();
		
		//SPM.ACC.DESCARGAR: DESCARGAR
		envio5Button.setAttribute("name", "SPM.ACC.DESCARGAR");
		envio5Button.setAttribute("value", "DESCARGAR");
		xmlPage = envio5Button.click();
		
		// FICHERO FIE
	    	String jsessionid = getJsessionid(xmlPage);
	    	String fieUrl = String.format("https://w2.seg-social.es/ProsaInternet/ViewDocUtf8%s?SECUENCIAL=1&TYPEVIEW=INFORME", jsessionid );
	    	
	    	Page page = webClient.getPage(fieUrl);

		return page.getWebResponse().getContentAsStream().readAllBytes();
	    }      
	}
	
	private static String getJsessionid(XmlPage xmlPage) {
	    	Matcher matcher = Pattern.compile(";jsessionid=\\S+", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE ).matcher(xmlPage.asXml());
	    	matcher.find(); 
	    	return matcher.group();
	}
}
