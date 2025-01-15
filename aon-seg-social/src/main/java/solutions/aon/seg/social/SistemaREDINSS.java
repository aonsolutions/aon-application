package solutions.aon.seg.social;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

public class SistemaREDINSS {
    
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
	    } catch (TransformerException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public static byte[] getFIE(InputStream certificateInputStream, String certificatePassword,
		String certificateType, String regime, String ccc, Date startDate, Date endDate) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, TransformerException {
	   
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
	    
		    webClient.getOptions().setUseInsecureSSL(true);
		    
		    HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage( webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV26L100") );
			
		    try {
		    	((HtmlRadioButtonInput) htmlPage.getElementById("OPCIONES_BUSQUEDA_4")).setChecked(true);
		    } catch (Exception e) {
				// TODO: handle exception
			}
			
		    HtmlInput regimenInput = (HtmlInput) htmlPage.getElementById("REG");
		    regimenInput.setValue(regime);
		    regimenInput.setValueAttribute(regime);
		    
		    HtmlInput provinceInput = (HtmlInput) htmlPage.getElementById("PROV");
		    provinceInput.setValue(ccc.substring(0, 2));
		    provinceInput.setValueAttribute(ccc.substring(0, 2));
		    
		    HtmlInput cccInput = (HtmlInput) htmlPage.getElementById("NUM");
		    cccInput.setValue(ccc.substring(2));
		    cccInput.setValueAttribute(ccc.substring(2));
		    
		    HtmlInput startInput = (HtmlInput) htmlPage.getElementById("fechaIniEmpresa");
		    startInput.setValue(DATE_FORMAT.format(startDate));
		    startInput.setValueAttribute(DATE_FORMAT.format(startDate));
		    
		    HtmlInput endInput = (HtmlInput) htmlPage.getElementById("fechaFinEmpresa");
		    endInput.setValue(DATE_FORMAT.format(endDate));
		    endInput.setValueAttribute(DATE_FORMAT.format(endDate));
		    
		    HtmlButton send = (HtmlButton) htmlPage.getElementById("ENVIO_10");
		    
		    htmlPage = HtmlUnitToolkit.transformXmlPage(send.click());
		    
		    HtmlButton download = (HtmlButton) htmlPage.getElementById("ENVIO_15");
		    
		    try {
			    htmlPage = HtmlUnitToolkit.transformXmlPage(download.click());
	
			    HtmlAnchor downloadAnchor = (HtmlAnchor) htmlPage.querySelector("#prevdocumentoseinformes a");
			    Page page = downloadAnchor.click();
			    
			    byte[] documentBytes = page.getWebResponse().getContentAsStream().readAllBytes();
			    return documentBytes;
		    } catch (Exception e) {
				return null;
			}
	    }      
	}
	
	private static String getJsessionid(XmlPage xmlPage) {
	    	Matcher matcher = Pattern.compile(";jsessionid=\\S+", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE ).matcher(xmlPage.asXml());
	    	matcher.find(); 
	    	return matcher.group();
	}
}
