package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import aon.sepe.objects.Certificates;
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
	
	public static String certEnterprise(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Certificates certificates) throws SepeException {
			try {
				return certEnterpriseImpl(certificateInputStream, certificatePassword, certificateType, certificates);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	private static String certEnterpriseImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, Certificates certificates ) throws IOException, SepeException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	String ctaCti = certificates.getCtaCti();
	    	String ipfManager= certificates.getIpfManager();
			String tipodocManager =  "NIF";
	    	String cargoManager = certificates.getCargoManager();
	    	String typeContract = certificates.getTypeContract();

	    	Integer dedicationPer = certificates.getDedicationPer();
	    	List<Map<String, String>> dataCtz = certificates.getDataCtz();

	    	if(Toolkit.identity(ipfManager).equals("4")) tipodocManager = "CIF";
	    	else if(Toolkit.identity(ipfManager).equals("6")) tipodocManager = "NIE"; 
	    	
	    	String[] fAE = Toolkit.formatDate(certificates.getfAEd());
	    	String[] fST = Toolkit.formatDate(certificates.getfSTd());
	    	
			HtmlPage htmlPage = first_page_sepe_cert(webClient);
			HtmlAnchor hrefButton = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("https://sede.sepe.gob.es/CertificadosRedTrabajaWEB/ActionMecanizacionEntradaEmpresa.do")).orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();
			
			HtmlForm form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
			
			{//DATA ENTERPRISE
				form.getInputByName("orDatosEmpresa.srCCCRegimenCot").setValueAttribute(certificates.getRegimen());
				form.getInputByName("orDatosEmpresa.srCCCProvincia").setValueAttribute(ctaCti.substring(0,2));
				form.getInputByName("orDatosEmpresa.srCCCSecuencial").setValueAttribute(ctaCti.substring(2,9));
				form.getInputByName("orDatosEmpresa.srCCCDC").setValueAttribute(ctaCti.substring(9));
				form.getInputByName("stDniNie").setValueAttribute(certificates.getIpf());
			}
			
			htmlPage = form.getInputByName("btBuscar").click();
			handleSepeExceptions(htmlPage);

			htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
			handleSepeExceptions(htmlPage);
			
			{//DATA REPRESENTATIVE
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				form.getInputByName("orDatosRepresentante.srNombreRepresentante").setValueAttribute(certificates.getNameManager());
				form.getInputByName("orDatosRepresentante.srPrimerApellidoRepresentante").setValueAttribute(certificates.getSurnameManager());
				if(certificates.getLastSurname()!=null) form.getInputByName("orDatosRepresentante.srSegundoApellidoRepresentante").setValueAttribute(certificates.getLastSurname());
				((HtmlSelect)form.querySelector("select[name=\"orDatosRepresentante.srTipoDocRepresentante\"]")).setSelectedAttribute(tipodocManager, true);
				form.getInputByName("orDatosRepresentante.srNifRepresentante").setValueAttribute(ipfManager);
				if(cargoManager!=null) form.getInputByName("orDatosRepresentante.srCargoRepresentante").setValueAttribute(cargoManager);
				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
			
			{//DATA EMPLOYEE
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csGrupoCotizacion.valor\"]")).setSelectedAttribute(certificates.getGz(), true);
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csTipoContrato.valor\"]")).setSelectedAttribute(typeContract, true);
				form.getInputByName("orDatosTrabajador.srDuracionContratoTrab").setValueAttribute(certificates.getDurationContract().toString());
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csIndicadorDuracionContrato.valor\"]")).setSelectedAttribute(certificates.getTypeDuration().getValue(), true);
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csTipoProfesion.valor\"]")).setSelectedAttribute(certificates.getCatProfessional(), true);
				if(typeContract.substring(0,1).equalsIgnoreCase("2") || typeContract.substring(0,1).equalsIgnoreCase("5")) {
					form.getInputByName("orDatosTrabajador.existenDetalles").setChecked(true);
				}
				if(certificates.getPublicPosition()!=null) {
					((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csTipoCargoPublicoOSindical.valor\"]")).setSelectedAttribute(certificates.getPublicPosition().getValue().toString(), true);
					if(dedicationPer!=null) {
						form.getInputByName("orDatosTrabajador.srPorcentualDedicacion").setValueAttribute(dedicationPer.toString());
					} 
				}
				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
			
			{//DATA SUSPENSION OR TERMINATION
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csCausaSuspension.valor\"]")).setSelectedAttribute(certificates.getCauseSuspension(), true);
				form.getInputByName("orDatosTrabajador.srDiaFechaAlta").setValueAttribute(fAE[0]);
				form.getInputByName("orDatosTrabajador.srMesFechaAlta").setValueAttribute(fAE[1]);
				form.getInputByName("orDatosTrabajador.srAnyoFechaAlta").setValueAttribute(fAE[2]);
				form.getInputByName("orDatosTrabajador.srDiaFechaInicioSuspension").setValueAttribute(fST[0]);
				form.getInputByName("orDatosTrabajador.srMesFechaInicioSuspension").setValueAttribute(fST[1]);
				form.getInputByName("orDatosTrabajador.srAnyoFechaInicioSuspension").setValueAttribute(fST[2]);

				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
			
			{//DATA COTINGENCIES
				{//DATA CTZ
					for(Map<String, String> ctz: dataCtz) {
						form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srAnyoCotizacion").setValueAttribute(ctz.get("anioCtz"));
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srMesCotizacion").setValueAttribute(ctz.get("monthCtz"));
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srDiasCotizacion").setValueAttribute(ctz.get("daysCtz"));
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasComunes").setValueAttribute(ctz.get("bccc"));
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasDesempleo").setValueAttribute(ctz.get("bcd"));
						htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btAnadir]")).click();
					}
					handleSepeExceptions(htmlPage);
				}
				
				{//DATA VACATION
					form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
					if(certificates.getDaysCtzVc()!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srDiasCotizacion").setValueAttribute(certificates.getDaysCtzVc().toString());
					if(certificates.getBcccVc()!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasComunes").setValueAttribute(certificates.getBcccVc());
					if(certificates.getBcdVc()!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasDesempleo").setValueAttribute(certificates.getBcdVc());
					htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btActualizarTotales]")).click();
					handleSepeExceptions(htmlPage);
				}

				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}

//	        Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testCertificates.html");
	        System.out.println("END " + htmlPage);
			return null;
		} 
	}
	
	private static HtmlPage first_page_sepe_cert(WebClient webClient)  throws SepeException , IOException, InterruptedException{
		  webClient.getOptions().setJavaScriptEnabled(true);
		  webClient.getOptions().setThrowExceptionOnScriptError(false);
		  webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
		  Page page = null;
	      Integer MAX_ATTEMPS = 10;
	      Integer i = 0;
	      while(!(page instanceof HtmlPage) &&  i < MAX_ATTEMPS) {
	    	  System.out.println("attempt " + (i+1));
		      try {
		    	  page = page_first_process(webClient);  
		      } catch (FailingHttpStatusCodeException e) { System.out.println("I do not load the page, retrying!");  }
	    	  i++;
	    	  Thread.sleep(1000);
	      }
		  HtmlPage htmlPage = (HtmlPage) page;
		  
		  HtmlUnitToolkit.manageStatusCode(htmlPage); 
		  return htmlPage;
	}
	
	private static Page page_first_process(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SepeException, InterruptedException{

	      HtmlPage htmlPage = webClient.getPage("https://isweb.sepe.gob.es/GetAccess/Saml/SSO/Init?GAURI=https%3A%2F%2Fsede.sepe.gob.es%2FDCertificadosWeb%2FActionNavegacion.do%3FaccesoGA%3Dempresas%26accion%3Dnavegacion&GA_SAML_AC_COMPARISON=minimum&GA_SAML_IS_PASSIVE=false&GA_SAML_AC_CLASS_REF=http%3A%2F%2Feidas.europa.eu%2FLoA%2Flow&GA_SAML_PROVIDER=Q2819009H_E00142804&GA_SAML_IDP=https%3A%2F%2Fpasarela.clave.gob.es%2FProxy2");

	      HtmlUnitToolkit.manageStatusCode(htmlPage); 
	      HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("idpRedirect")).orElseThrow();
	      formDatos.getInputByName("SelectedIdP").setValueAttribute("AFIRMA");
	      //create submit 
	      HtmlElement button =  (HtmlElement) HtmlUnitToolkit.createButton(htmlPage);
	      formDatos.appendChild(button);
	      Page newPage = button.click();
	      return newPage;
	}
	
	private static void handleSepeExceptions(HtmlPage htmlPage) throws SepeException{
		try {
			String error = htmlPage.querySelector("#contenido > form > p.formAviso").getVisibleText();
			if(!error.isEmpty()) 
				throw new SepeException(error);
		} catch (NullPointerException e) {}
	}
	
}
