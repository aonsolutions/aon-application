package solutions.aon.sepe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Certificado {

	private static void certEmpresaPdfImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, String nif, Date fecha ) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
			if(fecha==null) fecha = new Date();
			String[] fra = Toolkit.formatDate(fecha);
			
			HtmlPage htmlPage = first_page_sepe_cert(webClient);
			
			HtmlForm formDatos1 = (HtmlForm) htmlPage.querySelector("#contenido > form");
			formDatos1.getInputByName("nif").setValueAttribute(nif);
			htmlPage = formDatos1.getInputByName("btBuscar").click();
				  
			handleSepeExceptions(htmlPage);
				  
			System.out.println(htmlPage);
  
		} 
	}
	
	private static void handleSepeExceptions(HtmlPage htmlPage) throws Exception{
		try {
			String error = htmlPage.querySelector("#contenido > form > p.formAviso").getVisibleText();
			if(!error.isEmpty()) {
				throw new Exception(error);
			}
		} catch (NullPointerException e) {}
	}
	
	private static HtmlPage first_page_sepe_cert(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
	      HtmlPage htmlPage = webClient.getPage("https://isweb.sepe.gob.es/GetAccess/Saml/SSO/Init?GAURI=https%3A%2F%2Fsede.sepe.gob.es%2FDCertificadosWeb%2FActionNavegacion.do%3FaccesoGA%3Dempresas%26accion%3Dnavegacion&GA_SAML_AC_COMPARISON=minimum&GA_SAML_IS_PASSIVE=false&GA_SAML_AC_CLASS_REF=http%3A%2F%2Feidas.europa.eu%2FLoA%2Flow&GA_SAML_PROVIDER=Q2819009H_E00142804&GA_SAML_IDP=https%3A%2F%2Fpasarela.clave.gob.es%2FProxy2");

	      HtmlForm formDatos =  (HtmlForm) htmlPage.querySelector("form[name=idpRedirect]");
	      formDatos.getInputByName("SelectedIdP").setValueAttribute("AFIRMA");
	      //create submit 
	      HtmlElement button = (HtmlElement) htmlPage.createElement("button");
	      button.setAttribute("type", "submit");
	      
	      formDatos.appendChild(button);
	      
		  htmlPage = button.click();
		  
		  htmlPage = htmlPage.getAnchorByHref("https://sede.sepe.gob.es/ConsultasCertificadosRTWEB/ActionEntradaConsultas.do").click();
		  
		  return htmlPage;
	}
	
	public static void main(String[] args)  {
		try (final FileInputStream certificateInputStream =  new FileInputStream("src/test/resources/solutions/aon/SEPE.p12")) {	
			
				String certificatePassword = "aon@FNMT";
				String certificateType = "pkcs12";
//				String regimen = "0111";
//		     	String ctaCti = "01105360062";
//		     	String nss = "291136796369";
//				String ipf = "Y7514970X";
//				String grup_ctz = "01";
				String nif = "231";
				Date fecha = Toolkit.parseDate("29-12-2020", "dd-MM-yyyy");
				certEmpresaPdfImpl(certificateInputStream, certificatePassword, certificateType, nif, null);
		} 			  
		catch (Exception e) {
			System.out.println("errors>>" + e.getMessage());
//			e.printStackTrace();
		}
	}



}
