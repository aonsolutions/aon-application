package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import aon.sepe.exceptions.invalidData.InvalidDataException;
import aon.sepe.objects.Certificates;
import aon.sepe.objects.QuoteData;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Certificado {

//	Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/Documentos/testCertificates.html");

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

			HtmlForm formDatos1 = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.querySelector("#contenido > form"))
					.orElseThrow();
			formDatos1.getInputByName("nif").setValueAttribute(nif);
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
									columnCheck = firstColumn.getValueAttribute();
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

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

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
				formEnterprise.getInputByName("orDatosEmpresa.srCCCRegimenCot")
						.setValueAttribute(certificates.getRegimen());
				formEnterprise.getInputByName("orDatosEmpresa.srCCCProvincia")
						.setValueAttribute(ctaCti.substring(0, 2));
				formEnterprise.getInputByName("orDatosEmpresa.srCCCSecuencial")
						.setValueAttribute(ctaCti.substring(2, 9));
				formEnterprise.getInputByName("orDatosEmpresa.srCCCDC").setValueAttribute(ctaCti.substring(9));
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
				formRepresentative.getInputByName("orDatosRepresentante.srNombreRepresentante")
						.setValueAttribute(certificates.getNameManager());
				formRepresentative.getInputByName("orDatosRepresentante.srPrimerApellidoRepresentante")
						.setValueAttribute(certificates.getSurnameManager());

				((HtmlSelect) formRepresentative
						.querySelector("select[name=\"orDatosRepresentante.srTipoDocRepresentante\"]"))
						.setSelectedAttribute(tipodocManager, true);

				formRepresentative.getInputByName("orDatosRepresentante.srNifRepresentante")
						.setValueAttribute(ipfManager);

				certificates.getLastSurnameManager().ifPresent(d -> {
					DomNode input = formRepresentative
							.querySelector("[name=\"orDatosRepresentante.srSegundoApellidoRepresentante\"]");
					if (input != null)
						((HtmlInput) input).setValueAttribute(d);
				});

				certificates.getCargoManager().ifPresent(d -> {
					DomNode input = formRepresentative
							.querySelector("[name=\"orDatosRepresentante.srCargoRepresentante\"]");
					if (input != null)
						((HtmlInput) input).setValueAttribute(d);
				});

				htmlPage = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}

			{// DATA EMPLOYEE
				HtmlForm formEmployee = (HtmlForm) HtmlUnitToolkit
						.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();

				certificates.getEmployeeName().ifPresent(d -> {
					DomNode input = formEmployee.querySelector("[name=\"orDatosTrabajador.srNombreTrabajador\"]");
					if (input != null)
						((HtmlInput) input).setValueAttribute(d);
				});

				certificates.getEmployeeSurname().ifPresent(d -> {
					DomNode input = formEmployee
							.querySelector("[name=\"orDatosTrabajador.srPrimerApellidoTrabajador\"]");
					if (input != null)
						((HtmlInput) input).setValueAttribute(d);
				});

				certificates.getEmployeeSecondSurname().ifPresent(d -> {
					DomNode input = formEmployee
							.querySelector("[name=\"orDatosTrabajador.srSegundoApellidoTrabajador\"]");
					if (input != null)
						((HtmlInput) input).setValueAttribute(d);
				});

				certificates.getNaf().ifPresent(d -> {
					DomNode input = formEmployee.querySelector("[name=\"orDatosTrabajador.srNumSSTrabajador\"]");
					if (input != null)
						((HtmlInput) input).setValueAttribute(d);
				});

				DomNode duration = formEmployee.querySelector("[name=\"orDatosTrabajador.srDuracionContratoTrab\"]");
				if (duration != null && certificates.getDurationContract() != null) {
					((HtmlInput) duration).setValueAttribute(certificates.getDurationContract().toString());
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

				DomNode durationType = formEmployee
						.querySelector("select[name=\"orDatosTrabajador.csIndicadorDuracionContrato.valor\"]");
				if (durationType != null && certificates.getTypeDuration() != null) {
					((HtmlSelect) durationType).setSelectedAttribute(certificates.getTypeDuration().getValue(), true);
				}

				DomNode professionType = formEmployee
						.querySelector("select[name=\"orDatosTrabajador.csTipoProfesion.valor\"]");
				if (professionType != null && certificates.getCatProfessional() != null) {
					((HtmlSelect) professionType).setSelectedAttribute(certificates.getCatProfessional(), true);
				}

				if (Arrays.asList("2", "5").contains(typeContract.substring(0, 1))) {
					formEmployee.getInputByName("orDatosTrabajador.existenDetalles").setChecked(true);
				}

				if (certificates.getPublicPosition() != null) {
					((HtmlSelect) formEmployee
							.querySelector("select[name=\"orDatosTrabajador.csTipoCargoPublicoOSindical.valor\"]"))
							.setSelectedAttribute(certificates.getPublicPosition().getValue().toString(), true);

					certificates.getDedicationPer().ifPresent(d -> {
						DomNode input = formEmployee
								.querySelector("[name=\"orDatosTrabajador.srPorcentualDedicacion\"]");
						if (input != null)
							((HtmlInput) input).setValueAttribute(d.toString());
					});
				}
				htmlPage = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}

			{// DATA SUSPENSION OR TERMINATION
				HtmlForm formTermination = (HtmlForm) HtmlUnitToolkit
						.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
				if (certificates.getCauseSuspension() != null) {
					((HtmlSelect) formTermination
							.querySelector("select[name=\"orDatosTrabajador.csCausaSuspension.valor\"]"))
							.setSelectedAttribute(certificates.getCauseSuspension(), true);
				}
				formTermination.getInputByName("orDatosTrabajador.srDiaFechaAlta").setValueAttribute(fAE[0]);
				formTermination.getInputByName("orDatosTrabajador.srMesFechaAlta").setValueAttribute(fAE[1]);
				formTermination.getInputByName("orDatosTrabajador.srAnyoFechaAlta").setValueAttribute(fAE[2]);
				formTermination.getInputByName("orDatosTrabajador.srDiaFechaInicioSuspension")
						.setValueAttribute(fST[0]);
				formTermination.getInputByName("orDatosTrabajador.srMesFechaInicioSuspension")
						.setValueAttribute(fST[1]);
				formTermination.getInputByName("orDatosTrabajador.srAnyoFechaInicioSuspension")
						.setValueAttribute(fST[2]);

				htmlPage = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();
				handleSepeExceptions(htmlPage);
			}

			{// DATA CONTINGENCIES
				{// DATA CTZ
					for (QuoteData qdata : certificates.getQuoteData()) {
						HtmlForm formContingence = (HtmlForm) HtmlUnitToolkit
								.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();

						DomNode yearCtz = formContingence.querySelector(
								"[name=\"orDatosTrabajador.orDatosInsercionCotizacionPre.srAnyoCotizacion\"]");
						if (yearCtz == null) {
							yearCtz = formContingence.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionREAPre.srAnyoCotizacion\"]");
						}
						((HtmlInput) yearCtz).setValueAttribute(qdata.getAnio().toString());

						DomNode monthCtz = formContingence.querySelector(
								"[name=\"orDatosTrabajador.orDatosInsercionCotizacionPre.srMesCotizacion\"]");
						if (monthCtz == null) {
							monthCtz = formContingence.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionREAPre.srMesCotizacion\"]");
						}
						((HtmlInput) monthCtz).setValueAttribute(qdata.getMonth().toString());

						DomNode dayCtz = formContingence.querySelector(
								"[name=\"orDatosTrabajador.orDatosInsercionCotizacionPre.srDiasCotizacion\"]");
						if (dayCtz == null) {
							dayCtz = formContingence.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionREAPre.srMesesCotizados\"]");
						}
						((HtmlInput) dayCtz).setValueAttribute(qdata.getDays().toString());

						qdata.getBccc().ifPresent(d -> {
							DomNode input = formContingence.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasComunes\"]");
							if (input != null) {
								((HtmlInput) input).setValueAttribute(decimalFormat.format(d));
							}
						});

						qdata.getBcd().ifPresent(d -> {
							DomNode input = formContingence.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionPre.srBaseContingenciasDesempleo\"]");
							if (input == null) {
								input = formContingence.querySelector(
										"[name=\"orDatosTrabajador.orDatosInsercionCotizacionREAPre.srCotizacionDesempleo\"]");
							}
							if (input != null) {
								((HtmlInput) input).setValueAttribute(decimalFormat.format(d));
							}
						});

						htmlPage = ((HtmlSubmitInput) htmlPage
								.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btAnadir]")).click();
					}
					handleSepeExceptions(htmlPage);
				}

				{// DATA VACATION
					HtmlForm formVacation = (HtmlForm) HtmlUnitToolkit
							.wait4(htmlPage, p -> p.querySelector("#BeanMecanizacionOLIPre")).orElseThrow();
					if (certificates.getDaysCtzVc() != null) {
						DomNode input = formVacation.querySelector(
								"[name=\"orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srDiasCotizacion\"]");
						if (input == null) {
							input = formVacation.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionREAVacacionesPre.srMesesCotizados\"]");
						}
						if (input != null) {
							((HtmlInput) input).setValueAttribute(certificates.getDaysCtzVc().toString());
						}
					}

					certificates.getBcccVc().ifPresent(d -> {
						DomNode input = formVacation.querySelector(
								"[name=\"orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasComunes\"]");
						if (input != null) {
							((HtmlInput) input).setValueAttribute(decimalFormat.format(d));
						}
					});

					certificates.getBcdVc().ifPresent(d -> {
						DomNode input = formVacation.querySelector(
								"[name=\"orDatosTrabajador.orDatosInsercionCotizacionVacacionesPre.srBaseContingenciasDesempleo\"]");
						if (input == null) {
							input = formVacation.querySelector(
									"[name=\"orDatosTrabajador.orDatosInsercionCotizacionREAVacacionesPre.srCotizacionDesempleo\"]");
						}
						if (input != null) {
							((HtmlInput) input).setValueAttribute(decimalFormat.format(d));
						}
					});

					htmlPage = ((HtmlSubmitInput) htmlPage
							.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btActualizarTotales]"))
							.click();
					handleSepeExceptions(htmlPage);
				}

				Page page = ((HtmlSubmitInput) htmlPage
						.querySelector("form[name=BeanMecanizacionOLIPre] input[name=btSiguiente]")).click();

				if (page.isHtmlPage()) {
					handleSepeExceptions((HtmlPage) page);
				} else {
					try {
						return page.getWebResponse().getContentAsStream().readAllBytes();
					} catch (Exception e) {
						throw new InvalidDataException();
					}
				}
			}
			System.out.println("END");
		}
		return null;
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
		formDatos.getInputByName("SelectedIdP").setValueAttribute("AFIRMA");
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
		} catch (NullPointerException e) {
		}
	}
	
	
	private static void handleExceptionsErrors(HtmlPage htmlPage) throws SepeException {
		try {
			DomNode error = htmlPage.querySelector("#content > div > div.panel-body > .alert");
			if (error != null && !error.getVisibleText().isEmpty() && error.getVisibleText().toLowerCase().contains("please contact your system"))
				throw new SepeException("Certificado revocado o no v\u00e1lido");
		} catch (NullPointerException e) {
		}
	}
}
