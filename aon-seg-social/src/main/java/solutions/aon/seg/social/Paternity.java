package solutions.aon.seg.social;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.transform.TransformerException;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.StringWebResponse;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlHeading3;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.PaternityException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.object.PaternityCertificate;
import solutions.aon.seg.social.object.PaternityCertificate.ApplicantType;
import solutions.aon.seg.social.object.PaternityDetail;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class Paternity {
	// Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/testPaternity.html");
	
	private Paternity() {
		 throw new IllegalStateException("Utility class");
	}
	 
	private static String DATE_FORMAT = "dd/MM/yyyy";
	
	static final String ADOPTERS = "Adopción/Tutela/Acogimiento";
	static final String BASE_URL = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H100";

	public static boolean sendPaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docNum, PaternityCertificate.ApplicantType applicantType,
			PaternityCertificate.ReasonType reason, final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP,
			final int days) throws SegSocialException, IOException {
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			
			XmlPage xmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H101");
			HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("FORMULARIO_2");
			
			// REGIME
			formDatos.getInputByName("regimen").setValue(regime);
			// CCC
			formDatos.getInputByName("ccc").setValue(contributionAccount);
			// NAF
			formDatos.getInputByName("naf").setValue(affiliationNumber);
			// ID TYPE
			HtmlSelect idTypeSelect = htmlPage.querySelector("select[name=tipoIpf]");
			idTypeSelect.setSelectedAttribute(Toolkit.getIdentityType(docNum), true);
			// ID NUM
			formDatos.getInputByName("codIpf").setValue(docNum);
			// APPLICANT TYPE
			HtmlSelect applicantTypeSelect = formDatos.getSelectByName("tipoSolicitante");
			applicantTypeSelect.setSelectedAttribute(applicantType.getValueTGSS(), true);
			// REASON
			String reasonNameInput = "motivoMadreBiologica";
			if(applicantType.equals(ApplicantType.OTRO_PROGENITOR)) {
				reasonNameInput = "motivoOtroProgenitor";
			} else if(Arrays.asList(ApplicantType.PRIMER_ADOPTANTE, ApplicantType.SEGUNDO_ADOPTANTE).contains(applicantType) ) {
				reasonNameInput = "motivoAdoptantes";
			}
			HtmlSelect reasonSelect = formDatos.getSelectByName(reasonNameInput);
		
			reasonSelect.setSelectedAttribute(reason.getValueTGSS(), true);
			// START DATE
			Toolkit.formatDate(dateFrom, DATE_FORMAT).ifPresent(date->
				formDatos.getInputByName("fechaInicio").setValue(date)
			);
			
			// SUBMIT
			htmlPage = HtmlUnitToolkit.transformXmlPage(((HtmlButton) formDatos.querySelector("#ENVIO_5")).click());
			checkErrors(htmlPage);
			
			// GOES TO THE CONFIRM PAGE
			HtmlForm formDatos2 = (HtmlForm) htmlPage.getElementById("FORMULARIO_2");
		
			// END DATE
			Toolkit.formatDate(dateTo, DATE_FORMAT).ifPresent(date-> {
				for(int i=1; i<10; i++) {
					try {
						HtmlInput endDateInput = (HtmlInput) formDatos2.getInputByName("fechaFinPeriodo" + i);
						if(null != endDateInput) {
							endDateInput.setValue(date);
							break;
						}
					} catch (Exception e) {}
				}
			});
			
			// CONFIRM
			htmlPage = HtmlUnitToolkit.transformXmlPage(((HtmlButton) formDatos2.querySelector("#ENVIO_11")).click());
			checkErrors(htmlPage);
			
			try {
				HtmlHeading3 h3 = htmlPage.querySelector("#ARQcapaPrincipalPest>h3");
				return h3!=null && h3.getVisibleText().contains("Resumen del certificado");
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
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;

	}

	private static WebResponse skipDateFormatError (WebRequest request, WebResponse response) {
		
		if ( request.getUrl().getFile().endsWith("prosa.min.js")) {
			String content = response.getContentAsString();
			//content = content.replaceAll("a\s*=\s*E\\(.*msgErrorFormaFecha.*dd/mm/aaaa\"\\)\\]\\)", "a=!0");
			content = content.replaceAll("\"chrome\"", "\":-o\"");
			return new StringWebResponse(content, request.getUrl());
		}
		
		return response;
		
	}
		
	public static void removePaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String ccc, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
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
	
	public static List<PaternityCertificate> getPaternitysDetail(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			String regime, String ccc,  Date dateFrom,  Date dateTo,  Optional<String> nss, Optional<Date> startDate) throws SegSocialException, InterruptedException, IOException {
		return getPaternitysCondition(certificateInputStream, certificatePassword, certificateType, true, regime, ccc, dateFrom, dateTo, nss, startDate);
	}

	public static List<PaternityCertificate> getPaternitys(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			String regime, String ccc,  Date dateFrom,  Date dateTo,  Optional<String> nss, Optional<Date> startDate) throws SegSocialException, InterruptedException, IOException {
		return getPaternitysCondition(certificateInputStream, certificatePassword, certificateType, false, regime, ccc, dateFrom, dateTo, nss, startDate);
	}
	
	private static List<PaternityCertificate> getPaternitysCondition(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, boolean details,
			String regime, String ccc,  Date dateFrom,  Date dateTo,  Optional<String> nss, Optional<Date> startDate) throws SegSocialException, InterruptedException, IOException {
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);

			HtmlPage htmlPage = webClient.getPage(BASE_URL);
			
			HtmlForm form = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
			
			htmlPage = ((HtmlSubmitInput)form.getInputByName("SPM.ACC.AC_TAB2")).click();
			checkErrors(htmlPage);		
			
			HtmlForm formD = (HtmlForm) htmlPage.getElementById("formDatos");

			formD.getInputByName("regimen").setValue(regime);
			formD.getInputByName("ccc2").setValue(Toolkit.SplitString(ccc, 2)[0]);
			formD.getInputByName("ccc9").setValue(Toolkit.SplitString(ccc, 2)[1]);
			Toolkit.formatDate(dateFrom, DATE_FORMAT).ifPresent(date-> 
				formD.getInputByName("fechaDesde").setValue(date)
			);
			
			Toolkit.formatDate(dateTo, DATE_FORMAT).ifPresent(date->
				formD.getInputByName("fechaHasta").setValue(date)
			);
	
			// NAF
			if(nss.isPresent()) {
				String[] naf = Toolkit.SplitString(nss.get(), 2);
				formD.getInputByName("naf2").setValue(naf[0]);
				formD.getInputByName("naf10").setValue(naf[1]);
			}
			
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty()) 
				formD.getInputByName("fechaInicio").setValue(Toolkit.formatDate(startDate.get(), DATE_FORMAT).get());

			// SUBMIT
			htmlPage = formD.getInputByValue("Buscar").click();
			checkErrors(htmlPage);
			
			ArrayList<PaternityCertificate> paternityCertificates = new ArrayList<>();
			
			DomNode exist = htmlPage.querySelector("#ARQcapaPrincipalPest fieldset>div>table");
			
			if(exist!=null) {
				// CHECKING IF THE PAGE THREW RESULTS
				boolean last = false;
	
				while (!last) {
					ArrayList<PaternityCertificate> certificates = new ArrayList<>();
					int i = 0;
					HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("formDatos");
					HtmlTable table = (HtmlTable) formDatos.querySelector("#ARQcapaPrincipalPest fieldset>div>table");

					for (final HtmlTableRow row : table.getRows()) {
						try {
							if(i>0) {
								DomNode detail = formDatos.querySelector("[name='SPM.ACC.AC_CO_DETALLE']");

								PaternityCertificate pcb = new PaternityCertificate()
								.setCcc(ccc);
								
								setData(row, pcb);
				
								if(details && !pcb.getCanceled()) {
									try {
										HtmlInput resultInput = (HtmlInput) row.getCell(0).getFirstElementChild();
										resultInput.click();
										HtmlPage htmlAux = ((HtmlInput)detail).click();

										setDataGeneral(htmlAux, pcb);
										setDataBases(htmlAux, pcb);
										setDataDetail(htmlAux, pcb);
//											setPdf(htmlAux, pcb);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								certificates.add(pcb);
							}
						} catch (NullPointerException | ElementNotFoundException e) {
							e.printStackTrace();
						}
						i++;
					}
			        
					DomNode next = formDatos.querySelector("[name='SPM.ACC.AC_CO_SIGUIENTE']");
					if(next!=null) {
						htmlPage =((HtmlSubmitInput) next).click();
					} else {
						last = true;
					}
					paternityCertificates.addAll(certificates);
				}
			}
	
			return paternityCertificates.stream().sorted((o1, o2)-> o1.getStartDate().compareTo(o2.getStartDate())).collect(Collectors.toList());
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
	
	private static void setData(HtmlTableRow row, PaternityCertificate pcb) {
		pcb.setWorkerNaf(Toolkit.noSpaces(row.getCell(1).getVisibleText()));
		
		String startStr = Toolkit.removeNBSP(row.getCell(2).getVisibleText().trim());
		if(!startStr.isEmpty())
			pcb.setStartDate(Toolkit.parseDate(startStr, DATE_FORMAT));

		String endDateStr = Toolkit.removeNBSP(row.getCell(3).getVisibleText().trim());
		if(!endDateStr.isEmpty())
			pcb.setEndDate(Toolkit.parseDate(endDateStr, DATE_FORMAT));
		
		pcb.setWorkerApplicantType(PaternityCertificate.ApplicantType.safeValueOf(row.getCell(4).getVisibleText().trim()));
		
		String receptionStr = Toolkit.removeNBSP(row.getCell(5).getVisibleText().trim());
		if(!receptionStr.isEmpty())
			pcb.setReceptionDate(Toolkit.parseDate(receptionStr, DATE_FORMAT));
		
		if(row.getCell(6).getVisibleText().contains("Anulado")) 
			pcb.setCanceled(true);
	}
	

	private static void setDataBases(HtmlPage htmlPage, PaternityCertificate pcb) {
		DomNodeList<DomNode> tdList1 = htmlPage.querySelectorAll("table[class='margenIzq12 rellenoIzq12 ancho60 clearL'] td");
		pcb.setPeriodNumber(Integer.parseInt(Toolkit.removeNBSP(tdList1.get(0).getVisibleText().trim())));
		
		String startStr = Toolkit.removeNBSP(tdList1.get(1).getVisibleText().trim());
		if(!startStr.isEmpty())
			pcb.setStartDate(Toolkit.parseDate(startStr, DATE_FORMAT));

		String endStr = Toolkit.removeNBSP(tdList1.get(2).getVisibleText().trim());
		if(!endStr.isEmpty())
			pcb.setEndDate(Toolkit.parseDate(endStr, DATE_FORMAT));
		
		pcb.setPartiality( Toolkit.parseStringToFloat(tdList1.get(3).getVisibleText()) );
	}
	
	private static void setDataDetail(HtmlPage htmlPage, PaternityCertificate pcb) {
		DomNodeList<DomNode> trList = htmlPage.querySelectorAll("table[class='margenSup12 ancho60 clearL']>tbody>tr");
		for (int j = 1; j < trList.size(); j++) {
			DomNodeList<DomNode> tds = ((HtmlTableRow) trList.get(j)).getChildNodes();
			String dateStr = Toolkit.removeNBSP(tds.get(2).getVisibleText().trim());
			if(!dateStr.isEmpty()) {
				Date date = Toolkit.parseDate(dateStr, "yyyy/MM");
				
			String baseCCStr = Toolkit.removeNBSP(tds.get(3).getVisibleText().trim());
			String baseCPStr = Toolkit.removeNBSP(tds.get(4).getVisibleText().trim());
			String daysStr = Toolkit.removeNBSP(tds.get(5).getVisibleText().trim());
	
				pcb.addPaternityDetail(
						new PaternityDetail().setDate(date)
						.setBaseCC(Toolkit.parseStringToFloat(baseCCStr))
						.setBaseCP(Toolkit.parseStringToFloat(baseCPStr))
						.setDays(parseInt(daysStr))
				);
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
//			HtmlAnchor docAnchor = htmlPage.querySelector("a[title='Informe:Anulación de certificado de Otro progenitor (Nacimiento de hijo)']");
//			
//			InputStream is = docAnchor.click().getWebResponse().getContentAsStream();
//			byte[] pdf = is.readAllBytes();
//			pcb.setPdf(pdf);		
//			is.close();
//		} catch (Exception e) {}
//	}

	private static void setDataGeneral(HtmlPage htmlPage, PaternityCertificate pcb) {
		htmlPage.querySelectorAll("fieldset dt").forEach(dt->{
			String text = Toolkit.removeNBSP(dt.getVisibleText()).trim();
			String value = Toolkit.removeNBSP(dt.getNextElementSibling().getVisibleText().trim());
			if(!value.isEmpty()) {
				if (text.indexOf("C.C.C:")>=0) {
					pcb.setCcc(value.replaceAll("\\D+", ""));
				} else if (text.indexOf("C\u00f3digo Postal:")>=0) {
					pcb.setPostCode(value);
				} else if (text.indexOf("Domicilio:")>=0) {
					pcb.setAddress(value);
				} else if (text.indexOf("Provincia:")>=0) {
					pcb.setProvince(value);
				} else if (text.indexOf("Localidad:")>=0) {
					pcb.setMunicipality(value);
				} else if (text.indexOf("Motivo:")>=0) {
					pcb.setReason(PaternityCertificate.ReasonType.safeValueOf(value));
				} else if (text.indexOf("Fecha de recepción:")>=0) {
					pcb.setReceptionDate(Toolkit.parseDate(value, DATE_FORMAT));
				} else if (text.indexOf("Trabajador")>=0) {
					pcb.setWorkerName(value);
				} else if (text.indexOf("N.I.F./N.I.E.:")>=0) {
					pcb.setWorkerNif(Toolkit.removeExtraZeros(value));
				} else if (text.indexOf("N.A.F.:")>=0) {
					pcb.setWorkerNaf(Toolkit.removeExtraZeros(value));
				} else if (text.indexOf("Grupo cotizaci\u00f3n:")>=0) {
					pcb.setWorkerGroup(value);
				} else if (text.indexOf("F. alta empresa:")>=0) {
					pcb.setWorkerDischargeDate(Toolkit.parseDate(value, DATE_FORMAT));
				} else if (text.indexOf("F. baja empresa:")>=0) {
					pcb.setWorkerWithdrawalDate(Toolkit.parseDate(value, DATE_FORMAT));
				} else if (text.indexOf("C\u00f3digo contrato:")>=0) {
					pcb.setWorkerContractCode(value);
				} else if (text.indexOf("Tipo contrato:")>=0) {
					pcb.setWorkerContractType(value);
				} else if (text.indexOf("Coef. t. parcial:")>=0) {
					pcb.setWorkerPartialTimeCoef(Toolkit.parseStringToFloat(value));
				} else if (text.indexOf("ES EMPLEADO P\u00daBLICO")>=0) {
					if (value.indexOf("NO")>=0) {
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
			final String certificateType, final String nss, final String regime,
			final String ccc, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			
			XmlPage xmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H101");
			HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			// Entrar en Consultar
			htmlPage = HtmlUnitToolkit.transformXmlPage(htmlPage.getElementById("Consulta").click());
			
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("FORMULARIO_2");
			// REGIME
			formDatos.getInputByName("regimen").setValue(regime);
			// CCC
			formDatos.getInputByName("ccc").setValue(ccc);
			// NAF
			formDatos.getInputByName("naf").setValue(nss);
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty()) 
				formDatos.getInputByName("fechaInicio").setValue(Toolkit.formatDate(startDate.get(), DATE_FORMAT).get());

			// SUBMIT
			htmlPage = HtmlUnitToolkit.transformXmlPage(htmlPage.getElementById("ENVIO_4").click());

			// CHECKING IF THE PAGE THREW RESULTS
			try {
				
				HtmlTable resultTable = (HtmlTable) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("TABLA_9")).orElseThrow();
				HtmlInput firstOpt = (HtmlInput) resultTable.querySelector("input[name='isn']");
				firstOpt.click();
				
				// Ver detalle
				htmlPage = HtmlUnitToolkit.transformXmlPage(htmlPage.getElementById("ENVIO_10").click());
				
				// Imprimir
				HtmlButton docButton = (HtmlButton) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("ENVIO_12")).orElseThrow();
				htmlPage = HtmlUnitToolkit.transformXmlPage(docButton.click());
				
				HtmlAnchor anchor = htmlPage.getElementById("CONTENEDOR_prevdocumentoseinformes").querySelector("a");
				
				InputStream is = anchor.click().getWebResponse().getContentAsStream();
				byte[] ret = is.readAllBytes();
				is.close();
				return ret;

			} catch (NullPointerException | ElementNotFoundException | InterruptedException e) {
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
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	private static void checkErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
		if (errors != null && !errors.getVisibleText().contains("NO HAY DATOS"))
			throw new InvalidDataException(errors.getVisibleText());
	}
}
