package solutions.aon.seg.social;

import static java.lang.Integer.parseInt;
import static solutions.aon.seg.social.SistemaRED_ITParts.PartType.BAJA;
import static solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException.checkCertificate;
import static solutions.aon.seg.social.exceptions.statusCode.StatusCodeException.HandleStatusCodeException;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getElConstains;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.manageStatusCode;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.setUrlParse;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;
import static solutions.aon.seg.social.toolkit.Toolkit.SplitString;
import static solutions.aon.seg.social.toolkit.Toolkit.buildFile;
import static solutions.aon.seg.social.toolkit.Toolkit.dateString;
import static solutions.aon.seg.social.toolkit.Toolkit.getDateArray;
import static solutions.aon.seg.social.toolkit.Toolkit.getNextSibling;
import static solutions.aon.seg.social.toolkit.Toolkit.isFuture;
import static solutions.aon.seg.social.toolkit.Toolkit.noSpaces;
import static solutions.aon.seg.social.toolkit.Toolkit.parseDate;
import static solutions.aon.seg.social.toolkit.Toolkit.parseStringToFloat;
import static solutions.aon.seg.social.toolkit.Toolkit.removeNBSP;
import static solutions.aon.seg.social.toolkit.Toolkit.splitDecimal;
import static solutions.aon.seg.social.toolkit.Toolkit.splitStringMultiple;
import static solutions.aon.seg.social.toolkit.Toolkit.verifyData;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.invalidData.NoQueryData;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.ITPart;
import solutions.aon.seg.social.objects.ITPart.ITPartBuilder;
import solutions.aon.seg.social.objects.It;
import solutions.aon.seg.social.objects.It.ItBuilder;
import solutions.aon.seg.social.objects.ItPartId;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaRED_ITParts {

	private static String URL_BASE = "https://w2.seg-social.es/isincaA/inicio.do";

	// GET ITs
	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException {

		verifyData(new Object[] { regime, ccc, from, to });
		checkCertificate(certificateInputStream);

		ArrayList<It> its = new ArrayList<>();
		ArrayList<ITPart> itParts = (ArrayList<ITPart>) getFullItParts(certificateInputStream, certificatePassword,
				certificateType, regime, ccc, from, to);
		HashMap<ItPartId, Collection<ITPart>> orderedItParts = new HashMap<>();

		for (ITPart itp : itParts) {
			ItPartId id = new ItPartId(itp.getWorkLeaveDate(), itp.getNaf());
			if (orderedItParts.containsKey(id))
				orderedItParts.get(id).add(itp);
			else {
				ArrayList<ITPart> list = new ArrayList<>();
				list.add(itp);
				orderedItParts.put(id, list);
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
				if (itp.getPartType().toLowerCase().equals("alta"))
					end = itp;
				if (itp.getPartType().toLowerCase().equals("baja"))
					start = itp;
				if (itp.getPartType().toLowerCase().equals("confirmaci\u00F3n") && !confirmations.contains(itp))
					confirmations.add(itp);
			}

			its.add(builder.setStart(start).setConfirmations(confirmations).setEnd(end).build());
		}

		return its;
	}

	// HANDLE GETFULLITPARTS EXCEPTIONS
	public static Collection<ITPart> getFullItParts(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc, Date from,
			Date to) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {
			return getFullItPartsImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, from,
					to);
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

	// GET ALL THE ITPARTS
	private static Collection<ITPart> getFullItPartsImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, InvalidCertificateException,
			InvalidDataException {

		verifyData(new Object[] { regime, ccc, from, to });
		checkCertificate(certificateInputStream);

		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);

			if (isFuture(to))
				throw new InvalidDateException();

			ArrayList<ITPart> itParts = new ArrayList<>();
			boolean last = false;

			while (!last) {
				HtmlPage origen = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
				HtmlPage document = wait4(origen, p -> p.getAnchorByHref(URL_BASE)).orElseThrow().click();
				document = document.getAnchorByHref("/isincaA/menu.do?opcion=C").click();

				HtmlForm formularioPartes = document.getFormByName("BuscaPartesForm");
				formularioPartes.getInputByName("regimen").setValueAttribute(regime);
				document.getElementById("ccc1").setAttribute("value", ccc.substring(0, 2));
				formularioPartes.getInputByName("ccc2").setValueAttribute(ccc.substring(2));

				Integer[] fromArray = getDateArray(from);
				Integer[] toArray = getDateArray(to);

				formularioPartes.getInputByName("fechaDesde_dd").setValueAttribute(fromArray[0].toString());
				formularioPartes.getInputByName("fechaDesde_mm").setValueAttribute(fromArray[1].toString());
				formularioPartes.getInputByName("fechaDesde_aa").setValueAttribute(fromArray[2].toString());

				formularioPartes.getInputByName("fechaHasta_dd").setValueAttribute(toArray[0].toString());
				formularioPartes.getInputByName("fechaHasta_mm").setValueAttribute(toArray[1].toString());
				formularioPartes.getInputByName("fechaHasta_aa").setValueAttribute(toArray[2].toString());

				HtmlInput show = (HtmlInput) formularioPartes.querySelectorAll("input[type=submit]").get(0);
				document = show.click();
				handleItPartErrors(document);

				ITPartBuilder builder = new ITPartBuilder();
				String format = "dd/MM/yyyy";

				DomNodeList<DomNode> rows = document.querySelectorAll(".resultados>tbody>tr");
				for (DomNode row : rows) {
					ArrayList<String> data = new ArrayList<>();
					Iterable<DomNode> cells = row.getChildren();

					for (DomNode cell : cells)
						data.add(cell.getVisibleText().trim());

					String recDateStr = data.get(3);
					String naf = data.get(5);
					String workLeaveStr = data.get(7);
					String workRestartStr = data.get(9);
					String partDate = data.get(11);
					Integer partNum = null;

					try {
						partNum = parseInt(data.get(13).trim());
					} catch (NumberFormatException ignored) {
					}
					String partType = data.get(15);
					Boolean cancelled = Toolkit.toBoolean(data.get(17));
					Boolean wrong = Toolkit.toBoolean(data.get(19));

					ITPart part = builder.setReceptionDate(Toolkit.parseDate(recDateStr, format)).setNaf(naf)
							.setWorkLeaveDate(Toolkit.parseDate(workLeaveStr, format))
							.setWorkRestartDate(Toolkit.parseDate(workRestartStr, format))
							.setPartDate(Toolkit.parseDate(partDate, format)).setPartNum(partNum).setPartType(partType)
							.setCanceled(cancelled).setWrong(wrong).build();

					if (itParts.contains(part))
						last = true;
					else
						itParts.add(part);
				}
				to = itParts.get(itParts.size() - 1).getReceptionDate();
			}
			return itParts;

		}
	}

	// HANDLE IT PART ERRORS
	private static void handleItPartErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#errores > ul");
		if (errors != null)
			throw new InvalidDataException(errors.getVisibleText());
	}

	// CONTINGENCIES
	public enum Contingencies {
		ENFERMEDAD_COMUN, ACCIDENTE_NO_LABORAL, ACCIDENT_LABORAL, ENFERMEDAD_PROFESIONAL, PERIODOS_OBSERVACION
	}

	public enum AccidentType {
		LEVE, GRAVE, MUY_GRAVE
	}

	// PART TYPE
	public enum PartType {
		ALTA, CONFIRMACION, BAJA
	}

	// CONTRACTS
	public enum ContractType {
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL, RESTO_Y_AUTONOMOS
	}

	public enum SituationEmployee {
		ACTIVO, PERCEPTOR_DE_DESEMPLEO
	}

	public enum CauseType {
		CURACION("01"), FALLECIMIENTO("02"), INSPECCION_MEDICA("03"), PROPUESTA_INVALIDEZ("04"),
		AGOTAMIENTO_PLAZO("05"), MEJORIA_PERMITE_TRABAJAR("06"), INCOMPARECENCIA("07"), CONTROL_INSS_12_MESES("10"),
		RECUP_CAPACIDAD_PROF("17"), INCOMP_CTOS_FORM("18"), INICIO_DE_MATERNIDAD("20"),
		ALTA_MEDICA_INSPECCION_INSS("53"), PROPUESTA_DE_IP_EN_INSS("55"), FALLECIMIENTO_COMUNICADO_DESDE_EL_INSS("56"),
		ALTA_MATEPSS_ARTICULO_128("57");

		private String value;

		private CauseType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	// REGISTER IT START HANDLE EXCEPTIONS
	public static void registerItBaja(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, Contingencies contingency,
			SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias,
			Optional<String> occupation, Date startdate, ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<AccidentType> accidentType) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, situationEmployee, licenseNumber, cias,
				startdate, contractType, baseCot, cotDays });

		try {
			registerItBajaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee, licenseNumber, cias, occupation, startdate, contractType, baseCot,
					cotDays, fATEP, accidentType);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			throw new SegSocialException(e);
		}
	}

	// REGISTER IT START
	private static void registerItBajaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, Contingencies contingency,
			SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias,
			Optional<String> occupation, Date startdate, ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<AccidentType> accidentType) throws InvalidCertificateException,
			FailingHttpStatusCodeException, IOException, InvalidDataException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee, BAJA);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BajaPartesForm")).orElseThrow();

			// Data Contract
			HtmlOption contractTypeOption = null;
			HtmlInput cotBaseInput1 = null;
			HtmlInput cotBaseInput2 = null;
			HtmlInput cotDaysInput = null;

			String[] startDateArray = dateString(startdate);
			String[] baseCotArray = splitDecimal(baseCot, 2);

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
			contractTypeOption.click();

			form.getInputByName("fechaBaja_dd").setValueAttribute(startDateArray[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(startDateArray[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(startDateArray[2]);

			cotBaseInput1.setValueAttribute(baseCotArray[0]);
			cotBaseInput2.setValueAttribute(baseCotArray[1]);
			cotDaysInput.setValueAttribute(cotDays + "");

			if (occupation.isPresent()) {
				HtmlSelect ocup = form.querySelector("#ocupacion");
				ocup.setSelectedAttribute(occupation.get(), true);
			}

			if (licenseNumber.isPresent()) {
				ArrayList<String> colegiateNumberList = splitStringMultiple(licenseNumber.get(), new int[] { 2, 4 });
				form.getInputByName("ncol_0").setValueAttribute(colegiateNumberList.get(0));
				form.getInputByName("ncol_1").setValueAttribute(colegiateNumberList.get(1));
				form.getInputByName("ncol_2").setValueAttribute(colegiateNumberList.get(2));
				form.getInputByName("cias").setValueAttribute(cias.get());
			}

			if (fATEP.isPresent()) {
				String[] fATEPString = dateString(fATEP.get());
				form.getInputByName("fechaATEP_dd").setValueAttribute(fATEPString[0]);
				form.getInputByName("fechaATEP_mm").setValueAttribute(fATEPString[1]);
				form.getInputByName("fechaATEP_aa").setValueAttribute(fATEPString[2]);
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
				typeAccidentOption.click();
			}

			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);

			String message = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo").asText();
		}
	}

	// REGISTER IT CONFIRMATION HANDLE EXCEPTIONS
	public static void registerItConfirmation(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, Contingencies contingency,
			SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws SegSocialException {

		verifyData(new Object[] { regime, ccc, naf, contingency });
		try {
			registerItConfirmationImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee, licenseNumber, cias, fbaja, fconfirmation, npartConfimation);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			throw new SegSocialException(e);
		}
	}

	// REGISTER IT CONFIRMATION
	private static void registerItConfirmationImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, Contingencies contingency,
			SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws InvalidCertificateException,
			FailingHttpStatusCodeException, IOException, InvalidDataException, InterruptedException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee,
					PartType.CONFIRMACION);
			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("ConfirmacionPartesForm")).orElseThrow();

			String[] fbajaString = dateString(fbaja);
			String[] fconfirmationString = dateString(fconfirmation);

			form.getInputByName("fechaBaja_dd").setValueAttribute(fbajaString[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(fbajaString[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(fbajaString[2]);

			form.getInputByName("fechaParte_dd").setValueAttribute(fconfirmationString[0]);
			form.getInputByName("fechaParte_mm").setValueAttribute(fconfirmationString[1]);
			form.getInputByName("fechaParte_aa").setValueAttribute(fconfirmationString[2]);

			if (npartConfimation.isPresent()) {
				form.getInputByName("numParte").setValueAttribute(npartConfimation.get());
			}

			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);

			String message = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo").asText();
			buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(), "testIt.html");
		}
	}

	// REGISTER IT END HANDLE EXCEPTIONS
	public static void registerItAlta(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, Contingencies contingency,
			SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date falta, Optional<Date> fATEP, Optional<AccidentType> accidentType, CauseType causeType)
			throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency });
		try {
			registerItAltaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee, licenseNumber, cias, fbaja, falta, fATEP, accidentType, causeType);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SegSocialException(e);
		} catch (Exception e) {
			throw new SegSocialException(e);
		}
	}

	// REGISTER IT
	private static void registerItAltaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, Contingencies contingency,
			SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date falta, Optional<Date> fATEP, Optional<AccidentType> accidentType, CauseType causeType)
			throws InvalidCertificateException, FailingHttpStatusCodeException, IOException, InvalidDataException,
			InterruptedException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee, PartType.ALTA);
			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("AltaPartesForm")).orElseThrow();

			String[] faltaString = dateString(falta);
			String[] fbajaString = dateString(fbaja);

			form.getInputByName("fechaBaja_dd").setValueAttribute(faltaString[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(faltaString[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(faltaString[2]);

			form.getInputByName("fechaAlta_dd").setValueAttribute(fbajaString[0]);
			form.getInputByName("fechaAlta_mm").setValueAttribute(fbajaString[1]);
			form.getInputByName("fechaAlta_aa").setValueAttribute(fbajaString[2]);

			if (fATEP.isPresent()) {
				String[] fATEPString = dateString(fATEP.get());
				form.getInputByName("fechaAtEp_dd").setValueAttribute(fATEPString[0]);
				form.getInputByName("fechaAtEp_mm").setValueAttribute(fATEPString[1]);
				form.getInputByName("fechaAtEp_aa").setValueAttribute(fATEPString[2]);
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
				typeAccidentOption.click();
			}

			HtmlSelect cause = form.querySelector("#causaAlta");
			cause.setSelectedAttribute(causeType.getValue(), true);

			HtmlSubmitInput validate = form.querySelector("input[value=Validar]");
			htmlPage = validate.click();
			handleItPartErrors(htmlPage);

			HtmlSubmitInput confim = htmlPage.querySelector("#botones input[value=Confirmar]");
			htmlPage = confim.click();
			handleItPartErrors(htmlPage);

			String message = htmlPage.querySelector("#datos > fieldset > p > span.TextoFijo").asText();
		}
	}

	private static HtmlPage fillGeneralData(HtmlPage htmlPage, String regime, String ccc, String naf,
			Contingencies contingency, SituationEmployee situationEmployee, PartType type)
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
		typeOption.click();

		switch (situationEmployee) {
		case ACTIVO:
			situationOption = htmlPage.querySelector("#situacionTrabajador option:nth-child(2)");
			break;
		case PERCEPTOR_DE_DESEMPLEO:
			situationOption = htmlPage.querySelector("#situacionTrabajador option:nth-child(3)");
			break;
		}
		situationOption.click();

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
		contingencyOption.click();

		String[] cccArray = SplitString(ccc, 2);
		String[] nafArray = SplitString(naf, 2);

		regimeIn.setValueAttribute(regime);
		cccInput.setValueAttribute(cccArray[0]);
		cccInput2.setValueAttribute(cccArray[1]);

		nafInput.setValueAttribute(nafArray[0]);
		nafInput2.setValueAttribute(nafArray[1]);

		HtmlSubmitInput accept = (HtmlSubmitInput) htmlPage.querySelector("#datos input[type=submit]");
		htmlPage = accept.click();
		handleItPartErrors(htmlPage);

		return htmlPage;
	}

	// remove IT
	public static void removeIt(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String regime, String ccc, String naf, PartType partType, Date dateBj, Date dateProcess)
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
			throw new SegSocialException(e);
		}
	}

	// remove IT
	private static void removeItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, PartType partType, Date dateBj,
			Date dateProcess) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException,
			InterruptedException, InvalidDataException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);

			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=A").click();

			String[] cccArray = SplitString(ccc, 2);
			String[] nafArray = SplitString(naf, 2);
			String[] medicalDateArray = dateString(dateBj);
			String[] dateProcessArray = dateString(dateProcess);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValueAttribute(regime);
			form.getInputByName("ccc1").setValueAttribute(cccArray[0]);
			form.getInputByName("ccc2").setValueAttribute(cccArray[1]);
			form.getInputByName("naf1").setValueAttribute(nafArray[0]);
			form.getInputByName("naf2").setValueAttribute(nafArray[1]);
			form.getInputByName("fechaBaja_dd").setValueAttribute(medicalDateArray[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(medicalDateArray[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(medicalDateArray[2]);

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

			String message = htmlPage.querySelector("#miForm > div.importante > div.indent > span.TextoMensaje")
					.asText();
		}
	}

	// report IT
	public static byte[] pdfIt(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String regime, String ccc, String naf, PartType partType, Date dateBj, Date dateProcess)
			throws IOException, InterruptedException, SegSocialException {
		verifyData(new Object[] { regime, ccc, naf, dateBj });
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
			String certificateType, String regime, String ccc, String naf, PartType partType, Date dateBj,
			Date dateProcess)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=E").click();

			String[] cccArray = SplitString(ccc, 2);
			String[] nafArray = SplitString(naf, 2);

			String[] dateBjArray = dateString(dateBj);
			String[] dateProcessArray = dateString(dateProcess);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValueAttribute(regime);
			form.getInputByName("ccc1").setValueAttribute(cccArray[0]);
			form.getInputByName("ccc2").setValueAttribute(cccArray[1]);
			form.getInputByName("naf1").setValueAttribute(nafArray[0]);
			form.getInputByName("naf2").setValueAttribute(nafArray[1]);
			form.getInputByName("fechaBaja_dd").setValueAttribute(dateBjArray[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(dateBjArray[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(dateBjArray[2]);

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

			if (document instanceof HtmlPage)
				manageStatusCode((HtmlPage) document);
			else {
				InputStream inp = document.getWebResponse().getContentAsStream();
				byte[] pdf = inp.readAllBytes();
				inp.close();
				return pdf;
			}

			return null;
		}
	}

	// report IT
	public static ITPart getDataIt(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, PartType partType, Date dateBj,
			Date dateProcess) throws SegSocialException {
		verifyData(new Object[] { regime, ccc, naf, dateBj });
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

	// report IT
	private static ITPart getDataItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, PartType partType, Date dateBj,
			Date dateProcess) throws InvalidCertificateException, FailingHttpStatusCodeException, IOException,
			InterruptedException, InvalidDataException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(URL_BASE);
			htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=C").click();

			String[] cccArray = SplitString(ccc, 2);
			String[] nafArray = SplitString(naf, 2);

			String[] dateBjArray = dateString(dateBj);
			String[] dateProcessArray = dateString(dateProcess);

			HtmlForm form = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			form.getInputByName("regimen").setValueAttribute(regime);
			form.getInputByName("ccc1").setValueAttribute(cccArray[0]);
			form.getInputByName("ccc2").setValueAttribute(cccArray[1]);
			form.getInputByName("naf1").setValueAttribute(nafArray[0]);
			form.getInputByName("naf2").setValueAttribute(nafArray[1]);
			form.getInputByName("fechaBaja_dd").setValueAttribute(dateBjArray[0]);
			form.getInputByName("fechaBaja_mm").setValueAttribute(dateBjArray[1]);
			form.getInputByName("fechaBaja_aa").setValueAttribute(dateBjArray[2]);

			HtmlSubmitInput continueInput = form.querySelector("#botonesANULAR input[value=Continuar]");
			htmlPage = continueInput.click();
			handleItPartErrors(htmlPage);

			String dateProcessString = dateProcessArray[0] + "/" + dateProcessArray[1] + "/" + dateProcessArray[2];
			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, dateProcessString);

			if (firstColumn == null) {
				throw new NoQueryData("Sin datos de consulta");
			}

			htmlPage = setUrlParse(htmlPage, firstColumn).click();
			htmlPage = ((HtmlSubmitInput) htmlPage
					.querySelector("form[name=InfoParteForm] input[value=\"Datos Procesados\"]")).click();

			handleItPartErrors(htmlPage);
			ITPart part = infoPart(htmlPage);
			return part;
		}
	}

	private static HtmlAnchor getOneAnchorPaginate(HtmlPage htmlPage, PartType partType, String fecha)
			throws IOException {
		String anulado = "No";
		HtmlAnchor next = null;
		boolean last = false;
		HtmlAnchor firstColumn = null;
		HtmlTable table = (HtmlTable) htmlPage.querySelector("#datos2 > fieldset > table");
		int numberCell = 0;
		String partTypeStr = null;
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

		ITPartBuilder builder = new ITPartBuilder();
		// DATOS DE CONSULTA
		dtConsulta.forEach(dt -> {
			String dtStr = removeNBSP(dt.getVisibleText()).trim();
			String ddStr = removeNBSP(getNextSibling(dt).getVisibleText().trim());
			Integer ddInt = ddStr.length();
			if (ddInt > 0) {
				if (dtStr.indexOf("N.A.F.:") >= 0) {
					builder.setNaf(ddStr);
				} else if (dtStr.indexOf("C.C.C.:") >= 0) {
					builder.setCcc(ddStr);
				} else if (dtStr.indexOf("Fecha de baja:") >= 0) {
					builder.setWorkLeaveDate(Toolkit.parseDate(ddStr, "dd/MM/yyyy"));
				} else if (dtStr.indexOf("Tipo de parte:") >= 0) {
					builder.setPartType(ddStr);
				} else if (dtStr.indexOf("Tipo de proceso:") >= 0) {
					builder.setTypeProcess(ddStr);
				} else if (dtStr.indexOf("N\u00FCmero tarjeta sanitaria:") >= 0) {
					builder.setNumberHealth(Integer.parseInt(ddStr));
				} else if (dtStr.indexOf("Entidad emisora:") >= 0) {
					builder.setEntity(ddStr);
				} else if (dtStr.indexOf("Situaci\u00F3n del trabajador:") >= 0) {
					builder.setSituation(ddStr);
				} else if (dtStr.indexOf("Fecha de recepci\u00F3n:") >= 0) {
					builder.setReceptionDate(parseDate(ddStr, "dd/MM/yyyy"));
				}
			}
		});
		// DATOS PERSONALES
		dtPersonal.forEach(dt -> {
			String dtStr = removeNBSP(dt.getVisibleText()).trim();
			String ddStr = removeNBSP(getNextSibling(dt).getVisibleText().trim());
			Integer ddInt = ddStr.length();
			if (ddInt > 0) {
				if (dtStr.indexOf("Nombre:") >= 0) {
					builder.setNameEmployee(ddStr);
				} else if (dtStr.indexOf("IPF:") >= 0) {
					builder.setIpf(ddStr.replace("D.N.I.", ""));
				} else if (dtStr.indexOf("Direcci\u00F3n:") >= 0) {
					builder.setDirectionEmployee(ddStr);
				} else if (dtStr.indexOf("Ocupaci\u00F3n:") >= 0) {
					builder.setOccupation(ddStr);
				}
			}
		});
		// DATOS DE EMPRESA
		dtEmpresa.forEach(dt -> {
			String dtStr = removeNBSP(dt.getVisibleText()).trim();
			String ddStr = removeNBSP(getNextSibling(dt).getVisibleText().trim());
			Integer ddInt = ddStr.length();
			if (ddInt > 0) {
				if (dtStr.indexOf("Nombre:") >= 0) {
					builder.setNameEnterprise(ddStr);
				} else if (dtStr.indexOf("Direcci\u00F3n:") >= 0) {
					builder.setDirectionEnterprise(ddStr);
				}
			}
		});
		// DATOS MEDICOS
		dtMedicos.forEach(dt -> {
			String dtStr = removeNBSP(dt.getVisibleText()).trim();
			String ddStr = removeNBSP(getNextSibling(dt).getVisibleText().trim());
			Integer ddInt = ddStr.length();
			if (ddInt > 0) {
				if (dtStr.indexOf("N° colegiado:") >= 0) {
					builder.setCollegiateNumber(noSpaces(ddStr));
				} else if (dtStr.indexOf("C.I.A.S.:") >= 0) {
					builder.setCias(ddStr);
				} else if (dtStr.indexOf("Contingencia:") >= 0) {
					builder.setContingency(ddStr);
				} else if (dtStr.indexOf("Fecha de alta:") >= 0) {
					builder.setWorkRestartDate(parseDate(ddStr, "dd/MM/yyyy"));
				} else if (dtStr.indexOf("Causa de alta:") >= 0) {
					builder.setCauseRestart(ddStr);
				} else if (dtStr.indexOf("Fecha confirmaci\u00F3n:") >= 0) {
					builder.setDateConfirmation(parseDate(ddStr, "dd/MM/yyyy"));
				} else if (dtStr.indexOf("Reca\u00EDda:") >= 0) {
					builder.setRelapse(ddStr.equalsIgnoreCase("S\u00ED") ? true : false);
				} else if (dtStr.indexOf("N° parte:") >= 0) {
					builder.setPartNum(Integer.parseInt(ddStr));
				} else if (dtStr.indexOf("Duraci\u00F3n probable en d\u00EDas:") >= 0) {
					builder.setDurationDays(parseInt(ddStr));
				} else if (dtStr.indexOf("Fecha accidente trabajo/Enfermedad Profesional:") >= 0) {
					builder.setDateAcc(parseDate(ddStr, "dd/MM/yyyy"));
				} else if (dtStr.indexOf("Fecha de baja del proceso anterior:") >= 0) {
					builder.setDateBjPrev(parseDate(ddStr, "dd/MM/yyyy"));
				} else if (dtStr.indexOf("Fecha de baja del proceso inicial:") >= 0) {
					builder.setDateBjInit(parseDate(ddStr, "dd/MM/yyyy"));
				} else if (dtStr.indexOf("Tipo de accidente:") >= 0) {
					builder.setTypeAcc(ddStr);
				} else if (dtStr.indexOf("Tipo de asistencia:") >= 0) {
					builder.setTypeAssist(ddStr);
				} else if (dtStr.indexOf("Fecha siguiente revisi\u00F3n m\u00E9dica:") >= 0) {
					builder.setDateNextMedical(parseDate(ddStr, "dd/MM/yyyy"));
				}
			}
		});
		// DATOS ECONOMICOS
		dtEconomicos.forEach(dt -> {
			String dtStr = removeNBSP(dt.getVisibleText()).trim();
			String ddStr = removeNBSP(getNextSibling(dt).getVisibleText().trim());
			Integer ddInt = ddStr.length();
			if (ddInt > 0) {
				if (dtStr.indexOf("Base de cot.:") >= 0) {
					builder.setBaseCtz(parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("D\u00EDas cotizados:") >= 0) {
					builder.setDaysCtz(parseInt(ddStr));
				} else if (dtStr.indexOf("Cotiz. horas extraord.:") >= 0) {
					builder.setHoursCtzExtr(parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("Suma Base cot.:") >= 0) {
					builder.setSumBCtz(parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("Suma d\u00EDas cot.:") >= 0) {
					builder.setDaysSumCtz(parseInt(ddStr));
				} else if (dtStr.indexOf("Cot. horas otros conc.:") >= 0) {
					builder.setHoursCrzOther(parseStringToFloat(ddStr));
				} else if (dtStr.indexOf("Grupo de cot.:") >= 0) {
					builder.setGpCtz(ddStr);
				} else if (dtStr.indexOf("Cat. profesional:") >= 0) {
					builder.setCatProf(ddStr);
				} else if (dtStr.indexOf("Tipo de contrato:") >= 0) {
					builder.setTypeCto(ddStr);
				} else if (dtStr.indexOf("Carencia:") >= 0) {
					builder.setLack(parseInt(ddStr));
				}
			}
		});

		return builder.build();
	}

}
