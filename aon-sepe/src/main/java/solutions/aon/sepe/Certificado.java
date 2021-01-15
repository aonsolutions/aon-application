package solutions.aon.sepe;

//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Certificado {

	public static byte[] certEnterprisePdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String nif, Date fecha) throws SepeException {
			try {
				return certEnterprisePdfImpl(certificateInputStream, certificatePassword, certificateType, nif, fecha);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	
	private static byte[] certEnterprisePdfImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, String nif, Date fecha ) throws IOException, SepeException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
			if(fecha==null) fecha = new Date();
			String[] fra = Toolkit.formatDate(fecha);

			HtmlPage htmlPage = first_page_sepe_cert(webClient);
  
			HtmlAnchor hrefButton = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("https://sede.sepe.gob.es/ConsultasCertificadosRTWEB/ActionEntradaConsultas.do")).orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();
			
			HtmlForm formDatos1 =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#contenido > form")).orElseThrow();
			formDatos1.getInputByName("nif").setValueAttribute(nif);
			htmlPage = formDatos1.getInputByName("btBuscar").click();
			handleSepeExceptions(htmlPage);
			
			htmlPage = htmlPage.getElementByName("btMasCert").click();
			handleSepeExceptions(htmlPage);
			
			String finicio = fra[0]+"/"+fra[1]+"/"+fra[2];
			HtmlTable table = (HtmlTable) htmlPage.querySelector("#contenido > form > table");
			HtmlRadioButtonInput firstColumn;
			String columnCheck = "0";
			for (final HtmlTableRow row : table.getRows()) {
				HtmlTableCell cell = row.getCell(6);
				if(cell.getVisibleText().indexOf(finicio)>= 0) {
					firstColumn = row.getCell(0).querySelector("input[name=certSeleccionado]");
					columnCheck  = firstColumn.getValueAttribute();
					break;
				}
			}

			HtmlRadioButtonInput inputRadio = htmlPage.querySelector("#contenido form input[name=certSeleccionado][value=\""+columnCheck+"\"]");
	        htmlPage = (HtmlPage) inputRadio.click();
			htmlPage = htmlPage.getElementByName("btAceptar").click();
			handleSepeExceptions(htmlPage);
			
			HtmlRadioButtonInput inputRadio2 = htmlPage.querySelector("#contenido form input[value=\""+columnCheck+"\"]");
	        htmlPage = (HtmlPage) inputRadio2.click();

	        UnexpectedPage document = htmlPage.getElementByName("btMostrar").click();
			InputStream inp = document.getWebResponse().getContentAsStream();
			byte[] pdf = inp.readAllBytes();
			inp.close();

			return pdf;
		} 
	}
	
	private static HtmlPage first_page_sepe_cert(WebClient webClient)  throws IOException, SepeException, InterruptedException{
		  webClient.getOptions().setJavaScriptEnabled(true);
		  webClient.getOptions().setThrowExceptionOnScriptError(false);
		  webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
	      HtmlPage htmlPage = webClient.getPage("https://isweb.sepe.gob.es/GetAccess/Saml/SSO/Init?GAURI=https%3A%2F%2Fsede.sepe.gob.es%2FDCertificadosWeb%2FActionNavegacion.do%3FaccesoGA%3Dempresas%26accion%3Dnavegacion&GA_SAML_AC_COMPARISON=minimum&GA_SAML_IS_PASSIVE=false&GA_SAML_AC_CLASS_REF=http%3A%2F%2Feidas.europa.eu%2FLoA%2Flow&GA_SAML_PROVIDER=Q2819009H_E00142804&GA_SAML_IDP=https%3A%2F%2Fpasarela.clave.gob.es%2FProxy2");
		  HtmlUnitToolkit.manageStatusCode(htmlPage); 
	      HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("idpRedirect")).orElseThrow();
	     
	      formDatos.getInputByName("SelectedIdP").setValueAttribute("AFIRMA");
	      //create submit 
	      HtmlElement button =  (HtmlElement) HtmlUnitToolkit.createButton(htmlPage);
	      formDatos.appendChild(button);
		  htmlPage = button.click();
		  HtmlUnitToolkit.manageStatusCode(htmlPage); 
		  return htmlPage;
	}
	
	private static void handleSepeExceptions(HtmlPage htmlPage) throws SepeException{
		try {
			String error = htmlPage.querySelector("#contenido > form > p.formAviso").getVisibleText();
			if(!error.isEmpty()) 
				throw new SepeException(error);
		} catch (NullPointerException e) {}
	}
}
