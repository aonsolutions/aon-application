package solutions.aon.seg.social;

import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.xml.transform.TransformerException;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.UnexpectedPage;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaREDCCC {
    
    
    public static class CCC {
	private String regime;
	private String province;
	private String number;
	
	private String type;

	private String entrepriseName;
	
	public String getType() {
	    return type;
	}
	
	public String getNumber() {
	    return number;
	}
	
	public String getRegime() {
	    return regime;
	}
	
	public String getProvince() {
	    return province;
	}
	
	public String getEntrepriseName() {
	    return entrepriseName;
	}
    }
    
    public static void getAssignedCCCs(final byte[] certificateData, final String certificatePassword,
	    final String certificateType, Consumer<CCC> callback) 
		    throws SegSocialException, IOException{
	try ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData)){
	    getAssignedCCCs(certificateInputStream, certificatePassword, certificateType, callback);
	}
    }

    public static Stream<CCC> getAssignedCCCs(final InputStream certificateInputStream, final String certificatePassword,
	    final String certificateType)
	    throws SegSocialException, IOException {
	Builder<CCC> builder = Stream.builder();
	getAssignedCCCs(certificateInputStream, certificatePassword, certificateType, builder::add);
	return builder.build();
    }

    
    public static void getAssignedCCCs(final InputStream certificateInputStream, final String certificatePassword,
	    final String certificateType, Consumer<CCC> callback)
	    throws SegSocialException, IOException{

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
	
		    HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage( webClient.getPage(
			    "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P002") );
	
		    DomElement authorizedTable = htmlPage.getElementById("tabla_lisAutorizad");
		    if (authorizedTable == null) {
			getAssignedCCCs(htmlPage, callback);
		    }
		    
		    DomNodeList<HtmlElement> authorizedAnchors = authorizedTable.getElementsByTagName("a");
		    for (int i = 0; i < authorizedAnchors.size(); i++) {
			HtmlAnchor authorizedAnchor = (HtmlAnchor) authorizedAnchors.get(i);
			XmlPage authorizedXmlPage =  (XmlPage) authorizedAnchor.openLinkInNewWindow();
			HtmlPage authorizedHtmlPage = HtmlUnitToolkit.transformXmlPage(authorizedXmlPage);
			getAssignedCCCs(authorizedHtmlPage, callback);
		    }
		    
		} catch ( FailingHttpStatusCodeException | TransformerException e ) {
		    throw new SegSocialException(e); 
		}
	
    }

    private static void getAssignedCCCs(HtmlPage htmlPage, Consumer<CCC> callback) throws IOException, TransformerException {
	((HtmlRadioButtonInput) htmlPage.getElementById("autorizado_1")).setChecked(true);
	((HtmlSelect)htmlPage.getElementById("seleccion_2")).setSelectedAttribute("A", true); // Alta
	((HtmlSelect)htmlPage.getElementById("seleccion_3")).setSelectedAttribute("O", true); // Online
	
	XmlPage xmlPage = (XmlPage ) (htmlPage.getElementById("CRITERIOS")).click();
	
	getAssignedCCCs(xmlPage).forEach(callback);	
	while ( hasNextPage(xmlPage) ) {
	    xmlPage = nextPage(xmlPage);
	    getAssignedCCCs(xmlPage).forEach(callback);
	}
    }
    
    public static byte[] getAssignedCCCsPDF(final InputStream certificateInputStream, final String certificatePassword, final String certificateType) throws SegSocialException, IOException{
		byte[] certificateData = certificateInputStream.readAllBytes();
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateData, certificatePassword, certificateType)) {
				
			webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			
			XmlPage xmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P002");
			HtmlPage document = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			HtmlInput sitUsuSec = document.querySelector("#autorizado_1");
			sitUsuSec.click();
			
			webClient.waitForBackgroundJavaScript(5000);
			
			document = HtmlUnitToolkit.selectOption(document, "seleccion_2", "T");
			
			document = HtmlUnitToolkit.selectOption(document, "seleccion_3", "O");
			
			HtmlButton continueButton = document.querySelector("#INFORME");

			// Check if we have more than one CCC for this person
			try {
				document = HtmlUnitToolkit.transformXmlPage(continueButton.click());
				
				// Check table
				HtmlAnchor docButton = document.querySelector("section#SECCION_1 a");
				
				if(docButton != null)
					return getPDFDocument(docButton);
				else
					return null;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] getPDFDocument(HtmlElement linkElement) throws IllegalArgumentException {
		try {
			UnexpectedPage docPage = linkElement.click();
			return docPage.getWebResponse().getContentAsStream().readAllBytes();
		} catch (IOException e) {
			// Exception
			throw new IllegalArgumentException(e.getMessage());
		}
	}

    /**
     * @param xmlPage
     * @return
     */
    private static boolean hasNextPage(XmlPage xmlPage) {
	DomNodeList<DomElement> nextPages = xmlPage.getElementsByTagName("pagSiguiente");
	for (int i = 0; i < nextPages.getLength(); i++) {
	    if ( "true".equalsIgnoreCase(nextPages.get(i).getTextContent()) ) {
		return true;
	    }
	}
	return false;
    }

    private static XmlPage nextPage(XmlPage xmlPage) {
	try {
	    HtmlPage cccsHtmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
	    return cccsHtmlPage.getElementById("SIGUIENTE").click();
	} catch (IOException | TransformerException e) {
	    throw new IllegalArgumentException(e);
	}
    }
    /**
     * @param xmlPage
     * @return
     */
    private static Stream<CCC> getAssignedCCCs(XmlPage xmlPage) {
	return xmlPage.getElementsByTagName("asignadosCCC").stream().map(SistemaREDCCC::getAssignedCCC);
    }

    /**
     * @param assignedCCC
     * @return
     */
    private static CCC getAssignedCCC(DomElement assignedCCC) {
	CCC ccc = new CCC();
	String formatCCC = getTextContentByTagName(assignedCCC,"cccFormateado");
	String[] cccTexts = formatCCC.split(" ");
	ccc.regime = cccTexts[0];
	ccc.province = cccTexts[1];
	ccc.number = cccTexts[2];

	String name = getTextContentByTagName(assignedCCC, "razSoc");
	ccc.entrepriseName = name;
	String type = getTextContentByTagName(assignedCCC, "tbaTipoCcc", "codigoStr");
	ccc.type = type;
	return ccc;
    }
    
    private static String getTextContentByTagName(DomElement parent, String ...tagNames ) {
	DomElement el = parent;
	for (String tagName : tagNames) {
	    for (DomElement child : el.getChildElements()) {
		if (child.getTagName().equalsIgnoreCase(tagName)) {
		    el = child;
		    break;
		}
	    }
	}
	return el.getTextContent();
    }

    public static void main(String[] args) throws FailingHttpStatusCodeException, SegSocialException, IOException, InterruptedException, TransformerException {
//	try (InputStream is = new FileInputStream("/home/rtrepiana/Documents/TREPIANA_ZARATE_RAUL_44679529M.p12")) {
//	    Stream<CCC> cccs = getAssignedCCCs(is, "TREPIANA", "pkcs12");
//	    cccs.forEach(ccc -> {
//		System.out.println(ccc.regime + " " + ccc.getProvince() + " " + ccc.getNumber() + ": " + ccc.getEntrepriseName() + ", " + ccc.getType() );
//	    });
//	}
	
	try (InputStream is = new FileInputStream("/home/rtrepiana/Documents/ayudat.pfx")) {
	    getAssignedCCCs(is, "Alma1981", "pkcs12", ccc -> System.out.println(ccc.regime + " " + ccc.getProvince() + " " + ccc.getNumber() + ": " + ccc.getEntrepriseName() + ", " + ccc.getType() ) );
	}
	
//	Stream.iterate(0, i -> i < 5 , i -> i + 1 ).forEach( i -> System.out.println(i));
    }

}
