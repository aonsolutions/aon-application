package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;

import aon.sepe.exceptions.invalidData.InvalidDataException;
import aon.sepe.objects.Certificates;
import aon.sepe.objects.QuoteData;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Certificado {

	//	Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/test.html");

	private Certificado() {
		throw new IllegalStateException("Utility class");
	}

	private static DecimalFormat decimalFormat = new DecimalFormat("#00.00");

	public static byte[] getCertEnterprisePdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String nif, Date fecha)
			throws SepeException {
		try {
			return getCertEnterprisePdfImpl(certificateInputStream, certificatePassword, certificateType, nif, fecha);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (MalformedURLException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SepeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e.getMessage());
		}
	}

	public static byte[] sendCertEnterprise(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Certificates certificates) throws SepeException {
		try {
			return sendCertEnterpriseImpl(certificateInputStream, certificatePassword, certificateType, certificates);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (MalformedURLException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (InterruptedException e) {
			throw new SepeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e.getMessage());
		}
	}

	private static byte[] getCertEnterprisePdfImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String nif, Date fecha)
			throws IOException, SepeException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setUseInsecureSSL(true);

			HtmlPage htmlPage = firstPageSepeCert(webClient);
			
			HtmlAnchor hrefButton = HtmlUnitToolkit
					.wait4(htmlPage,
							p -> p.getAnchorByHref(
									"https://sede.sepe.gob.es/ConsultasCertificadosRTWEB/ActionEntradaConsultas.do"))
					.orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();

			HtmlForm formDatos1 = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#contenido > form")).orElseThrow();
			
			formDatos1.getInputByName("nif").setValue(nif);
			
			htmlPage = formDatos1.getInputByName("btBuscar").click();
			handleSepeExceptions(htmlPage);
			
			Page page = null;

			String columnCheck = "0"; // FIRST RESULT DEFAULT

			DomNode documentSelected = htmlPage
					.querySelector("#contenido form input[name=documentoSeleccionado][value=\"" + columnCheck + "\"]");

			if (documentSelected != null) {
				page = getPageFirstPdf(htmlPage);
			} else {
				DomNode btnGenerarPdf = htmlPage.querySelector("[name=btGenerarPDF]");

				DomNode btMasCert = htmlPage.querySelector("[name=btMasCert]");

				if (btnGenerarPdf != null && (btMasCert == null || fecha == null)) {
					page = ((HtmlSubmitInput) btnGenerarPdf).click();
				} else {
					htmlPage = ((HtmlSubmitInput) btMasCert).click();
					handleSepeExceptions(htmlPage);

					HtmlTable table = (HtmlTable) htmlPage.querySelector("#contenido > form table");

					if (null != table) {
						if (fecha != null) {
							String[] fra = Toolkit.formatDate(fecha);
							String ffin = fra[0] + "/" + fra[1] + "/" + fra[2];
							for (final HtmlTableRow row : table.getRows()) {
								HtmlTableCell cell = row.getCell(6);
								if (cell.getVisibleText().contains(ffin)) {
									HtmlRadioButtonInput firstColumn = row.getCell(0)
											.querySelector("input[name=certSeleccionado]");
									columnCheck = firstColumn.getValue();
									break;
								}
							}
						}

						HtmlRadioButtonInput inputRadio = htmlPage.querySelector(
								"#contenido form input[name=certSeleccionado][value=\"" + columnCheck + "\"]");
						inputRadio.click();

						page = htmlPage.getElementByName("btAceptar").click();

						page = getPageFirstPdf(page);
					}

				}
			}

			if (page != null) {
				if (page.isHtmlPage()) {
					htmlPage = (HtmlPage) page;
					handleSepeExceptions(htmlPage);
				} else {
					try {
						return page.getWebResponse().getContentAsStream().readAllBytes();
					} catch (Exception e) {
						throw new InvalidDataException();
					}
				}
			}
		}

		return null;
	}

	private static byte[] sendCertEnterpriseImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Certificates certificates)
			throws IOException, SepeException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			webClient.getOptions().setUseInsecureSSL(true);

			String ctaCti = certificates.getCtaCti();
			String ipfManager = certificates.getIpfManager();
			String typeContract = certificates.getTypeContract();
			String tipodocManager = "NIF";
			if (Toolkit.getIdentityType(ipfManager).equals("4")) {
				tipodocManager = "CIF";
			} else if (Toolkit.getIdentityType(ipfManager).equals("6")) {
				tipodocManager = "NIE";
			}

			String[] fAE = Toolkit.formatDate(certificates.getfAEd());
			String[] fST = Toolkit.formatDate(certificates.getfSTd());

			HtmlPage htmlPage = firstPageSepeCert(webClient);
			
			HtmlAnchor hrefButton = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("https://sede.sepe.gob.es/CertificadosRedTrabajaWEB/ActionMecanizacionEntradaEmpresa.do"))
					.orElseThrow();
			htmlPage = (HtmlPage) hrefButton.click();

			{// DATA ENTERPRISE
				HtmlForm formEnterprise = (HtmlForm) HtmlUnitToolkit
						.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				
				formEnterprise.getInputByName("orDatosEmpresa.srCCCRegimenCot").setValue(certificates.getRegimen());
				formEnterprise.getInputByName("orDatosEmpresa.srCCCRegimenCot").setValueAttribute(certificates.getRegimen());
				
				formEnterprise.getInputByName("orDatosEmpresa.srCCCProvincia").setValue(ctaCti.substring(0, 2));
				formEnterprise.getInputByName("orDatosEmpresa.srCCCProvincia").setValueAttribute(ctaCti.substring(0, 2));
				
				formEnterprise.getInputByName("orDatosEmpresa.srCCCSecuencial").setValue(ctaCti.substring(2, 9));
				formEnterprise.getInputByName("orDatosEmpresa.srCCCSecuencial").setValueAttribute(ctaCti.substring(2, 9));
				
				formEnterprise.getInputByName("orDatosEmpresa.srCCCDC").setValue(ctaCti.substring(9));
				formEnterprise.getInputByName("orDatosEmpresa.srCCCDC").setValueAttribute(ctaCti.substring(9));
				
				formEnterprise.getInputByName("stDniNie").setValue(certificates.getIpf());
				formEnterprise.getInputByName("stDniNie").setValueAttribute(certificates.getIpf());
				
				htmlPage = formEnterprise.getInputByName("btBuscar").click();
				handleSepeExceptions(htmlPage);
			}

			htmlPage = ((HtmlSubmitInput) htmlPage
					.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
			handleSepeExceptions(htmlPage);
			
			{// DATA REPRESENTATIVE
				HtmlForm formRepresentative = (HtmlForm) HtmlUnitToolkit
						.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				
				formRepresentative.getInputByName("orDatosRepresentante.srNombreRepresentante").setValue(certificates.getNameManager());
				formRepresentative.getInputByName("orDatosRepresentante.srNombreRepresentante").setValueAttribute(certificates.getNameManager());
				
				formRepresentative.getInputByName("orDatosRepresentante.srPrimerApellidoRepresentante").setValue(certificates.getSurnameManager());
				formRepresentative.getInputByName("orDatosRepresentante.srPrimerApellidoRepresentante").setValueAttribute(certificates.getSurnameManager());

				((HtmlSelect) formRepresentative
						.querySelector("select[name=\"orDatosRepresentante.srTipoDocRepresentante\"]"))
						.setSelectedAttribute(tipodocManager, true);

				formRepresentative.getInputByName("orDatosRepresentante.srNifRepresentante").setValue(ipfManager);
				formRepresentative.getInputByName("orDatosRepresentante.srNifRepresentante").setValueAttribute(ipfManager);

				certificates.getLastSurnameManager().ifPresent(d -> {
					formRepresentative.getInputByName("orDatosRepresentante.srSegundoApellidoRepresentante").setValue(d);
					formRepresentative.getInputByName("orDatosRepresentante.srSegundoApellidoRepresentante").setValueAttribute(d);
				});

				certificates.getCargoManager().ifPresent(d -> {
					formRepresentative.getInputByName("orDatosRepresentante.srCargoRepresentante").setValue(d);
					formRepresentative.getInputByName("orDatosRepresentante.srCargoRepresentante").setValueAttribute(d);
				});

				htmlPage = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}

			{// DATA EMPLOYEE
				HtmlForm formEmployee = (HtmlForm) HtmlUnitToolkit
						.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();

				try {
					HtmlInput nameInput = formEmployee.getInputByName("orDatosTrabajador.srNombreTrabajador");
					if (nameInput != null && nameInput.getValue().isBlank()) {
						nameInput.setValue(certificates.getEmployeeName().get());
						nameInput.setValueAttribute(certificates.getEmployeeName().get());
					}
					
					HtmlInput surnameInput = formEmployee.getInputByName("orDatosTrabajador.srPrimerApellidoTrabajador");
					if (surnameInput != null && surnameInput.getValue().isBlank()) {
						surnameInput.setValue(certificates.getEmployeeSurname().get());
						surnameInput.setValueAttribute(certificates.getEmployeeSurname().get());
					}
					
					HtmlInput secondSurnameInput = formEmployee.getInputByName("orDatosTrabajador.srSegundoApellidoTrabajador");
					if (secondSurnameInput != null && secondSurnameInput.getValue().isBlank()) {
						secondSurnameInput.setValue(certificates.getEmployeeSecondSurname().get());
						secondSurnameInput.setValueAttribute(certificates.getEmployeeSecondSurname().get());
					}
				} catch (Exception e) {
					// Ya esta rellenado por defecto
				}
				
				HtmlInput ipfInput = formEmployee.getInputByName("orDatosTrabajador.srNifTrabajador");
				if (ipfInput != null && ipfInput.getValue().isBlank()) {
					ipfInput.setValue(certificates.getIpf());
					ipfInput.setValueAttribute(certificates.getNaf().get());
				}
				
				HtmlInput ssnInput = formEmployee.getInputByName("orDatosTrabajador.srNumSSTrabajador");
				if (ssnInput != null && ssnInput.getValue().isBlank()) {
					ssnInput.setValue(certificates.getNaf().get());
					ssnInput.setValueAttribute(certificates.getNaf().get());
				}
				
				DomNode gc = formEmployee.querySelector("select[name=\"orDatosTrabajador.csGrupoCotizacion.valor\"]");
				if (gc != null && certificates.getGz() != null) {
					((HtmlSelect) gc).setSelectedAttribute(certificates.getGz(), true);
				}

				DomNode contractType = formEmployee
						.querySelector("select[name=\"orDatosTrabajador.csTipoContrato.valor\"]");
				if (contractType != null && typeContract != null) {
					((HtmlSelect) contractType).setSelectedAttribute(typeContract, true);
				}

				HtmlInput durationInput = formEmployee.getInputByName("orDatosTrabajador.srDuracionContratoTrab");
				if (durationInput != null && certificates.getDurationContract() != null) {
					durationInput.setValue(certificates.getDurationContract().toString());
					durationInput.setValueAttribute(certificates.getDurationContract().toString());
				}

				DomNode durationType = formEmployee
						.querySelector("select[name=\"orDatosTrabajador.csIndicadorDuracionContrato.valor\"]");
				if (durationType != null && certificates.getTypeDuration() != null) {
					((HtmlSelect) durationType).setSelectedAttribute(certificates.getTypeDuration().getValue(), true);
				}

				if (Arrays.asList("2", "5").contains(typeContract.substring(0, 1))) {
					formEmployee.getInputByName("orDatosTrabajador.existenDetalles").setChecked(true);
				}

				DomNode professionType = formEmployee
						.querySelector("select[name=\"orDatosTrabajador.csTipoProfesion.valor\"]");
				if (professionType != null && certificates.getCatProfessional() != null) {
					((HtmlSelect) professionType).setSelectedAttribute(certificates.getCatProfessional(), true);
				}

				if (certificates.getPublicPosition() != null) {
					((HtmlSelect) formEmployee
							.querySelector("select[name=\"orDatosTrabajador.csTipoCargoPublicoOSindical.valor\"]"))
							.setSelectedAttribute(certificates.getPublicPosition().getValue().toString(), true);

					certificates.getDedicationPer().ifPresent(d -> {
						formEmployee.getInputByName("orDatosTrabajador.srPorcentualDedicacion").setValue(d.toString());
						formEmployee.getInputByName("orDatosTrabajador.srPorcentualDedicacion").setValueAttribute(d.toString());
					});
				}
				
				htmlPage = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				
				handleSepeExceptions(htmlPage);
				
			}

			{// DATA SUSPENSION OR TERMINATION
				HtmlForm formTermination = (HtmlForm) HtmlUnitToolkit
						.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				
				if (certificates.getCauseSuspension() != null)
					formTermination.getSelectByName("orDatosTrabajador.csCausaSuspension.valor").setSelectedAttribute(certificates.getCauseSuspension(), true);
				
				formTermination.getInputByName("orDatosTrabajador.srDiaFechaAlta").setValue(fAE[0]);
				formTermination.getInputByName("orDatosTrabajador.srMesFechaAlta").setValue(fAE[1]);
				formTermination.getInputByName("orDatosTrabajador.srAnyoFechaAlta").setValue(fAE[2]);
				
				formTermination.getInputByName("orDatosTrabajador.srDiaFechaInicioSuspension").setValue(fST[0]);
				formTermination.getInputByName("orDatosTrabajador.srMesFechaInicioSuspension").setValue(fST[1]);
				formTermination.getInputByName("orDatosTrabajador.srAnyoFechaInicioSuspension").setValue(fST[2]);
				
				htmlPage = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}

			{// DATA CONTINGENCIES
				{// DATA CTZ
					for (QuoteData qdata : certificates.getQuoteData()) {
					
						HtmlForm formContingence = (HtmlForm) HtmlUnitToolkit
								.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
						
						if(certificates.getRegimen().equals("0163")) {
							HtmlInput yearCtzInput = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionREAPre.srAnyoCotizacion");
							yearCtzInput.setValue(qdata.getAnio().toString());
							
							HtmlInput monthCtzInput = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionREAPre.srMesCotizacion");
							monthCtzInput.setValue(qdata.getMonth().toString());
							
							HtmlInput dayCtzInput = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionREAPre.srMesesCotizados");
							dayCtzInput.setValue(qdata.getDays().toString());
							
							qdata.getBcd().ifPresent(d -> {
								HtmlInput input = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionREAPre.srCotizacionDesempleo");

								if (input != null)
									input.setValue(decimalFormat.format(d));
								
							});
						} else {
							HtmlInput yearCtzInput = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srAnyoCotizacion");
							yearCtzInput.setValue(qdata.getAnio().toString());
							
							HtmlInput monthCtzInput = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srMesCotizacion");
							monthCtzInput.setValue(qdata.getMonth().toString());
							
							HtmlInput dayCtzInput = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srDiasCotizacion");
							dayCtzInput.setValue(qdata.getDays().toString());
							
							qdata.getBccc().ifPresent(d -> 
								formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasComunes").setValue(decimalFormat.format(d))
							);
							
							qdata.getBcd().ifPresent(d -> {
								HtmlInput input = formContingence.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasDesempleo");

								if (input != null)
									input.setValue(decimalFormat.format(d));
								
							});
						}

						htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btAnadir]")).click();
						
					}

					handleSepeExceptions(htmlPage);
				}

				{// DATA VACATION
					HtmlForm formVacation = (HtmlForm) HtmlUnitToolkit
							.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
					if(certificates.getDaysCtzVc() != null && certificates.getDaysCtzVc() != 0) {
						
						if(certificates.getRegimen().equals("0163")) {
							if (certificates.getDaysCtzVc() != null) {
								HtmlInput input = formVacation.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionREAVacacionesPre.srMesesCotizados");
								
								if (input != null)
									input.setValue(certificates.getDaysCtzVc().toString());
							}
							
							certificates.getBcdVc().ifPresent(d -> {
								HtmlInput input = formVacation.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionREAVacacionesPre.srCotizacionDesempleo");
								
								if (input != null) input.setValue(decimalFormat.format(d));
								
							});
						} else {
							if (certificates.getDaysCtzVc() != null) {
								HtmlInput input = formVacation.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srDiasCotizacion");

								if (input != null)
									input.setValue(certificates.getDaysCtzVc().toString());
							}
							
							certificates.getBcccVc().ifPresent(d ->
								formVacation.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasComunes").setValue(decimalFormat.format(d))
							);
							
							certificates.getBcdVc().ifPresent(d -> {
								HtmlInput input = formVacation.getInputByName("orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasDesempleo");

								if (input != null) input.setValue(decimalFormat.format(d));
							});
						}
					}
					
					htmlPage = ((HtmlSubmitInput) htmlPage
							.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btActualizarTotales]"))
							.click();
					
					handleSepeExceptions(htmlPage);
				}

				Page page = ((HtmlSubmitInput) htmlPage.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				
				if (page.isHtmlPage()) {
					handleSepeExceptions((HtmlPage) page);
					throw new SepeException("No se ha podido comunicar el Certific@2, intentelo mas tarde");
				} else {
					try {
						return page.getWebResponse().getContentAsStream().readAllBytes();
					} catch (Exception e) {
						throw new InvalidDataException();
					}
				}
			}
		}
	}

	private static HtmlPage firstPageSepeCert(WebClient webClient)
			throws SepeException, IOException, InterruptedException {
		
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
		
		Page page = null;
		Integer maxAttemps = 10;
		Integer i = 0;
		
		while (!(page instanceof HtmlPage) && i < maxAttemps) {
			System.out.println("ATTEMPT " + (i + 1));
			try {
				page = getPageFirstProcess(webClient);
			} catch (FailingHttpStatusCodeException e) {
				System.out.println("I do not load the page, retrying!");
			}
			
			i++;
			Thread.sleep(1000);
		}
		HtmlPage htmlPage = (HtmlPage) page;
		HtmlUnitToolkit.manageStatusCode(htmlPage);
		handleExceptionsErrors(htmlPage);
		
		return htmlPage;
	}

	private static Page getPageFirstProcess(WebClient webClient) throws FailingHttpStatusCodeException, IOException, SepeException, InterruptedException {

		HtmlPage htmlPage = webClient.getPage(
				"https://isweb.sepe.gob.es/GetAccess/Saml/SSO/Init?GAURI=https%3A%2F%2Fsede.sepe.gob.es%2FDCertificadosWeb%2FActionNavegacion.do%3FaccesoGA%3Dempresas%26accion%3Dnavegacion&GA_SAML_AC_COMPARISON=minimum&GA_SAML_IS_PASSIVE=false&GA_SAML_AC_CLASS_REF=http%3A%2F%2Feidas.europa.eu%2FLoA%2Flow&GA_SAML_PROVIDER=Q2819009H_E00142804&GA_SAML_IDP=https%3A%2F%2Fpasarela.clave.gob.es%2FProxy2");

		HtmlUnitToolkit.manageStatusCode(htmlPage);
		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("idpRedirect")).orElseThrow();
		formDatos.getInputByName("SelectedIdP").setValue("AFIRMA");
		// create submit
		HtmlElement button = HtmlUnitToolkit.createButton(htmlPage);

		formDatos.appendChild(button);

		return button.click();
	}

	private static Page getPageFirstPdf(Page page) throws SepeException, IOException {
		if (page.isHtmlPage()) {
			HtmlPage htmlPage = (HtmlPage) page;
			handleSepeExceptions(htmlPage);
			DomNode inputRadio2 = htmlPage.querySelector("#contenido form input[name=documentoSeleccionado][value=\"0\"]");
			if (inputRadio2 != null) {
				((HtmlRadioButtonInput) inputRadio2).click();
				page = htmlPage.getElementByName("btMostrar").click();
			}
		}
		return page;
	}

	private static void handleSepeExceptions(HtmlPage htmlPage) throws SepeException {
		try {
			DomNode error = htmlPage.querySelector("#contenido > form > p.formAviso");
			if (error != null && !error.getVisibleText().isEmpty())
				throw new SepeException(error.getVisibleText());
		} catch (NullPointerException e) {}
		
		try {
			DomNode warningMessage = htmlPage.querySelector("#contenido .msj_advertencia > p");
			if(warningMessage != null && !warningMessage.getVisibleText().isEmpty())
				throw new SepeException(warningMessage.getVisibleText());
		} catch (NullPointerException e) {}
	}
	
	
	private static void handleExceptionsErrors(HtmlPage htmlPage) throws SepeException {
		try {
			DomNode error = htmlPage.querySelector("#content > div > div.panel-body > .alert");
			if (error != null && !error.getVisibleText().isEmpty() && error.getVisibleText().toLowerCase().contains("please contact your system")) {				
				throw new SepeException("Certificado revocado o no v\u00e1lido");
			}
		} catch (NullPointerException e) {}
	}
}
