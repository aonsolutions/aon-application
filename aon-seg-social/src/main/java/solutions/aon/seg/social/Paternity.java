package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.UnfilledMandatory;
import solutions.aon.seg.social.exceptions.paternity.PaternityException;
import solutions.aon.seg.social.exceptions.paternity.PaternityNotFoundException;
import solutions.aon.seg.social.exceptions.paternity.PaternityWrongDataException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.PaternityCertificate;
import solutions.aon.seg.social.objects.PaternityCertificate.PaternityCertificateBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class Paternity {
	final static String[] ID_TYPE = { "NIF", "NIE" };
	// M -> Madre, P -> 'Otro progenitor', A -> Primer adoptante, B -> Segundo
	// adoptante
	final static String[] APPLICANT_TYPE = { "M", "P", "A", "B" };
	final static String[] MOTHER_REASON = { "Nacimiento de hijo", "Fallecimiento de la madre",
			"Cesión/Opción en favor del otro progenitor", "Parto múltiple",
			"Inicio del descanso antes del parto (solo para madre biológica ET)" };
	final static String[] FATHER_REASON = { "Nacimiento de hijo", "Parto múltiple" };
	// ADOPTERS es válido para las opciones del primer y segundo adoptante
	final static String ADOPTERS = "Adopción/Tutela/Acogimiento";

	public static boolean grabarCertificado(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docType, final String docNum, final String applicantType,
			final String reason, final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP,
			final int days) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
			htmlPage = htmlPage
					.getAnchorByHref("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ"
							+ ".SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100")
					.click();
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");

			// REGIME
			formDatos.getInputByName("regimen").setValueAttribute(regime);
			// CCC
			formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			// NAF
			formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			// ID TYPE
			HtmlSelect idTypeSelect = formDatos.getSelectByName("tipoIpf");
			idTypeSelect.getOptionByText(docType).setSelected(true);
			// ID NUM
			formDatos.getInputByName("codIpf").setValueAttribute(docNum);
			// APPLICANT TYPE
			HtmlSelect applicantTypeSelect = formDatos.getSelectByName("tipoPrestacion");
			applicantTypeSelect.getOptionByValue(applicantType).setSelected(true);
			// REASON
			HtmlSelect reasonSelect = formDatos.getSelectByName("motivoMadreBiologica");
			reasonSelect.getOptionByText(reason).setSelected(true);
			// START DATE
			formDatos.getInputByName("fechaInicio").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());

			// SUBMIT
			htmlPage = formDatos.getInputByValue("Validar").click();

			// GOES TO THE CONFIRM PAGE
			HtmlForm formDatos2 = (HtmlForm) htmlPage.getElementById("formDatos");
			// END DATE
			formDatos2.getInputByName("fechaFinPeriodo1")
					.setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
			// BASE CC
			formDatos2.getInputByName("baseCC1").setValueAttribute("" + Float.toString(baseCC).replace(".", ","));
			formDatos2.getInputByName("baseCP1").setValueAttribute("" + Float.toString(baseCP).replace(".", ","));
			formDatos2.getInputByName("prestacion1").setValueAttribute("" + days);
			// CONFIRM
			htmlPage = formDatos2.getInputByValue("Confirmar").click();

			try {
				HtmlHeading3 h3 = htmlPage.querySelector("#ARQcapaPrincipalPest>h3");
				if (h3.getVisibleText().equalsIgnoreCase("Resumen del certificado")) {
					return true;
				} else {
					return false;
				}
			} catch (ElementNotFoundException | NullPointerException e) {
				throw new SegSocialException();
			}

		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return false;

	}

	public static void voidPaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100");
			// MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			htmlPage = formDatos.getInputByValue("Modificar/Anular certificado").click();
			formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			// REGIME
			formDatos.getInputByName("regimen").setValueAttribute(regime);
			// CCC
			formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			// DATE FROM
			formDatos.getInputByName("fechaDesde").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());
			// END DATE
			formDatos.getInputByName("fechaHasta").setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
			// NAF
			formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty()) {
				formDatos.getInputByName("fechaInicio")
						.setValueAttribute(Toolkit.formatDate(startDate.get(), "dd/MM/yyyy").get());
			}

			// SUBMIT
			htmlPage = formDatos.getInputByValue("Buscar").click();
			// CHECKING IF THE PAGE THREW RESULTS
			try {
				HtmlTable resultTable = (HtmlTable) htmlPage.querySelector("#ARQcapaPrincipalPest fieldset>div>table");
				int rows = resultTable.getRowCount() - 2;
				formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
				htmlPage = htmlPage.getElementById("isn" + rows).click();
				HtmlPage htmlAux = formDatos.getInputByValue("Anular").click();
				htmlAux = htmlAux.getElementById("SPM.ACC.AC_GE_ANULAR").click();

			} catch (NullPointerException | ElementNotFoundException e) {
				try {
					HtmlListItem errorLi = (HtmlListItem) htmlPage
							.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
					if (errorLi.getVisibleText().trim().equalsIgnoreCase(
							"Régimen/Cuenta de Cotización NO HAY DATOS PARA ESTOS CRITERIOS DE CONSULTA")) {
						throw new PaternityNotFoundException();
					} else {
						throw new PaternityWrongDataException();
					}
				} catch (NullPointerException e1) {
					throw new PaternityException();
				}
			}

		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (NoSuchElementException e) {
			throw new PaternityException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
	}

	public static Collection<PaternityCertificate> consultCertificates(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String affiliationNumber,
			final String regime, final String contributionAccount, final Date dateFrom, final Date dateTo,
			final Optional<Date> startDate) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100");
			// MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			htmlPage = formDatos.getInputByValue("Consultar certificado").click();
			formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			// REGIME
			formDatos.getInputByName("regimen").setValueAttribute(regime);
			// CCC
			formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			// DATE FROM
			formDatos.getInputByName("fechaDesde").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());
			// END DATE
			formDatos.getInputByName("fechaHasta").setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
			// NAF
			try {
			formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			} catch (InvalidDataException e) {
				throw new UnfilledMandatory("No ccc found");
			}
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty()) {
				formDatos.getInputByName("fechaInicio")
						.setValueAttribute(Toolkit.formatDate(startDate.get(), "dd/MM/yyyy").get());
			}

			// SUBMIT
			htmlPage = formDatos.getInputByValue("Buscar").click();

			// CHECKING IF THE PAGE THREW RESULTS
			try {
				HtmlTable resultTable = (HtmlTable) htmlPage.querySelector("#ARQcapaPrincipalPest fieldset>div>table");
				int rows = resultTable.getRowCount() - 1;
				formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
				ArrayList<PaternityCertificate> ret = new ArrayList<PaternityCertificate>();
				for (int i = 1; i <= rows; i++) {
					HtmlTableCell resultCell = resultTable.getCellAt(i, 0);

					try {
						HtmlInput resultInput = (HtmlInput) resultCell.getFirstElementChild();
						htmlPage = resultInput.click();
						HtmlPage htmlAux = formDatos.getInputByValue("Ver detalle").click();

						// TAKING DATA FROM EACH PAGE
						// Picking info placed into dd's
						DomNodeList<DomNode> dtList = htmlAux.querySelectorAll("fieldset dt");
						PaternityCertificateBuilder pcb = new PaternityCertificateBuilder();
						for (DomNode dt : dtList) {
							chooseDataType(dt, pcb);
						}

						DomNodeList<DomNode> tdList1 = htmlAux
								.querySelectorAll("table[class='margenIzq12 rellenoIzq12 ancho60 clearL'] td");
						pcb.setPeriodNumber(
								Integer.parseInt(Toolkit.removeNBSP(tdList1.get(0).getVisibleText().trim())));
						try {
							Date startD = new SimpleDateFormat("dd/MM/yyyy")
									.parse(Toolkit.removeNBSP(tdList1.get(1).getVisibleText().trim()));
							pcb.setStartDate(startD);
						} catch (java.text.ParseException e) {
							pcb.setStartDate(null);
						}
						try {
							Date endD = new SimpleDateFormat("dd/MM/yyyy")
									.parse(Toolkit.removeNBSP(tdList1.get(2).getVisibleText().trim()));
							pcb.setEndDate(endD);
						} catch (java.text.ParseException e) {
							pcb.setEndDate(null);
						}
						pcb.setPartiality(Toolkit.removeNBSP(tdList1.get(3).getVisibleText().trim()));

						// REGISTRIES
						DomNodeList<DomNode> trRegistryList = htmlAux
								.querySelectorAll("table[class='margenSup12 ancho60 clearL']>tbody>tr");
						ArrayList<String[]> registries = new ArrayList<String[]>();
						for (int j = 1; j < trRegistryList.size(); j++) {
							HtmlTableRow trElement = (HtmlTableRow) trRegistryList.get(j);
							Iterable<DomElement> tdElements = trElement.getChildElements();
							String[] registry = new String[5];
							int k = 0;
							int countEmpty = 0;
							for (DomElement td : tdElements) {
								registry[k] = Toolkit.removeNBSP(td.getVisibleText().trim());
								if ((registry[k] == null) || (registry[k].equalsIgnoreCase("")))
									countEmpty++;
								k++;
							}
							if (countEmpty == 4) {
								break;
							}
							registries.add(registry);
						}
						pcb.setRegistry(registries);
						htmlAux = htmlAux.getElementById("SPM.ACC.AC_CO_INFORME").click();
						HtmlButton docButton = htmlAux.querySelector("button[class='botonDesplegable desplegar']");
						htmlAux = docButton.click();
						HtmlAnchor docAnchor = htmlAux.querySelector(
								"a[title='Informe:Anulación de certificado de Otro progenitor (Nacimiento de hijo)']");

						InputStream is = docAnchor.click().getWebResponse().getContentAsStream();
						byte[] pdf = is.readAllBytes();
						is.close();
						pcb.setPdf(pdf);
						PaternityCertificate pc = pcb.build();
						ret.add(pc);
					} catch (NullPointerException | ElementNotFoundException e) {
						// If radiobutton doesn't exist
					}
				}
				return ret;
			} catch (NullPointerException | ElementNotFoundException e) {
				try {
					HtmlListItem errorLi = (HtmlListItem) htmlPage
							.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
					if (errorLi.getVisibleText().trim().equalsIgnoreCase(
							"Régimen/Cuenta de Cotización NO HAY DATOS PARA ESTOS CRITERIOS DE CONSULTA")) {
						throw new PaternityNotFoundException();
					} else {
						throw new PaternityWrongDataException();
					}
				} catch (NullPointerException e1) {
					throw new PaternityException();
				}
			}

		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (NoSuchElementException e) {
			throw new PaternityException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}

	private static void chooseDataType(DomNode dt, PaternityCertificateBuilder pcb) {
		if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("C.C.C:")) {
			try {
				String text = dt.getNextElementSibling().getVisibleText().trim();
				text = text.substring(0, text.indexOf(' '));
				text = Toolkit.removeExtraZeros(text);
				pcb.setCcc(text);
			} catch (NullPointerException e) {
				pcb.setCcc(null);
			}
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Código Postal:")) {
			pcb.setPostCode(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Domicilio:")) {
			pcb.setAddress(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Provincia:")) {
			pcb.setProvince(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Localidad:")) {
			pcb.setMunicipality(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Motivo:")) {
			pcb.setReason(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Fecha de recepción:")) {
			String strDate = Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim());
			try {
				pcb.setReceptionDate(new SimpleDateFormat("dd/MM/yyyy").parse(strDate));
			} catch (java.text.ParseException e) {
				pcb.setReceptionDate(null);
			}
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Trabajador")) {
			pcb.setWorkerName(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("N.I.F./N.I.E.:")) {
			pcb.setWorkerNif(
					Toolkit.removeExtraZeros(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim())));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("N.A.F.:")) {
			pcb.setWorkerNaf(
					Toolkit.removeExtraZeros(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim())));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Grupo cotización:")) {
			pcb.setWorkerGroup(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("F. alta empresa:")) {
			String strDate = Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim());
			try {
				pcb.setWorkerDischargeDate(new SimpleDateFormat("dd/MM/yyyy").parse(strDate));
			} catch (java.text.ParseException e) {
				pcb.setWorkerDischargeDate(null);
			}
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("F. baja empresa:")) {
			String strDate = Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim());
			try {
				pcb.setWorkerWithdrawalDate(new SimpleDateFormat("dd/MM/yyyy").parse(strDate));
			} catch (java.text.ParseException e) {
				pcb.setWorkerWithdrawalDate(null);
			}
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Código contrato:")) {
			pcb.setWorkerContractCode(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Tipo contrato:")) {
			pcb.setWorkerContractType(Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("Coef. t. parcial:")) {
			pcb.setWorkerPartialTimeCoef(Float.parseFloat(Toolkit.removeNBSP(
					dt.getNextElementSibling().getVisibleText().trim().replace(',', '.').replace(".", ""))));
		} else if (Toolkit.removeNBSP(dt.getVisibleText()).trim().equalsIgnoreCase("¿ES EMPLEADO PÚBLICO?")) {
			if (Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()).equalsIgnoreCase("NO")) {
				pcb.setIsPublicEmployee(false);
			} else if (Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim()).equalsIgnoreCase("")) {
				pcb.setIsPublicEmployee(null);
			} else {
				pcb.setIsPublicEmployee(true);
			}
		}

	}

	// Gets the pdf of the first element of the query
	public static byte[] getCertificatePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100");
			// MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			htmlPage = formDatos.getInputByValue("Consultar certificado").click();
			formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			// REGIME
			formDatos.getInputByName("regimen").setValueAttribute(regime);
			// CCC
			formDatos.getInputByName("ccc2").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[0]);
			formDatos.getInputByName("ccc9").setValueAttribute(Toolkit.SplitString(contributionAccount, 2)[1]);
			// DATE FROM
			formDatos.getInputByName("fechaDesde").setValueAttribute(Toolkit.formatDate(dateFrom, "dd/MM/yyyy").get());
			// END DATE
			formDatos.getInputByName("fechaHasta").setValueAttribute(Toolkit.formatDate(dateTo, "dd/MM/yyyy").get());
			// NAF
			formDatos.getInputByName("naf2").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[0]);
			formDatos.getInputByName("naf10").setValueAttribute(Toolkit.SplitString(affiliationNumber, 2)[1]);
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty()) {
				formDatos.getInputByName("fechaInicio")
						.setValueAttribute(Toolkit.formatDate(startDate.get(), "dd/MM/yyyy").get());
			}

			// SUBMIT
			htmlPage = formDatos.getInputByValue("Buscar").click();

			// CHECKING IF THE PAGE THREW RESULTS
			try {
				HtmlTable resultTable = (HtmlTable) htmlPage.querySelector("#ARQcapaPrincipalPest fieldset>div>table");
				int rows = resultTable.getRowCount() - 2;
				formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
				htmlPage = htmlPage.getElementById("isn" + rows).click();
				htmlPage = formDatos.getInputByValue("Ver detalle").click();
				htmlPage = htmlPage.getElementById("SPM.ACC.AC_CO_INFORME").click();
				HtmlButton docButton = htmlPage.querySelector("button[class='botonDesplegable desplegar']");
				htmlPage = docButton.click();

				HtmlAnchor anchor = htmlPage.getElementById("contenedor_documentosInformes").querySelector("a");

				InputStream is = anchor.click().getWebResponse().getContentAsStream();
				byte[] ret = is.readAllBytes();
				is.close();
				return ret;

			} catch (NullPointerException | ElementNotFoundException e) {
				try {
					HtmlListItem errorLi = (HtmlListItem) htmlPage
							.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
					if (errorLi.getVisibleText().trim().equalsIgnoreCase(
							"Régimen/Cuenta de Cotización NO HAY DATOS PARA ESTOS CRITERIOS DE CONSULTA")) {
						throw new PaternityNotFoundException();
					} else {
						throw new PaternityWrongDataException();
					}
				} catch (NullPointerException e1) {
					throw new PaternityException();
				}
			}

		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (NoSuchElementException e) {
			throw new PaternityException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		}
		return null;
	}

}
