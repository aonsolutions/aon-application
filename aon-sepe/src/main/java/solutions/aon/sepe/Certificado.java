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
import com.gargoylesoftware.htmlunit.html.HtmlButton;
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
	    	
	    	String regimen = "0111";
	    	String ctaCti = "01105360062";
	    	String ipf = "Y9999999X";
	    	String ipf_rep = "Y9999999X";
	    	String name = "NOMBRE";
	    	String surname = "PRIMER APELLIDO";
	    	String lastSurname = "SEGUNDO APELLIDO";
	    	String tipodoc =  "NIF";
	    	String cargo = "Cargo de la empresa";
	    	String typeContract = "501";
	    	String gz = "06"; //01-12
	    	String durationContract = "30";
	    	TypeDuration typeDuration = TypeDuration.DIAS;
	    	String catProfessional = "2722";
	    	String causeSuspension = "33"; //01 - 33
	    	
	    	String officePublic = null;
	    	String dedicationPer =  null;
	    	
	    	//data cot vac
			String srDiasCotizacion = "12";
			String srBaseContingenciasComunes = "000001200";
			String srBaseContingenciasDesempleo = "000001200";
	    	
			@SuppressWarnings("deprecation")
			Date fAEd =  new Date("2020/01/01"); // fecha de alta de empresa
			Date fSTd =  new Date(); // fecha de extension 

	    	if(Toolkit.identity(ipf_rep).equals("4")) tipodoc = "CIF";
	    	else if(Toolkit.identity(ipf_rep).equals("6")) tipodoc = "NIE"; 
	    	
	    	String[] fAE = Toolkit.formatDate(fAEd);
	    	String[] fST = Toolkit.formatDate(fSTd);
	    	
			HtmlPage htmlPage = first_page_sepe_cert(webClient);
			HtmlAnchor hrefButton = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("https://sede.sepe.gob.es/CertificadosRedTrabajaWEB/ActionMecanizacionEntradaEmpresa.do")).orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();
			
			HtmlForm form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
			
			//DATA ENTERPRISE
			{
				form.getInputByName("orDatosEmpresa.srCCCRegimenCot").setValueAttribute(regimen);
				form.getInputByName("orDatosEmpresa.srCCCProvincia").setValueAttribute(ctaCti.substring(0,2));
				form.getInputByName("orDatosEmpresa.srCCCSecuencial").setValueAttribute(ctaCti.substring(2,9));
				form.getInputByName("orDatosEmpresa.srCCCDC").setValueAttribute(ctaCti.substring(9));
				form.getInputByName("stDniNie").setValueAttribute(ipf);
			}
			
			htmlPage = form.getInputByName("btBuscar").click();
			handleSepeExceptions(htmlPage);

			htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
			handleSepeExceptions(htmlPage);
			
			//DATA REPRESENTATIVE
			{
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				form.getInputByName("orDatosRepresentante.srNombreRepresentante").setValueAttribute(name);
				form.getInputByName("orDatosRepresentante.srPrimerApellidoRepresentante").setValueAttribute(surname);
				if(lastSurname!=null) form.getInputByName("orDatosRepresentante.srSegundoApellidoRepresentante").setValueAttribute(lastSurname);
				((HtmlSelect)form.querySelector("select[name=\"orDatosRepresentante.srTipoDocRepresentante\"]")).setSelectedAttribute(tipodoc, true);
				form.getInputByName("orDatosRepresentante.srNifRepresentante").setValueAttribute(ipf_rep);
				form.getInputByName("orDatosRepresentante.srCargoRepresentante").setValueAttribute(cargo);
				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
			
			
			//DATA EMPLOYEE
			{
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csGrupoCotizacion.valor\"]")).setSelectedAttribute(gz, true);
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csTipoContrato.valor\"]")).setSelectedAttribute(typeContract, true);
				form.getInputByName("orDatosTrabajador.srDuracionContratoTrab").setValueAttribute(durationContract);
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csIndicadorDuracionContrato.valor\"]")).setSelectedAttribute(typeDuration.getValue(), true);
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csTipoProfesion.valor\"]")).setSelectedAttribute(catProfessional, true);
				if(typeContract.substring(0,1).equalsIgnoreCase("2") || typeContract.substring(0,1).equalsIgnoreCase("5")) {
					form.getInputByName("orDatosTrabajador.existenDetalles").setChecked(true);
				}

				if(officePublic!=null) {
					((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csTipoCargoPublicoOSindical.valor\"]")).setSelectedAttribute(officePublic, true);
					if(dedicationPer!=null) {
						form.getInputByName("orDatosTrabajador.srPorcentualDedicacion").setValueAttribute(dedicationPer);
					} 
				}
				
				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
			//DATA SUSPENSION OR TERMINATION
			{
				form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				((HtmlSelect)form.querySelector("select[name=\"orDatosTrabajador.csCausaSuspension.valor\"]")).setSelectedAttribute(causeSuspension, true);
				form.getInputByName("orDatosTrabajador.srDiaFechaAlta").setValueAttribute(fAE[0]);
				form.getInputByName("orDatosTrabajador.srMesFechaAlta").setValueAttribute(fAE[1]);
				form.getInputByName("orDatosTrabajador.srAnyoFechaAlta").setValueAttribute(fAE[2]);
				form.getInputByName("orDatosTrabajador.srDiaFechaInicioSuspension").setValueAttribute(fST[0]);
				form.getInputByName("orDatosTrabajador.srMesFechaInicioSuspension").setValueAttribute(fST[1]);
				form.getInputByName("orDatosTrabajador.srAnyoFechaInicioSuspension").setValueAttribute(fST[2]);

				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}
				
			//DATA COTINGENCIES
			{
				//DATA CTZ
				{
					//FOR
//					for (int i = 0; i < 2; i++) {
						form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srAnyoCotizacion").setValueAttribute("2021");
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srMesCotizacion").setValueAttribute("01");
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srDiasCotizacion").setValueAttribute("12");
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasComunes").setValueAttribute("000001200");
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasDesempleo").setValueAttribute("000001200");
						htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btAnadir]")).click();
//					}
						
					handleSepeExceptions(htmlPage);
				}
				
				//DATA VACATION
				{

					form =  (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
					if(srDiasCotizacion!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srDiasCotizacion").setValueAttribute(srDiasCotizacion);
					if(srBaseContingenciasComunes!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasComunes").setValueAttribute(srBaseContingenciasComunes);
					if(srBaseContingenciasDesempleo!=null)
						form.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasDesempleo").setValueAttribute(srBaseContingenciasDesempleo);
					htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btActualizarTotales]")).click();
					handleSepeExceptions(htmlPage);
				}
				
//				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
//				handleSepeExceptions(htmlPage);
				
			}


	        Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testCertificates.html");
			return null;
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
	      
		  htmlPage = (HtmlPage) HtmlUnitToolkit.wait4(formDatos, p -> p.getButtonByName("submitCustom")).orElseThrow().click();
		  
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
	
	public enum TypeDuration {
		DIAS("D"), 
		MESES("M"),
		ANIOS("A");
		
		private String value;
		
		public String getValue() {
			return value;
		}
		
		private TypeDuration(String value) {
			this.value = value;
		}
	}
}
