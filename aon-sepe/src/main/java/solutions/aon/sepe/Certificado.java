package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import aon.sepe.exceptions.invalidData.InvalidDataException;
import aon.sepe.objects.Certificates;
import aon.sepe.objects.QuoteData;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Certificado {

	private static DecimalFormat decimalFormat = new DecimalFormat("#00.00");
	
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
	    	
	    	webClient.getOptions().setUseInsecureSSL(true);
	    	
			//if(fecha==null) fecha = new Date();
			//String[] fra = Toolkit.formatDate(fecha);

			HtmlPage htmlPage = firstPageSepeCert(webClient);
  
			HtmlAnchor hrefButton = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("https://sede.sepe.gob.es/ConsultasCertificadosRTWEB/ActionEntradaConsultas.do")).orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();
			
			HtmlForm formDatos1 =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#contenido > form")).orElseThrow();
			formDatos1.getInputByName("nif").setValueAttribute(nif);
			htmlPage = formDatos1.getInputByName("btBuscar").click();
			handleSepeExceptions(htmlPage);
			
			// La fecha fin ya no importa (lo dejo comentado) y creo que se podria quitar este argumento,
			// ya que ahora no se guarda con fecha fin de contrato si no con fecha de presentacion del Certfic@2
			// String finicio = fra[0]+"/"+fra[1]+"/"+fra[2];
			
			// Ahora la tabla esta dentro de un fieldset
			HtmlTable table = (HtmlTable) htmlPage.querySelector("#contenido > form > fieldset > table");
			HtmlRadioButtonInput firstColumn;
			String columnCheck = "0";
			
			// Creo que siempre va a ser el ultimo Certific@2 el primero de la tabla
			// Pero para el futuro igual habria que guardar la fecha de presentacion por que cuando un trabajador tenga varios Certific@2 presentados
			// habra que buscar la forma de filtrarlo, ahora de momento lo he dejado para que siempre coja el mas reciente
			if(null != table) {
				firstColumn = table.getRows().get(1).getCell(0).querySelector("input[name=documentoSeleccionado]");
				columnCheck  = firstColumn.getValueAttribute();
			}
			
			HtmlRadioButtonInput inputRadio = htmlPage.querySelector("#contenido form input[name=documentoSeleccionado][value=\""+columnCheck+"\"]");
	        htmlPage = (HtmlPage) inputRadio.click();

	        Page page = htmlPage.getElementByName("btMostrar").click();
			if(page.isHtmlPage()) {
				htmlPage = (HtmlPage) page;
				handleSepeExceptions(htmlPage);
			} else {
				try{
					return page.getWebResponse().getContentAsStream().readAllBytes();
				}
				catch(Exception e){throw new InvalidDataException();}
			}
		}
	    
		return null; 
	}
	
	public static byte[] certEnterprise(final InputStream certificateInputStream,
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
	
	private static byte[] certEnterpriseImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, Certificates certificates ) throws IOException, SepeException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	webClient.getOptions().setUseInsecureSSL(true);
	    	
	    	String ctaCti = certificates.getCtaCti();
	    	String ipfManager= certificates.getIpfManager();
			
	    	String typeContract = certificates.getTypeContract();

	    	String tipodocManager =  "NIF";
	    	
	    	if(Toolkit.identity(ipfManager).equals("4")) 
	    		tipodocManager = "CIF";
	    	else if(Toolkit.identity(ipfManager).equals("6")) 
	    		tipodocManager = "NIE"; 
	    	
	    	String[] fAE = Toolkit.formatDate(certificates.getfAEd());
	    	String[] fST = Toolkit.formatDate(certificates.getfSTd());
	    	
			HtmlPage htmlPage = firstPageSepeCert(webClient);
			HtmlAnchor hrefButton = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("https://sede.sepe.gob.es/CertificadosRedTrabajaWEB/ActionMecanizacionEntradaEmpresa.do")).orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();
			
			HtmlForm form = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
			
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
				if(certificates.getLastSurname().isPresent()) {
					form.getInputByName("orDatosRepresentante.srSegundoApellidoRepresentante").setValueAttribute(certificates.getLastSurname().get());
				} 
					
				((HtmlSelect)form.querySelector("select[name=\"orDatosRepresentante.srTipoDocRepresentante\"]")).setSelectedAttribute(tipodocManager, true);
				form.getInputByName("orDatosRepresentante.srNifRepresentante").setValueAttribute(ipfManager);
				
				if(certificates.getCargoManager().isPresent()) {
					form.getInputByName("orDatosRepresentante.srCargoRepresentante").setValueAttribute(certificates.getCargoManager().get());
				}
					
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
					if(certificates.getDedicationPer().isPresent()) {
						form.getInputByName("orDatosTrabajador.srPorcentualDedicacion").setValueAttribute(certificates.getDedicationPer().get().toString());
					}  
				}
				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
			
			{//DATA SUSPENSION OR TERMINATION
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				if(certificates.getCauseSuspension()!=null)
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
					for(QuoteData qdata: certificates.getQuoteData()) {
						form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
						
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srAnyoCotizacion")
						.setValueAttribute(qdata.getAnio().toString());
						
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srMesCotizacion")
						.setValueAttribute(qdata.getMonth().toString());
						
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srDiasCotizacion")
						.setValueAttribute(qdata.getDays().toString());
						
						if(qdata.getBccc().isPresent()) {
							form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasComunes")
							.setValueAttribute(decimalFormat.format(qdata.getBccc().get()));
						}
						
						if(qdata.getBcd().isPresent()) {
							form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasDesempleo")
							.setValueAttribute(decimalFormat.format(qdata.getBcd().get()));
						}
						htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btAnadir]")).click();
					}
					handleSepeExceptions(htmlPage);
				}
				
				{//DATA VACATION
					form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
					if(certificates.getDaysCtzVc()!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srDiasCotizacion").setValueAttribute(certificates.getDaysCtzVc().toString());
					if(certificates.getBcccVc().isPresent())
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasComunes").setValueAttribute(decimalFormat.format(certificates.getBcccVc().get()));
					if(certificates.getBcdVc().isPresent())
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasDesempleo").setValueAttribute(decimalFormat.format(certificates.getBcdVc().get()));
					htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btActualizarTotales]")).click();
					handleSepeExceptions(htmlPage);
				}
				
				Page page =((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				if(page.isHtmlPage()) {
					handleSepeExceptions((HtmlPage) page);
				} else {
					try{
						byte[] pdf = page.getWebResponse().getContentAsStream().readAllBytes();
//						System.out.println(Base64.getEncoder().encodeToString(pdf));
						return pdf;
					}
					catch(Exception e){throw new InvalidDataException();}
				}
			}
			
//	        Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testCertificates.html");
	        System.out.println("END");
		} 
	    return null;
	}
	
	private static HtmlPage firstPageSepeCert(WebClient webClient)  throws SepeException , IOException, InterruptedException{
		  webClient.getOptions().setJavaScriptEnabled(true);
		  webClient.getOptions().setThrowExceptionOnScriptError(false);
		  webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
		  Page page = null;
	      Integer MAX_ATTEMPS = 10;
	      Integer i = 0;
	      while(!(page instanceof HtmlPage) &&  i < MAX_ATTEMPS) {
	    	  System.out.println("ATTEMPT " + (i+1));
		      try {
		    	  page = pageFirstProcess(webClient);  
		      } catch (FailingHttpStatusCodeException e) { System.out.println("I do not load the page, retrying!");  }
	    	  i++;
	    	  Thread.sleep(1000);
	      }
		  HtmlPage htmlPage = (HtmlPage) page;
		  
		  HtmlUnitToolkit.manageStatusCode(htmlPage); 
		  return htmlPage;
	}
	
	private static Page pageFirstProcess(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SepeException, InterruptedException{

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
			DomNode error = htmlPage.querySelector("#contenido > form > p.formAviso");
			if(error!=null && !error.getVisibleText().isEmpty()) 
				throw new SepeException(error.getVisibleText());
		} catch (NullPointerException e) {}
	}
	
}
