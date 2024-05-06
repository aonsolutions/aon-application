package solutions.aon.seg.social;

import static java.lang.Integer.parseInt;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.transform.TransformerException;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlHeading3;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.javascript.host.html.HTMLButtonElement;
import org.htmlunit.javascript.host.html.HTMLInputElement;
import org.htmlunit.xml.XmlPage;
import org.w3c.dom.Node;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.PaternityException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.PaternityCertificate;
import solutions.aon.seg.social.object.PaternityCertificate.ApplicantType;
import solutions.aon.seg.social.object.PaternityDetail;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class Paternity {
	// Toolkit.buildFile(htmlPage.asXml().getBytes(),
	// System.getProperty("user.home")+"/testPaternity.html");

	private Paternity() {
		throw new IllegalStateException("Utility class");
	}

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	static final String ADOPTERS = "AdopciÃ³n/Tutela/Acogimiento";
	static final String BASE_URL = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H101";
	private static final String TRY_AGAIN = "Intente nuevamente!";
	private static final String DT = ".//div[@class='datosEnLinea']/dl/div/dt";

	public static boolean sendPaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docNum, PaternityCertificate.ApplicantType applicantType,
			PaternityCertificate.ReasonType reason, final Date dateFrom, final Date dateTo, final float baseCC,
			final float baseCP, final int days) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/GetAccess/ResourceList");
			htmlPage = htmlPage
					.getAnchorByHref("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ"
							+ ".SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100")
					.click();
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");

			// REGIME
			formDatos.getInputByName("regimen").setValue(regime);
			// CCC
			String[] cccArr = Toolkit.SplitString(contributionAccount, 2);
			formDatos.getInputByName("ccc2").setValue(cccArr[0]);
			formDatos.getInputByName("ccc9").setValue(cccArr[1]);
			// NAF
			String[] nssArr = Toolkit.SplitString(affiliationNumber, 2);
			formDatos.getInputByName("naf2").setValue(nssArr[0]);
			formDatos.getInputByName("naf10").setValue(nssArr[1]);
			// ID TYPE
			HtmlSelect idTypeSelect = htmlPage.querySelector("select[name=tipoIpf]");
			idTypeSelect.setSelectedAttribute(Toolkit.getIdentityType(docNum), true);
			// ID NUM
			formDatos.getInputByName("codIpf").setValue(docNum);
			// APPLICANT TYPE
			HtmlSelect applicantTypeSelect = formDatos.getSelectByName("tipoPrestacion");
			applicantTypeSelect.setSelectedAttribute(applicantType.getValueTGSS(), true);
			// REASON
			String reasonNameInput = "motivoMadreBiologica";
			if (applicantType.equals(ApplicantType.OTRO_PROGENITOR)) {
				reasonNameInput = "motivoOtroProgenitor";
			} else if (Arrays.asList(ApplicantType.PRIMER_ADOPTANTE, ApplicantType.SEGUNDO_ADOPTANTE)
					.contains(applicantType)) {
				reasonNameInput = "motivoAdoptantes";
			}
			HtmlSelect reasonSelect = formDatos.getSelectByName(reasonNameInput);

			reasonSelect.setSelectedAttribute(reason.getValueTGSS(), true);
			// START DATE
			Toolkit.formatDate(dateFrom, DATE_FORMAT)
					.ifPresent(date -> formDatos.getInputByName("fechaInicio").setValue(date));
			// SUBMIT
			htmlPage = formDatos.getInputByValue("Validar").click();
			checkErrors(htmlPage);

			// GOES TO THE CONFIRM PAGE
			HtmlForm formDatos2 = (HtmlForm) htmlPage.getElementById("formDatos");

			// END DATE
			Toolkit.formatDate(dateTo, DATE_FORMAT)
					.ifPresent(date -> formDatos2.getInputByName("fechaFinPeriodo1").setValue(date));

			// BASE CC
			// Toolkit.buildFile(htmlPage.asXml().getBytes(),
			// "/Users/svaldepenas/Desktop/Paternity.html");
			// formDatos2.getInputByName("baseCC1").setValueAttribute("" +
			// Float.toString(baseCC).replace(".", ","));
			// formDatos2.getInputByName("baseCP1").setValueAttribute("" +
			// Float.toString(baseCP).replace(".", ","));
			// formDatos2.getInputByName("prestacion1").setValueAttribute("" + days);

			// CONFIRM
			htmlPage = formDatos2.getInputByValue("Confirmar").click();
			checkErrors(htmlPage);
			try {
				HtmlHeading3 h3 = htmlPage.querySelector("#ARQcapaPrincipalPest>h3");
				return h3 != null && h3.getVisibleText().contains("Resumen del certificado");
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

	public static void removePaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime, final String ccc,
			final Date dateFrom, final Date dateTo, final Optional<Date> startDate) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = webClient.getPage(BASE_URL);
			// MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			htmlPage = formDatos.getInputByValue("Modificar/Anular certificado").click();
			formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			// REGIME
			formDatos.getInputByName("regimen").setValue(regime);
			// CCC
			formDatos.getInputByName("ccc2").setValue(Toolkit.SplitString(ccc, 2)[0]);
			formDatos.getInputByName("ccc9").setValue(Toolkit.SplitString(ccc, 2)[1]);
			// DATE FROM
			formDatos.getInputByName("fechaDesde").setValue(Toolkit.formatDate(dateFrom, DATE_FORMAT).get());
			// END DATE
			formDatos.getInputByName("fechaHasta").setValue(Toolkit.formatDate(dateTo, DATE_FORMAT).get());
			// NAF
			formDatos.getInputByName("naf2").setValue(Toolkit.SplitString(affiliationNumber, 2)[0]);
			formDatos.getInputByName("naf10").setValue(Toolkit.SplitString(affiliationNumber, 2)[1]);
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty()) {
				formDatos.getInputByName("fechaInicio")
						.setValue(Toolkit.formatDate(startDate.get(), DATE_FORMAT).get());
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
				checkErrors(htmlPage);
			} catch (NullPointerException | ElementNotFoundException e) {
				checkErrors(htmlPage);
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

	public static List<PaternityCertificate> getPaternitysDetail(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc, Date dateFrom,
			Date dateTo, Optional<String> nss, Optional<Date> startDate)
			throws SegSocialException, InterruptedException, IOException {
		return getPaternitysCondition(certificateInputStream, certificatePassword, certificateType, true, regime, ccc,
				dateFrom, dateTo, nss, startDate);
	}

	public static List<PaternityCertificate> getPaternitys(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regime, String ccc, Date dateFrom,
			Date dateTo, Optional<String> nss, Optional<Date> startDate)
			throws SegSocialException, InterruptedException, IOException {
		return getPaternitysCondition(certificateInputStream, certificatePassword, certificateType, false, regime, ccc,
				dateFrom, dateTo, nss, startDate);
	}

	private static List<PaternityCertificate> getPaternitysCondition(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, boolean details, String regime, String ccc,
			Date dateFrom, Date dateTo, Optional<String> nss, Optional<Date> startDate)
			throws SegSocialException, InterruptedException, IOException {
		InvalidCertificateException.checkCertificate(certificateInputStream);

		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);

			ArrayList<PaternityCertificate> paternityCertificates = new ArrayList<>();

			XmlPage xmlPage = webClient.getPage(BASE_URL);
			HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			xmlPage = htmlPage.getElementById("Consulta").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			checkErrors(htmlPage);

			wait4(htmlPage, p -> p.querySelector("[name=\"regimen\"]"))
					.orElseThrow(() -> new SegSocialException(TRY_AGAIN));

			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_2"))
					.orElseThrow(() -> new SegSocialException(TRY_AGAIN));

			form.getInputByName("regimen").setValue(regime);
			form.getInputByName("ccc").setValue(ccc);
			Toolkit.formatDate(dateFrom, DATE_FORMAT)
					.ifPresent(date -> form.getInputByName("fechaDesde").setValue(date));

			Toolkit.formatDate(dateTo, DATE_FORMAT).ifPresent(date -> form.getInputByName("fechaHasta").setValue(date));

			// NAF
			if (nss.isPresent()) {
				form.getInputByName("naf").setValue(nss.orElse(""));
			}

			// START DATE (OPTIONAL)
			if (!startDate.isEmpty())
				form.getInputByName("fechaInicio").setValue(Toolkit.formatDate(startDate.get(), DATE_FORMAT).get());

			// SUBMIT
			xmlPage = htmlPage.getElementById("ENVIO_4").click();
			htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			checkErrors(htmlPage);
			List<HtmlTableBody> tableBodies = htmlPage
					.getByXPath("//div[@class='pr_tablaResponsive']/table[@id='TABLA_9']/tbody");
			for (HtmlTableBody htmlTableBody : tableBodies) {
				List<HtmlTableRow> rows = htmlTableBody.getRows();
				for (HtmlTableRow row : rows) {
					List<HtmlInput> selected = row.getByXPath(".//input");
					selected.get(0).click();
					xmlPage = htmlPage.getElementById("ENVIO_10").click();
					HtmlPage detailsHtmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
					PaternityCertificate certificate = new PaternityCertificate();

					DomElement datosEmpresa = detailsHtmlPage.getElementById("SECCION_14");
					List<DomElement> dtEmpresas = datosEmpresa.getByXPath(DT);
					for (DomElement dtEmpresa : dtEmpresas) {
						if ("CCC:".equals(dtEmpresa.getTextContent())) {
							String ddConsulta = dtEmpresa.getNextElementSibling().getTextContent();
							certificate.setCcc(ddConsulta);
						} else if ("Código Postal:".equals(dtEmpresa.getTextContent())) {
							String ddEmpresa = dtEmpresa.getNextElementSibling().getTextContent();
							certificate.setPostCode(ddEmpresa);
						} else if ("Domicilio:".equals(dtEmpresa.getTextContent())) {
							String ddEmpresa = dtEmpresa.getNextElementSibling().getTextContent();
							certificate.setAddress(ddEmpresa);
						} else if ("Provincia:".equals(dtEmpresa.getTextContent())) {
							String ddEmpresa = dtEmpresa.getNextElementSibling().getTextContent();
							certificate.setProvince(ddEmpresa);
						} else if ("Localidad:".equals(dtEmpresa.getTextContent())) {
							String ddEmpresa = dtEmpresa.getNextElementSibling().getTextContent();
							certificate.setMunicipality(ddEmpresa);
						}
					}

					DomElement datosPrestacion = detailsHtmlPage.getElementById("SECCION_15");
					List<DomElement> dtPrestaciones = datosPrestacion.getByXPath(DT);
					for (DomElement dtPrestacion : dtPrestaciones) {
						if ("Fecha de recepción:".equals(dtPrestacion.getTextContent())) {
							String ddPrestacion = dtPrestacion.getNextElementSibling().getTextContent();
							certificate.setReceptionDate(Toolkit.parseDate(ddPrestacion, DATE_FORMAT));
						}
					}
					List<DomElement> trPrestaciones = datosPrestacion
							.getByXPath("//div[@class='pr_tablaResponsive']/table[@id='TABLA_16']/tbody/tr");
					for (DomElement trPrestacion : trPrestaciones) {
						certificate.setPeriodNumber(
								Integer.parseInt(trPrestacion.getFirstElementChild().getTextContent()));
						certificate.setStartDate(Toolkit.parseDate(
								trPrestacion.getFirstElementChild().getNextElementSibling().getTextContent(),
								DATE_FORMAT));
						certificate.setEndDate(Toolkit.parseDate(trPrestacion.getFirstElementChild()
								.getNextElementSibling().getNextElementSibling().getTextContent(), DATE_FORMAT));
						String parciality = trPrestacion.getFirstElementChild().getNextElementSibling()
								.getNextElementSibling().getNextElementSibling().getTextContent();
						if (!parciality.isEmpty()) {
							certificate.setParciality(
									Toolkit.parseStringToFloat(parciality));
						}
						
					}
					
					DomElement datosTrabajador = detailsHtmlPage.getElementById("SECCION_17");
					List<DomElement> dtTrabajadores = datosTrabajador.getByXPath(DT);
					for (DomElement dtTrabajador : dtTrabajadores) {
						if ("Trabajor:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerName(ddTrabajador);
						} else if ("NIF:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerNif(ddTrabajador);
						} else if ("NAF:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerNaf(ddTrabajador);
						} else if ("Grupo cotización:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerGroup(ddTrabajador);
						} else if ("F.alta empresa:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerDischargeDate(Toolkit.parseDate(ddTrabajador, DATE_FORMAT));
						} else if ("F.baja empresa:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerWithdrawalDate(Toolkit.parseDate(ddTrabajador, DATE_FORMAT));
						} else if ("Código contrato:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerContractCode(ddTrabajador);
						} else if ("Coef. t. parcial:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerPartialTimeCoef(Toolkit.parseStringToFloat(ddTrabajador));
						} else if ("Tipo contrato:".equals(dtTrabajador.getTextContent())) {
							String ddTrabajador = dtTrabajador.getNextElementSibling().getTextContent();
							certificate.setWorkerContractType(ddTrabajador);
						}
						
					}
					
					DomElement empleadosPublicos = detailsHtmlPage.getElementById("SECCION_18");
					List<DomElement> dtEmpleadosPublicos = empleadosPublicos.getByXPath(DT);
					for (DomElement dtEmpleadoPublico : dtEmpleadosPublicos) {
						if ("¿ES EMPLEADO PUBLICO?".equals(dtEmpleadoPublico.getTextContent())) {
							String ddEmpleadoPublico = dtEmpleadoPublico.getNextElementSibling().getTextContent();
							certificate.setIsPublicEmployee(Toolkit.toBoolean(ddEmpleadoPublico));
						}
					}
				paternityCertificates.add(certificate);
				}
			}
			return paternityCertificates.stream().sorted((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()))
					.collect(Collectors.toList());
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new PaternityException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		} catch (TransformerException e1) {
			throw new SegSocialException();
		}
		return null;
	}

	private static void setData(HtmlTableRow row, PaternityCertificate pcb) {
		pcb.setWorkerNaf(Toolkit.noSpaces(row.getCell(1).getVisibleText()));

		String startStr = Toolkit.removeNBSP(row.getCell(2).getVisibleText().trim());
		if (!startStr.isEmpty())
			pcb.setStartDate(Toolkit.parseDate(startStr, DATE_FORMAT));

		String endDateStr = Toolkit.removeNBSP(row.getCell(3).getVisibleText().trim());
		if (!endDateStr.isEmpty())
			pcb.setEndDate(Toolkit.parseDate(endDateStr, DATE_FORMAT));

		pcb.setWorkerApplicantType(
				PaternityCertificate.ApplicantType.safeValueOf(row.getCell(4).getVisibleText().trim()));

		String receptionStr = Toolkit.removeNBSP(row.getCell(5).getVisibleText().trim());
		if (!receptionStr.isEmpty())
			pcb.setReceptionDate(Toolkit.parseDate(receptionStr, DATE_FORMAT));

		if (row.getCell(6).getVisibleText().contains("Anulado"))
			pcb.setCanceled(true);
	}

	private static void setDataBases(HtmlPage htmlPage, PaternityCertificate pcb) {
		DomNodeList<DomNode> tdList1 = htmlPage
				.querySelectorAll("table[class='margenIzq12 rellenoIzq12 ancho60 clearL'] td");
		pcb.setPeriodNumber(Integer.parseInt(Toolkit.removeNBSP(tdList1.get(0).getVisibleText().trim())));

		String startStr = Toolkit.removeNBSP(tdList1.get(1).getVisibleText().trim());
		if (!startStr.isEmpty())
			pcb.setStartDate(Toolkit.parseDate(startStr, DATE_FORMAT));

		String endStr = Toolkit.removeNBSP(tdList1.get(2).getVisibleText().trim());
		if (!endStr.isEmpty())
			pcb.setEndDate(Toolkit.parseDate(endStr, DATE_FORMAT));

		pcb.setPartiality(Toolkit.parseStringToFloat(tdList1.get(3).getVisibleText()));
	}

	private static void setDataDetail(HtmlPage htmlPage, PaternityCertificate pcb) {
		DomNodeList<DomNode> trList = htmlPage.querySelectorAll("table[class='margenSup12 ancho60 clearL']>tbody>tr");
		for (int j = 1; j < trList.size(); j++) {
			DomNodeList<DomNode> tds = ((HtmlTableRow) trList.get(j)).getChildNodes();
			String dateStr = Toolkit.removeNBSP(tds.get(2).getVisibleText().trim());
			if (!dateStr.isEmpty()) {
				Date date = Toolkit.parseDate(dateStr, "yyyy/MM");

				String baseCCStr = Toolkit.removeNBSP(tds.get(3).getVisibleText().trim());
				String baseCPStr = Toolkit.removeNBSP(tds.get(4).getVisibleText().trim());
				String daysStr = Toolkit.removeNBSP(tds.get(5).getVisibleText().trim());

				pcb.addPaternityDetail(
						new PaternityDetail().setDate(date).setBaseCC(Toolkit.parseStringToFloat(baseCCStr))
								.setBaseCP(Toolkit.parseStringToFloat(baseCPStr)).setDays(parseInt(daysStr)));
			} else
				break;
		}
	}

//	private static void setPdf(HtmlPage htmlPage, PaternityCertificate pcb) {
//		try {
//			htmlPage = htmlPage.getElementById("SPM.ACC.AC_CO_INFORME").click();
//
//			HtmlButton docButton = htmlPage.querySelector("button[class='botonDesplegable desplegar']");
//			htmlPage = docButton.click();
//			HtmlAnchor docAnchor = htmlPage.querySelector("a[title='Informe:AnulaciÃ³n de certificado de Otro progenitor (Nacimiento de hijo)']");
//			
//			InputStream is = docAnchor.click().getWebResponse().getContentAsStream();
//			byte[] pdf = is.readAllBytes();
//			pcb.setPdf(pdf);		
//			is.close();
//		} catch (Exception e) {}
//	}

	private static void setDataGeneral(HtmlPage htmlPage, PaternityCertificate pcb) {
		htmlPage.querySelectorAll("fieldset dt").forEach(dt -> {
			String text = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String value = Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim());
			if (!value.isEmpty()) {
				if (text.indexOf("C.C.C:") >= 0) {
					pcb.setCcc(value.replaceAll("\\D+", ""));
				} else if (text.indexOf("C\u00f3digo Postal:") >= 0) {
					pcb.setPostCode(value);
				} else if (text.indexOf("Domicilio:") >= 0) {
					pcb.setAddress(value);
				} else if (text.indexOf("Provincia:") >= 0) {
					pcb.setProvince(value);
				} else if (text.indexOf("Localidad:") >= 0) {
					pcb.setMunicipality(value);
				} else if (text.indexOf("Motivo:") >= 0) {
					pcb.setReason(PaternityCertificate.ReasonType.safeValueOf(value));
				} else if (text.indexOf("Fecha de recepciÃ³n:") >= 0) {
					pcb.setReceptionDate(Toolkit.parseDate(value, DATE_FORMAT));
				} else if (text.indexOf("Trabajador") >= 0) {
					pcb.setWorkerName(value);
				} else if (text.indexOf("N.I.F./N.I.E.:") >= 0) {
					pcb.setWorkerNif(Toolkit.removeExtraZeros(value));
				} else if (text.indexOf("N.A.F.:") >= 0) {
					pcb.setWorkerNaf(Toolkit.removeExtraZeros(value));
				} else if (text.indexOf("Grupo cotizaci\u00f3n:") >= 0) {
					pcb.setWorkerGroup(value);
				} else if (text.indexOf("F. alta empresa:") >= 0) {
					pcb.setWorkerDischargeDate(Toolkit.parseDate(value, DATE_FORMAT));
				} else if (text.indexOf("F. baja empresa:") >= 0) {
					pcb.setWorkerWithdrawalDate(Toolkit.parseDate(value, DATE_FORMAT));
				} else if (text.indexOf("C\u00f3digo contrato:") >= 0) {
					pcb.setWorkerContractCode(value);
				} else if (text.indexOf("Tipo contrato:") >= 0) {
					pcb.setWorkerContractType(value);
				} else if (text.indexOf("Coef. t. parcial:") >= 0) {
					pcb.setWorkerPartialTimeCoef(Toolkit.parseStringToFloat(value));
				} else if (text.indexOf("ES EMPLEADO P\u00daBLICO") >= 0) {
					if (value.indexOf("NO") >= 0) {
						pcb.setIsPublicEmployee(false);
					} else if (value.equalsIgnoreCase("")) {
						pcb.setIsPublicEmployee(null);
					} else {
						pcb.setIsPublicEmployee(true);
					}
				}
			}
		});
	}

	// Gets the pdf of the first element of the query
	public static byte[] getCertificatePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String nss, final String regime, final String ccc, final Date dateFrom,
			final Date dateTo, final Optional<Date> startDate) throws SegSocialException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage(BASE_URL);
			// MOVING TO 'MODIFICAR/ANULAR CERTIFICADOS' SECTION
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			htmlPage = formDatos.getInputByValue("Consultar certificado").click();
			formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
			// REGIME
			formDatos.getInputByName("regimen").setValue(regime);
			// CCC
			formDatos.getInputByName("ccc2").setValue(Toolkit.SplitString(ccc, 2)[0]);
			formDatos.getInputByName("ccc9").setValue(Toolkit.SplitString(ccc, 2)[1]);
			// DATE FROM
			formDatos.getInputByName("fechaDesde").setValue(Toolkit.formatDate(dateFrom, DATE_FORMAT).get());
			// END DATE
			formDatos.getInputByName("fechaHasta").setValue(Toolkit.formatDate(dateTo, DATE_FORMAT).get());
			// NAF
			formDatos.getInputByName("naf2").setValue(Toolkit.SplitString(nss, 2)[0]);
			formDatos.getInputByName("naf10").setValue(Toolkit.SplitString(nss, 2)[1]);
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty())
				formDatos.getInputByName("fechaInicio")
						.setValue(Toolkit.formatDate(startDate.get(), DATE_FORMAT).get());

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
				checkErrors(htmlPage);
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

	private static void checkErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
		if (errors != null && !errors.getVisibleText().contains("NO HAY DATOS"))
			throw new InvalidDataException(errors.getVisibleText());
	}

	public static void main(String[] args) throws IOException, SegSocialException, ParseException,
			FailingHttpStatusCodeException, InterruptedException, TransformerException {
		try (InputStream is = new FileInputStream("/home/ndiaz/Documentos/pvasesores.p12");
				FileOutputStream os = new FileOutputStream(File.createTempFile("tgss", ".pdf"))) {
			Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/09/2023");

			List<PaternityCertificate> paternityCertificates = getPaternitysCondition(is, "7624", "PKCS12", false,
					"0111", "41017063249", startDate, new Date(), Optional.empty(), Optional.empty());
			for (PaternityCertificate paternityCertificate : paternityCertificates) {
				System.out.println(paternityCertificate);
			}
		}
	}
}
