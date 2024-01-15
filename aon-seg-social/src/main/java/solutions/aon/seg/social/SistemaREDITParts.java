package solutions.aon.seg.social;

import static java.lang.Integer.parseInt;
import static solutions.aon.seg.social.SistemaRED.PartType.BAJA;
import static solutions.aon.seg.social.exception.InvalidCertificateException.checkCertificate;
import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getElConstains;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.manageStatusCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.setUrlParse;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.ForbiddenException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.It.ItBuilder;
import solutions.aon.seg.social.object.ItPartId;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDITParts {
	
	//Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/test.html");
	
	private static String URL_BASE = "https://w2.seg-social.es/isincaA/inicio.do";
	
	private static String FORMAT_DATE = "dd/MM/yyyy";

	@Deprecated /** use SistemaREDITPart.getIts*/
	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to, Optional<String> nss) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, from, to });
		checkCertificate(certificateInputStream);

		ArrayList<It> its = new ArrayList<>();
		
		ArrayList<ITPart> itParts = (ArrayList<ITPart>) getFullItParts(certificateInputStream, certificatePassword,
				certificateType, regime, ccc, from, to, nss);
		

		HashMap<ItPartId, Collection<ITPart>> orderedItParts = new HashMap<>();

		for (ITPart itp : itParts) {
			Optional<Date> workLeaveDate = itp.getWorkLeaveDate();
			Optional<String> naf = itp.getNaf();
			
			if( workLeaveDate.isPresent() && naf.isPresent() ) {
				
				ItPartId id = new ItPartId(workLeaveDate.get(), naf.get());
				
				if (orderedItParts.containsKey(id)) {					
					orderedItParts.get(id).add(itp);
				} else {
					ArrayList<ITPart> list = new ArrayList<>();
					list.add(itp);
					orderedItParts.put(id, list);
				}
			}
		}
		
		ItBuilder builder = new ItBuilder();
		
		Set<ItPartId> partIds = orderedItParts.keySet();
		for (ItPartId id : partIds) {
	
			itParts = (ArrayList<ITPart>) orderedItParts.get(id);
			ITPart end = null;
			ITPart start = null;
			ArrayList<ITPart> confirmations = new ArrayList<>();

			for (ITPart itp : itParts) {
				String partStr = itp.getPartType().toLowerCase();
				if (partStr.indexOf("baja")>=0) {					
					start = itp;
				} else if (partStr.indexOf("confirmaci\u00F3n")>=0 && !confirmations.contains(itp)) {					
					confirmations.add(itp);
				} else if (partStr.indexOf("alta")>=0) {					
					end = itp;
				}
			}
			
			if(null!=start) {
				
				Optional<String> typeProcess = start.getTypeProcess();
				if(null==end && typeProcess.isPresent() && typeProcess.get().toLowerCase().contains("muy corto")) {
					ITPart tmp = new ITPart();
					tmp.setReceptionDate(start.getReceptionDate());
					tmp.setCauseRestart("6 Mejor\u00EDa permite trabajar");
					tmp.setPartType("Parte de alta");
					start.getNaf().ifPresent(tmp::setNaf);
					start.getWorkLeaveDate().ifPresent(workDate->{
						tmp.setWorkLeaveDate(workDate);
						tmp.setWorkRestartDate(Toolkit.addDays(workDate, 1));
					});
					end = tmp;
				}
				
				its.add(builder.setStart(start).setConfirmations(confirmations).setEnd(end).build());
			}
		}
	
		return its.stream().sorted((o1, o2)-> o1.getStart().getWorkLeaveDate().get().compareTo(o2.getStart().getWorkLeaveDate().get())).collect(Collectors.toList());
	}
	
	// REGISTER IT START HANDLE EXCEPTIONS
	@Deprecated /** use SistemaREDITPart.registerItBaja*/
	public static void registerItBaja(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, Optional<String> licenseNumber, Optional<String> cias,
			Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, situationEmployee, licenseNumber, cias,
				startdate, contractType, baseCot, cotDays });

		try {
			registerItBajaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
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
			throw new SegSocialException(e.getMessage());
		}
	}
	
	// REGISTER IT CONFIRMATION HANDLE EXCEPTIONS
	@Deprecated /** use SistemaREDITPart.registerItConfirmation*/
	public static void registerItConfirmation(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency });
		try {
			registerItConfirmationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
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
	}
	

	// REGISTER IT END HANDLE EXCEPTIONS
	@Deprecated /** use SistemaREDITPart.registerItAlta*/
	public static void registerItAlta(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date fbaja, Date falta, 
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, SistemaRED.CauseType causeType, Optional<String> licenseNumber, Optional<String> cias)
			throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, fbaja, falta });
		try {
			registerItAltaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
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
	}
	
	// remove IT
	@Deprecated /** use SistemaREDITPart.removeIt*/
	public static void removeIt(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj, Date dateProcess)
			throws IOException, InterruptedException, SegSocialException {
		Toolkit.verifyData(new Object[] { regime, ccc, naf, dateBj });
		try {
			removeItImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType,
					dateBj, dateProcess);
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
	}

	
	// HANDLE GETFULLITPARTS EXCEPTIONS
	private static Collection<ITPart> getFullItParts(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc, Date from,
			Date to, Optional<String> nss) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			return getFullItPartsImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, from,
					to, nss);
		} catch (FailingHttpStatusCodeException e) {
			switch (e.getStatusCode()) {
				case 403:
					throw new ForbiddenException();
				default:
					throw new StatusCodeException();
			}
		} catch (IOException | InterruptedException e) {
			throw new SegSocialException(e);
		}

	}
	
	// REGISTER IT START
	private static void registerItBajaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType,
			Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee, BAJA);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BajaPartesForm")).orElseThrow();

			// Data Contract
			HtmlOption contractTypeOption = null;
			HtmlInput cotBaseInput1 = null;
			HtmlInput cotBaseInput2 = null;
			HtmlInput cotDaysInput = null;
	
			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL:
					contractTypeOption = form.querySelector("#tipoContrato option:nth-child(2)");
					cotBaseInput1 = form.getInputByName("sumaBC1");
					cotBaseInput2 = form.getInputByName("sumaBC2");
					cotDaysInput = form.getInputByName("sumaDias");
				break;
				case RESTO_Y_AUTONOMOS:
					contractTypeOption = form.querySelector("#tipoContrato option:nth-child(3)");
					cotBaseInput1 = form.getInputByName("baseCotizacion1");
					cotBaseInput2 = form.getInputByName("baseCotizacion2");
					cotDaysInput = form.getInputByName("diasCot");	
				break;
			}
			
			if(null!=contractTypeOption) {				
				contractTypeOption.click();
			}

			String[] startDateArray = Toolkit.dateString(startdate);
			form.getInputByName("fechaBaja_dd").setValue(startDateArray[0]);
			form.getInputByName("fechaBaja_mm").setValue(startDateArray[1]);
			form.getInputByName("fechaBaja_aa").setValue(startDateArray[2]);
			
			String[] baseCotArray = Toolkit.splitDecimal(baseCot, 2);
			cotBaseInput1.setValue(baseCotArray[0]);
			cotBaseInput2.setValue(baseCotArray[1]);
			cotDaysInput.setValue(cotDays + "");

			if (occupation.isPresent()) 
				 ((HtmlSelect) form.querySelector("#ocupacion")).setSelectedAttribute(occupation.get(), true);

			if (licenseNumber.isPresent()) {
				ArrayList<String> colegiateNumberList = Toolkit.splitStringMultiple(licenseNumber.get(), new int[] { 2, 4 });
				form.getInputByName("ncol_0").setValue(colegiateNumberList.get(0));
				form.getInputByName("ncol_1").setValue(colegiateNumberList.get(1));
				form.getInputByName("ncol_2").setValue(colegiateNumberList.get(2));
			}

			cias.ifPresent(c-> form.getInputByName("cias").setValue(c));

			if (fATEP.isPresent()) {
				String[] fATEPString = Toolkit.dateString(fATEP.get());
				form.getInputByName("fechaATEP_dd").setValue(fATEPString[0]);
				form.getInputByName("fechaATEP_mm").setValue(fATEPString[1]);
				form.getInputByName("fechaATEP_aa").setValue(fATEPString[2]);
			}

			if (accidentType.isPresent()) {
				HtmlOption typeAccidentOption = null;
				switch (accidentType.get()) {
				case LEVE:
					typeAccidentOption = form.querySelector("#tipoAccidente option:nth-child(2)");
					break;
				case GRAVE:
					typeAccidentOption = form.querySelector("#tipoAccidente option:nth-child(3)");
					break;
				case MUY_GRAVE:
					typeAccidentOption = form.querySelector("#tipoAccidente option:nth-child(4)");
					break;
				}
				if(null!=typeAccidentOption) typeAccidentOption.click();
			}

			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);
			
			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);
			
			DomNode elem = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo");
			if(null!=elem) {
				System.out.println(elem.asNormalizedText());
				if (elem.asNormalizedText().contains("no se ha dado")) {					
					throw new InvalidDataException(elem.asNormalizedText());
				}
			}
		}
	}

	// REGISTER IT CONFIRMATION
	private static void registerItConfirmationImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			
			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee, SistemaRED.PartType.CONFIRMACION);
			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("ConfirmacionPartesForm")).orElseThrow();

			String[] fbajaString = Toolkit.dateString(fbaja);
			String[] fconfirmationString = Toolkit.dateString(fconfirmation);

			form.getInputByName("fechaBaja_dd").setValue(fbajaString[0]);
			form.getInputByName("fechaBaja_mm").setValue(fbajaString[1]);
			form.getInputByName("fechaBaja_aa").setValue(fbajaString[2]);

			form.getInputByName("fechaParte_dd").setValue(fconfirmationString[0]);
			form.getInputByName("fechaParte_mm").setValue(fconfirmationString[1]);
			form.getInputByName("fechaParte_aa").setValue(fconfirmationString[2]);
			
			npartConfimation.ifPresent(c-> form.getInputByName("numParte").setValue(c));

			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);

			DomNode elem = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo");
			if(null!=elem) {
				System.out.println(elem.asNormalizedText());
				if (elem.asNormalizedText().contains("no se ha dado")) {					
					throw new InvalidDataException(elem.asNormalizedText());
				}
			}
		}
	}

	// REGISTER IT
	private static void registerItAltaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date fbaja, Date falta, Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, 
			SistemaRED.CauseType causeType, Optional<String> licenseNumber, Optional<String> cias)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee, SistemaRED.PartType.ALTA);
			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("AltaPartesForm")).orElseThrow();


			String[] fbajaString = Toolkit.dateString(fbaja);

			form.getInputByName("fechaBaja_dd").setValue(fbajaString[0]);
			form.getInputByName("fechaBaja_mm").setValue(fbajaString[1]);
			form.getInputByName("fechaBaja_aa").setValue(fbajaString[2]);
			
			String[] faltaString = Toolkit.dateString(falta);
			form.getInputByName("fechaAlta_dd").setValue(faltaString[0]);
			form.getInputByName("fechaAlta_mm").setValue(faltaString[1]);
			form.getInputByName("fechaAlta_aa").setValue(faltaString[2]);

			if (fATEP.isPresent()) {
				String[] fATEPString = Toolkit.dateString(fATEP.get());
				form.getInputByName("fechaAtEp_dd").setValue(fATEPString[0]);
				form.getInputByName("fechaAtEp_mm").setValue(fATEPString[1]);
				form.getInputByName("fechaAtEp_aa").setValue(fATEPString[2]);
			}

			if (accidentType.isPresent()) {
				HtmlOption typeAccidentOption = null;
				switch (accidentType.get()) {
					case LEVE:
						typeAccidentOption = form.querySelector("#tipoAccidente option:nth-child(2)");
						break;
					case GRAVE:
						typeAccidentOption = form.querySelector("#tipoAccidente option:nth-child(3)");
						break;
					case MUY_GRAVE:
						typeAccidentOption = form.querySelector("#tipoAccidente option:nth-child(4)");
						break;
				}
				if(null!=typeAccidentOption) typeAccidentOption.click();
			}

			HtmlSelect cause = form.querySelector("#causaAlta");
			cause.setSelectedAttribute(causeType.getValue(), true);

			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);
			
			DomNode elem = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo");
			if(null!=elem) {
				System.out.println(elem.asNormalizedText());
				if (elem.asNormalizedText().contains("no se ha dado")) {					
					throw new InvalidDataException(elem.asNormalizedText());
				}
			}
		}
	}

	private static HtmlPage fillGeneralData(HtmlPage htmlPage, String regime, String ccc, String naf,
			SistemaRED.Contingencies contingency, SistemaRED.SituationEmployee situationEmployee, SistemaRED.PartType type)
			throws IOException, InvalidDataException {

		HtmlInput regimeIn = htmlPage.querySelector("#regimen");
		HtmlInput cccInput = htmlPage.querySelector("#ccc1");
		HtmlInput cccInput2 = htmlPage.querySelector("#ccc2");
		HtmlInput nafInput = htmlPage.querySelector("#naf1");
		HtmlInput nafInput2 = htmlPage.querySelector("#naf2");
		HtmlOption typeOption = null;
		HtmlOption contingencyOption = null;
		HtmlOption situationOption = null;

		switch (type) {
			case BAJA:
				typeOption = htmlPage.querySelector("#tipoParte option:nth-child(2)");
				break;
			case CONFIRMACION:
				typeOption = htmlPage.querySelector("#tipoParte option:nth-child(3)");
				break;
			case ALTA:
				typeOption = htmlPage.querySelector("#tipoParte option:nth-child(4)");
				break;
		}
		if(null!=typeOption) typeOption.click();

		switch (situationEmployee) {
			case ACTIVO:
				situationOption = htmlPage.querySelector("#situacionTrabajador option:nth-child(2)");
				break;
			case PERCEPTOR_DE_DESEMPLEO:
				situationOption = htmlPage.querySelector("#situacionTrabajador option:nth-child(3)");
				break;
		}
		if(null!=situationOption) situationOption.click();

		switch (contingency) {
			case ENFERMEDAD_COMUN:
				contingencyOption = htmlPage.querySelector("#contingencia option:nth-child(2)");
				break;
			case ACCIDENTE_NO_LABORAL:
				contingencyOption = htmlPage.querySelector("#contingencia option:nth-child(3)");
				break;
			case ACCIDENT_LABORAL:
				contingencyOption = htmlPage.querySelector("#contingencia option:nth-child(4)");
				break;
			case ENFERMEDAD_PROFESIONAL:
				contingencyOption = htmlPage.querySelector("#contingencia option:nth-child(5)");
				break;
			case PERIODOS_OBSERVACION:
				contingencyOption = htmlPage.querySelector("#contingencia option:nth-child(6)");
				break;
		}
		if(null!=contingencyOption) contingencyOption.click();

		String[] cccArray = Toolkit.SplitString(ccc, 2);
		String[] nafArray = Toolkit.SplitString(naf, 2);

		regimeIn.setValue(regime);
		cccInput.setValue(cccArray[0]);
		cccInput2.setValue(cccArray[1]);

		nafInput.setValue(nafArray[0]);
		nafInput2.setValue(nafArray[1]);

		HtmlSubmitInput accept = (HtmlSubmitInput) htmlPage.querySelector("#datos input[type=submit]");
		htmlPage = accept.click();
		handleItPartErrors(htmlPage);

		return htmlPage;
	}
	
	// remove ITImpl
	private static void removeItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess) throws FailingHttpStatusCodeException, IOException,
			InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=A").click();

			String[] cccArray = Toolkit.SplitString(ccc, 2);
			String[] nafArray = Toolkit.SplitString(naf, 2);
			String[] medicalDateArray = Toolkit.dateString(dateBj);
			String[] dateProcessArray = Toolkit.dateString(dateProcess);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValue(regime);
			form.getInputByName("ccc1").setValue(cccArray[0]);
			form.getInputByName("ccc2").setValue(cccArray[1]);
			form.getInputByName("naf1").setValue(nafArray[0]);
			form.getInputByName("naf2").setValue(nafArray[1]);
			form.getInputByName("fechaBaja_dd").setValue(medicalDateArray[0]);
			form.getInputByName("fechaBaja_mm").setValue(medicalDateArray[1]);
			form.getInputByName("fechaBaja_aa").setValue(medicalDateArray[2]);

			HtmlSubmitInput continueInput = form.querySelector("#botonesANULAR input[value=Continuar]");
			htmlPage = continueInput.click();
			handleItPartErrors(htmlPage);

			String dateProcessString = dateProcessArray[0] + "/" + dateProcessArray[1] + "/" + dateProcessArray[2];
			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, dateProcessString);

			if (firstColumn == null) {				
				throw new NoQueryData("Sin datos de consulta");
			}

			htmlPage = setUrlParse(htmlPage, firstColumn).click();

			HtmlSubmitInput anular = htmlPage.querySelector("#botones input[value=Anular]");
			htmlPage = anular.click();

			HtmlSubmitInput confirm = htmlPage.querySelector("#general > form input[value=Confirmar]");
			htmlPage = confirm.click();
			handleItPartErrors(htmlPage);
			
			DomNode elem = htmlPage.querySelector("#miForm > div.importante > div.indent > span.TextoMensaje");
			if(null!=elem) {
				System.out.println(elem.asNormalizedText());
				if (elem.asNormalizedText().contains("no se ha dado")) {					
					throw new InvalidDataException(elem.asNormalizedText());
				}
			}

		}
	}

	// report IT
	@Deprecated /** use SistemaREDITPart.getITReport*/
	public static byte[] pdfIt(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj, Date dateProcess)
			throws IOException, InterruptedException, SegSocialException {
		Toolkit.verifyData(new Object[] { regime, ccc, naf, dateBj });
		try {
			return pdfItImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType,
					dateBj, dateProcess);
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		}
		return null;
	}

	// report IT
	private static byte[] pdfItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=E").click();

			String[] cccArray = Toolkit.SplitString(ccc, 2);
			String[] nafArray = Toolkit.SplitString(naf, 2);

			String[] dateBjArray = Toolkit.dateString(dateBj);
			String[] dateProcessArray = Toolkit.dateString(dateProcess);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValue(regime);
			form.getInputByName("ccc1").setValue(cccArray[0]);
			form.getInputByName("ccc2").setValue(cccArray[1]);
			form.getInputByName("naf1").setValue(nafArray[0]);
			form.getInputByName("naf2").setValue(nafArray[1]);
			form.getInputByName("fechaBaja_dd").setValue(dateBjArray[0]);
			form.getInputByName("fechaBaja_mm").setValue(dateBjArray[1]);
			form.getInputByName("fechaBaja_aa").setValue(dateBjArray[2]);

			HtmlSubmitInput continueInput = form.querySelector("#botonesANULAR input[value=Continuar]");
			htmlPage = continueInput.click();
			handleItPartErrors(htmlPage);

			String dateProcessString = dateProcessArray[0] + "/" + dateProcessArray[1] + "/" + dateProcessArray[2];
			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, dateProcessString);

			if (firstColumn == null) {				
				throw new NoQueryData("Sin datos de consulta");
			}

			htmlPage = setUrlParse(htmlPage, firstColumn).click();
			Page document = setUrlParse(htmlPage, (HtmlAnchor) htmlPage.querySelector("#botones > p > a")).click();

			if (document instanceof HtmlPage) {				
				manageStatusCode((HtmlPage) document);
			} else {
				InputStream inp = document.getWebResponse().getContentAsStream();
				byte[] pdf = inp.readAllBytes();
				inp.close();
				return pdf;
			}

			return null;
		}
	}
	
	// GET ALL THE ITPARTS
	private static Collection<ITPart> getFullItPartsImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to, Optional<String> nss)
			throws SegSocialException, FailingHttpStatusCodeException, IOException, InterruptedException {

		Toolkit.verifyData(new Object[] { regime, ccc, from, to });
		
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);

			if (Toolkit.isFuture(to))
				throw new InvalidDateException();

			ArrayList<ITPart> itParts = new ArrayList<>();
			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			handleItPartErrors(htmlPage);

			Optional<DomNode> element = wait4(htmlPage, p -> p.querySelector("[href='"+URL_BASE+"']"));
			if(element.isPresent()) {
				htmlPage = ((HtmlAnchor)element.get()).click();
			} else {				
				throw new SegSocialException("Sin permisos para ver esta opci\u00f3n en TGSS");
			}

			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=C").click();
			
			handleItPartErrors(htmlPage);
			
			HtmlForm formularioPartes = htmlPage.getFormByName("BuscaPartesForm");
			formularioPartes.getInputByName("regimen").setValue(regime);
			htmlPage.getElementById("ccc1").setAttribute("value", ccc.substring(0, 2));
			formularioPartes.getInputByName("ccc2").setValue(ccc.substring(2));

			Integer[] fromArray = Toolkit.getDateArray(from);
			Integer[] toArray = Toolkit.getDateArray(to);
			
			if(nss.isPresent()) {
				String naf = nss.get();
				formularioPartes.getInputByName("naf1").setValue(naf.substring(0, 2));
				formularioPartes.getInputByName("naf2").setValue(naf.substring(2));
			}

			formularioPartes.getInputByName("fechaDesde_dd").setValue(fromArray[0].toString());
			formularioPartes.getInputByName("fechaDesde_mm").setValue(fromArray[1].toString());
			formularioPartes.getInputByName("fechaDesde_aa").setValue(fromArray[2].toString());

			formularioPartes.getInputByName("fechaHasta_dd").setValue(toArray[0].toString());
			formularioPartes.getInputByName("fechaHasta_mm").setValue(toArray[1].toString());
			formularioPartes.getInputByName("fechaHasta_aa").setValue(toArray[2].toString());

			webClient.getOptions().setJavaScriptEnabled(true);
			HtmlInput show = (HtmlInput) formularioPartes.querySelectorAll("input[type=submit]").get(0);
			htmlPage = show.click();
			handleItPartErrors(htmlPage);
			
			DomNode node = htmlPage.querySelector(".resultados");
			if(node!=null) {
				boolean last = false;
				while (!last) {
					node = htmlPage.querySelector(".resultados");
					if(node instanceof HtmlTable) {
						HtmlTable table = (HtmlTable)node;
						int i = 0;
						for (final HtmlTableRow row : table.getRows()) {
							if(i>0) {
								int cellSize = row.getCells().size();
			
								Boolean cancelled = Toolkit.toBoolean(row.getCell(cellSize-2).getVisibleText());
								Boolean wrong = Toolkit.toBoolean(row.getCell(cellSize-1).getVisibleText());

								if(Boolean.FALSE.equals(cancelled) && Boolean.FALSE.equals(wrong)) {

									HtmlAnchor desc = (HtmlAnchor) row.getCell(0).getFirstElementChild();
									HtmlPage document = HtmlUnitToolkit.setUrlParse(htmlPage, desc).click();
							
									handleItPartErrors(document);
									ITPart part = infoPart(document);
									if (!itParts.contains(part)) {										
										itParts.add(part);
									}
								}
							}
							i++;
						}
					}

					DomNode next = HtmlUnitToolkit.getElConstains(htmlPage, ".derecha a", "Siguiente");
		
					if(next instanceof HtmlAnchor) {
						
						HtmlAnchor anchor = (HtmlAnchor)next;

						String urlBase = "/isincaA/buscaPartes.do?";
						String query = anchor.getHrefAttribute().replace(urlBase, "");

					    String decodedQuery = Arrays.stream(query.split("&"))
			    	    .map(param -> {
			    	    	param = param.trim();
			    	    	if(!param.isEmpty()) {
			    	    		String[] split = param.split("=");
			    	    		return split[0] + "=" + (split.length>1 ? encode(split[1]): "");
			    	    	}
			    	    	return "";
			    	    })
			    	    .collect(Collectors.joining("&"));
					    
						anchor.setAttribute("href", "https://w2.seg-social.es"+urlBase+decodedQuery);
						htmlPage = anchor.click();
					}  else {
						last = true;						
					}
				}
			}
			return itParts;
		}
	}

	public static ITPart getDataIt(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess) throws SegSocialException {
		Toolkit.verifyData(new Object[] { regime, ccc, naf, dateBj });
		try {
			return getDataItImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					partType, dateBj, dateProcess);
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			throw new SegSocialException(e);
		}
		return null;
	}

	private static ITPart getDataItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj,
			Date dateProcess) throws FailingHttpStatusCodeException, IOException,
			InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=C").click();

			String[] cccArray = Toolkit.SplitString(ccc, 2);
			String[] nafArray = Toolkit.SplitString(naf, 2);

			String[] dateBjArray = Toolkit.dateString(dateBj);
			String[] dateProcessArray = Toolkit.dateString(dateProcess);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValue(regime);
			form.getInputByName("ccc1").setValue(cccArray[0]);
			form.getInputByName("ccc2").setValue(cccArray[1]);
			form.getInputByName("naf1").setValue(nafArray[0]);
			form.getInputByName("naf2").setValue(nafArray[1]);
			form.getInputByName("fechaBaja_dd").setValue(dateBjArray[0]);
			form.getInputByName("fechaBaja_mm").setValue(dateBjArray[1]);
			form.getInputByName("fechaBaja_aa").setValue(dateBjArray[2]);

			HtmlSubmitInput continueInput = form.querySelector("#botonesANULAR input[value=Continuar]");
			htmlPage = continueInput.click();
			handleItPartErrors(htmlPage);

			String dateProcessString = dateProcessArray[0] + "/" + dateProcessArray[1] + "/" + dateProcessArray[2];
			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, dateProcessString);

			if (firstColumn == null) {				
				throw new NoQueryData("Sin datos de consulta");
			}

			htmlPage = setUrlParse(htmlPage, firstColumn).click();
			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("form[name=InfoParteForm] input[value=\"Datos Procesados\"]")).click();

			handleItPartErrors(htmlPage);
			
			return infoPart(htmlPage);
		}
	}

	private static HtmlAnchor getOneAnchorPaginate(HtmlPage htmlPage, SistemaRED.PartType partType, String fecha)
			throws IOException {
		
		HtmlTable table = (HtmlTable) htmlPage.querySelector("#datos2 > fieldset > table");
		HtmlAnchor next = null;		
		HtmlAnchor firstColumn = null;
		String partTypeStr = null;
		boolean last = false;
		int numberCell = 0;
		String anulado = "No";
		
		switch (partType) {
			case BAJA:
				numberCell = 2;
				partTypeStr = "Baja";
				break;
			case ALTA:
				numberCell = 3;
				partTypeStr = "Alta";
				break;
			case CONFIRMACION:
				numberCell = 4;
				partTypeStr = "Confirmaci\u00F3n";
				break;
		}
		if (table != null) {
			while (!last) {
				next = (HtmlAnchor) getElConstains(htmlPage, "#datos2 > fieldset > div > a", "Siguiente");
				for (final HtmlTableRow row : table.getRows()) {
					HtmlTableCell fCell = row.getCell(numberCell);
					HtmlTableCell typeCell = row.getCell(6);
					HtmlTableCell anulCell = row.getCell(7);
					if (fCell.getVisibleText().equalsIgnoreCase(fecha)
							&& typeCell.getVisibleText().equalsIgnoreCase(partTypeStr)
							&& anulCell.getVisibleText().equalsIgnoreCase(anulado)) {
						firstColumn = row.getCell(0).querySelector("a");
						break;
					}
				}
				
				if (firstColumn == null && next != null) {					
					htmlPage = setUrlParse(htmlPage, next).click();
				} else {					
					last = true;
				}
			}
		}
		return firstColumn;
	}

	private static ITPart infoPart(HtmlPage htmlPage) {
		HtmlForm form = htmlPage.querySelector("form[name=InfoParteForm]");
		DomNodeList<DomNode> dtConsulta = form.querySelectorAll("#datos fieldset dt[title]");
		DomNodeList<DomNode> dtPersonal = form.querySelectorAll("#datos2 fieldset dt[title]");
		DomNodeList<DomNode> dtEmpresa = form.querySelectorAll("#datos3 fieldset dt[title]");
		DomNodeList<DomNode> dtMedicos = form.querySelectorAll("#datos4 fieldset dt[title]");
		DomNodeList<DomNode> dtEconomicos = form.querySelectorAll("#datos5 fieldset dt[title]");
		ITPart itPart = new ITPart();
		// DATOS DE CONSULTA
		dtConsulta.forEach(dt -> {
			String dtStr = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String ddStr = Toolkit.removeNBSP(Toolkit.getNextSibling(dt).getVisibleText().trim());
			if (ddStr.length() > 0) {
				if (dtStr.indexOf("N.A.F.:") >= 0) {
					itPart.setNaf(ddStr);
				} else if (dtStr.indexOf("C.C.C.:") >= 0) {
					if(ddStr!=null)  {
						ddStr = ddStr.substring(4, ddStr.length());
					}
					itPart.setCcc(ddStr);
				} else if (dtStr.indexOf("Fecha de baja:") >= 0) {
					itPart.setWorkLeaveDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				} else if (dtStr.indexOf("Tipo de parte:") >= 0) {
					itPart.setPartType(ddStr);
				} else if (dtStr.indexOf("Tipo de proceso:") >= 0) {
					itPart.setTypeProcess(ddStr);
				} else if (dtStr.indexOf("N\u00FCmero tarjeta sanitaria:") >= 0) {
					itPart.setNumberHealth(Integer.parseInt(ddStr));
				} else if (dtStr.indexOf("Entidad emisora:") >= 0) {
					itPart.setEntity(ddStr);
				} else if (dtStr.indexOf("Situaci\u00F3n del trabajador:") >= 0) {
					itPart.setSituation(ddStr);
				} else if (dtStr.indexOf("Fecha de recepci\u00F3n:") >= 0) {
					itPart.setReceptionDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				}
			}
		});
		// DATOS PERSONALES
		dtPersonal.forEach(dt -> {
			String dtStr = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String ddStr = Toolkit.removeNBSP(Toolkit.getNextSibling(dt).getVisibleText().trim());
			if (ddStr.length() > 0) {
				if (dtStr.indexOf("Nombre:") >= 0) {
					itPart.setNameEmployee(ddStr);
				} else if (dtStr.indexOf("IPF:") >= 0) {
					itPart.setIpf(ddStr.replace("D.N.I.", "").trim());
				} else if (dtStr.indexOf("Direcci\u00F3n:") >= 0) {
					itPart.setDirectionEmployee(ddStr);
				} else if (dtStr.indexOf("Ocupaci\u00F3n:") >= 0) {
					itPart.setOccupation(ddStr);
				}
			}
		});
		// DATOS DE EMPRESA
		dtEmpresa.forEach(dt -> {
			String dtStr = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String ddStr = Toolkit.removeNBSP(Toolkit.getNextSibling(dt).getVisibleText().trim());
			if ( ddStr.length() > 0) {
				if (dtStr.indexOf("Nombre:") >= 0) {
					itPart.setNameEnterprise(ddStr);
				} else if (dtStr.indexOf("Direcci\u00F3n:") >= 0) {
					itPart.setDirectionEnterprise(ddStr);
				}
			}
		});
		// DATOS MEDICOS
		dtMedicos.forEach(dt -> {
			String dtStr = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String ddStr = Toolkit.removeNBSP(Toolkit.getNextSibling(dt).getVisibleText().trim());
			if (ddStr.length() > 0) {
				if (dtStr.indexOf("N� colegiado:") >= 0) {
					itPart.setCollegiateNumber(Toolkit.noSpaces(ddStr));
				} else if (dtStr.indexOf("C.I.A.S.:") >= 0) {
					itPart.setCias(ddStr);
				} else if (dtStr.indexOf("Contingencia:") >= 0) {
					itPart.setContingency(ddStr);
				} else if (dtStr.indexOf("Fecha de alta:") >= 0 && !ddStr.contains("00/00/0000")) {
					itPart.setWorkRestartDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				} else if (dtStr.indexOf("Causa de alta:") >= 0) {
					itPart.setCauseRestart(ddStr);
				} else if (dtStr.indexOf("Fecha confirmaci\u00F3n:") >= 0 && !ddStr.contains("00/00/0000")) {
					itPart.setConfirmationDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				} else if (dtStr.indexOf("Reca\u00EDda:") >= 0) {
					itPart.setRelapse(ddStr.equalsIgnoreCase("S\u00ED"));
				} else if (dtStr.indexOf("N� parte:") >= 0) {
					itPart.setPartNum(Integer.parseInt(ddStr));
				} else if (dtStr.indexOf("Duraci\u00F3n probable en d\u00EDas:") >= 0) {
					itPart.setDurationDays(parseInt(ddStr));
				} else if (dtStr.indexOf("Fecha accidente trabajo/Enfermedad Profesional:") >= 0) {
					itPart.setAccDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				} else if (dtStr.indexOf("Fecha de baja del proceso anterior:") >= 0 && !ddStr.contains("00/00/0000")) {
					itPart.setBjPrevDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				} else if (dtStr.indexOf("Fecha de baja del proceso inicial:") >= 0 && !ddStr.contains("00/00/0000")) {
					itPart.setBjInitDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				} else if (dtStr.indexOf("Tipo de accidente:") >= 0) {
					itPart.setTypeAcc(ddStr);
				} else if (dtStr.indexOf("Tipo de asistencia:") >= 0) {
					itPart.setTypeAssist(ddStr);
				} else if (dtStr.indexOf("Fecha siguiente revisi\u00F3n m\u00E9dica:") >= 0 && !ddStr.contains("00/00/0000")) {
					itPart.setNextMedicalDate(Toolkit.parseDate(ddStr, FORMAT_DATE));
				}
			}
		});
		// DATOS ECONOMICOS
		dtEconomicos.forEach(dt -> {
			String dtStr = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String ddStr = Toolkit.removeNBSP(Toolkit.getNextSibling(dt).getVisibleText().trim());
			if (ddStr.length() > 0) {
				if (dtStr.indexOf("Base de cot.:") >= 0) {
					itPart.setBaseCtz(Toolkit.parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("D\u00EDas cotizados:") >= 0) {
					itPart.setDaysCtz(parseInt(ddStr));
				} else if (dtStr.indexOf("Cotiz. horas extraord.:") >= 0) {
					itPart.setHoursCtzExtr(Toolkit.parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("Suma Base cot.:") >= 0) {
					itPart.setSumBCtz(Toolkit.parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("Suma d\u00EDas cot.:") >= 0) {
					itPart.setDaysSumCtz(parseInt(ddStr));
				} else if (dtStr.indexOf("Cot. horas otros conc.:") >= 0) {
					itPart.setHoursCrzOther(Toolkit.parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("Grupo de cot.:") >= 0) {
					itPart.setGpCtz(ddStr);
				} else if (dtStr.indexOf("Cat. profesional:") >= 0) {
					itPart.setCatProf(ddStr);
				} else if (dtStr.indexOf("Tipo de contrato:") >= 0) {
					itPart.setTypeCto(ddStr);
				} else if (dtStr.indexOf("Carencia:") >= 0) {
					itPart.setLack(parseInt(ddStr));
				}
			}
		});
		return itPart;
	}
	
	private static String encode(String url) {
	       try {
	    	   return URLEncoder.encode(url, "UTF-8");
	       } catch (Exception e) {
	          return "Issue while encoding" + e.getMessage();
	       }
	 }
	 
	// HANDLE IT PART ERRORS
	private static void handleItPartErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#errores > ul");
		if (errors != null) {			
			throw new InvalidDataException(errors.getVisibleText());
		}
	}
}
