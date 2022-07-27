package solutions.aon.sepe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

import aon.sepe.exceptions.invalidData.InvalidDataException;
import aon.sepe.objects.Contract;
import aon.sepe.objects.ContractExtension;
import aon.sepe.objects.Contract.ContractBuilder;
import aon.sepe.objects.Contract.SexType;
import aon.sepe.objects.CopyBasic;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.exceptions.certificate.InvalidCertificateException;
import solutions.aon.sepe.exceptions.statusCode.StatusCodeException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrata {
	
	//	Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/Documentos/test.html");
	
	private Contrata() {
		throw new IllegalStateException("Utility class");
	}
	  
	public static String sendContrata(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Contract cto) throws SepeException {
			try {
				return sendContrataImpl(certificateInputStream, certificatePassword, certificateType, cto);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
			return null;
	}
	
	public static void sendContrataExtension(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, ContractExtension contractExtension) throws SepeException {
			try {
				sendContrataExtensionImpl(certificateInputStream, certificatePassword, certificateType, contractExtension);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
	}
	
	public static void sendTransformation(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Contract cto, CopyBasic copyBasic) throws SepeException {
			try {
				sendTransformationImpl(certificateInputStream, certificatePassword, certificateType, cto, copyBasic);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
	}
	
	public static String sendCopyBasic(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, CopyBasic copyBasic) throws SepeException {
			try {
				return sendCopyBasicImpl(certificateInputStream, certificatePassword, certificateType, copyBasic);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
			return null;
	}
	
	public static String sendTransformationCopyBasic(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, CopyBasic copyBasic, String cif, Date startDate) throws SepeException {
			try {
				return sendTransformationCopyBasicImpl(certificateInputStream, certificatePassword, certificateType, copyBasic, cif, startDate);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
			return null;
	}
	
	public static byte[] getContratoPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId) throws SepeException {
			try {
				return getContratoPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, startDate, endDate, sepeId);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
	}
	
	
	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId) throws SepeException {
			try {
				return getCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, startDate, endDate, sepeId);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
	}
	
	public static byte[] getTransformationPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date startDate, Optional<String> sepeId) throws SepeException {
			try {
				return getTransformationPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, cif, startDate, sepeId);
			}
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
	}
	
	public static byte[] getTransformationCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date startDate, Optional<String> sepeId) throws SepeException {
			try {
				return getTransformationCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, cif, startDate, sepeId);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	public static void validateCert(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws SepeException {
			try {
				validateCertImpl(certificateInputStream, certificatePassword, certificateType);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e);
			}
	}
	
	private static String sendContrataImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, Contract cto) 
			throws SepeException, FailingHttpStatusCodeException, InterruptedException, MalformedURLException, IOException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			
			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);
			
			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
	
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/atraves_comunicacion.jsp").click();
	        handleSepeExceptions(htmlPage);
	        
	        String contract = cto.getCodContract();
	        
	        htmlPage = contractPage(htmlPage, contract);
	        handleSepeExceptions(htmlPage);

			try {
				HtmlSelect codContract = ((HtmlSelect)htmlPage.querySelector("select[name=codcontrato]"));
				codContract.getOptionByValue(contract).setSelected(true);
			} catch (ElementNotFoundException e) {
				throw new SepeException("Contrato "+contract+" no soportado");
			}

			HtmlSubmitInput sb = htmlPage.querySelector("#enviar");
			htmlPage = sb.click();
			handleSepeExceptions(htmlPage);
			
			DomNode text = htmlPage.querySelector(".tac.azneg");
			if(text!=null && text.getVisibleText().indexOf("Desea comunicar el contrato")>=0) {
				htmlPage = ((HtmlSubmitInput)htmlPage.querySelector("#enviar")).click();
				handleSepeExceptions(htmlPage);
			}	
			
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			
			{// DATA ENTERPRISE
				String ctaCti = cto.getCtaCti();
				String regimen = cto.getRegimen();
				
				if(cto.getCifEnterprise()!=null) {					
					form.getInputByName("cifnif").setValueAttribute(cto.getCifEnterprise());
				}

				form.getInputByName("regimen").setValueAttribute(regimen);
				form.getInputByName("numsecprov").setValueAttribute(ctaCti.substring(0,2));
				form.getInputByName("numsec").setValueAttribute(ctaCti.substring(2,9));
				form.getInputByName("digcont").setValueAttribute(ctaCti.substring(9));
				form.getInputByName("cuentacotizacion").setValueAttribute(regimen+ctaCti);
			}

			{//DATA EMPLOYEE
				String tipodoc = "D";
				if(Toolkit.getIdentityType(cto.getIpf()).equals("6")) {
					tipodoc = "E"; // NIE
				}

				String nss = cto.getNss();
				((HtmlSelect)form.querySelector("select[name=tipodoc]")).setSelectedAttribute(tipodoc, true);
				form.getInputByName("nif").setValueAttribute(cto.getIpf());
				form.getInputByName("nifnie").setValueAttribute(tipodoc+"  "+cto.getIpf());
	
				form.getInputByName("nombre").setValueAttribute(cto.getName());
				form.getInputByName("apellido1").setValueAttribute(cto.getSurname());
				
				if(cto.getLastSurname()!=null) {					
					form.getInputByName("apellido2").setValueAttribute(cto.getLastSurname());
				}
				
				if(cto.getSex()!=null) {					
					((HtmlSelect)form.querySelector("select[name=codsexo]")).setSelectedAttribute(cto.getSex().getValue().toString(), true);//SELECT  ("-1"=>"","1"=>"HOMBRE","2"=>"MUJER")
				}

				if(cto.getDateBirth()!=null) {
					String[] dateBirth = Toolkit.dateString(cto.getDateBirth());
					form.getInputByName("diafechanac").setValueAttribute(dateBirth[0]);
					form.getInputByName("mesfechanac").setValueAttribute(dateBirth[1]);
					form.getInputByName("anniofechanac").setValueAttribute(dateBirth[2]);
				}

				((HtmlSelect)form.querySelector("select[name=nacionalidad]")).setSelectedAttribute(cto.getCodNationality().toString(), true);
				((HtmlSelect)form.querySelector("select[name=codpaisdomicilio]")).setSelectedAttribute(cto.getCodPaisDom().toString(), true);

				if(cto.getCodMunDom()!=null) {
					form.getInputByName("municipio").setValueAttribute(cto.getCodMunDom());
				}
					
				form.getInputByName("nass1").setValueAttribute(nss.substring(0, 2));

				form.getInputByName("nass2").setValueAttribute(nss.substring(2, 10));

				form.getInputByName("nass3").setValueAttribute(nss.substring(10));
			}
			
			{//DATA CONTRACT
				form.getInputByName("contratoEscrito").setValueAttribute("N"); //  contratoEscrito si la fecha fin es menor a 28 
				
				if(cto.getDateIniContract()!=null) {
					String[] dateInitContract = Toolkit.dateString(cto.getDateIniContract());

					form.getInputByName("diafechaini").setValueAttribute(dateInitContract[0]);
					form.getInputByName("mesfechaini").setValueAttribute(dateInitContract[1]);
					form.getInputByName("anniofechaini").setValueAttribute(dateInitContract[2]);
					
					//NIVEL FORMATIVO
					if(cto.getCodFormativo()!=null && cto.getCodFormativo() > 0) {						
						htmlPage = ((HtmlSelect)form.querySelector("select[name=codnivelformativo]")).setSelectedAttribute(cto.getCodFormativo().toString(), true);
						form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					}
				}
				
				setOccupation(cto, form);
	
				if(cto.getCodPaisWork()!=null) {					
					((HtmlSelect)form.querySelector("select[name=codpais]")).setSelectedAttribute(cto.getCodPaisWork().toString(), true);
				}
				
				if(cto.getCodMunWork()!=null) {					
					form.getInputByName("municipiocontrato").setValueAttribute(cto.getCodMunWork());//disabled
				}
		
				DomNode ofertaEmpleo = form.querySelector("select[name=procedeDeOfertaEmpleo]");
				if(ofertaEmpleo!=null) {					
					((HtmlSelect)ofertaEmpleo).setSelectedAttribute(cto.getOffer().getValue(), true);
				}
			}
			
			{//OTHERS DATA CONTRACT (OPTIONAL)
				if(cto.getDateFinContract()!=null) {
					DomNode endDay = form.querySelector("[name=\"diafechafin\"]");
					if(endDay!=null) {
						String[] dateFinContract = Toolkit.dateString(cto.getDateFinContract());
						((HtmlInput)endDay).setValueAttribute(dateFinContract[0]);
						form.getInputByName("mesfechafin").setValueAttribute(dateFinContract[1]);
						form.getInputByName("anniofechafin").setValueAttribute(dateFinContract[2]);
					}
				}

				if(cto.getJndType()!=null) {
					((HtmlSelect)form.querySelector("select[name=codtipojornada]")).setSelectedAttribute(cto.getJndType().getValue(), true);				
				}
			
				if(cto.getDurationTypeJndHour()!=null) {					
					form.getInputByName("horasduracionjornada").setValueAttribute(cto.getDurationTypeJndHour());
				}
				
				if(cto.getDurationTypeJndMin()!=null) {					
					form.getInputByName("minutosduracionjornada").setValueAttribute(cto.getDurationTypeJndMin());
				}
				
				//TIEMPO PARCIAL
				if(cto.getDurationTypeCvnHour()!=null) {					
					form.getInputByName("horasduracionconvenio").setValueAttribute(cto.getDurationTypeCvnHour());
				}
			
				if(cto.getDurationTypeCvnMin()!=null) {					
					form.getInputByName("minutosduracionconvenio").setValueAttribute(cto.getDurationTypeCvnMin());
				}
				
				// Titulacion academica
				DomNode numNivelForm = form.querySelector("[name=\"numNivelForm\"]");
				if(numNivelForm!=null) {
					((HtmlSelect)numNivelForm).setSelectedAttribute(cto.getCodFormativo().toString(), true);
				}
				
				//TITULACION
				Optional<String> titulacion = cto.getTitulacion();
				DomNode titulacionEl = form.querySelector("[name=\"titulacion\"]");
				if(titulacionEl!=null && titulacion.isPresent()) {
					((HtmlSelect)titulacionEl).setSelectedAttribute(titulacion.get(), true);
				}

				//INTERINIDAD OR SUBSTITUTION
				Optional<String> interinidad = cto.getInterinidad();
				if(interinidad.isPresent()) {
		
					form.getInputByName("pagina2").setValueAttribute("2");
					
					HtmlCheckBoxInput check = (HtmlCheckBoxInput)form.getInputByName("checkInterinidad");
					check.click();
					
					htmlPage = ((HtmlSubmitInput)form.querySelector("[name=aceptar]")).click();
					handleSepeExceptions(htmlPage);

					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					
					((HtmlSelect)form.querySelector("select[name=codobjetointerinidad]")).setSelectedAttribute(interinidad.get(), true);
				}
			}	
		
			htmlPage = ((HtmlSubmitInput)form.querySelector("[name=aceptar]")).click();
			handleSepeAlert(alertHandler.getCollectedAlerts());
			
			String message = null;
			
			for (int i = 0; i < 3; i++) {
				message = getSuccessMessage(htmlPage);
				if(message==null || (message!=null && message.indexOf("E")>=0)) {
					break;
				} else if(message.contains("returnInit")) {
					htmlPage = sepeReturnInitPage(htmlPage, cto); 
				} 
			}
			
			if(message!=null && message.indexOf("E")>=0) {
				message = message.substring(1);	
			} else {
				handleSepeExceptions(htmlPage);
				throw new SepeException("Error no aceptada la comunicaci\u00f3n");
			}
			
			return message;
		}
	}

	private static void sendTransformationImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, Contract cto, CopyBasic copyBasic) 
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);
			
			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/TransformacionServlet?pagina=inicio").click();
	        handleSepeExceptions(htmlPage);
	      
	      
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			
			{//DATA ENTERPRISE
				
				HtmlRadioButtonInput forNif = (HtmlRadioButtonInput)form.querySelector("[name=tipoacceso][value='2']");
				forNif.click();
				
				form.getInputByName("tipoacceso").setValueAttribute("2");
				
				String tipodocEnterprise =  " "; // CIF
				if(Toolkit.getIdentityType(cto.getCifEnterprise()).equals("1")) tipodocEnterprise = "D"; // NIF
				
				((HtmlSelect)form.querySelector("select[name=tipodocumentoaux]")).setSelectedAttribute(tipodocEnterprise, true);
				
				if(cto.getCifEnterprise()!=null) {					
					form.getInputByName("cifnifnie").setValueAttribute(cto.getCifEnterprise());
				}
			}

			{//DATA EMPLOYEE
				String tipodoc =  "D";
				if(Toolkit.getIdentityType(cto.getIpf()).equals("6")) tipodoc = "E"; // NIE

				((HtmlSelect)form.querySelector("select[name=tipodocumento]")).setSelectedAttribute(tipodoc, true);

				form.getInputByName("nifnietrabajador").setValueAttribute("  "+cto.getIpf());
			}
//
			{//DATA CONTRACT
				//FECHA DE INICIO DEL CONTRATO ANTERIOR
				String[] dateInitContract = Toolkit.dateString(cto.getOldDateIniContract());
				form.getInputByName("diafechaini").setValueAttribute(dateInitContract[0]);
				form.getInputByName("mesfechaini").setValueAttribute(dateInitContract[1]);
				form.getInputByName("anniofechaini").setValueAttribute(dateInitContract[2]);
				
				String contract = cto.getCodContract();
				
				try {
					HtmlSelect codtransformacion = ((HtmlSelect)form.querySelector("select[name=codtransformacion]"));
					codtransformacion.getOptionByValue(contract).setSelected(true);
				} catch (ElementNotFoundException e) {
					throw new SepeException("Contrato "+contract+" no soportado");
				}
				
				//FECHA DE COMUNICACION
				String[] dateComContract = Toolkit.dateString(cto.getDateComContract());
				form.getInputByName("dia").setValueAttribute(dateComContract[0]);
				form.getInputByName("mes").setValueAttribute(dateComContract[1]);
				form.getInputByName("annio").setValueAttribute(dateComContract[2]);	
			}
			
			htmlPage = ((HtmlSubmitInput)form.querySelector("[name=aceptar]")).click();
			handleSepeAlert(alertHandler.getCollectedAlerts());
			handleSepeExceptions(htmlPage);
	
			form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		
			{// DATA TRANSFORMATION
				form.getInputByName("ocupacion").setValueAttribute(cto.getCodOccupation()); // disabled
				form.getInputByName("cocupacion").setValueAttribute(cto.getCodOccupation());// repeat cod contract
				
				((HtmlSelect)form.querySelector("select[name=nacionalidadCT]")).setSelectedAttribute(cto.getCodPaisWork().toString(), true);
				
				String municipio = cto.getCodMunWork();
				form.getInputByName("municipiocontrato").setValueAttribute(municipio);// repeat cod contract
				form.getInputByName("municipioCT").setValueAttribute(municipio);// repeat cod contract
				
				DomNode fijoDiscontinuo = form.querySelector("[name=fijoDiscontinuo]");
				if(fijoDiscontinuo!=null) {
					((HtmlSelect)fijoDiscontinuo).setSelectedAttribute(cto.getDiscontinuo() ? "003" : "004", true);
				}
				
				DomNode discontinuoReason = form.querySelector("[name=discontinuidad]");
				if(discontinuoReason!=null && cto.getDiscontinuoReason()!=null) {
					((HtmlSelect)discontinuoReason).setSelectedAttribute(cto.getDiscontinuoReason().getValue(), true);
				}
				
				DomNode endDateDay = form.querySelector("[name=diafechafin]");
				if(endDateDay!=null && cto.getOldDateFindContract()!=null) {	// FECHA DE FIN DE CONTRATO DEL CONTRATO ANTERIOR
					String[] endDateOld = Toolkit.dateString(cto.getOldDateFindContract());
					((HtmlInput)endDateDay).setValueAttribute(endDateOld[0]);
					form.getInputByName("mesfechafin").setValueAttribute(endDateOld[1]);
					form.getInputByName("anniofechafin").setValueAttribute(endDateOld[2]);	
				}

				{//DATA JORNADA
					DomNode tipoJornada = form.querySelector("select[name=tipoJornada]");
					if(tipoJornada!=null && cto.getJndType()!=null && cto.getDurationTypeJndHour()!=null && cto.getDurationTypeJndMin()!=null) {
						((HtmlSelect)tipoJornada).setSelectedAttribute(cto.getJndType().getValue(), true);
						String hours = Toolkit.fillStringLeft(cto.getDurationTypeJndHour(), "0", 4);
						String min   = cto.getDurationTypeJndMin();
						form.getInputByName("horas").setValueAttribute(hours);
						form.getInputByName("minutos").setValueAttribute(min);
						form.getInputByName("duracjornada").setValueAttribute(hours+min);
						
//						DomNode duracConvenio = form.querySelector("select[name=duracconvenio]");
//						if(duracConvenio!=null) {
//							((HtmlInput) duracConvenio).setValueAttribute(hours+min);
//						}
					}
					
//					DomNode pregunta = form.querySelector("select[name=pregunta]");// El periodo de actividad es sin fecha cierta?
//					if(pregunta!=null) {
//						((HtmlSelect)pregunta).setSelectedAttribute("S", true); // "S", "N" 
//					}
				}
			}
			
			{//DATA COPY BASIC IF NO EXIST
				DomNode codtipofirma = form.querySelector("select[name=codtipofirma]");
				DomNode areadeDomicilio = form.querySelector("[name=areadeDomicilio]");
				DomNode areadeTexto = form.querySelector("[name=areadeTexto]");
				if(copyBasic!=null && codtipofirma!=null && areadeDomicilio!=null && areadeTexto!=null) {
					  ((HtmlSelect)codtipofirma).setSelectedAttribute(copyBasic.getFirmType().getValue().toString(), true);
				      ((HtmlTextArea)areadeDomicilio).setText(copyBasic.getWorkAddress());
				      ((HtmlTextArea)areadeTexto).setText(copyBasic.getRestContract());
				}
			}
			
			handleSepeAlert(alertHandler.getCollectedAlerts());
			
			htmlPage = ((HtmlSubmitInput)form.querySelector("[name=aceptar]")).click();
			handleSepeExceptions(htmlPage);
			handleSepeAlert(alertHandler.getCollectedAlerts());

	        String message = getSuccessMessage(htmlPage);
			if(message!=null && message.contains("se ha realizado correctamente")) {				
				System.out.println(message);
			} else {
				throw new SepeException("Error no aceptada la comunicaci\u00f3n");
			}
		} 
	}
	
	public static Contract getContractData(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date startDate, Date endDate) throws SepeException {
			try {
				return getContractDataImpl(certificateInputStream, certificatePassword, certificateType, ipf, startDate, endDate);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException | InterruptedException  e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (Exception e) {
				e.printStackTrace();
				throw new SepeException(e.getMessage());
			}
			return null;
	}
	
	private static Contract getContractDataImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	webClient.getOptions().setUseInsecureSSL(true);
	    	
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultasgeneral&origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=").click();
	        handleSepeExceptions(htmlPage);

	        htmlPage = pageContracOrCopybasic(htmlPage, startDate, endDate, ipf);

	    	HtmlForm form = htmlPage.querySelector("form[name=datos]");
	    	DomNodeList<DomNode> data =form.querySelectorAll("fieldset > div > div[class*=titulo]");
	    	
			ContractBuilder builder = new ContractBuilder();

			//DATOS DE CONSULTA
			data.forEach(title->{
				String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
				DomNode valueNode = Toolkit.getNextSibling(title);
				if(titleStr.length()>0 && valueNode!=null) {
					String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
					if(valueStr.length() > 0) {
						if(titleStr.indexOf("CIF")>=0) { 
							builder.setCifEnterprise(valueStr); 
						} else if(titleStr.indexOf("Cuenta de Cotizaci\u00F3n :")>=0) { 
							String newStr = Toolkit.noSpaces(valueStr);
							builder.setRegimen(newStr.substring(0,4));
							builder.setCtaCti(newStr.substring(4));
						} else if(titleStr.indexOf("NIF/NIE :")>=0) { 
							builder.setIpf(valueStr); 
						} else if(titleStr.indexOf("Fecha de Nacimiento :")>=0) { 
							builder.setDateBirth(Toolkit.parseDate(valueStr, "dd/MM/yyyy") ); 
						} else if(titleStr.indexOf("Nombre / Apellidos :")>=0) { 
							builder.setName(valueStr); 
						} else if(titleStr.indexOf("Sexo :")>=0) {
							if(valueStr.indexOf("HOM")>=0) {
								builder.setSex(SexType.HOMBRE);
							} else if(valueStr.indexOf("MUJ")>=0) {
								builder.setSex(SexType.MUJER);
							}
						} else if(titleStr.indexOf("N\u00FCmero de afiliación SS :")>=0) { 
							builder.setNss(valueStr); 
						} else if(titleStr.indexOf("Identificador del contrato :")>=0) {
							builder.setSepeId(valueStr.trim().replace("-", "").substring(1)); 
						} else if(titleStr.indexOf("Fecha de inicio del contrato :")>=0) { 
							builder.setDateIniContract( Toolkit.parseDate(valueStr, "dd/MM/yyyy") ); 
						} else if(titleStr.indexOf("Fecha Fin del Contrato :")>=0) { 
							builder.setDateFinContract( Toolkit.parseDate(valueStr, "dd/MM/yyyy") ); 
						} else if(titleStr.indexOf("Fecha en que se comunica :")>=0) { 
							builder.setDateComContract( Toolkit.parseDate(valueStr, "dd/MM/yyyy") ); 
						}
					}
				}
			});
			
	        return builder.build();
		} 
	}
	
	private static void sendContrataExtensionImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, 
			ContractExtension contractExtension) 
			throws SepeException, FailingHttpStatusCodeException, InterruptedException, IOException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			
			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);
			
			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
	
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletProrrogas?pagina=inicio").click();
	        handleSepeExceptions(htmlPage);
	        
	        String cifTypeStr = Toolkit.getIdentityType(contractExtension.getCif()); 
	        
	        Integer cifType  = 0; 
	        
	        if( cifTypeStr.equals("1") ) {
	        	cifType = 1;
	        } else if( cifTypeStr.equals("6") ) {			        	
	        	cifType = 2;
	        }
	        
		    Optional<String> sepeId = contractExtension.getSepeId();
		    
	    	HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    	
	    	if(sepeId.isPresent()) { // por identificacion de la comunicacion
	            String ide = sepeId.get();
	    		((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();
	    		String ide1 = ide.substring(0, 2);
	    		String ide2 = ide.substring(2, 6);
	    		String ide3 = ide.substring(6);
	            formDatos.getInputByName("idcomunicacion1").setValueAttribute(ide1);
	            formDatos.getInputByName("idcomunicacion2").setValueAttribute(ide2);
	            formDatos.getInputByName("idcomunicacion3").setValueAttribute(ide3);
	            formDatos.getInputByName("idcontrato").setValueAttribute(ide1+"-"+ide2+"-"+ide3);
	    	} else {
				throw new InvalidDataException("Sepe IDE requerido");
	    	}
	    	
			htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);
		    
		    HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		    
		    String cifValue = form.getInputByName("cifnifempresapro").getValueAttribute();
		    if(cifValue!=null && cifValue.isEmpty()) {
				HtmlOption option = (HtmlOption)  form.querySelectorAll("select[name=tipodocumento]>option").get(cifType);//" " cif, "D" NIF, "E" NIE			
				option.click();

				form.getInputByName("cifnifempresapro").setValueAttribute(contractExtension.getCif());
		    }
	    	
	    	String ccc = contractExtension.getCtaCti();
			form.getInputByName("cuentacotizacion1pro").setValueAttribute(contractExtension.getRegime());
			form.getInputByName("cuentacotizacion2pro").setValueAttribute(ccc.substring(0,9));
			form.getInputByName("cuentacotizacion3pro").setValueAttribute(ccc.substring(9));
			
			String[] startDate = Toolkit.dateString(contractExtension.getStartDate());
			form.getInputByName("diainiciopro").setValueAttribute(startDate[0]);
			form.getInputByName("mesiniciopro").setValueAttribute(startDate[1]);
			form.getInputByName("annoiniciopro").setValueAttribute(startDate[2]);
			
			String[] endDate = Toolkit.dateString(contractExtension.getEndDate());
			form.getInputByName("diafinpro").setValueAttribute(endDate[0]);
			form.getInputByName("mesfinpro").setValueAttribute(endDate[1]);
			form.getInputByName("annofinpro").setValueAttribute(endDate[2]);
			
			DomNode discontinuidad = form.querySelector("[name=\"discontinuidad\"]");
			if(discontinuidad!=null && contractExtension.getDiscontinuo()) { 
				((HtmlSelect)discontinuidad).setSelectedAttribute("S", true);
			}
			
			Optional<String> exist = htmlPage.querySelectorAll("form[name=\"datos\"] fieldset div[class*=titulo]")
	    	.stream()
	    	.filter(e-> !e.getTextContent().isEmpty() && e.getTextContent().trim().toLowerCase().contains("ya se ha comunicado"))
	    	.map(e -> e.getTextContent().trim()).findFirst();
	    	
	    	String messageError = "Error no aceptada la comunicaci\u00f3n";
	    	
	    	if(exist.isEmpty()) {
		        htmlPage = ((HtmlSubmitInput)form.querySelector("[name=enviar]")).click();

		        handleSepeExceptions(htmlPage);
		        
		        String message = getSuccessMessage(htmlPage);
				if(message!=null && message.contains("se ha realizado correctamente")) {
					System.out.println(message);
					return;
				}
	    	} else {
	    		messageError = exist.get();
	    	}
	    	
	    	throw new SepeException(messageError);
		}
	}
	
	private static String sendCopyBasicImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, 
			CopyBasic copyBasic) 
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=copiabasica").click(); 
	        handleSepeExceptions(htmlPage);
	        
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_comunica_copiaBasicaContrato.jsp?origen=copiabasica").click();
		    handleSepeExceptions(htmlPage);
		    
		    Optional<String> sepeId = copyBasic.getSepeId();
		    
		    if(sepeId.isPresent()) {
		    	htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idcomunicacion&origen=copiabasica").click(); 
			    handleSepeExceptions(htmlPage);
			    
	        	HtmlForm formTwo = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	            String ide = sepeId.get();
	    		String ide1 = ide.substring(0, 2);
	    		String ide2 = ide.substring(2, 6);
	    		String ide3 = ide.substring(6);
	    		formTwo.getInputByName("idcomunicacion1").setValueAttribute(ide1);
	    		formTwo.getInputByName("idcomunicacion2").setValueAttribute(ide2);
	    		formTwo.getInputByName("idcomunicacion3").setValueAttribute(ide3);
	    		formTwo.getInputByName("idcomunicacion").setValueAttribute(ide1+"-"+ide2+"-"+ide3);
	            
	    		htmlPage = formTwo.getInputByName("aceptar").click();
	    		
		        handleSepeExceptions(htmlPage);
		    } else {
		    	htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=copiabasica").click(); 
			    handleSepeExceptions(htmlPage);
			    
			    Optional<Date> startDateOpt = copyBasic.getFini();
			    Optional<Date> endDateOpt = copyBasic.getFend();
			    Optional<String> ipfOpt = copyBasic.getIpf();
			    
		        Date startDate = startDateOpt.get();
		        Date endDate = endDateOpt.isPresent() ? endDateOpt.get() : startDate;
		        
		        htmlPage = pageContracOrCopybasic(htmlPage, startDate, endDate, ipfOpt.get());
		    }
		    
		    HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		    
	        DomNode codTipoFirma = form.querySelector("select[name=codtipofirma]");
	        if(codTipoFirma!=null) {
		        ((HtmlSelect)codTipoFirma).setSelectedAttribute(copyBasic.getFirmType().getValue().toString(), true);
	        }
	        
	        DomNode areadeDomicilio = form.querySelector("[name=areadeDomicilio]");
	        if(areadeDomicilio!=null && copyBasic.getWorkAddress()!=null) {
		        ((HtmlTextArea)areadeDomicilio).setText(copyBasic.getWorkAddress());
	        }
	        
	        DomNode areadeTexto = form.querySelector("[name=areadeTexto]");
	        if(areadeTexto!=null && copyBasic.getRestContract()!=null) {
		        ((HtmlTextArea)areadeTexto).setText(copyBasic.getRestContract());
	        }

	    	Optional<String> exist = htmlPage.querySelectorAll("form[name=\"datos\"] fieldset div[class*=titulo]")
	    	.stream()
	    	.filter(e-> !e.getTextContent().isEmpty() && e.getTextContent().trim().toLowerCase().contains("ya se ha comunicado"))
	    	.map(e -> e.getTextContent().trim()).findFirst();
	    	
	    	String messageError = "Error no aceptada la comunicaci\u00f3n";
	    	
	    	if(exist.isEmpty()) {
		        htmlPage = ((HtmlSubmitInput)form.querySelector("[name=enviar]")).click();
		        handleSepeExceptions(htmlPage);
		        
		        String message = getSuccessMessage(htmlPage);
				if(message!=null && message.contains("se ha realizado correctamente")) {
				     return message;
				}
	    	} else {
	    		messageError = exist.get();
	    	}
	    	
	    	throw new SepeException(messageError);
		} 
	}
	
	private static String sendTransformationCopyBasicImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, 
			CopyBasic copyBasic, String cif, Date startDate) 
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		webClient.getOptions().setUseInsecureSSL(true);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=copiabasica").click(); 
	        handleSepeExceptions(htmlPage);
	        
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaTransformacion?pagina=entradaCBTransf").click();
		    handleSepeExceptions(htmlPage);
		    
		    Optional<String> sepeId = copyBasic.getSepeId();
		    
	    	HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    	
	    	if(sepeId.isPresent()) { // por identificacion de la comunicacion
	            String ide = sepeId.get();
	    		((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();
	    		String ide1 = ide.substring(0, 2);
	    		String ide2 = ide.substring(2, 6);
	    		String ide3 = ide.substring(6);
	            formDatos.getInputByName("idcomunicacion1").setValueAttribute(ide1);
	            formDatos.getInputByName("idcomunicacion2").setValueAttribute(ide2);
	            formDatos.getInputByName("idcomunicacion3").setValueAttribute(ide3);
	            formDatos.getInputByName("idcontrato").setValueAttribute(ide1+"-"+ide2+"-"+ide3);
	    	} else {
		   
		    	((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"2\"]")).click();
		    	
			    //ENTERPRISE
			    String cifValue = formDatos.getInputByName("cifnifnie").getValueAttribute();
			    
			    if(cifValue!=null && cifValue.isEmpty() && cif!=null) {
			        String cifTypeStr = Toolkit.getIdentityType(cif); 
			        Integer cifType = 0; 
			        if( cifTypeStr.equals("1")) 
			        	cifType = 1;
			        else if(cifTypeStr.equals("6")) 
			        	cifType = 2;

					HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumentoaux]>option").get(cifType);//" " cif, "D" NIF, "E" NIE			
					option.click();

					formDatos.getInputByName("cifnifnie").setValueAttribute(cif);
			    }
				String ipf = copyBasic.getIpf().get();
				//EMPLOYEE
				Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; //1 NIE, 0 NIF
				HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumento]>option").get(ident);				
				option.click();

				formDatos.getInputByName("nifnietrabajador").setValueAttribute(Toolkit.appendStringLeft(ipf, " ", 2));
				
				String[] fri = Toolkit.formatDate(startDate);
				formDatos.getInputByName("diafechaini").setValueAttribute(fri[0]);
				formDatos.getInputByName("mesfechaini").setValueAttribute(fri[1]);
				formDatos.getInputByName("anniofechaini").setValueAttribute(fri[2]);
	    	}
	    	
			htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);
		    
		    HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		    
	        DomNode codTipoFirma = form.querySelector("select[name=codtipofirma]");
	        if(codTipoFirma!=null) {
		        ((HtmlSelect)codTipoFirma).setSelectedAttribute(copyBasic.getFirmType().getValue().toString(), true);
	        }
	        
	        DomNode areadeDomicilio = form.querySelector("[name=areadeDomicilio]");
	        if(areadeDomicilio!=null && copyBasic.getWorkAddress()!=null) {
		        ((HtmlTextArea)areadeDomicilio).setText(copyBasic.getWorkAddress());
	        }
	        
	        DomNode areadeTexto = form.querySelector("[name=areadeTexto]");
	        if(areadeTexto!=null && copyBasic.getRestContract()!=null) {
		        ((HtmlTextArea)areadeTexto).setText(copyBasic.getRestContract());
	        }
	        
	    	Optional<String> exist = htmlPage.querySelectorAll("form[name=\"datos\"] fieldset div[class*=titulo]")
	    	.stream()
	    	.filter(e-> !e.getTextContent().isEmpty() && e.getTextContent().trim().toLowerCase().contains("ya se ha comunicado"))
	    	.map(e -> e.getTextContent().trim()).findFirst();
	    	
	    	String messageError = "Error no aceptada la comunicaci\u00f3n";
	    	
	    	if(exist.isEmpty()) {
		        htmlPage = ((HtmlSubmitInput)form.querySelector("[name=enviar]")).click();
		        handleSepeExceptions(htmlPage);
		        
		        String message = getSuccessMessage(htmlPage);
				if(message!=null && message.contains("se ha realizado correctamente")) {					
					return message;
				}
	    	} else {
	    		messageError = exist.get();
	    	}
	    	
	    	throw new SepeException(messageError);
		} 
	}
	
	public static void removeContrato(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException {
		 try { removeContrataImpl(certificateInputStream, certificatePassword, certificateType, ide); }
		catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
		catch (MalformedURLException e) {throw new SepeException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SepeException(e);}
		catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}
	
	public static void removeTransformation(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException{
		try { removeTransformationImpl(certificateInputStream, certificatePassword, certificateType, ide); }
		catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
		catch (MalformedURLException e) {throw new SepeException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SepeException(e);}
		catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}
	
	private static byte[] getContratoPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
	    	 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultasgeneral&origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        if(sepeId.isPresent()) {
	        	htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idcomunicacion&origen=").click();//por identificador de la comunicacion
	            handleSepeExceptions(htmlPage);
	            
	        	HtmlForm formTwo = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	            String ide = sepeId.get();
	            formTwo.getInputByName("idcomunicacion1").setValueAttribute(ide.substring(0, 2));
	            formTwo.getInputByName("idcomunicacion2").setValueAttribute(ide.substring(2, 6));
	            formTwo.getInputByName("idcomunicacion3").setValueAttribute(ide.substring(6));
	            
	    		htmlPage = formTwo.getInputByName("aceptar").click();
		        handleSepeExceptions(htmlPage);
	        } else {
		        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=").click();//por identificador del trabajador
		        handleSepeExceptions(htmlPage);
		        
		        htmlPage = pageContracOrCopybasic(htmlPage, startDate, endDate, ipf);
	        }

	        HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        
	    	Page page = formDatos1.getInputByName("Boton_imprimir").click();
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
	
	private static byte[] getCopyBasicPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		    
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultaImpCB&origen=consultaImpresionCB").click();//PARA CONTRATOS INICIALES
	        handleSepeExceptions(htmlPage);
	        
	        if(sepeId.isPresent()) {
	            htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idcomunicacion&origen=consultaImpresionCB").click();//por identificador de la comunicacion
	            handleSepeExceptions(htmlPage);
	            
	        	HtmlForm formTwo = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	            String ide = sepeId.get();
	    		String ide1 = ide.substring(0, 2);
	    		String ide2 = ide.substring(2, 6);
	    		String ide3 = ide.substring(6);
	    		formTwo.getInputByName("idcomunicacion1").setValueAttribute(ide1);
	    		formTwo.getInputByName("idcomunicacion2").setValueAttribute(ide2);
	    		formTwo.getInputByName("idcomunicacion3").setValueAttribute(ide3);
	    		formTwo.getInputByName("idcomunicacion").setValueAttribute(ide1+"-"+ide2+"-"+ide3);
	            
	    		htmlPage = formTwo.getInputByName("aceptar").click();
	    		
		        handleSepeExceptions(htmlPage);
	        } else {
		        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idtrabajador&origen=consultaImpresionCB").click();//por identificador del trabajador
		        handleSepeExceptions(htmlPage);
		        
		        htmlPage = pageContracOrCopybasic(htmlPage, startDate, endDate, ipf);
	        }
	        

	        HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        
	     	Page page = formDatos1.getInputByName("Boton_imprimir").click();
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
	
	private static byte[] getTransformationCopyBasicPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date startDate, Optional<String> sepeId) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=init&origen=consultaImpresionCB").click();//PARA CONTRATOS TRANSFORMACIONES
	        handleSepeExceptions(htmlPage);
	        
	        HtmlUnitToolkit.manageStatusCode(htmlPage);

	    	HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    	
	    	if(sepeId.isPresent()) { // por identificacion de la comunicacion
	            String ide = sepeId.get();
	    		((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();
	    		String ide1 = ide.substring(0, 2);
	    		String ide2 = ide.substring(2, 6);
	    		String ide3 = ide.substring(6);
	            formDatos.getInputByName("idcomunicacion1").setValueAttribute(ide1);
	            formDatos.getInputByName("idcomunicacion2").setValueAttribute(ide2);
	            formDatos.getInputByName("idcomunicacion3").setValueAttribute(ide3);
	            formDatos.getInputByName("idcontrato").setValueAttribute(ide1+"-"+ide2+"-"+ide3);
	    	} else {
		   
		    	((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"2\"]")).click();
		    	
			    //ENTERPRISE
			    String cifValue = formDatos.getInputByName("cifnifnie").getValueAttribute();
			    
			    if(cifValue!=null && cifValue.isEmpty()) {
			        String cifTypeStr = Toolkit.getIdentityType(cif); 
			        Integer cifType = 0; 
			        if( cifTypeStr.equals("1")) 
			        	cifType = 1;
			        else if(cifTypeStr.equals("6")) 
			        	cifType = 2;

					HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumentoaux]>option").get(cifType);//" " cif, "D" NIF, "E" NIE			
					option.click();

					formDatos.getInputByName("cifnifnie").setValueAttribute(cif);
			    }
				
				//EMPLOYEE
				Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; //1 NIE, 0 NIF
				HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumento]>option").get(ident);				
				option.click();

				formDatos.getInputByName("nifnietrabajador").setValueAttribute(Toolkit.appendStringLeft(ipf, " ", 2));
				
				String[] fri = Toolkit.formatDate(startDate);
				formDatos.getInputByName("diafechaini").setValueAttribute(fri[0]);
				formDatos.getInputByName("mesfechaini").setValueAttribute(fri[1]);
				formDatos.getInputByName("anniofechaini").setValueAttribute(fri[2]);
	    	}
	    	
			htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);

			
	        HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        
	     	Page page = formDatos1.getInputByName("Boton_imprimir").click();
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
	
	private static void removeContrataImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide)  throws SepeException, FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, InterruptedException {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

	    	htmlPage = firstPageRemove(htmlPage);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initC").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = lastPageRemove(htmlPage, ide);
	        handleSepeExceptions(htmlPage);
	        
	        String message = getSuccessMessage(htmlPage);
			if(message!=null && message.contains("se ha realizado correctamente")){
				System.out.println(message);
			} else 
				throw new SepeException("no se ha realizado");
		} 
	}
	
	private static void removeTransformationImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide) throws SepeException, FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, InterruptedException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
	    	htmlPage = firstPageRemove(htmlPage);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initT").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = lastPageRemove(htmlPage, ide);

	        String message = getSuccessMessage(htmlPage);
			if(message!=null && message.contains("se ha realizado correctamente")){
				System.out.println(message);
			} else 
				throw new SepeException("no se ha realizado");
		} 
	}
	
	private static HtmlPage firstPageRemove(HtmlPage htmlPage) throws SepeException, ElementNotFoundException, IOException  {
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=anulacioncomunicacion").click(); 
			try {
				DomNode error = htmlPage.querySelector("#contIzq > div.contIzqc > p");
				if(error!=null && !error.getVisibleText().isEmpty() && error.getVisibleText().contains("disponible para el usuario principal")) 
					throw new SepeException(error.getVisibleText());
			} catch (NullPointerException e) {}
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_anulacion_bajas.jsp?origen=anulacioncomunicacion").click();

	        handleSepeExceptions(htmlPage);
	        return htmlPage;
	}
	
	private static HtmlPage lastPageRemove(HtmlPage htmlPage, String ide) throws SepeException, ElementNotFoundException, IOException, InterruptedException  {
		if(ide==null) throw new InvalidDataException("Sepe IDE requerido");
	
		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		String ide1 = ide.substring(0, 2); //2 digits
		String ide2 = ide.substring(2, 6); //4 digits
		String ide3 = ide.substring(6);    //7 digits
		formDatos.getInputByName("idcomunicacion1").setValueAttribute(ide1);
		formDatos.getInputByName("idcomunicacion2").setValueAttribute(ide2);
		formDatos.getInputByName("idcomunicacion3").setValueAttribute(ide3);
		
		DomNode ideEl = formDatos.querySelector("[name=\"idcomunicacion\"]");
		if(ideEl!=null) {
			((HtmlInput)ideEl).setValueAttribute(ide);
		}
	
		HtmlElement inputSubmit = formDatos.querySelector("input[value=aceptar]");
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
	
		formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    inputSubmit = formDatos.querySelector("#anular");
	   
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
		
	    inputSubmit = htmlPage.querySelector("form input[name=enviar]");
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
	
        return htmlPage;
	}
	
	private static HtmlPage pageContracOrCopybasic(HtmlPage htmlPage, Date startDate, Date endDate, String ipf) throws InterruptedException, IOException, SepeException  {
    	String[] fri  = Toolkit.formatDate(startDate);
    	String[] fre  = Toolkit.formatDate(endDate);
	    Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; // 1 NIE, 0 NIF //NIF DEFAULT

		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodoc2]>option").get(ident);				
		option.click();
		formDatos.getInputByName("nifnietrabajador").setValueAttribute(Toolkit.appendStringLeft(ipf, " ", 2));
		formDatos.getInputByName("diadesde").setValueAttribute(fri[0]);
		formDatos.getInputByName("mesdesde").setValueAttribute(fri[1]);
		formDatos.getInputByName("anniodesde").setValueAttribute(fri[2]);
		
		formDatos.getInputByName("diahasta").setValueAttribute(fre[0]);
		formDatos.getInputByName("meshasta").setValueAttribute(fre[1]);
		formDatos.getInputByName("anniohasta").setValueAttribute(fre[2]);
	
		htmlPage = formDatos.getInputByName("aceptar").click();
		handleSepeExceptions(htmlPage);
		
		String startDateStr = fri[0]+"/"+fri[1]+"/"+fri[2];
		HtmlTable table = (HtmlTable) htmlPage.querySelector("table.tableScroll");
		
		HtmlCheckBoxInput firstColumn;
		String columnCheck = "a0";
		for (final HtmlTableRow row : table.getRows()) {
			HtmlTableCell cell = row.getCell(5);
			if(cell.getVisibleText().indexOf(startDateStr)>= 0) {
				firstColumn = row.getCell(0).querySelector("input[name=indice]");
				columnCheck  = firstColumn.getValueAttribute();
				break;
			}
		}

        htmlPage = ((HtmlCheckBoxInput)htmlPage.querySelector("form[name=datos] input[value="+columnCheck+"]")).click();
        handleSepeExceptions(htmlPage);
        return htmlPage;
	}
	
	private static byte[] getTransformationPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date startDate, Optional<String> sepeId) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SepeException, InterruptedException {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
		
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        handleSepeExceptions(htmlPage);
	        
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaTransformacion?pagina=entrada").click();
	        handleSepeExceptions(htmlPage);
	        
	        HtmlUnitToolkit.manageStatusCode(htmlPage);

	    	HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    	
	    	if(sepeId.isPresent()) { // por identificacion de la comunicacion
	            String ide = sepeId.get();
	    		((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();

	            formDatos.getInputByName("idcomunicacion1").setValueAttribute(ide.substring(0, 2));
	            formDatos.getInputByName("idcomunicacion2").setValueAttribute(ide.substring(2, 6));
	            formDatos.getInputByName("idcomunicacion3").setValueAttribute(ide.substring(6));
	    	} else {
	    		
			    ((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"2\"]")).click();
		    	
			    //ENTERPRISE
			    String cifValue = formDatos.getInputByName("cifnifnie").getValueAttribute();
			    
			    if(cifValue!=null && cifValue.isEmpty()) {
			        String cifTypeStr = Toolkit.getIdentityType(cif); 
			        Integer cifType = 0; 
			        if( cifTypeStr.equals("1")) 
			        	cifType = 1;
			        else if(cifTypeStr.equals("6")) 
			        	cifType = 2;

					HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumentoaux]>option").get(cifType);//" " cif, "D" NIF, "E" NIE			
					option.click();

					formDatos.getInputByName("cifnifnie").setValueAttribute(cif);
			    }

				
				//EMPLOYEE
				Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; //1 NIE, 0 NIF
				HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumento]>option").get(ident);				
				option.click();
				
				formDatos.getInputByName("nifnietrabajador").setValueAttribute(Toolkit.appendStringLeft(ipf, " ", 2));
				
		    	String[] fri = Toolkit.formatDate(startDate);
				formDatos.getInputByName("diafechaini").setValueAttribute(fri[0]);
				formDatos.getInputByName("mesfechaini").setValueAttribute(fri[1]);
				formDatos.getInputByName("anniofechaini").setValueAttribute(fri[2]);
	    	}
	 
	    	
	    	htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);
			
	     	Page page = htmlPage.getElementByName("enviar").click();
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
	
	private static void setOccupation(Contract cto, HtmlForm form) {
		if(cto.getCodOccupation()!=null) {
			DomNode ocupacion = form.querySelector("[name=\"ocupacion\"]");
			if(ocupacion!=null) {
				((HtmlInput) ocupacion).setValueAttribute(cto.getCodOccupation());
			}
			
			DomNode cocupacion = form.querySelector("[name=\"cocupacion\"]");
			if(cocupacion!=null) {
				((HtmlInput) cocupacion).setValueAttribute(cto.getCodOccupation());
			}
		}
	}
	
	private static HtmlPage getFirstPageSepeContrata(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SepeException {
		  webClient.getOptions().setJavaScriptEnabled(true);
		  webClient.getOptions().setThrowExceptionOnScriptError(false);
		  webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
		  HtmlPage htmlPage = webClient.getPage("https://www.sepe.es:444/ccomunicacto/servlet/ServletInicio?CCAA=99&idioma=14");
		  HtmlUnitToolkit.manageStatusCode(htmlPage); 
		  handleSepeExceptions(htmlPage);
	      return htmlPage;
	}
	
	private static HtmlPage contractPage(HtmlPage htmlPage, String codCto) throws ElementNotFoundException, IOException, SepeException {
		String href = null;
        String oneCodCto = codCto.substring(0,1);
        switch (oneCodCto) {
			case "1": //INDEFINIDO_TIEMPO_COMPLETO
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=1";
			break;
			case "2": // INDEFINIDO_TIEMPO_PARCIAL
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=2";
			break;
			case "3": // FIJO_DISCONTINUO
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=3";
			break;
			case "4": // TEMPORAL_TIEMPO_COMPLETO
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=4";
			break;
			case "5": // TEMPORAL_TIEMPO_PARCIAL
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=5";
			break;
			default:
				throw new SepeException("Contrato no soportado");
		}
        //        /ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=7  // FP TC
        //        /ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=8  // FP TP
        htmlPage = htmlPage.getAnchorByHref(href).click();
        return htmlPage;
	}
	
	private static String getSuccessMessage(HtmlPage htmlPage) {
		DomNodeList<DomNode> texts = htmlPage.querySelectorAll("#contIzq p");
		String msg = null;
		
		final List<String> list = Arrays.asList(
				"sin fecha de t\u00E9rmino", 
				"f\u00EDsica en la base de datos", 
				"igual o inferior a 90", //previsible inferior o igual a 90 dias
				"convenio colectivo que autoriza", //previsible mayor a 90 dias
				"certificado de profesionalidad" // indicar si el trabajador tiene Certificado de Profesionalidad
		);
		
		for( DomNode p: texts) {
			String pStr = Toolkit.removeNBSP(p.getVisibleText()).trim();
			Integer pInt = pStr.length();
			boolean b = false;
			if(pInt > 2 && !pStr.isEmpty()) {
				String pLowerCase = pStr.toLowerCase();
				if(pLowerCase.contains("identificador de la comunicaci\u00F3n")) {
					String[] parts = pStr.split(":");
					if(parts.length > 0) {
						msg = (parts[1]).trim().replace("-", "");
					}
					b = true;
				} else if(pLowerCase.contains("se ha realizado correctamente")) {
					msg = pStr;
					b = true;
				} else if( list.stream().anyMatch(pLowerCase::contains) ) {
					msg = "returnInit";
					b = true;
				} 
				
				if(b) {
					break;
				}
			}
		}
		return msg;
	}
	
	private static HtmlPage sepeReturnInitPage(HtmlPage htmlPage, Contract cto) throws IOException, InterruptedException, SepeException {
		
		htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("#volver")).click();
		HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		form.getInputByName("cocupacion").setValueAttribute(cto.getCodOccupation().toString());// repeat cod contract
		form.getInputByName("contratoEscrito").setValueAttribute("N"); //  contratoEscrito si la fecha fin es menor a 28 
		form.getInputByName("nass").setValueAttribute(cto.getNss()); 
		
		//---------------------PREVISIBLE---------------------
		if( Arrays.asList("402", "502").contains(cto.getCodContract()) ) { // es previsible
			DomNode previsible = form.querySelector("select[name=preg90dias]"); 
			if(previsible!=null) {	//previsible inferior o igual a 90 dias
				((HtmlSelect)previsible).setSelectedAttribute(cto.getPrevisible() ? "S" : "N", true);
			} else {
				DomNode previsibleAutoriza = form.querySelector("select[name=AutorizaDuracion]");
				if(previsibleAutoriza!=null) { //previsible mayor a 90 dias
					((HtmlSelect)previsibleAutoriza).setSelectedAttribute(cto.getPrevisible() ? "1" : "0", true);
				}
			}
		}
		
		DomNode certificadoProf = form.querySelector("select[name=certificadoProf]"); 
		if(certificadoProf!=null) {
			((HtmlSelect)certificadoProf).setSelectedAttribute(cto.getCertificateProfessional() ? "S" : "N", true);
		}
		
		
		htmlPage = ((HtmlSubmitInput)form.querySelector("[name=aceptar]")).click();
		
		return htmlPage;
	}

	private static void handleSepeExceptions(HtmlPage htmlPage) throws SepeException{
//		Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/Documentos/test.html");
		try {
			DomNode error = htmlPage.querySelector("#avisos > div > p:last-child");
			if(error!=null && !error.getVisibleText().isEmpty()) {
				throw new SepeException(error.getVisibleText());
			} else {
				String body = htmlPage.asText();
				if(body!=null) {	
					Pattern pattern = Pattern.compile("certificado\\s*digital\\s*no\\s*v.lido", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(body);
					if(matcher.find()){
						throw new InvalidCertificateException("Certificado digital no v\u00e1lido");
					}
				}
			}
	
		} catch (NullPointerException e) {}
	}
	
	private static void handleSepeAlert(List<String> list) throws SepeException{
		if(!list.isEmpty()) {
			throw new SepeException(list.get(0));
		}
	}
	
	private static void validateCertImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType)  throws SepeException, IOException{
		byte[] certByte = certificateInputStream.readAllBytes();
	    try (WebClient webClient = HtmlUnitToolkit.getWebClientCert(new ByteArrayInputStream(certByte), certificatePassword, certificateType)) {
	    	validateCertExpired( new ByteArrayInputStream(certByte), certificatePassword);
	    	HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        handleSepeExceptions(htmlPage);
	        DomNode fielset = htmlPage.querySelector("form > fieldset");
	        if(fielset!=null && !fielset.getVisibleText().isEmpty() && fielset.getVisibleText().indexOf("errores")>=0) {
	        	DomNode error =  fielset.querySelector("p");
	        	if(error!=null && !error.getVisibleText().isEmpty())  throw new SepeException(error.getVisibleText());
	        }	        
	
		} catch (Exception e) {throw new SepeException(e.getMessage());}
	}
	
	private static void validateCertExpired(InputStream certificateInputStream,  String certificatePassword) throws Exception {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(certificateInputStream, certificatePassword.toCharArray());
        Enumeration<?> aliases = keystore.aliases();
        Date expiryDate = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        for(; aliases.hasMoreElements();) {
            String alias = (String) aliases.nextElement();
            expiryDate = ((X509Certificate) keystore.getCertificate(alias)).getNotAfter();
            if(expiryDate.compareTo(cal.getTime()) < 0 ) 
            	throw new Exception("El certificado ha expirado");
        }
	}	
}
