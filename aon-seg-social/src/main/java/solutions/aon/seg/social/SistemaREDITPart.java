package solutions.aon.seg.social;

import static solutions.aon.seg.social.SistemaRED.PartType.BAJA;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.transform.TransformerException;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.ScriptException;
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
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.javascript.JavaScriptErrorListener;
import org.htmlunit.util.WebConnectionWrapper;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.object.ITPart;
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
	public static byte[] sendEconomicData(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, Optional<String> licenseNumber, Optional<String> cias,
			Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, situationEmployee, licenseNumber, cias,
				startdate, contractType, baseCot, cotDays });

		try {
			return sendEconomicDataImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee,startdate, contractType, baseCot,
					cotDays, fATEP, accidentType, licenseNumber, cias, occupation, job, jobDescription);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return null;
	}
	
	// REGISTER IT START
	private static byte[] sendEconomicDataImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType,
			Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		
		
		byte[] certificateData = certificateInputStream.readAllBytes();
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateData, certificatePassword, certificateType);
			WebConnectionWrapper wrapper = HtmlUnitToolkit.transformXmlPage(webClient, certificateData, certificatePassword, certificateType, Collections.emptyMap(), SistemaREDITPart::skipDateFormatError)) {
			
			webClient.getOptions().setCssEnabled(true);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setFetchPolyfillEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptErrorListener( new JavaScriptErrorListener() {
				
				@Override
				public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
					System.out.println("warn: " + message + " sourceName: " + sourceName + " line: " + line + " lineSource: " + lineSource + " lineOffset: " + lineOffset);
				}
				
				@Override
				public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
					System.out.println("timeoutError: " + allowedTime + " executionTime: " + executionTime);
				}
				
				@Override
				public void scriptException(HtmlPage page, ScriptException scriptException) {
					System.out.println("scriptException: " + scriptException.getMessage());
				}
				
				@Override
				public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
					System.out.println("malformedScriptURL: " + url + " malformedURLException: " + malformedURLException.getMessage());
				}
				
				@Override
				public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
					System.out.println("loadScriptError: " + scriptUrl + " exception: " + exception.getMessage());
				}
			});
			
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			htmlPage = HtmlUnitToolkit.secureTransformHtmlPage(htmlPage);
			
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, startdate, contingency, situationEmployee, BAJA);
			htmlPage = HtmlUnitToolkit.secureTransformHtmlPage(htmlPage);
			
			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			if (job.isPresent()) {		
				HtmlInput jobInput = form.getInputByName("puestoTrabajo");
				jobInput.focus();
				jobInput.type(job.get());
				jobInput.blur();
			}
			
			// Data Contract
			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL:
					webClient.waitForBackgroundJavaScript(5000);
					htmlPage = HtmlUnitToolkit.selectOption(htmlPage, "tipoContrato", "1");
					
	
					wait4(htmlPage, p -> p.getElementById("sumaBaseCot"))
					.orElseThrow(() -> new SegSocialException(TRY_AGAIN));
	
					break;
				case RESTO_Y_AUTONOMOS:
					webClient.waitForBackgroundJavaScript(5000);
					htmlPage = HtmlUnitToolkit.selectOption(htmlPage, "tipoContrato", "2");
	
					wait4(htmlPage, p -> p.getElementById("BaseCot"))
					.orElseThrow(() -> new SegSocialException(TRY_AGAIN));
	
					break;	
			}
			

			if(fATEP.isPresent()) {
				try {
					HtmlInput inputATEP = form.getInputByName("fechaATEP");
					inputATEP.setValue(Toolkit.formatDate(fATEP.get(), DATE_FORMAT).get());
				} catch ( ElementNotFoundException e ) {
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
			htmlPage = HtmlUnitToolkit.secureTransformHtmlPage(htmlPage);
			
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML
			
			HtmlButton confim = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Confirmar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = confim.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			htmlPage = HtmlUnitToolkit.secureTransformHtmlPage(htmlPage);
			
			return getPdfProcess(htmlPage, "#ENVIO_8");
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
	
	// report IT
	private static byte[] getITReportImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String nss, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage htmlPage = null;
			
			Page page = webClient.getPage(BASE_URI);
			htmlPage = HtmlUnitToolkit.transformPage(page);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			page = htmlPage.getElementById("PEST_3").click();
			htmlPage = HtmlUnitToolkit.transformPage(page);
		
			wait4(htmlPage, p -> p.querySelector("[name=\"regimenConsulta\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			form.getInputByName(ARQ_SPM_OUT).remove(); 

			form.getInputByName("regimenConsulta").setValue(regime);
			form.getInputByName("cccConsulta").setValue(ccc);
			form.getInputByName("nafConsulta").setValue(nss);
			Toolkit.formatDate(dateBj, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBajaMedConsulta").setValue(d));
			
			HtmlButton continueIn = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[value=\"CONTINUAR_CONSULTA\"]")).orElseThrow();
			page = continueIn.click();
			htmlPage = HtmlUnitToolkit.transformPage(page);
			
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlTable table = (HtmlTable) htmlPage.getElementById("TABLA_12");
			List<HtmlTableBody> tableBodies = table.getBodies();
			HtmlTableBody firstRow = tableBodies.get(0);
			List<HtmlAnchor> anchor = firstRow.getByXPath(".//a");
			page= anchor.get(0).click();
			htmlPage = HtmlUnitToolkit.transformPage(page);
			
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
		htmlPage = accept.click();
		HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

		return htmlPage;
	}
	
	private static byte[] getPdfProcess(HtmlPage htmlPage, String continueSelector) throws InterruptedException, IOException, SegSocialException {
		
		HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_4")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		
		formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML

		HtmlButton continueIn = (HtmlButton) wait4(htmlPage, p ->p.querySelector(continueSelector)).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		htmlPage = continueIn.click();
		htmlPage = HtmlUnitToolkit.secureTransformHtmlPage(htmlPage);
		
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
		Page page = doc.click();
		htmlPage = HtmlUnitToolkit.transformPage(page);

		wait4(htmlPage, p -> p.getElementById("prevdocumentoseinformes"));
		
		HtmlAnchor docAnchor = htmlPage.querySelector("#CONTENEDOR_prevdocumentoseinformes > ul > li > a");
		
		page = docAnchor.click();
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
		
		
		
	public static void main(String[] args) throws IOException, SegSocialException, ParseException, FailingHttpStatusCodeException, InterruptedException, TransformerException {
		try ( InputStream certificateIs = new FileInputStream("/home/rtrepiana/Downloads/carcellemorcillo.aonsolutions.org.p12")){
			Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse("26/09/2024");
			SistemaREDITPart.sendEconomicData(
					certificateIs,
					"000000",
					"PKCS12",
					"0111", 
					"50122070978",
					"500073203489",
					SistemaRED.Contingencies.ACCIDENT_LABORAL,
					SistemaRED.SituationEmployee.ACTIVO,
					startDate,
					SistemaRED.ContractType.RESTO_Y_AUTONOMOS,
					2115.46f,
					31,
					Optional.of(startDate),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.of("NIVEL XII"),
					Optional.of("Las propias de NIVEL XII")
					);
			
		}
	}
}
