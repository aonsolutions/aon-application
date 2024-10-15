package solutions.aon.seg.social;

import static solutions.aon.seg.social.SistemaRED.PartType.ALTA;
import static solutions.aon.seg.social.SistemaRED.PartType.BAJA;
import static solutions.aon.seg.social.SistemaRED.PartType.CONFIRMACION;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getElConstains;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.StringWebResponse;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.util.WebConnectionWrapper;
import org.htmlunit.xml.XmlPage;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.ITPartPage;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDITPart extends ServicioREDPartUtils {
	
	//Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/test.html");
	private static final String MESSAGE_ERROR  = "Error no aceptada la comunicaci\u00f3n";
	private static final String TRY_AGAIN  = "Intente nuevamente!";
	private static final String BASE_URI = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=IWXP0002";
	private static final String ARQ_SPM_OUT = "ARQ.SPM.OUT";
	private static final String DT = ".//div[@class='datosEnLinea']/dl/div/dt";


	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date startDate, Date endDate, Optional<String> nss)
			throws SegSocialException {
		return orderByIT(
			getItsImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, nss, Optional.ofNullable(startDate), Optional.ofNullable(endDate), Optional.empty())
		); 
	}
	
	public static Optional<ITPart> getDataIT(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, String nss, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess)
			throws SegSocialException, FailingHttpStatusCodeException {
		return getItsImpl(
			certificateInputStream, certificatePassword, certificateType, regime, ccc, Optional.of(nss), 
			Optional.empty(), Optional.empty(), Optional.ofNullable(dateBj)
		)
		.stream().filter(p-> p.checkForType(partType, dateProcess))
		.findFirst();
	}
	
	// REGISTER IT START HANDLE EXCEPTIONS
	public static byte[] registerItBaja(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, Optional<String> licenseNumber, Optional<String> cias,
			Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, situationEmployee, licenseNumber, cias,
				startdate, contractType, baseCot, cotDays });

		try {
			return registerItBajaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee,startdate, contractType, baseCot,
					cotDays, fATEP, accidentType, licenseNumber, cias, occupation, job, jobDescription);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}
	
	// REGISTER IT END HANDLE EXCEPTIONS
	public static byte[] registerItAlta(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date fbaja, Date falta, 
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, SistemaRED.CauseType causeType, Optional<String> licenseNumber, Optional<String> cias)
			throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, fbaja, falta });
		try {
			return registerItAltaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee, fbaja, falta, fATEP, accidentType, causeType, licenseNumber, cias);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}
	
	// REGISTER IT CONFIRMATION HANDLE EXCEPTIONS
	public static byte[] registerItConfirmation(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency });
		try {
			return registerItConfirmationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee, licenseNumber, cias, fbaja, fconfirmation, npartConfimation);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			throw new SegSocialException(e.getMessage());
		} 
		return null;
	}
	
	// remove IT
	public static void removeIt(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj, Date dateProcess)
			throws IOException, InterruptedException, SegSocialException {
		Toolkit.verifyData(new Object[] { regime, ccc, naf, dateProcess });
		try {
			removeItImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType,
					dateBj, dateProcess);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			throw new SegSocialException(e.getMessage());
		} 
	}
	
	// remove IT
	public static byte[] getITReport(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String nss, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess)
			throws IOException, InterruptedException, SegSocialException {
		Toolkit.verifyData(new Object[] { regime, ccc, nss, dateProcess });
		try {
			return getITReportImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, nss, partType,
					dateBj, dateProcess);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null; 
	}
	
	private static List<ITPart> getItsImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Optional<String> nss, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> dateBj)
			throws SegSocialException{


		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);
			
			if (endDate.isPresent() && Toolkit.isFuture(endDate.get()) )
				throw new InvalidDateException();

			ArrayList<ITPart> itParts = new ArrayList<>();

			XmlPage xmlPage = webClient.getPage(BASE_URI);
			HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			handleItPartErrors(htmlPage);

			xmlPage = htmlPage.getElementById("PEST_3").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);

			wait4(htmlPage, p -> p.querySelector("[name=\"regimenConsulta\"]"))
					.orElseThrow(() -> new SegSocialException(TRY_AGAIN));

			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4"))
					.orElseThrow(() -> new SegSocialException(TRY_AGAIN));
			form.getInputByName(ARQ_SPM_OUT).remove();

			form.getInputByName("regimenConsulta").setValue(regime);
			form.getInputByName("cccConsulta").setValue(ccc);
			form.getInputByName("nafConsulta").setValue(nss.orElse(""));
			
			if ( startDate.isPresent() ) {
				Toolkit.formatDate(startDate.get(), DATE_FORMAT).ifPresent(f -> form.getInputByName("fechaDesdeConsulta").setValue(f));
			}
			if (endDate.isPresent()) {
				Toolkit.formatDate(endDate.get(), DATE_FORMAT).ifPresent(t -> form.getInputByName("fechaHastaConsulta").setValue(t));	
			}
			

			HtmlButton continueIn = (HtmlButton) wait4(htmlPage,
					p -> p.querySelector("button[value=\"CONTINUAR_CONSULTA\"]")).orElseThrow();
			xmlPage = continueIn.click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			boolean nextExist = true;
			do {
				HtmlTable tableResults = (HtmlTable) htmlPage.getElementById("TABLA_12");
				List<HtmlTableBody> tableBodies = tableResults.getBodies();
				for (HtmlTableBody htmlTableBody : tableBodies) {
					List<HtmlTableRow> rows = htmlTableBody.getRows();
					for (HtmlTableRow row : rows) {
						List<HtmlAnchor> anchor = row.getByXPath(".//a");
						xmlPage = anchor.get(0).click();
						HtmlPage detailsHtmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);

						ITPart itPart = new ITPart();
						DomElement datosConsulta = detailsHtmlPage.getElementById("CONTENEDOR_SECCION_11");
						List<DomElement> dtConsultas = datosConsulta.getByXPath(DT);
						for (DomElement dtConsulta : dtConsultas) {
							if ("C.C.C.:".equals(dtConsulta.getTextContent())) {
								String ddConsulta = dtConsulta.getNextElementSibling().getTextContent();
								itPart.setCcc(ddConsulta);
						    }
							else if ("N.A.F.:".equals(dtConsulta.getTextContent())) {
								String ddConsulta = dtConsulta.getNextElementSibling().getTextContent();
								itPart.setNaf(ddConsulta);
							}
							else if ("Contingencia:".equals(dtConsulta.getTextContent())) {
								String ddConsulta = dtConsulta.getNextElementSibling().getTextContent();
								itPart.setContingency(ddConsulta);
							}
							else if ("Fecha de baja:".equals(dtConsulta.getTextContent())) {
								String ddConsulta = dtConsulta.getNextElementSibling().getTextContent();
								itPart.setWorkLeaveDate(Toolkit.parseDate(ddConsulta, DATE_FORMAT));
							}
							else if ("Tipo de parte:".equals(dtConsulta.getTextContent())) {
								String ddConsulta = dtConsulta.getNextElementSibling().getTextContent();
								itPart.setPartType(ddConsulta);
							}
							else if ("Fecha de recepción:".equals(dtConsulta.getTextContent())) {
								String ddConsulta = dtConsulta.getNextElementSibling().getTextContent();
								itPart.setReceptionDate(Toolkit.parseDate(ddConsulta, DATE_FORMAT));
							}
						}
						
						
						DomElement datosPersonales = detailsHtmlPage.getElementById("CONTENEDOR_SECCION_12");
						List<DomElement> dtPersonales = datosPersonales.getByXPath(DT);
						for (DomElement dtPersonal : dtPersonales) {
							if ("Nombre:".equals(dtPersonal.getTextContent())) {
								String ddPersonal = dtPersonal.getNextElementSibling().getTextContent();
								itPart.setNameEmployee(ddPersonal);
						    }
							else if ("IPF:".equals(dtPersonal.getTextContent())) {
								String ddPersonal = dtPersonal.getNextElementSibling().getTextContent();
								itPart.setIpf(ddPersonal);
							}
							else if ("Dirección:".equals(dtPersonal.getTextContent())) {
								String ddPersonal = dtPersonal.getNextElementSibling().getTextContent();
								itPart.setDirectionEmployee(ddPersonal);
							}
						}
						DomElement datosEmpresa = detailsHtmlPage.getElementById("CONTENEDOR_SECCION_13");
						List<DomElement> dtEmpresas = datosEmpresa.getByXPath(DT);
						for (DomElement dtEmpresa : dtEmpresas) {
							if ("Nombre:".equals(dtEmpresa.getTextContent())) {
								String ddEmpresa = dtEmpresa.getNextElementSibling().getTextContent();
								itPart.setNameEnterprise(ddEmpresa);
						    }
							else if ("Dirección:".equals(dtEmpresa.getTextContent())) {
								String ddEmpresa= dtEmpresa.getNextElementSibling().getTextContent();
								itPart.setDirectionEnterprise(ddEmpresa);
							}
						}
						DomElement contrato = detailsHtmlPage.getElementById("CONTENEDOR_SECCION_18");
						List<DomElement> dtContratos = contrato.getByXPath(DT);
						for (DomElement dtContrato : dtContratos) {
							if ("Tipo de contrato:".equals(dtContrato.getTextContent())) {
								String ddContrato = dtContrato.getNextElementSibling().getTextContent();
								itPart.setTypeCto(ddContrato);
						    }
							else if ("Puesto de trabajo:".equals(dtContrato.getTextContent())) {
								String ddContrato = dtContrato.getNextElementSibling().getTextContent();
								itPart.setCatProf(ddContrato);
							}
							else if ("Base de cotización:".equals(dtContrato.getTextContent())) {
								String ddContrato = dtContrato.getNextElementSibling().getTextContent();
								itPart.setBaseCtz(Toolkit.parseStringToFloat(ddContrato));
							}
							else if ("Suma base de cotización:".equals(dtContrato.getTextContent())) {
								String ddContrato = dtContrato.getNextElementSibling().getTextContent();
								itPart.setSumBCtz(Toolkit.parseStringToFloat(ddContrato)); 
							}
							else if ("Días cotizados/mes:".equals(dtContrato.getTextContent())) {
								String ddContrato = dtContrato.getNextElementSibling().getTextContent();
								itPart.setDaysCtz(Integer.parseInt(ddContrato));
							}
							else if ("Suma días cotizados:".equals(dtContrato.getTextContent())) {
								String ddContrato = dtContrato.getNextElementSibling().getTextContent();
								itPart.setDaysSumCtz(Integer.parseInt(ddContrato));
							}
						}
						itParts.add(itPart);
					}
				}	
				DomElement next = htmlPage.getElementById("ENLACE_PAGINACION_11");
				if (next != null) {
					nextExist = true;
					xmlPage = next.click();
					htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
					webClient.waitForBackgroundJavaScript(10000);
				}
				else {
					nextExist = false;
				}
			} while (nextExist);
			
			return itParts;
		} catch (IOException | TransformerException | InterruptedException e) {
			throw new SegSocialException(e);
		}
	}
		
	// REGISTER IT START
	private static byte[] registerItBajaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType,
			Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		
		
		byte[] certificateData = certificateInputStream.readAllBytes();
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateData, certificatePassword, certificateType);
			WebConnectionWrapper wrapper =HtmlUnitToolkit.transformXmlPage(webClient, certificateData, certificatePassword, certificateType, Collections.emptyMap(), SistemaREDITPart::skipDateFormatError)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, startdate, contingency, situationEmployee, BAJA);

			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			if (job.isPresent()) {		
				HtmlInput jobInput = form.getInputByName("puestoTrabajo");
				jobInput.focus();
				jobInput.type(job.get());
				jobInput.blur();
			}
			
			// Data Contract
			HtmlOption contractTypeOption = null;
			
			switch (contractType) {
			case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL:
				webClient.waitForBackgroundJavaScript(5000);
				htmlPage = HtmlUnitToolkit.selectOption(htmlPage, "tipoContrato", "1");
				

				wait4(htmlPage, p -> form.getInputByName("sumaBaseCot"))
				.orElseThrow(() -> new SegSocialException(TRY_AGAIN));

				break;
			case RESTO_Y_AUTONOMOS:
				webClient.waitForBackgroundJavaScript(5000);
				htmlPage = HtmlUnitToolkit.selectOption(htmlPage, "tipoContrato", "2");

				wait4(htmlPage, p -> form.getInputByName("BaseCot"))
				.orElseThrow(() -> new SegSocialException(TRY_AGAIN));

				break;
				
			}
			

			if(fATEP.isPresent()) {
				DomNode inputATEP = form.getInputByName("fechaATEP");
				if(null != inputATEP) {
					((HtmlInput)inputATEP).setValue(Toolkit.formatDate(fATEP.get(), DATE_FORMAT).get());
				}
			}


			if (occupation.isPresent()) {				
				form.getSelectByName("ocupacion").setSelectedAttribute(occupation.get(), true);
			}
			
			if (jobDescription.isPresent()) {		
				form.getTextAreaByName("funcDesempe").click();
				form.getTextAreaByName("funcDesempe").setText(jobDescription.get());
			}
			

			String baseCotStr = Toolkit.parseDecimalToString(baseCot);
			switch (contractType) {
			case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL:
				form.getInputByName("sumaBaseCot").setValue(baseCotStr);
				form.getInputByName("sumaDiasCot").setValue(cotDays+"");
				break;
			case RESTO_Y_AUTONOMOS:
				form.getInputByName("BaseCot").setValue(baseCotStr);;
				form.getInputByName("DiasCot").setValue(cotDays+"");
				break;
				
			}

			//form.getElementsByTagName("input").forEach( input ->  System.out.println( ((HtmlInput)input).getId() + " = "  + ((HtmlInput)input).getValue() + " : " +  ((HtmlInput)input).isValid()  + " , " + ((HtmlInput)input).isValidValidityState() ));
			
			HtmlButton validate = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Validar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = validate.click();
			
			
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML
			
			HtmlButton confim = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Confirmar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = confim.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			return getPdfProcess(htmlPage, "#ENVIO_8");
		}
	}

	// REGISTER IT END
	private static byte[] registerItAltaImpl(InputStream certificateInputStream, String certificatePassword,
				String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
				SistemaRED.SituationEmployee situationEmployee, Date fbaja, Date falta, Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, 
				SistemaRED.CauseType causeType, Optional<String> licenseNumber, Optional<String> cias)
				throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, fbaja, contingency, situationEmployee, ALTA);

			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			wait4(htmlPage, p -> p.querySelector("[name=\"fechaBaja\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			form.getInputByName(ARQ_SPM_OUT).remove(); 
			

			Toolkit.formatDate(fbaja, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBaja").setValue(d));
			
			Toolkit.formatDate(falta, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaAlta").setValue(d));
			
			if (fATEP.isPresent()) {
				Toolkit.formatDate(fATEP.get(), DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaATEP").setValue(d));
			}

			if (accidentType.isPresent()) {
				HtmlOption typeAccidentOption = null;
				switch (accidentType.get()) {
				case LEVE:
					typeAccidentOption = form.querySelector("[name=\"tipoAccidente\"] option[value=\"1\"]");
					break;
				case GRAVE:
					typeAccidentOption = form.querySelector("[name=\"tipoAccidente\"] option[value=\"2\"]");
					break;
				case MUY_GRAVE:
					typeAccidentOption = form.querySelector("[name=\"tipoAccidente\"] option[value=\"3\"]");
					break;
				}
				if(null!=typeAccidentOption) typeAccidentOption.click();
			}
			
			HtmlSelect cause = form.querySelector("#causaAlta");
			cause.setSelectedAttribute(causeType.getValue(), true);
			
			HtmlButton validate = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Validar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = validate.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

			HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML
			
			HtmlButton confim = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Confirmar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = confim.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			return getPdfProcess(htmlPage, "#ENVIO_8");
		}
	}

	// REGISTER IT CONFIRMATION
	private static byte[] registerItConfirmationImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, fbaja, contingency, situationEmployee, CONFIRMACION);

			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			wait4(htmlPage, p -> p.querySelector("[name=\"fechaBaja\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			form.getInputByName(ARQ_SPM_OUT).remove(); 
			
			Toolkit.formatDate(fbaja, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBaja").setValue(d));
			
			Toolkit.formatDate(fconfirmation, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaConfirmacion").setValue(d));
			
			npartConfimation.ifPresent(c-> form.getInputByName("numConfirmacion").setValue(c));

			HtmlButton validate = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Validar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = validate.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML
			
			HtmlButton confim = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Confirmar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = confim.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			return getPdfProcess(htmlPage, "#ENVIO_10");
		}
	}
	
	// remove ITImpl
	private static void removeItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj, Date dateProcess ) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = setUrlParseRemoveXml(htmlPage, (HtmlAnchor)htmlPage.getElementById("PEST_4"));
		
			wait4(htmlPage, p -> p.querySelector("[name=\"regimenAnulacion\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			form.getInputByName(ARQ_SPM_OUT).remove(); 

			form.getInputByName("regimenAnulacion").setValue(regime);
			form.getInputByName("cccAnulacion").setValue(ccc);
			form.getInputByName("nafAnulacion").setValue(naf);
			Toolkit.formatDate(dateBj, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBajaMedAnulacion").setValue(d) );
			
			HtmlButton continueIn= (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[value=\"CONTINUAR_ANULACION\"]")).orElseThrow();
			htmlPage = continueIn.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, dateProcess);
			
			if (firstColumn == null) {				
				throw new NoQueryData("Sin datos de consulta");
			}
			
			htmlPage = setUrlParseRemoveXml(htmlPage, firstColumn);
			
			HtmlForm formCancel = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			formCancel.getInputByName(ARQ_SPM_OUT).remove(); 

			HtmlButton anular = (HtmlButton) formCancel.querySelector("button[value=\"ACEPTAR_ANULACION\"]");
			htmlPage = anular.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formConfirm = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			formConfirm.getInputByName(ARQ_SPM_OUT).remove(); 

			HtmlButton confirm = formConfirm.querySelector("button[value=\"CONFIRMAR_ANULACION\"]");
			htmlPage = confirm.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			String message = HtmlUnitToolkit.getMessageSuccess(htmlPage);
			if (!message.isEmpty()) {				
				System.out.println(message);
				if (!message.contains("exito")) {					
					throw new InvalidDataException(message);
				}
			} 
		}
	}
	
	// report IT
	private static byte[] getITReportImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String nss, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			
			XmlPage xmlPage = webClient.getPage(BASE_URI);
			HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			xmlPage = htmlPage.getElementById("PEST_3").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
		
			wait4(htmlPage, p -> p.querySelector("[name=\"regimenConsulta\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			form.getInputByName(ARQ_SPM_OUT).remove(); 

			form.getInputByName("regimenConsulta").setValue(regime);
			form.getInputByName("cccConsulta").setValue(ccc);
			form.getInputByName("nafConsulta").setValue(nss);
			Toolkit.formatDate(dateBj, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBajaMedConsulta").setValue(d));
			
			HtmlButton continueIn = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[value=\"CONTINUAR_CONSULTA\"]")).orElseThrow();
			xmlPage = continueIn.click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlTable table = (HtmlTable) htmlPage.getElementById("TABLA_12");
			List<HtmlTableBody> tableBodies = table.getBodies();
			HtmlTableBody firstRow = tableBodies.get(0);
			List<HtmlAnchor> anchor = firstRow.getByXPath(".//a");
			xmlPage= anchor.get(0).click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			return getPdfProcess2(htmlPage);
		}
	}
	
	private static HtmlPage fillGeneralData(HtmlPage htmlPage, String regime, String ccc, String naf, Date date,
			SistemaRED.Contingencies contingency, SistemaRED.SituationEmployee situationEmployee, SistemaRED.PartType type)
			throws IOException, InterruptedException, SegSocialException, TransformerException {
		HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		wait4(htmlPage, p ->p.querySelector("[name=\"regimen\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));

		form.getInputByName("regimen").setValue(regime); 
		form.getInputByName("ccc").setValue(ccc); 
		form.getInputByName("naf").setValue(naf); 
		Toolkit.formatDate(date, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBaja").setValue(d));
		
		form.getInputByName(ARQ_SPM_OUT).remove(); 
		
		HtmlOption typeOption = null;
		HtmlOption contingencyOption = null;
		HtmlOption situationOption = null;

		switch (type) {
			case ALTA:
				typeOption = form.querySelector("#partes option[value=\"1\"]");
				break;
			case BAJA:
				typeOption = form.querySelector("#partes option[value=\"2\"]");
				break;
			case CONFIRMACION:
				typeOption = form.querySelector("#partes option[value=\"3\"]");
				break;
		
		}
		if(null!=typeOption) typeOption.click();

		switch (situationEmployee) {
			case ACTIVO:
				situationOption = form.querySelector("#situacionTrab option[value=\"1\"]");
				break;
			case PERCEPTOR_DE_DESEMPLEO:
				situationOption = form.querySelector("#situacionTrab option[value=\"2\"]");
				break;
		}
		if(null!=situationOption) situationOption.click();

		switch (contingency) {
			case ENFERMEDAD_COMUN:
				contingencyOption = form.querySelector("#contingencias option[value=\"1\"]");
				break;
			case ACCIDENTE_NO_LABORAL:
				contingencyOption = form.querySelector("#contingencias option[value=\"2\"]");
				break;
			case ACCIDENT_LABORAL:
				contingencyOption = form.querySelector("#contingencias option[value=\"3\"]");
				break;
			case ENFERMEDAD_PROFESIONAL:
				contingencyOption = form.querySelector("#contingencias option[value=\"4\"]");
				break;
			case PERIODOS_OBSERVACION:
				contingencyOption = form.querySelector("#contingencias option[value=\"5\"]");
				break;
		}
		if(null!=contingencyOption) contingencyOption.click();

		HtmlButton accept = (HtmlButton) wait4(htmlPage, p ->p.getElementById("ENVIO_7")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		//XmlPage xmlPage = accept.click();
		//htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
		htmlPage = accept.click();
		HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

		return htmlPage;
	}

	private static List<ITPart> getParts(CloseableHttpClient httpClient, String xml, String link, String ticket, boolean detail) throws ParserConfigurationException, IOException, SegSocialException {
		List<ITPart> parts = new ArrayList<>();
		String error = "";
		boolean next = false;
		
		int page = 0;
		int maxPage = 2000;
		
		link = link+"?SPM.CONTEXT=internet&ARQ.SPM.OUT=XML_STYLESHEET&ES_FW4=1&SPM.HAYJS=1&ARQ.SPM.TICKET="+ticket+"&SPM.ISPOPUP=0";
	
		do {
			try {
				ITPartPage itPartPage = getInfoPartsByXml(xml);
	
				next = itPartPage.getNext();
				
				for (Map.Entry<Integer, ITPart> entry : itPartPage.getData().entrySet()) {
					Integer position = entry.getKey();
					ITPart part = entry.getValue();
					
					String newLink = link+"&SPM.ACC.DETALLES_PARTE_CONSULTA=DETALLES_PARTE_CONSULTA&position="+position;
					
					String xmlDetail = Toolkit.getBodyGET(httpClient, newLink);
					setInfoPart(xmlDetail, part);
				
					if (!parts.contains(part)) {										
						parts.add(part);
					}
				}

				if(next) {
					xml = Toolkit.getBodyGET(httpClient, link+"&SPM.ACC.PAG_SIG_CONSULTA=PAG_SIG_CONSULTA");
				} 
				
				page++;
		    } catch (SAXException e) {
				e.printStackTrace();
				error = e.getMessage();
				next = false;
			}
		} while(next && page<=maxPage);
		
		if(parts.isEmpty() && !error.isEmpty()) {
			throw new SegSocialException(error);
		} 
			
		return parts;
	}
	
	private static byte[] getPdfProcess(HtmlPage htmlPage, String continueSelector) throws InterruptedException, IOException, SegSocialException {
		
		HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		
		formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML

		HtmlButton continueIn = (HtmlButton) wait4(htmlPage, p ->p.querySelector(continueSelector)).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		htmlPage = continueIn.click();
		
		HtmlAnchor doc = (HtmlAnchor) wait4(htmlPage, p ->p.querySelector("a[href*=\"INFORME\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		Page page = doc.click();
	
		if (page.isHtmlPage()) {
			htmlPage = (HtmlPage) page;
			HtmlUnitToolkit.manageStatusCode(htmlPage);
		} else {
			try {
				return page.getWebResponse().getContentAsStream().readAllBytes();
			} catch (Exception e) {
				throw new InvalidDataException();
			}
		}
		throw new SegSocialException(MESSAGE_ERROR);
	}
	
	private static byte[] getPdfProcess2(HtmlPage htmlPage) throws InterruptedException, IOException, SegSocialException, TransformerException {
		
		HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		
		formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML

		HtmlButton doc = (HtmlButton) wait4(htmlPage, p ->p.getElementByName("SPM.ACC.GENERAR_INFORME_EMISION")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		XmlPage xmlPage = doc.click();
		htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);

		wait4(htmlPage, p -> p.getElementById("prevdocumentoseinformes"));
		
		HtmlAnchor docAnchor = htmlPage.querySelector("#CONTENEDOR_prevdocumentoseinformes > ul > li > a");
		
		Page page = docAnchor.click();
		if (page.isHtmlPage()) {
			htmlPage = (HtmlPage) page;
			HtmlUnitToolkit.manageStatusCode(htmlPage);
		} else {
			try {
				return page.getWebResponse().getContentAsStream().readAllBytes();
			} catch (Exception e) {
				throw new InvalidDataException();
			}
		}
		throw new SegSocialException(MESSAGE_ERROR);
	}
	
	private static HtmlAnchor getOneAnchorPaginate(HtmlPage htmlPage, SistemaRED.PartType partType, Date date)
			throws IOException {
		
		String dateStr = Toolkit.formatDate(date, DATE_FORMAT).orElse(null);
		
		HtmlTable table = (HtmlTable) htmlPage.querySelector("#FORMULARIO_6 table");
		HtmlAnchor next = null;		
		HtmlAnchor firstColumn = null;
		boolean last = false;
		int numberCell = 0;
		
		switch (partType) {
			case ALTA:
				numberCell = 2;
				break;
			case BAJA:
				numberCell = 3;
				break;
			case CONFIRMACION:
				numberCell = 4;
				break;
		}
		
		if (table != null) {
			
			while (!last) {
				next = (HtmlAnchor) getElConstains(htmlPage, "#FORMULARIO_6 a", "Siguiente");
				for (final HtmlTableRow row : table.getRows()) {
					HtmlTableCell fCell = row.getCell(numberCell);
					HtmlTableCell typeCell = row.getCell(6);
					HtmlTableCell anulCell = row.getCell(7);
					if (
							fCell.getVisibleText().contains(dateStr)
							&& typeCell.getVisibleText().toLowerCase().contains(partType.getDescription().substring(0, 4).toLowerCase())
							&& anulCell.getVisibleText().contains("N")
					) {
						firstColumn = row.getCell(0).querySelector("a");
						break;
					}
				}
				
				if (firstColumn == null && next != null) {					
					htmlPage = setUrlParseRemoveXml(htmlPage, next);
				} else {					
					last = true;
				}
			}
		}
		return firstColumn;
	}
	
	private static HtmlAnchor getOneAnchorPaginate2(HtmlPage htmlPage, SistemaRED.PartType partType, Date date)
			throws IOException {
		
		String dateStr = Toolkit.formatDate(date, DATE_FORMAT).orElse(null);
		
		HtmlTable table = (HtmlTable) htmlPage.querySelector("#TABLA_15");
		HtmlAnchor next = null;		
		HtmlAnchor firstColumn = null;
		boolean last = false;
		int numberCell = 0;
		
		switch (partType) {
			case ALTA:
				numberCell = 2;
				break;
			case BAJA:
				numberCell = 3;
				break;
			case CONFIRMACION:
				numberCell = 4;
				break;
		}
		
		if (table != null) {
			
			while (!last) {
				next = (HtmlAnchor) getElConstains(htmlPage, "#FORMULARIO_6 a", "Siguiente");
				for (final HtmlTableRow row : table.getRows()) {
					HtmlTableCell fCell = row.getCell(numberCell);
					HtmlTableCell anulCell = row.getCell(7);
					if (
							fCell.getVisibleText().contains(dateStr)
							&& anulCell.getVisibleText().contains("N")
					) {
						firstColumn = row.getCell(0).querySelector("a");
						break;
					}
				}
				
				if (firstColumn == null && next != null) {					
					htmlPage = setUrlParseRemoveXml(htmlPage, next);
				} else {					
					last = true;
				}
			}
		}
		return firstColumn;
	}
	
	private static WebResponse skipDateFormatError (WebRequest request, WebResponse response) {
		
		if ( request.getUrl().getFile().endsWith("prosa.min.js")) {
			String content ;
			try ( InputStream is  = SistemaREDITPart.class.getResourceAsStream("prosa.min.js") ) {
				content = new String(is.readAllBytes(), StandardCharsets.UTF_8 );
			} catch ( IOException e ) {
				content = response.getContentAsString();				
			}
			//String content = response.getContentAsString();
			//content = content.replaceAll("a\s*=\s*E\\(.*msgErrorFormaFecha.*dd/mm/aaaa\"\\)\\]\\)", "a=!0");
			content = content.replaceAll("\"chrome\"", "\":-o\"");
			return new StringWebResponse(content, request.getUrl());
		}
		
		return response;
		
	}
	
	// HANDLE IT PART ERRORS
		private static void handleItPartErrors(HtmlPage htmlPage) throws InvalidDataException {
			DomNode errors = htmlPage.querySelector("#errores > ul");
			if (errors != null) {
				throw new InvalidDataException(errors.getVisibleText());
			}
		}
		
		
		
	public static void __main(String[] args) throws IOException, SegSocialException, ParseException, FailingHttpStatusCodeException, InterruptedException, TransformerException {
		try ( InputStream is = new FileInputStream("/home/ndiaz/Documentos/pvasesores.p12");
				FileOutputStream os = new FileOutputStream(File.createTempFile("tgss", ".pdf"))) {
			Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/09/2023");
			List<ITPart> itParts = getItsImpl(is, "7624", "PKCS12", "0111", "41017063249", Optional.empty(), Optional.of(startDate), Optional.of(new Date()), Optional.empty());
			for (ITPart itPart : itParts) {
				System.out.println(itPart);  
			}
			System.out.println( "---------------------------");
			List<It> its = orderByIT(itParts);
			for (It it: its) {
				System.out.println(it);
			}
		}
	}
}
