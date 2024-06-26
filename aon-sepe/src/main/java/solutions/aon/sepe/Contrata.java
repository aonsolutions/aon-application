package solutions.aon.sepe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.htmlunit.CollectingAlertHandler;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.WebWindowEvent;
import org.htmlunit.WebWindowListener;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlLegend;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.javascript.JavaScriptErrorListener;
import org.htmlunit.util.WebConnectionWrapper;

import aon.sepe.exceptions.invalidData.InvalidDataException;
import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.ContractBuilder;
import aon.sepe.objects.Contract.ContractDetail;
import aon.sepe.objects.Contract.DetailType;
import aon.sepe.objects.Contract.Over52Years;
import aon.sepe.objects.Contract.SexType;
import aon.sepe.objects.ContractExtension;
import aon.sepe.objects.CopyBasic;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.exceptions.certificate.InvalidCertificateException;
import solutions.aon.sepe.exceptions.statusCode.StatusCodeException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrata {

	// Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/test.html");

	private static final String MESSAGE_ERROR  = "Error no aceptada la comunicaci\u00f3n";
	private static final String FORMAT_DATE_ES = "dd/MM/yyyy";
	private static final String RETURN_INIT    = "returnInit";
	
	private Contrata() {
		throw new IllegalStateException("Utility class");
	}
	
	public static void sendLlamamiento(final InputStream certificateInputStream, final String certificatePassword, final String certificateType, String cif, String ccc,
			String nif, Date startDate, Date endDate, String ide) throws SepeException {
		try {
			sendLlamamientoImpl(certificateInputStream, certificatePassword, certificateType, cif, ccc, nif, startDate, endDate, ide);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static String sendContrata(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Contract cto) throws SepeException {
		try {
			return sendContrataImpl(certificateInputStream, certificatePassword, certificateType, cto);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
		return null;
	}

	public static String sendContrataExtension(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, ContractExtension contractExtension)
			throws SepeException {
		try {
			return sendContrataExtensionImpl(certificateInputStream, certificatePassword, certificateType,
					contractExtension);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
		return null;
	}

	public static void sendTransformation(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Contract cto, CopyBasic copyBasic) throws SepeException {
		try {
			sendTransformationImpl(certificateInputStream, certificatePassword, certificateType, cto, copyBasic);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static String sendCopyBasic(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, CopyBasic copyBasic) throws SepeException {
		try {
			return sendCopyBasicImpl(certificateInputStream, certificatePassword, certificateType, copyBasic);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
		return null;
	}

	public static String sendTransformationCopyBasic(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, CopyBasic copyBasic, String cif,
			Date startDate) throws SepeException {
		try {
			return sendTransformationCopyBasicImpl(certificateInputStream, certificatePassword, certificateType,
					copyBasic, cif, startDate);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
		return null;
	}

	public static byte[] getContratoPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId)
			throws SepeException {
		try {
			return getContratoPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, startDate,
					endDate, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (InterruptedException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId)
			throws SepeException {
		try {
			return getCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, startDate,
					endDate, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static byte[] getTransformationPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif, Date startDate,
			Optional<String> sepeId) throws SepeException {
		try {
			return getTransformationPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, cif,
					startDate, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static byte[] getContractExtensionPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif, Date startDate,
			Integer nprorroga, Optional<String> sepeId) throws SepeException {
		try {
			return getContractExtensionPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, cif,
					startDate, nprorroga, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static byte[] getTransformationCopyBasicPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif, Date startDate,
			Optional<String> sepeId) throws SepeException {
		try {
			return getTransformationCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf,
					cif, startDate, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			throw new SepeException(e);
		}
	}

	public static void validateCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SepeException {
		try {
			validateCertImpl(certificateInputStream, certificatePassword, certificateType);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static Contract getContractData(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate) throws SepeException {
		try {
			return getContractDataImpl(certificateInputStream, certificatePassword, certificateType, ipf, startDate,
					endDate);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e.getMessage());
		}
		return null;
	}

	public static Contract getTransformationData(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif,
			Date oldDateIniContract, Optional<String> sepeId) throws SepeException {
		try {
			return getTransformationDataImpl(certificateInputStream, certificatePassword, certificateType, ipf, cif,
					oldDateIniContract, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e.getMessage());
		}
		return null;
	}

	// TODO
	public static Contract getContractExtensionData(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif,
			Date oldDateIniContract, Optional<String> sepeId) throws SepeException {
		try {
			return getContractExtensionDataImpl(certificateInputStream, certificatePassword, certificateType, ipf, cif,
					oldDateIniContract, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e.getMessage());
		}
		return null;
	}

	public static void removeContrato(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String sepeId) throws SepeException {
		try {
			removeContrataImpl(certificateInputStream, certificatePassword, certificateType, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static void removeTransformation(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String sepeId) throws SepeException {
		try {
			removeTransformationImpl(certificateInputStream, certificatePassword, certificateType, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}

	public static void removeContractExtension(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String sepeId) throws SepeException {
		try {
			removeContractExtensionImpl(certificateInputStream, certificatePassword, certificateType, sepeId);
		} catch (FailingHttpStatusCodeException e) {
			throw new SepeException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SepeException(e);
		}
	}
	
	private static void sendLlamamientoImpl(final InputStream certificateInputStream, final String certificatePassword, final String certificateType, String cif, String ccc,
			String nif, Date startDate, Date endDate, String ide) throws SepeException, FailingHttpStatusCodeException,
			InterruptedException, IOException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);

			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletLlamamientos?pagina=inicio").click();
			handleSepeExceptions(htmlPage);

			
//			String[] fB = Toolkit.dateString(endDate);
			
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			
			{// -------------------------DATA ENTERPRISE----------------------------
				((HtmlSelect) form.querySelector("select[name=tipodocumentoaux]")).setSelectedAttribute(getCifType(cif), true);
				form.getInputByName("cifnifnie").setValue(cif);
				
				form.getInputByName("cuentacotizacion").setValue(ccc.substring(0, 4));
				form.getInputByName("cuentacotizacion1").setValue(ccc.substring(4, 6));
				form.getInputByName("cuentacotizacion2").setValue(ccc.substring(6, 13));
				form.getInputByName("cuentacotizacion3").setValue(ccc.substring(13));
			}
			
			{// -------------------------DATA LLAMAMIENTO----------------------------
				String[] fA = Toolkit.dateString(startDate);
				
				String tipodoc = Toolkit.getIdentityType(nif).equals("6") ? "E" : "D"; //NIE OR DNI
				((HtmlSelect) form.querySelector("select[name=tipodocumento0]")).setSelectedAttribute(tipodoc, true);
				
				form.getInputByName("nifnietrabajador0").setValue(nif);
				
				form.getInputByName("diainicio0").setValue(fA[0]);
				form.getInputByName("mesinicio0").setValue(fA[1]);
				form.getInputByName("annoinicio0").setValue(fA[2]);
				
				if(null != endDate) {
					String[] fB = Toolkit.dateString(endDate);
					
					form.getInputByName("diafin0").setValue(fB[0]);
					form.getInputByName("mesfin0").setValue(fB[1]);
					form.getInputByName("annofin0").setValue(fB[2]);
				}
				
				if(null != ide) {
					form.getInputByName("tipoidcontrato0").setValue(ide.substring(0, 1));
					form.getInputByName("provinciaid0").setValue(ide.substring(1, 3));
					form.getInputByName("a�oid0").setValue(ide.substring(3, 7));
					form.getInputByName("numeroid0").setValue(ide.substring(7, 14));
					form.getInputByName("transformaid0").setValue(ide.substring(14));
				}
				
				((HtmlSelect) form.querySelector("select[name=preguntaactividad0]")).setSelectedAttribute("S", true);
			}
			
			htmlPage = ((HtmlSubmitInput) form.querySelector("[name=enviar]")).click();
			handleSepeAlert(alertHandler.getCollectedAlerts());
			
			// TODO: ver que hay en la siguiente pantalla
		}
	}

	private static String sendContrataImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, Contract cto) throws SepeException, FailingHttpStatusCodeException,
			InterruptedException, IOException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setPopupBlockerEnabled(false);

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
			
			Integer page = 1;
			
			String[] startDate = Toolkit.dateString(cto.getDateIniContract());
			String[] now = Toolkit.dateString(new Date());

			try {
				HtmlSelect codContract = ((HtmlSelect) htmlPage.querySelector("select[name=codcontrato]"));
				codContract.getOptionByValue(contract).setSelected(true);
			} catch (ElementNotFoundException e) {
				throw new SepeException("Contrato " + contract + " no soportado");
			}

			HtmlSubmitInput sb = htmlPage.querySelector("#enviar");
			htmlPage = sb.click();
			handleSepeExceptions(htmlPage);

			DomNode text = htmlPage.querySelector(".tac.azneg");
			if (text != null && text.getVisibleText().indexOf("Desea comunicar el contrato") >= 0) {
				htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("#enviar")).click();
				handleSepeExceptions(htmlPage);
			}

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			
			form.getInputByName("hoy").setValue(now[0]+"/"+now[1]+"/"+now[2]);
			form.getInputByName("DIA").setValue(now[0]);
			form.getInputByName("MES").setValue(now[1]);
			form.getInputByName("ANYO").setValue(now[2]);

			{// -------------------------DATA ENTERPRISE----------------------------
				String ctaCti = cto.getCtaCti();
				String regimen = cto.getRegimen();
				String cif = cto.getCifEnterprise();
				
				if (cif!= null) {
					((HtmlSelect) form.querySelector("select[name=tipodoc2]")).setSelectedAttribute(getCifType(cif), true);
					
					form.getInputByName("cifnif").setValueAttribute(cif);
					form.getInputByName("cifnif").setValue(cif);
				}

				form.getInputByName("regimen").setValue(regimen);
				form.getInputByName("numsecprov").setValue(ctaCti.substring(0, 2));
				form.getInputByName("numsec").setValue(ctaCti.substring(2, 9));
				form.getInputByName("digcont").setValue(ctaCti.substring(9));
				form.getInputByName("cuentacotizacion").setValue(regimen + ctaCti);
				
				form.getInputByName("regimen").setValueAttribute(regimen);
				form.getInputByName("numsecprov").setValueAttribute(ctaCti.substring(0, 2));
				form.getInputByName("numsec").setValueAttribute(ctaCti.substring(2, 9));
				form.getInputByName("digcont").setValueAttribute(ctaCti.substring(9));
				form.getInputByName("cuentacotizacion").setValueAttribute(regimen + ctaCti);
			}

			{// ---------------------------DATA EMPLOYEE-------------------------
				String idetType = Toolkit.getIdentityType(cto.getIpf());
				String tipodoc = idetType.length() == 0 ? "U" : (idetType.equals("6") ? "E" : "D"); //NIE OR DNI

				String nss = cto.getNss();
				((HtmlSelect) form.querySelector("select[name=tipodoc]")).setSelectedAttribute(tipodoc, true);
				form.getInputByName("nif").setValue(cto.getIpf());
				form.getInputByName("nifnie").setValue(tipodoc + "  " + cto.getIpf());
				form.getInputByName("nif").setValueAttribute(cto.getIpf());
				form.getInputByName("nifnie").setValueAttribute(tipodoc + "  " + cto.getIpf());
        
				String name = cto.getName();
				if(null != name && name.length() > 15) name = name.substring(0, 15);
				form.getInputByName("nombre").setValue(name);
				form.getInputByName("apellido1").setValue(cto.getSurname());
				form.getInputByName("nombre").setValueAttribute(name);
				form.getInputByName("apellido1").setValueAttribute(cto.getSurname());

				if (cto.getLastSurname() != null) {
					form.getInputByName("apellido2").setValue(cto.getLastSurname());
					form.getInputByName("apellido2").setValueAttribute(cto.getLastSurname());
				}

				if (cto.getSex() != null) {
					((HtmlSelect) form.querySelector("select[name=codsexo]"))
							.setSelectedAttribute(cto.getSex().getValue().toString(), true);// SELECT
																							// ("-1"=>"","1"=>"HOMBRE","2"=>"MUJER")
				}

				if (cto.getDateBirth() != null) {
					String[] dateBirth = Toolkit.dateString(cto.getDateBirth());
					form.getInputByName("diafechanac").setValue(dateBirth[0]);
					form.getInputByName("mesfechanac").setValue(dateBirth[1]);
					form.getInputByName("anniofechanac").setValue(dateBirth[2]);
					form.getInputByName("fechanacimiento").setValue(dateBirth[0]+"/"+dateBirth[1]+"/"+dateBirth[2]);
					form.getInputByName("diafechanac").setValueAttribute(dateBirth[0]);
					form.getInputByName("mesfechanac").setValueAttribute(dateBirth[1]);
					form.getInputByName("anniofechanac").setValueAttribute(dateBirth[2]);
					form.getInputByName("fechanacimiento").setValueAttribute(dateBirth[0]+"/"+dateBirth[1]+"/"+dateBirth[2]);
				}

				((HtmlSelect) form.querySelector("select[name=nacionalidad]"))
						.setSelectedAttribute(cto.getCodNationality().toString(), true);
				((HtmlSelect) form.querySelector("select[name=codpaisdomicilio]"))
						.setSelectedAttribute(cto.getCodPaisDom().toString(), true);

				if (cto.getCodMunDom() != null) {
					form.getInputByName("municipio").setValue(cto.getCodMunDom());
					form.getInputByName("municipio").setValueAttribute(cto.getCodMunDom());
				}

				form.getInputByName("nass1").setValue(nss.substring(0, 2));
				form.getInputByName("nass2").setValue(nss.substring(2, 10));
				form.getInputByName("nass3").setValue(nss.substring(10));
				form.getInputByName("nass").setValue(nss);
				
				form.getInputByName("nass1").setValueAttribute(nss.substring(0, 2));
				form.getInputByName("nass2").setValueAttribute(nss.substring(2, 10));
				form.getInputByName("nass3").setValueAttribute(nss.substring(10));
				form.getInputByName("nass").setValueAttribute(nss);
			}

			{// ----------------------DATA CONTRACT--------------------
				form.getInputByName("contratoEscrito").setValue("N"); // contratoEscrito si la fecha fin es menor a 28
				form.getInputByName("fechainicio").setValue(startDate[0]+"/"+startDate[1]+"/"+startDate[2]);
				form.getInputByName("diafechaini").setValue(startDate[0]);
				form.getInputByName("mesfechaini").setValue(startDate[1]);
				form.getInputByName("anniofechaini").setValue(startDate[2]);
				form.getInputByName("fechainicio").setValueAttribute(startDate[0]+"/"+startDate[1]+"/"+startDate[2]);
				form.getInputByName("diafechaini").setValueAttribute(startDate[0]);
				form.getInputByName("mesfechaini").setValueAttribute(startDate[1]);
				form.getInputByName("anniofechaini").setValueAttribute(startDate[2]);

				// NIVEL FORMATIVO
				if (cto.getCodFormativo() != null && cto.getCodFormativo() > 0) {
					htmlPage = ((HtmlSelect) form.querySelector("select[name=codnivelformativo]"))
							.setSelectedAttribute(cto.getCodFormativo().toString(), true);
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
				}

				setOccupation(cto, form);

				if (cto.getCodPaisWork() != null) {
					((HtmlSelect) form.querySelector("select[name=codpais]"))
							.setSelectedAttribute(cto.getCodPaisWork().toString(), true);
				}

				if (cto.getCodMunWork() != null) {
					form.getInputByName("municipiocontrato").setValue(cto.getCodMunWork());// disabled
					form.getInputByName("municipiocontrato").setValueAttribute(cto.getCodMunWork());// disabled
				}

				DomNode ofertaEmpleo = form.querySelector("select[name=procedeDeOfertaEmpleo]");
				if (ofertaEmpleo != null) {
					((HtmlSelect) ofertaEmpleo).setSelectedAttribute(cto.getOffer().getValue(), true);
				}
				
				DomNode planRecovery = form.querySelector("select[name=preguntaAcogePRTR]");
				if (planRecovery != null) {
					((HtmlSelect) planRecovery).setSelectedAttribute(cto.getPlanRecovery() ? "S": "N", true);
				}
			}

			{// --------------------OTHERS DATA CONTRACT (OPTIONAL)-----------------
				if (cto.getDateFinContract() != null) {
					DomNode endDay = form.querySelector("[name=\"diafechafin\"]");
					if (endDay != null) {
						String[] dateFinContract = Toolkit.dateString(cto.getDateFinContract());
						((HtmlInput) endDay).setValue(dateFinContract[0]);
						form.getInputByName("mesfechafin").setValue(dateFinContract[1]);
						form.getInputByName("anniofechafin").setValue(dateFinContract[2]);
						((HtmlInput) endDay).setValueAttribute(dateFinContract[0]);
						form.getInputByName("mesfechafin").setValueAttribute(dateFinContract[1]);
						form.getInputByName("anniofechafin").setValueAttribute(dateFinContract[2]);
					}
				}
				
				{ //------------------ DATA JORNADA--------------------
					DomNode tipoJornada = form.querySelector("select[name=codtipojornada]");
					if (cto.getJndType() != null && tipoJornada!=null) {
						((HtmlSelect) tipoJornada).setSelectedAttribute(cto.getJndType().getValue(), true);
						
						// ----------HORAS DE JORNADA
						DomNode jornadaHour = form.querySelector("[name=\"horasduracionjornada\"]");
						if (jornadaHour!=null && cto.getDurationTypeJndHour() != null) {
							((HtmlInput)jornadaHour).setValue(cto.getDurationTypeJndHour());
							((HtmlInput)jornadaHour).setValueAttribute(cto.getDurationTypeJndHour());
						}

						DomNode jornadaMin = form.querySelector("[name=\"minutosduracionjornada\"]");
						if (jornadaMin!=null && cto.getDurationTypeJndMin() != null) {
							((HtmlInput)jornadaMin).setValue(cto.getDurationTypeJndMin());
							((HtmlInput)jornadaMin).setValueAttribute(cto.getDurationTypeJndMin());
						}

						// ----------HORAS DE CONVENIO
						DomNode convenioHour = form.querySelector("[name=\"horasduracionconvenio\"]");
						if (convenioHour!=null && cto.getDurationTypeCvnHour() != null) {
							((HtmlInput)convenioHour).setValue(cto.getDurationTypeCvnHour());
							((HtmlInput)convenioHour).setValueAttribute(cto.getDurationTypeCvnHour());
						}
						
						DomNode convenioMin = form.querySelector("[name=\"minutosduracionconvenio\"]"); 
						if (convenioMin!=null && cto.getDurationTypeCvnMin() != null) {
							((HtmlInput)convenioMin).setValue(cto.getDurationTypeCvnMin());
							((HtmlInput)convenioMin).setValueAttribute(cto.getDurationTypeCvnMin());
						}
						
						// ---------- HORAS DE FORMACION
						DomNode formationHour = form.querySelector("[name=\"horasformacion\"]");
						if (formationHour!=null && cto.getDurationFormationHour() != null) {
							((HtmlInput)formationHour).setValue(cto.getDurationFormationHour());
							((HtmlInput)formationHour).setValueAttribute(cto.getDurationFormationHour());
						}
						
						DomNode formationMin = form.querySelector("[name=\"minutosformacion\"]");
						if (formationMin!=null && cto.getDurationFormationMin() != null) {
							((HtmlInput)formationMin).setValue(cto.getDurationFormationMin());
							((HtmlInput)formationMin).setValueAttribute(cto.getDurationFormationMin());
						}
					}
				}
			
				// Titulacion academica
				DomNode numNivelForm = form.querySelector("[name=\"numNivelForm\"]");
				if (numNivelForm != null) {
					((HtmlSelect) numNivelForm).setSelectedAttribute(cto.getCodFormativo().toString(), true);
				}

				// TITULACION
				Optional<String> titulacion = cto.getTitulacion();
				DomNode titulacionEl = form.querySelector("[name=\"titulacion\"]");
				if (titulacionEl != null && titulacion.isPresent()) {
					((HtmlSelect) titulacionEl).setSelectedAttribute(titulacion.get(), true);
				}

				// INTERINIDAD OR SUBSTITUTION
				Optional<String> interinidad = cto.getInterinidad();
				if (interinidad.isPresent()) {
					page++;
					form.getInputByName("pagina2").setValue(page.toString());
					form.getInputByName("pagina2").setValueAttribute(page.toString());

					HtmlCheckBoxInput check = (HtmlCheckBoxInput) form.getInputByName("checkInterinidad");
					check.click();
					
					htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
					handleSepeAlert(alertHandler.getCollectedAlerts());
					handleSepeExceptions(htmlPage);
					
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					
					// Claves menores de 30
					List<String> causaSustitucionMenor31 = new ArrayList<>(List.of("H", "G", "R", "F", "N", "I", "O"));
					if(causaSustitucionMenor31.contains(interinidad.get())) {
						((HtmlRadioButtonInput) form.querySelector("input[name=causaSustitucionMenor31]")).setAttribute("checked", "checked");
						htmlPage = ((HtmlRadioButtonInput) form.querySelector("input[name=causaSustitucionMenor31]")).click();
						
						((HtmlSelect) htmlPage.getElementById("cauIntLMenor31")).setSelectedAttribute(interinidad.get(), true);
						((HtmlSelect) form.querySelector("select[name=codobjetointerinidadMenor31]"))
							.setSelectedAttribute(interinidad.get(), true);
					} else {
						// Mayores de 31
						((HtmlRadioButtonInput) form.querySelector("input[name=causaSustitucionMayor31]")).setAttribute("checked", "checked");
						htmlPage = ((HtmlRadioButtonInput) form.querySelector("input[name=causaSustitucionMayor31]")).click();
						
						
						((HtmlSelect) htmlPage.getElementById("cauIntLMayor31")).setSelectedAttribute(interinidad.get(), true);
						((HtmlSelect) form.querySelector("select[name=codobjetointerinidadMayor31]"))
							.setSelectedAttribute(interinidad.get(), true);
					}
					
				}
			}
			
			{//------------------DATA CONTRACT SPECIFIC -----------------
				
				//------------DISCAPACIDAD
				if(cto.isDiscapacidad()) {
					page++;
					form.getInputByName("pagina2").setValue(page.toString());
					form.getInputByName("pagina2").setValueAttribute(page.toString());
					
					HtmlCheckBoxInput check = (HtmlCheckBoxInput) form.getInputByName("checkDiscapacidad");
					check.click();
				
					htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
					handleSepeAlert(alertHandler.getCollectedAlerts());
					handleSepeExceptions(htmlPage);
					
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					
					Optional<String> discapacidadType = cto.getDiscapacidadType();
					if(discapacidadType.isPresent()) {
						((HtmlSelect) form.querySelector("select[name=\"coddiscapacidad\"]")).setSelectedAttribute(discapacidadType.get(), true);
					}
					
					Optional<String> collectiveType = cto.getCollectiveType();
					if(collectiveType.isPresent()) {			
						String type = Integer.parseInt(collectiveType.get())+"";
						HtmlSelect codbonificaciondisca = (HtmlSelect) form.querySelector("select[name=\"codbonificaciondisca\"]");
						codbonificaciondisca.removeAttribute("disabled");
						codbonificaciondisca.setSelectedAttribute(type, true);
						
						htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
						handleSepeAlert(alertHandler.getCollectedAlerts());
		
						form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					}
				}
				
				//------------BONIFICACION
				if(cto.isBonus()) {
					page++;
					form.getInputByName("pagina2").setValue(page.toString());

					HtmlCheckBoxInput check = (HtmlCheckBoxInput) form.getInputByName("checkBonificacion");
					htmlPage = check.click();
						
//					htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
					handleSepeAlert(alertHandler.getCollectedAlerts());
					handleSepeExceptions(htmlPage);
					
					Optional<String> collectiveType = cto.getCollectiveType();
					if(collectiveType.isPresent()) {		
						String collective = collectiveType.get();
						
						outerLoop:
						for (final DomNode element : htmlPage.querySelectorAll(".fila")) {
							DomNode radioNode = element.querySelector("[type=radio]");
							
							if(radioNode!=null) {
								DomNode selectNode = element.querySelector("select");
								
								if(selectNode!=null) {
									HtmlSelect select = (HtmlSelect) selectNode;
									
									for (final HtmlOption option : select.getOptions()) {
										
										if(option.getValueAttribute().equals(collective)) {
											try { 
												((HtmlRadioButtonInput) radioNode).click();
												
												select.setSelectedAttribute(collective, true);
												break outerLoop;
											} catch (IOException e) { 
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
					
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
				}
				
				//------------MAYORES DE 52
				if(cto.getOver52Years().isPresent()) {
					page++;
					form.getInputByName("pagina2").setValue(page.toString());
					form.getInputByName("pagina2").setValueAttribute(page.toString());
					
					Over52Years over25Years = cto.getOver52Years().get();

					HtmlCheckBoxInput check = (HtmlCheckBoxInput) form.getInputByName("checkMayor52");
					check.click();
						
					htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
					handleSepeAlert(alertHandler.getCollectedAlerts());
					handleSepeExceptions(htmlPage);
					
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					
					HtmlCheckBoxInput checkTwo = (HtmlCheckBoxInput) form.getInputByName(over25Years.getValue());
					checkTwo.click();
					
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
				}
			}
			
			webClient.waitForBackgroundJavaScript(5000);
			htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();

			// For contract 502 check if duration equals or less than 90 days
			try {
				if((contract.equals("502") || contract.equals("402")) && htmlPage.querySelector("#avisos > div > p:last-child").getVisibleText().equals("1. Obligatorio indicar si el contrato tiene duraci�n igual o inferior a 90 d�as.")) {
					htmlPage = htmlPage.getElementById("volver").click();
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					
					if(null != cto.getDateFinContract()) {
						long daysBetween = ChronoUnit.DAYS.between(cto.getDateIniContract().toInstant(), cto.getDateFinContract().toInstant());
						((HtmlSelect) form.querySelector("select[name=preg90dias]")).setSelectedAttribute(daysBetween <= 90 ? "S" : "N", true);
					} else ((HtmlSelect) form.querySelector("select[name=preg90dias]")).setSelectedAttribute("N", true);
					
					setOccupation(cto, form);
					
					webClient.waitForBackgroundJavaScript(5000);
					htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
				}
			} catch (Exception e) {}
			
			// For contract 402 check if has writen contract
			
			
			try {
				if(contract.equals("402")) {
					webClient.waitForBackgroundJavaScript(5000);
					htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("[name=volver]")).click();
					
					form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
					setOccupation(cto, form);
					
					form.getInputByName("contratoEscrito").setValue(cto.isWrittenContract() ? "S" : "N");

					webClient.waitForBackgroundJavaScript(5000);
					htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("[name=aceptar]")).click();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			handleSepeAlert(alertHandler.getCollectedAlerts());
			handleSepeExceptions(htmlPage);
			
			String message = null;
			for (int i = 0; i < 3; i++) {
				message = getSuccessMessage(htmlPage);
				if (message == null || (message != null && message.indexOf("E") >= 0)) {
					break;
				} else if (message.contains(RETURN_INIT)) {
					htmlPage = goBackContract(htmlPage, cto);
				}
			}

			if (message != null && message.indexOf("E") >= 0) {
				message = message.substring(1);
			} else {
				handleSepeExceptions(htmlPage);
				throw new SepeException(MESSAGE_ERROR);
			}

			return message;
		}
	}

	private static Contract getContractDataImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date startDate, Date endDate)
			throws FailingHttpStatusCodeException, IOException, InterruptedException,
			SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
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
			
			List<DomNode> fieldsets = form.querySelectorAll("fieldset").stream().filter(fieldset-> fieldset.querySelector("legend")!=null)
					.collect(Collectors.toList());
			
			ContractDetail contractDetail = new ContractDetail().setType(DetailType.CONTRATO);
			ContractDetail transformationDetail = new ContractDetail().setType(DetailType.TRANSFORMACION);

			ContractBuilder builder = new ContractBuilder();
				
			// DATOS DE CONSULTA
			fieldsets.forEach(fieldset -> {
				DomNodeList<DomNode> titles = fieldset.querySelectorAll("div > div[class*=titulo]");
				HtmlLegend legend = (HtmlLegend)fieldset.querySelector("legend");
				String legendStr = legend.getVisibleText().toLowerCase();
				
				titles.forEach(title->{
					String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
					DomNode valueNode = Toolkit.getNextSibling(title);
					if (titleStr.length() > 0 && valueNode != null) {
						titleStr = titleStr.toLowerCase();
						String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
						if (valueStr.length() > 0) {
							if (titleStr.contains("cif")) {
								builder.setCifEnterprise(valueStr);
							} else if (titleStr.contains("cuenta de cotizaci\u00F3n")) {
								String newStr = Toolkit.noSpaces(valueStr);
								builder.setRegimen(newStr.substring(0, 4));
								builder.setCtaCti(newStr.substring(4));
							} else if (titleStr.contains("nif/nie")) {
								builder.setIpf(valueStr);
							} else if (titleStr.contains("fecha de nacimiento")) {
								builder.setDateBirth(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
							} else if (titleStr.contains("nombre / apellidos")) {
								builder.setName(valueStr);
							} else if (titleStr.contains("sexo")) {
								if (valueStr.contains("HOM")) {
									builder.setSex(SexType.HOMBRE);
								} else if (valueStr.contains("MUJ")) {
									builder.setSex(SexType.MUJER);
								}
							} else if (titleStr.contains("n\u00FCmero de afiliaci\u00F3n ss")) {
								builder.setNss(valueStr);
							} else if (titleStr.contains("identificador del contrato")) {
								String sepeId = Toolkit.noSpaces(valueStr.replace("-", "").substring(1));
								builder.setSepeId(sepeId);
								contractDetail.setSepeId(sepeId);
							} else if (titleStr.contains("identificador de la transformaci\u00f3n")) {
								transformationDetail.setSepeId(Toolkit.noSpaces(valueStr.replace("-", "").substring(1)));
							}
						}
					}
				});
		
				//-------------DATOS DEL CONTRATO
				if(legendStr.contains("datos del contrato")) {
					titles.forEach(title->{
						String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
						DomNode valueNode = Toolkit.getNextSibling(title);
						if (titleStr.length() > 0 && valueNode != null) {
							titleStr = titleStr.toLowerCase();
							String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
							if (valueStr.length() > 0) {
								if (titleStr.contains("fecha de inicio del contrato")) {
									Date start = Toolkit.parseDate(valueStr, FORMAT_DATE_ES);
									builder.setDateIniContract(start);
									contractDetail.setStartDate(start);
								} else if (titleStr.contains("fecha fin del contrato")) {
									Date end = Toolkit.parseDate(valueStr, FORMAT_DATE_ES);
									builder.setDateFinContract(end);
									contractDetail.setEndDate(end);
								} else if (titleStr.contains("fecha en que se comunica")) {
									Date comun = Toolkit.parseDate(valueStr, FORMAT_DATE_ES);
									builder.setDateComContract(comun);
									contractDetail.setCommunicationDate(comun);
								}
							}
						}
					});

				} else if(legendStr.contains("pr\u00f3rrogas del contrato")) {	//-----------PRORROGAS
					ContractDetail extension = new ContractDetail().setType(DetailType.PRORROGA);
					titles.forEach(title->{
						String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
						DomNode valueNode = Toolkit.getNextSibling(title);
						if (titleStr.length() > 0 && valueNode != null) {
							titleStr = titleStr.toLowerCase();
							String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
							if (valueStr.length() > 0) {
								if (titleStr.contains("prorroga") && valueStr.contains("E-")) {
									String sepeId = Toolkit.noSpaces(valueStr.replace("-", "").substring(1));
									extension.setSepeId(sepeId);
								} else if (titleStr.contains("fecha inicio")) {
									extension.setStartDate(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
								} else if (titleStr.contains("fecha fin")) {
									extension.setEndDate(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
								} else if (titleStr.contains("fecha comunicaci\u00f3n")) {
									extension.setCommunicationDate(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
								}
							}
						}
					});
					
					if(extension.getSepeId().isPresent()){
						builder.addDetail(extension);
					}
				} else if(legendStr.contains("datos de la transformaci\u00f3n") && transformationDetail.getSepeId().isPresent()) {  /*---------TRANSFORMATION*/
					titles.forEach(title->{
						String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
						DomNode valueNode = Toolkit.getNextSibling(title);
						if (titleStr.length() > 0 && valueNode != null) {
							titleStr = titleStr.toLowerCase();
							String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
							if (valueStr.length() > 0) {
								if (titleStr.contains("fecha de inicio")) {
									transformationDetail.setStartDate(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
								} else if (titleStr.contains("fecha de fin")) {
									transformationDetail.setEndDate(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
								} else if (titleStr.contains("fecha comunicacion")) {
									transformationDetail.setCommunicationDate(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
								}
							}
						}
					});
				
				}
			});

			if(contractDetail.getSepeId().isPresent()) {
				builder.addDetail(contractDetail);
			}
			
			if(transformationDetail.getSepeId().isPresent()) {
				builder.addDetail(transformationDetail);
			}
			
			return builder.build();
		}
	}

	private static void sendTransformationImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, Contract cto, CopyBasic copyBasic)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setUseInsecureSSL(true);
			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/TransformacionServlet?pagina=inicio").click();
			handleSepeExceptions(htmlPage);

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			{// DATA ENTERPRISE

				String cif = cto.getCifEnterprise();
				
				HtmlRadioButtonInput forNif = (HtmlRadioButtonInput) form.querySelector("[name=tipoacceso][value='2']");
				forNif.click();

				form.getInputByName("tipoacceso").setValue("2");

				if (cif != null) {
					form.getInputByName("cifnifnie").setValue(cif);
					((HtmlSelect) form.querySelector("select[name=tipodocumentoaux]")).setSelectedAttribute(getCifType(cif), true);
				}
			}

			{// DATA EMPLOYEE
				String tipodoc = "D";
				if (Toolkit.getIdentityType(cto.getIpf()).equals("6"))
					tipodoc = "E"; // NIE

				((HtmlSelect) form.querySelector("select[name=tipodocumento]")).setSelectedAttribute(tipodoc, true);

				form.getInputByName("nifnietrabajador").setValue("  " + cto.getIpf());
			}
//
			{// DATA CONTRACT
				// FECHA DE INICIO DEL CONTRATO ANTERIOR
				String[] dateInitContract = Toolkit.dateString(cto.getOldDateIniContract());
				form.getInputByName("diafechaini").setValue(dateInitContract[0]);
				form.getInputByName("mesfechaini").setValue(dateInitContract[1]);
				form.getInputByName("anniofechaini").setValue(dateInitContract[2]);

				String contract = cto.getCodContract();

				try {
					HtmlSelect codtransformacion = ((HtmlSelect) form.querySelector("select[name=codtransformacion]"));
					codtransformacion.getOptionByValue(contract).setSelected(true);
				} catch (ElementNotFoundException e) {
					throw new SepeException("Contrato " + contract + " no soportado");
				}

				// FECHA DE COMUNICACION
				String[] dateComContract = Toolkit.dateString(cto.getDateComContract());
				form.getInputByName("dia").setValue(dateComContract[0]);
				form.getInputByName("mes").setValue(dateComContract[1]);
				form.getInputByName("annio").setValue(dateComContract[2]);
			}

			htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
			handleSepeAlert(alertHandler.getCollectedAlerts());
			handleSepeExceptions(htmlPage);

			form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			{// DATA TRANSFORMATION
				form.getInputByName("ocupacion").setValue(cto.getCodOccupation()); // disabled
				form.getInputByName("cocupacion").setValue(cto.getCodOccupation());// repeat cod contract

				((HtmlSelect) form.querySelector("select[name=nacionalidadCT]"))
						.setSelectedAttribute(cto.getCodPaisWork().toString(), true);

				String municipio = cto.getCodMunWork();
				form.getInputByName("municipiocontrato").setValue(municipio);// repeat cod contract
				form.getInputByName("municipioCT").setValue(municipio);// repeat cod contract

				DomNode fijoDiscontinuo = form.querySelector("[name=fijoDiscontinuo]");
				if (fijoDiscontinuo != null) {
					((HtmlSelect) fijoDiscontinuo).setSelectedAttribute(cto.getDiscontinuo() ? "003" : "004", true);
				}

				DomNode discontinuoReason = form.querySelector("[name=discontinuidad]");
				if (discontinuoReason != null && cto.getDiscontinuoReason() != null) {
					((HtmlSelect) discontinuoReason).setSelectedAttribute(cto.getDiscontinuoReason().getValue(), true);
				}

				DomNode endDateDay = form.querySelector("[name=diafechafin]");
				if (endDateDay != null && cto.getOldDateFindContract() != null) { // FECHA DE FIN DE CONTRATO DEL
																					// CONTRATO ANTERIOR
					String[] endDateOld = Toolkit.dateString(cto.getOldDateFindContract());
					((HtmlInput) endDateDay).setValue(endDateOld[0]);
					form.getInputByName("mesfechafin").setValue(endDateOld[1]);
					form.getInputByName("anniofechafin").setValue(endDateOld[2]);
				}

				{// DATA JORNADA
					DomNode tipoJornada = form.querySelector("select[name=tipoJornada]");
					if (tipoJornada != null && cto.getJndType() != null && cto.getDurationTypeJndHour() != null && cto.getDurationTypeJndMin() != null) {
						((HtmlSelect) tipoJornada).setSelectedAttribute(cto.getJndType().getValue(), true);
						String hours = Toolkit.fillStringLeft(cto.getDurationTypeJndHour(), "0", 4);
						String min = cto.getDurationTypeJndMin();
						form.getInputByName("horas").setValue(hours);
						form.getInputByName("minutos").setValue(min);
						form.getInputByName("duracjornada").setValue(hours + min);

//						DomNode duracConvenio = form.querySelector("select[name=duracconvenio]");
//						if(duracConvenio!=null) {
//							((HtmlInput) duracConvenio).setValue(hours+min);
//						}
					}

//					DomNode pregunta = form.querySelector("select[name=pregunta]");// El periodo de actividad es sin fecha cierta?
//					if(pregunta!=null) {
//						((HtmlSelect)pregunta).setSelectedAttribute("S", true); // "S", "N" 
//					}
				}
			}

			{// DATA COPY BASIC IF NO EXIST
				DomNode codtipofirma = form.querySelector("select[name=codtipofirma]");
				DomNode areadeDomicilio = form.querySelector("[name=areadeDomicilio]");
				DomNode areadeTexto = form.querySelector("[name=areadeTexto]");
				if (copyBasic != null && codtipofirma != null && areadeDomicilio != null && areadeTexto != null) {
					((HtmlSelect) codtipofirma).setSelectedAttribute(copyBasic.getFirmType().getValue().toString(),
							true);
					((HtmlTextArea) areadeDomicilio).setText(copyBasic.getWorkAddress());
					((HtmlTextArea) areadeTexto).setText(copyBasic.getRestContract());
				}
			}

			handleSepeAlert(alertHandler.getCollectedAlerts());

			htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();
			handleSepeExceptions(htmlPage);
			handleSepeAlert(alertHandler.getCollectedAlerts());

			String message = getSuccessMessage(htmlPage);
			if (message != null && message.contains("se ha realizado correctamente")) {
				System.out.println(message);
			} else {
				throw new SepeException(MESSAGE_ERROR);
			}
		}
	}

	private static Contract getTransformationDataImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif,
			Date oldDateIniContract, Optional<String> sepeId) throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaTransformacion?pagina=entrada").click();
			handleSepeExceptions(htmlPage);

			htmlPage = getPageContracByStartDate(htmlPage, ipf, cif, oldDateIniContract, sepeId);
			handleSepeExceptions(htmlPage);

			HtmlForm form = htmlPage.querySelector("form[name=datos]");
			DomNodeList<DomNode> data = form.querySelectorAll("fieldset > div > div[class*=titulo]");

			ContractBuilder builder = new ContractBuilder();

			// DATOS DE CONSULTA
			data.forEach(title -> {
				String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
				DomNode valueNode = Toolkit.getNextSibling(title);
				if (titleStr.length() > 0 && valueNode != null) {
					titleStr = titleStr.toLowerCase();
					String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
					if (valueStr.length() > 0) {
						if (titleStr.contains("cif")) {
							builder.setCifEnterprise(valueStr);
						} else if (titleStr.contains("cuenta de cotizaci\u00F3n")) {
							String newStr = Toolkit.noSpaces(valueStr);
							builder.setRegimen(newStr.substring(0, 4));
							builder.setCtaCti(newStr.substring(4));
						} else if (titleStr.contains("nif/nie")) {
							builder.setIpf(valueStr);
						} else if (titleStr.contains("nombre / apellidos")) {
							builder.setName(valueStr);
						} else if (titleStr.contains("identificador de la transformaci\u00F3n")) {
							builder.setSepeId(Toolkit.noSpaces(valueStr.replace("-", "").substring(1)));
						} else if (titleStr.contains("fecha inicio :")) {
							builder.setOldDateIniContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						} else if (titleStr.contains("fecha fin :")) {
							builder.setOldDateFinContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						} else if (titleStr.contains("fecha comunicaci\u00F3n")) {
							builder.setDateComContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						} else if (titleStr.contains("fecha de inicio de la transformaci\u00F3n :")) {
							builder.setDateIniContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						}
					}
				}
			});

			return builder.build();
		}
	}

	private static Contract getContractExtensionDataImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif,
			Date oldDateIniContract, Optional<String> sepeId) throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setUseInsecureSSL(true);

			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaProrrogas?pagina=entrada").click();
			handleSepeExceptions(htmlPage);

			htmlPage = getPageContracByStartDate(htmlPage, ipf, cif, oldDateIniContract, sepeId, Optional.of(1));
			handleSepeAlert(alertHandler.getCollectedAlerts());
			handleSepeExceptions(htmlPage);
			
			HtmlForm form = htmlPage.querySelector("form[name=datos]");
			DomNodeList<DomNode> dataContract = form
					.querySelectorAll("fieldset[id=fieldset] > div > div[class*=titulo]");

			ContractBuilder builder = new ContractBuilder();

			// DATOS DE CONSULTA
			dataContract.forEach(title -> {
				String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
				DomNode valueNode = Toolkit.getNextSibling(title);
				if (titleStr.length() > 0 && valueNode != null) {
					titleStr = titleStr.toLowerCase();
					String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
					if (valueStr.length() > 0) {
						if (titleStr.contains("cif")) {
							builder.setCifEnterprise(valueStr);
						} else if (titleStr.contains("cuenta de cotizaci\u00F3n")) {
							String newStr = Toolkit.noSpaces(valueStr);
							builder.setRegimen(newStr.substring(0, 4));
							builder.setCtaCti(newStr.substring(4));
						} else if (titleStr.contains("nif/nie")) {
							builder.setIpf(Toolkit.noSpaces(valueStr));
						} else if (titleStr.contains("nombre / apellidos")) {
							builder.setName(valueStr);
						} else if (titleStr.contains("fecha de inicio")) {
							builder.setOldDateIniContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						} else if (titleStr.contains("fecha de t\u00e9rmino")) {
							builder.setOldDateFinContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						}
					}
				}
			});

			form.querySelectorAll("fieldset[id=fieldset2] > div > div[class*=titulo]")
			.forEach(title -> {
				String titleStr = Toolkit.removeNBSP(title.getVisibleText()).trim();
				DomNode valueNode = Toolkit.getNextSibling(title);
				if (titleStr.length() > 0 && valueNode != null) {
					titleStr = titleStr.toLowerCase();
					String valueStr = Toolkit.removeNBSP(valueNode.getVisibleText().trim());
					if (valueStr.length() > 0) {
						if (titleStr.contains("prorroga")) {
							builder.setSepeId(Toolkit.noSpaces(valueStr.replace("-", "").substring(1)));
						} else if (titleStr.contains("fecha de inicio")) {
							builder.setDateIniContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						} else if (titleStr.contains("fecha de t\u00e9rmino")) {
							builder.setDateFinContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						} else if (titleStr.contains("fecha comunicaci\u00F3n")) {
							builder.setDateComContract(Toolkit.parseDate(valueStr, FORMAT_DATE_ES));
						}
					}
				}
			});

			return builder.build();
		}
	}

	private static String sendContrataExtensionImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, ContractExtension contractExtension)
			throws SepeException, FailingHttpStatusCodeException, InterruptedException, IOException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);

			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletProrrogas?pagina=inicio").click();
			handleSepeExceptions(htmlPage);
			
			Optional<String> sepeId = contractExtension.getSepeId();

			HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			if (sepeId.isEmpty()) {
				throw new InvalidDataException("Sepe IDE requerido");
			}

			((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();
			
			String ide = sepeId.get();
			String ide1 = ide.substring(0, 2);
			String ide2 = ide.substring(2, 6);
			String ide3 = ide.substring(6);
			((HtmlInput) htmlPage.getElementByName("idcomunicacion1")).setValue(ide1);
			((HtmlInput) htmlPage.getElementByName("idcomunicacion1")).setValueAttribute(ide1);
			((HtmlInput) htmlPage.getElementByName("idcomunicacion2")).setValue(ide2);
			((HtmlInput) htmlPage.getElementByName("idcomunicacion2")).setValueAttribute(ide2);
			((HtmlInput) htmlPage.getElementByName("idcomunicacion3")).setValue(ide3);
			((HtmlInput) htmlPage.getElementByName("idcomunicacion3")).setValueAttribute(ide3);
			
//			formDatos.getInputByName("idcomunicacion1").setValue(ide1);
//			formDatos.getInputByName("idcomunicacion2").setValue(ide2);
//			formDatos.getInputByName("idcomunicacion3").setValue(ide3);
//			formDatos.getInputByName("idcontrato").setValue(ide1 + "-" + ide2 + "-" + ide3);

			htmlPage = htmlPage.getElementById("enviar").click();
			
//			htmlPage = formDatos.getInputByName("enviar").click();
			handleSepeExceptions(htmlPage);

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			String cifValue = form.getInputByName("cifnifempresapro").getValue();
			if (cifValue != null && cifValue.isEmpty()) {
				String cif = contractExtension.getCif();
				
				((HtmlSelect) form.querySelector("select[name=tipodocumento]")).setSelectedAttribute(getCifType(cif), true);
				form.getInputByName("cifnifempresapro").setValue(cif);
			}

			HtmlInput ccc1 = form.getInputByName("cuentacotizacion2pro");
			String cccValue = ccc1.getValue();
			if (cccValue != null && cccValue.isEmpty()) {
				String ccc = contractExtension.getCtaCti();
				form.getInputByName("cuentacotizacion1pro").setValue(contractExtension.getRegime());
				ccc1.setValue(ccc.substring(0, 9));
				form.getInputByName("cuentacotizacion3pro").setValue(ccc.substring(9));
			}

			String[] startDate = Toolkit.dateString(contractExtension.getStartDate());
			((HtmlInput) htmlPage.getElementByName("diainiciopro")).setValue(startDate[0]);
			((HtmlInput) htmlPage.getElementByName("diainiciopro")).setValueAttribute(startDate[0]);
			((HtmlInput) htmlPage.getElementByName("mesiniciopro")).setValue(startDate[1]);
			((HtmlInput) htmlPage.getElementByName("mesiniciopro")).setValueAttribute(startDate[1]);
			((HtmlInput) htmlPage.getElementByName("annoiniciopro")).setValue(startDate[2]);
			((HtmlInput) htmlPage.getElementByName("annoiniciopro")).setValueAttribute(startDate[2]);
//			form.getInputByName("diainiciopro").setValue(startDate[0]);
//			form.getInputByName("mesiniciopro").setValue(startDate[1]);
//			form.getInputByName("annoiniciopro").setValue(startDate[2]);

			String[] endDate = Toolkit.dateString(contractExtension.getEndDate());
			((HtmlInput) htmlPage.getElementByName("diafinpro")).setValue(endDate[0]);
			((HtmlInput) htmlPage.getElementByName("diafinpro")).setValueAttribute(endDate[0]);
			((HtmlInput) htmlPage.getElementByName("mesfinpro")).setValue(endDate[1]);
			((HtmlInput) htmlPage.getElementByName("mesfinpro")).setValueAttribute(endDate[1]);
			((HtmlInput) htmlPage.getElementByName("annofinpro")).setValue(endDate[2]);
			((HtmlInput) htmlPage.getElementByName("annofinpro")).setValueAttribute(endDate[2]);
//			form.getInputByName("diafinpro").setValue(endDate[0]);
//			form.getInputByName("mesfinpro").setValue(endDate[1]);
//			form.getInputByName("annofinpro").setValue(endDate[2]);

			HtmlSelect discontinuidad = htmlPage.getElementByName("discontinuidad");
			if (discontinuidad != null && contractExtension.isDiscontinuo()) {
				discontinuidad.setSelectedAttribute("S", true);
			}
			
//			DomNode discontinuidad = form.querySelector("[name=\"discontinuidad\"]");
//			if (discontinuidad != null && contractExtension.isDiscontinuo()) {
//				((HtmlSelect) discontinuidad).setSelectedAttribute("S", true);
//			}

			form.getInputByName("idprorroga").setValue(ide1 + "-" + ide2 + "-" + ide3);
			
			checkContractExist(htmlPage);

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("[name=\"enviar\"]")).click();
			handleSepeAlert(alertHandler.getCollectedAlerts());
			
			String message = null;

			for (int i = 0; i < 2; i++) {
				message = getSuccessMessage(htmlPage);
				if (message == null || (message != null && message.indexOf("E") >= 0)) {
					break;
				} else if (message.contains(RETURN_INIT)) {
					htmlPage = sepeReturnInitContractExtension(htmlPage, contractExtension);
				}
			}

			if (message != null && message.indexOf("E") >= 0) {
				return message.substring(1);
			} else {
				handleSepeExceptions(htmlPage);
			}
		

			throw new SepeException(MESSAGE_ERROR);
		}
	}

	private static String sendCopyBasicImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, CopyBasic copyBasic)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=copiabasica").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_comunica_copiaBasicaContrato.jsp?origen=copiabasica").click();
			handleSepeExceptions(htmlPage);

			Optional<String> sepeId = copyBasic.getSepeId();

			if (sepeId.isPresent()) {
				htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idcomunicacion&origen=copiabasica").click();
				handleSepeExceptions(htmlPage);

				HtmlForm formTwo = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
				String ide = sepeId.get();
				String ide1 = ide.substring(0, 2);
				String ide2 = ide.substring(2, 6);
				String ide3 = ide.substring(6);
				formTwo.getInputByName("idcomunicacion1").setValue(ide1);
				formTwo.getInputByName("idcomunicacion2").setValue(ide2);
				formTwo.getInputByName("idcomunicacion3").setValue(ide3);
				formTwo.getInputByName("idcomunicacion").setValue(ide1 + "-" + ide2 + "-" + ide3);

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
			if (codTipoFirma != null) {
				((HtmlSelect) codTipoFirma).setSelectedAttribute(copyBasic.getFirmType().getValue().toString(), true);
			}

			DomNode areadeDomicilio = form.querySelector("[name=areadeDomicilio]");
			if (areadeDomicilio != null && copyBasic.getWorkAddress() != null) {
				((HtmlTextArea) areadeDomicilio).setText(copyBasic.getWorkAddress());
			}

			DomNode areadeTexto = form.querySelector("[name=areadeTexto]");
			if (areadeTexto != null && copyBasic.getRestContract() != null) {
				((HtmlTextArea) areadeTexto).setText(copyBasic.getRestContract());
			}
			
			checkContractExist(htmlPage);

			htmlPage = ((HtmlSubmitInput) form.querySelector("[name=enviar]")).click();
			handleSepeExceptions(htmlPage);

			String message = getSuccessMessage(htmlPage);
			if (message != null && message.contains("se ha realizado correctamente")) {
				return message;
			}

			throw new SepeException(MESSAGE_ERROR);
		}
	}

	private static String sendTransformationCopyBasicImpl(InputStream certificateInputStream,
			String certificatePassword, String certificateType, CopyBasic copyBasic, String cif, Date startDate)
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=copiabasica").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaTransformacion?pagina=entradaCBTransf")
					.click();
			handleSepeExceptions(htmlPage);

			Optional<String> sepeId = copyBasic.getSepeId();

			HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			if (sepeId.isPresent()) { // por identificacion de la comunicacion
				String ide = sepeId.get();
				((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();
				String ide1 = ide.substring(0, 2);
				String ide2 = ide.substring(2, 6);
				String ide3 = ide.substring(6);
				formDatos.getInputByName("idcomunicacion1").setValue(ide1);
				formDatos.getInputByName("idcomunicacion2").setValue(ide2);
				formDatos.getInputByName("idcomunicacion3").setValue(ide3);
				formDatos.getInputByName("idcontrato").setValue(ide1 + "-" + ide2 + "-" + ide3);
			} else {

				((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"2\"]")).click();

				// ENTERPRISE
				if (cif != null) {
					((HtmlSelect) formDatos.querySelector("select[name=tipodocumentoaux]")).setSelectedAttribute(getCifType(cif), true);
					
					formDatos.getInputByName("cifnifnie").setValue(cif);
				}
				
				String ipf = copyBasic.getIpf().get();
				// EMPLOYEE
				Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; // 1 NIE, 0 NIF
				HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipodocumento]>option").get(ident);
				option.click();

				formDatos.getInputByName("nifnietrabajador").setValue(Toolkit.appendStringLeft(ipf, " ", 2));

				String[] fri = Toolkit.formatDate(startDate);
				formDatos.getInputByName("diafechaini").setValue(fri[0]);
				formDatos.getInputByName("mesfechaini").setValue(fri[1]);
				formDatos.getInputByName("anniofechaini").setValue(fri[2]);
			}

			htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			DomNode codTipoFirma = form.querySelector("select[name=codtipofirma]");
			if (codTipoFirma != null) {
				((HtmlSelect) codTipoFirma).setSelectedAttribute(copyBasic.getFirmType().getValue().toString(), true);
			}

			DomNode areadeDomicilio = form.querySelector("[name=areadeDomicilio]");
			if (areadeDomicilio != null && copyBasic.getWorkAddress() != null) {
				((HtmlTextArea) areadeDomicilio).setText(copyBasic.getWorkAddress());
			}

			DomNode areadeTexto = form.querySelector("[name=areadeTexto]");
			if (areadeTexto != null && copyBasic.getRestContract() != null) {
				((HtmlTextArea) areadeTexto).setText(copyBasic.getRestContract());
			}
			
			checkContractExist(htmlPage);

			htmlPage = ((HtmlSubmitInput) form.querySelector("[name=enviar]")).click();
			handleSepeExceptions(htmlPage);

			String message = getSuccessMessage(htmlPage);
			if (message != null && message.contains("se ha realizado correctamente")) {
				return message;
			}
		
			throw new SepeException(MESSAGE_ERROR);
		}
	}

	private static byte[] getContratoPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date startDate, Date endDate, Optional<String> sepeId)
			throws FailingHttpStatusCodeException, IOException, InterruptedException,
			SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultasgeneral&origen=")
					.click();
			handleSepeExceptions(htmlPage);

			if (sepeId.isPresent()) {
				htmlPage = htmlPage
						.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idcomunicacion&origen=")
						.click();// por identificador de la comunicacion
				handleSepeExceptions(htmlPage);

				HtmlForm formTwo = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
				String ide = sepeId.get();
				
				if(ide.contains("-")) ide = ide.replaceAll("-", "");
				
				String province = "";
				String year = "";
				String code = "";
				
				if(ide.length() == 13) {
					province = ide.substring(0, 2);
					year = ide.substring(2, 6);
					code = ide.substring(6);
				} else if(ide.length() == 14) {
					province = ide.substring(1, 3);
					year = ide.substring(3, 7);
					code = ide.substring(7);
				}
				
				formTwo.getInputByName("idcomunicacion1").setValue(province);
				formTwo.getInputByName("idcomunicacion2").setValue(year);
				formTwo.getInputByName("idcomunicacion3").setValue(code);
				formTwo.getInputByName("idcomunicacion1").setValueAttribute(province);
				formTwo.getInputByName("idcomunicacion2").setValueAttribute(year);
				formTwo.getInputByName("idcomunicacion3").setValueAttribute(code);

				htmlPage = formTwo.getInputByName("aceptar").click();
				handleSepeExceptions(htmlPage);
			} else {
				htmlPage = htmlPage
						.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=")
						.click();// por identificador del trabajador
				handleSepeExceptions(htmlPage);

				htmlPage = pageContracOrCopybasic(htmlPage, startDate, endDate, ipf);
			}

			HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			
			Page page = formDatos1.getInputByName("Boton_imprimir").click();
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
		return null;
	}

	private static byte[] getCopyBasicPdfImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date startDate, Date endDate,
			Optional<String> sepeId) throws FailingHttpStatusCodeException, IOException,
			InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultaImpCB&origen=consultaImpresionCB")
					.click();// PARA CONTRATOS INICIALES
			handleSepeExceptions(htmlPage);

			if (sepeId.isPresent()) {
				htmlPage = htmlPage.getAnchorByHref(
						"/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idcomunicacion&origen=consultaImpresionCB")
						.click();// por identificador de la comunicacion
				handleSepeExceptions(htmlPage);

				HtmlForm formTwo = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
				String ide = sepeId.get();
				
				if(ide.contains("-")) ide = ide.replaceAll("-", "");
				
				String province = "";
				String year = "";
				String code = "";
				
				if(ide.length() == 13) {
					province = ide.substring(0, 2);
					year = ide.substring(2, 6);
					code = ide.substring(6);
				} else if(ide.length() == 14) {
					province = ide.substring(1, 3);
					year = ide.substring(3, 7);
					code = ide.substring(7);
				}
				
				formTwo.getInputByName("idcomunicacion1").setValue(province);
				formTwo.getInputByName("idcomunicacion2").setValue(year);
				formTwo.getInputByName("idcomunicacion3").setValue(code);
				formTwo.getInputByName("idcomunicacion").setValue(province + "-" + year + "-" + code);

				htmlPage = formTwo.getInputByName("aceptar").click();

				handleSepeExceptions(htmlPage);
			} else {
				htmlPage = htmlPage
						.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idtrabajador&origen=consultaImpresionCB")
						.click();// por identificador del trabajador
				handleSepeExceptions(htmlPage);

				htmlPage = pageContracOrCopybasic(htmlPage, startDate, endDate, ipf);
			}

			HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			
			Page page = formDatos1.getInputByName("Boton_imprimir").click();
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
		return null;
	}

	private static byte[] getTransformationCopyBasicPdfImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif, Date startDate,
			Optional<String> sepeId) throws FailingHttpStatusCodeException, IOException,
			InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage
					.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=init&origen=consultaImpresionCB")
					.click();// PARA CONTRATOS TRANSFORMACIONES
			handleSepeExceptions(htmlPage);

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			if (sepeId.isPresent()) { // por identificacion de la comunicacion
				String ide = sepeId.get();
				((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();
				String ide1 = ide.substring(0, 2);
				String ide2 = ide.substring(2, 6);
				String ide3 = ide.substring(6);
				formDatos.getInputByName("idcomunicacion1").setValue(ide1);
				formDatos.getInputByName("idcomunicacion2").setValue(ide2);
				formDatos.getInputByName("idcomunicacion3").setValue(ide3);
				formDatos.getInputByName("idcontrato").setValue(ide1 + "-" + ide2 + "-" + ide3);
			} else {

				((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"2\"]")).click();

				// ENTERPRISE
				String cifValue = formDatos.getInputByName("cifnifnie").getValue();

				if (cifValue != null && cifValue.isEmpty()) {
					String cifTypeStr = Toolkit.getIdentityType(cif);
					Integer cifType = 0;
					if (cifTypeStr.equals("1")) {						
						cifType = 1;
					} else if (cifTypeStr.equals("6")) {						
						cifType = 2;
					}

					HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipodocumentoaux]>option")
							.get(cifType);// " " cif, "D" NIF, "E" NIE
					option.click();

					formDatos.getInputByName("cifnifnie").setValue(cif);
				}

				// EMPLOYEE
				Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; // 1 NIE, 0 NIF
				HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipodocumento]>option")
						.get(ident);
				option.click();

				formDatos.getInputByName("nifnietrabajador").setValue(Toolkit.appendStringLeft(ipf, " ", 2));

				String[] fri = Toolkit.formatDate(startDate);
				formDatos.getInputByName("diafechaini").setValue(fri[0]);
				formDatos.getInputByName("mesfechaini").setValue(fri[1]);
				formDatos.getInputByName("anniofechaini").setValue(fri[2]);
			}

			htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);

			HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

			Page page = formDatos1.getInputByName("Boton_imprimir").click();
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
		return null;
	}

	private static void removeContrataImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide) throws SepeException, FailingHttpStatusCodeException, IOException, ElementNotFoundException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = firstPageRemove(htmlPage);
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initC").click();
			handleSepeExceptions(htmlPage);
			
			htmlPage = lastPageRemove(htmlPage, ide);
			handleSepeExceptions(htmlPage);

			String message = getSuccessMessage(htmlPage);
			if (message != null && message.contains("se ha realizado correctamente")) {
				System.out.println(message);
			} else
				throw new SepeException(MESSAGE_ERROR);
		}
	}

	private static void removeTransformationImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ide)
			throws SepeException, FailingHttpStatusCodeException, IOException,
			ElementNotFoundException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			htmlPage = firstPageRemove(htmlPage);
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initT").click();
			handleSepeExceptions(htmlPage);

			htmlPage = lastPageRemove(htmlPage, ide);

			String message = getSuccessMessage(htmlPage);
			if (message != null && message.contains("se ha realizado correctamente")) {
				System.out.println(message);
			} else {
				throw new SepeException(MESSAGE_ERROR);
			}
		}
	}

	private static void removeContractExtensionImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ide)
			throws SepeException, FailingHttpStatusCodeException, IOException,
			ElementNotFoundException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			htmlPage = firstPageRemove(htmlPage);
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initP").click();
			handleSepeExceptions(htmlPage);

			htmlPage = lastPageRemove(htmlPage, ide);

			String message = getSuccessMessage(htmlPage);
			if (message != null && message.contains("se ha realizado correctamente")) {
				System.out.println(message);
			} else {
				throw new SepeException(MESSAGE_ERROR);
			}
		}
	}

	private static HtmlPage firstPageRemove(HtmlPage htmlPage)
			throws SepeException, ElementNotFoundException, IOException {
		htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=anulacioncomunicacion").click();
		try {
			DomNode error = htmlPage.querySelector("#contIzq > div.contIzqc > p");
			if (error != null && !error.getVisibleText().isEmpty()
					&& error.getVisibleText().contains("disponible para el usuario principal"))
				throw new SepeException(error.getVisibleText());
		} catch (NullPointerException e) {
		}

		htmlPage = htmlPage
				.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_anulacion_bajas.jsp?origen=anulacioncomunicacion")
				.click();

		handleSepeExceptions(htmlPage);
		return htmlPage;
	}

	private static HtmlPage lastPageRemove(HtmlPage htmlPage, String ide)
			throws SepeException, ElementNotFoundException, IOException, InterruptedException {
		if (ide == null)
			throw new InvalidDataException("Sepe IDE requerido");

		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		String ide1 = ide.substring(0, 2); // 2 digits
		String ide2 = ide.substring(2, 6); // 4 digits
		String ide3 = ide.substring(6, 13); // 7 digits
		String ide4 = ide.substring(13);
		boolean ide4Exist = ide4 != null && !ide4.isEmpty();

		formDatos.getInputByName("idcomunicacion1").setValue(ide1);
		formDatos.getInputByName("idcomunicacion2").setValue(ide2);
		formDatos.getInputByName("idcomunicacion3").setValue(ide3);

		DomNode idcomunicacion4Node = formDatos.querySelector("[name=\"idcomunicacion4\"]");
		if (idcomunicacion4Node != null && ide4Exist) {
			HtmlInput idcomunicacion4 = ((HtmlInput) idcomunicacion4Node);
			if (idcomunicacion4.getValue().isEmpty()) {
				idcomunicacion4.setValue(ide4);
			}
		}

		DomNode ideEl = formDatos.querySelector("[name=\"idcomunicacion\"]");
		if (ideEl != null) {
			String ideStr = ide1 + "-" + ide2 + "-" + ide3;
			if (ide4Exist) {
				ideStr += "-" + Toolkit.fillStringLeft(ide4, "0", 2);
			}
			((HtmlInput) ideEl).setValue(ideStr);
		}
	
		HtmlElement inputSubmit = formDatos.querySelector("input[value=aceptar]");
		htmlPage = (HtmlPage) inputSubmit.click();
		handleSepeExceptions(htmlPage);

		formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		inputSubmit = formDatos.querySelector("#anular");

		htmlPage = (HtmlPage) inputSubmit.click();
		handleSepeExceptions(htmlPage);

		inputSubmit = htmlPage.querySelector("form input[name=enviar]");
		htmlPage = (HtmlPage) inputSubmit.click();
		handleSepeExceptions(htmlPage);

		return htmlPage;
	}

	private static HtmlPage pageContracOrCopybasic(HtmlPage htmlPage, Date startDate, Date endDate, String ipf)
			throws InterruptedException, IOException, SepeException {
		String[] fri = Toolkit.formatDate(startDate);
		String[] fre = Toolkit.formatDate(endDate);
		Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; // 1 NIE, 0 NIF //NIF DEFAULT

		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipodoc2]>option").get(ident);
		option.click();
		
		formDatos.getInputByName("nifnietrabajador").setValue(Toolkit.appendStringLeft(ipf, " ", 2));
		formDatos.getInputByName("nifnietrabajador").setValueAttribute(Toolkit.appendStringLeft(ipf, " ", 2));
		
		formDatos.getInputByName("diadesde").setValue(fri[0]);
		formDatos.getInputByName("mesdesde").setValue(fri[1]);
		formDatos.getInputByName("anniodesde").setValue(fri[2]);
		formDatos.getInputByName("diadesde").setValueAttribute(fri[0]);
		formDatos.getInputByName("mesdesde").setValueAttribute(fri[1]);
		formDatos.getInputByName("anniodesde").setValueAttribute(fri[2]);

		formDatos.getInputByName("diahasta").setValue(fre[0]);
		formDatos.getInputByName("meshasta").setValue(fre[1]);
		formDatos.getInputByName("anniohasta").setValue(fre[2]);
		formDatos.getInputByName("diahasta").setValueAttribute(fre[0]);
		formDatos.getInputByName("meshasta").setValueAttribute(fre[1]);
		formDatos.getInputByName("anniohasta").setValueAttribute(fre[2]);
		
		htmlPage = formDatos.getInputByName("aceptar").click();
		handleSepeExceptions(htmlPage);

		String startDateStr = fri[0] + "/" + fri[1] + "/" + fri[2];
		HtmlTable table = (HtmlTable) htmlPage.querySelector("table.tableScroll");

		HtmlCheckBoxInput firstColumn;
		String columnCheck = "a0";
		for (final HtmlTableRow row : table.getRows()) {
			HtmlTableCell cell = row.getCell(5);
			if (cell.getVisibleText().indexOf(startDateStr) >= 0) {
				firstColumn = row.getCell(0).querySelector("input[name=indice]");
				columnCheck = firstColumn.getValue();
				break;
			}
		}

		htmlPage = ((HtmlCheckBoxInput) htmlPage.querySelector("form[name=datos] input[value=" + columnCheck + "]"))
				.click();
		handleSepeExceptions(htmlPage);
		return htmlPage;
	}

	private static HtmlPage getPageContracByStartDate(HtmlPage htmlPage, String ipf, String cif,
			Date oldDateIniContract, Optional<String> sepeId) throws InterruptedException, IOException, SepeException {
		return getPageContracByStartDate(htmlPage, ipf, cif, oldDateIniContract, sepeId, Optional.empty());
	}

	private static HtmlPage getPageContracByStartDate(HtmlPage htmlPage, String ipf, String cif,
			Date oldDateIniContract, Optional<String> sepeId, Optional<Integer> nprorroga)
			throws InterruptedException, IOException, SepeException {
		
		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();

		if (sepeId.isPresent()) { // por identificacion de la comunicacion
			String ide = sepeId.get();
			String ide1 = ide.substring(0, 2);
			String ide2 = ide.substring(2, 6);
			String ide3 = ide.substring(6, 13);
			String ide4 = ide.substring(13);
			boolean ide4Exist = ide4 != null && !ide4.isEmpty();

			((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"1\"]")).click();

			formDatos.getInputByName("idcomunicacion1").setValue(ide1);
			formDatos.getInputByName("idcomunicacion2").setValue(ide2);
			formDatos.getInputByName("idcomunicacion3").setValue(ide3);

			DomNode idcomunicacion4Node = formDatos.querySelector("[name=\"idcomunicacion4\"]");
			if (idcomunicacion4Node != null && ide4Exist) {
				HtmlInput idcomunicacion4 = ((HtmlInput) idcomunicacion4Node);
				if (idcomunicacion4.getValue().isEmpty()) {
					idcomunicacion4.setValue(Toolkit.fillStringLeft(ide4, "0", 2));
				}
			}

			DomNode idcontratoNode = formDatos.querySelector("[name=\"idcontrato\"]");
			if (idcontratoNode != null) {
				HtmlInput idcontrato = ((HtmlInput) idcontratoNode);
				String ideStr = ide1 + "-" + ide2 + "-" + ide3;
				if (ide4Exist) {
					ideStr += "-" + Toolkit.fillStringLeft(ide4, "0", 2);
				}
				idcontrato.setValue(ideStr);
			}

		} else {

			((HtmlRadioButtonInput) formDatos.querySelector("[name=\"tipoacceso\"][value=\"2\"]")).click();

			// ENTERPRISE
			String cifValue = formDatos.getInputByName("cifnifnie").getValue();

			if (cifValue != null && cifValue.isEmpty()) {
				String cifTypeStr = Toolkit.getIdentityType(cif);
				Integer cifType = 0;
				if (cifTypeStr.equals("1")) {
					cifType = 1;
				} else if (cifTypeStr.equals("6")) {
					cifType = 2;
				}

				HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipodocumentoaux]>option")
						.get(cifType);// " " cif, "D" NIF, "E" NIE
				option.click();

				formDatos.getInputByName("cifnifnie").setValue(cif);
			}

			// EMPLOYEE
			Integer ident = Toolkit.getIdentityType(ipf).equals("6") ? 1 : 0; // 1 NIE, 0 NIF
			HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipodocumento]>option").get(ident);
			option.click();

			formDatos.getInputByName("nifnietrabajador").setValue(Toolkit.appendStringLeft(ipf, " ", 2));

			String[] fri = Toolkit.formatDate(oldDateIniContract);
			formDatos.getInputByName("diafechaini").setValue(fri[0]);
			formDatos.getInputByName("mesfechaini").setValue(fri[1]);
			formDatos.getInputByName("anniofechaini").setValue(fri[2]);
			

			nprorroga.ifPresent(n -> {
				DomNode numprorrogaNode = formDatos.querySelector("[name=\"numprorroga\"]");
				if (numprorrogaNode != null) {
					((HtmlInput) numprorrogaNode).setValue(n + "");
				}
			});
		}

		htmlPage = formDatos.getInputByName("aceptar").click();

		return htmlPage;

	}

	private static byte[] getTransformationPdfImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif, Date startDate,
			Optional<String> sepeId) throws FailingHttpStatusCodeException, IOException,
			SepeException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaTransformacion?pagina=entrada")
					.click();
			handleSepeExceptions(htmlPage);

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = getPageContracByStartDate(htmlPage, ipf, cif, startDate, sepeId);
			handleSepeExceptions(htmlPage);

			Page page = htmlPage.getElementByName("enviar").click();
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
		return null;
	}

	private static byte[] getContractExtensionPdfImpl(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, String cif,
			Date oldDateIniContract, Integer nprorroga, Optional<String> sepeId) throws FailingHttpStatusCodeException, IOException, SepeException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);

			CollectingAlertHandler alertHandler = new CollectingAlertHandler();
			webClient.setAlertHandler(alertHandler);

			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=")
					.click();
			handleSepeExceptions(htmlPage);

			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaProrrogas?pagina=entrada")
					.click();
			handleSepeExceptions(htmlPage);

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = getPageContracByStartDate(htmlPage, ipf, cif, oldDateIniContract, sepeId,
					Optional.ofNullable(nprorroga));
			handleSepeAlert(alertHandler.getCollectedAlerts());
			handleSepeExceptions(htmlPage);

			Page page = htmlPage.getElementByName("Boton_imprimir").click();
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
		return null;
	}

	private static void setOccupation(Contract cto, HtmlForm form) {
		if (cto.getCodOccupation() != null) {
			DomNode ocupacion = form.querySelector("[name=\"ocupacion\"]");
			if (ocupacion != null) {
				((HtmlInput) ocupacion).setValue(cto.getCodOccupation());
				((HtmlInput) ocupacion).setValueAttribute(cto.getCodOccupation());
			}

			DomNode cocupacion = form.querySelector("[name=\"cocupacion\"]");
			if (cocupacion != null) {
				((HtmlInput) cocupacion).setValue(cto.getCodOccupation());
				((HtmlInput) cocupacion).setValueAttribute(cto.getCodOccupation());
			}
		}
	}

	private static HtmlPage getFirstPageSepeContrata(WebClient webClient)
			throws FailingHttpStatusCodeException, IOException, SepeException {
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
		HtmlPage htmlPage = webClient
				.getPage("https://www.sepe.es:444/ccomunicacto/servlet/ServletInicio?CCAA=99&idioma=14");
		HtmlUnitToolkit.manageStatusCode(htmlPage);
		handleSepeExceptions(htmlPage);
		return htmlPage;
	}

	private static HtmlPage contractPage(HtmlPage htmlPage, String codCto)
			throws ElementNotFoundException, IOException, SepeException {
		
		String href = null;

		if(Arrays.asList("421", "450").contains(codCto)) { // Formación en alternancia tiempo completo
			href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=6";
		} else if(codCto.equals("420")) { //Formativo para la obtención de la práctica profesional tiempo completo
			href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=7";
		} else if(Arrays.asList("520", "550").contains(codCto)) { // Formativo para la obtención de la práctica profesional tiempo parcial
			href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=8";
		} else {
			String oneCodCto = codCto.substring(0, 1);
			switch (oneCodCto) {
			case "1": // INDEFINIDO_TIEMPO_COMPLETO
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
		}
			
		htmlPage = htmlPage.getAnchorByHref(href).click();
		return htmlPage;
	}

	private static String getSuccessMessage(HtmlPage htmlPage) {
		DomNodeList<DomNode> texts = htmlPage.querySelectorAll("#contIzq p");
		String msg = null;

		final List<String> list = Arrays.asList(
			"sin fecha de t\u00E9rmino"
			,"f\u00EDsica en la base de datos"
			,"igual o inferior a 90" // previsible inferior o igual a 90 dias
			,"convenio colectivo que autoriza" // previsible mayor a 90 dias
			,"certificado de profesionalidad" // indicar si el trabajador tiene Certificado de Profesionalidad
            ,"seleccionar el campo convenio colectivo"
            ,"sin fecha cierta"
		);

		for (DomNode p : texts) {
			String pStr = Toolkit.removeNBSP(p.getVisibleText()).trim();
			Integer pInt = pStr.length();
			boolean b = false;
			if (pInt > 2 && !pStr.isEmpty()) {
				String pLowerCase = pStr.toLowerCase();
				if (pLowerCase.contains("identificador de la")) {
					String[] parts = pStr.split(":");
					if (parts.length>0) {
						msg = Toolkit.noSpaces( (parts.length > 1 ? parts[1] : parts[0]).trim().replace("-", "") );
					}
					b = true;
				} else if (pLowerCase.contains("se ha realizado correctamente")) {
					msg = pStr;
					b = true;
				} else if (list.stream().anyMatch(pLowerCase::contains)) {
					msg = RETURN_INIT;
					b = true;
				}

				if (b) {
					break;
				}
			}
		}
		return msg;
	}

	private static HtmlPage goBackContract(HtmlPage htmlPage, Contract cto)
			throws IOException, InterruptedException, SepeException {

		htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("#volver")).click();
		HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		form.getInputByName("cocupacion").setValue(cto.getCodOccupation());// repeat cod contract
		form.getInputByName("contratoEscrito").setValue("N"); // contratoEscrito si la fecha fin es menor a 28
		form.getInputByName("nass").setValue(cto.getNss());

		// ---------------------PREVISIBLE---------------------
		if (Arrays.asList("402", "502").contains(cto.getCodContract())) { // es previsible
			DomNode previsible              = form.querySelector("select[name=preg90dias]");
			DomNode previsibleAutoriza      = form.querySelector("select[name=AutorizaDuracion]");
			DomNode periodoActividadJornada = form.querySelector("select[name=periodoActividadJornada]");
			
			if (previsible != null) { // previsible inferior o igual a 90 dias
				((HtmlSelect) previsible).setSelectedAttribute(cto.getPrevisible() ? "S" : "N", true);
			} else if(previsibleAutoriza!=null){// previsible mayor a 90 dias
				((HtmlSelect) previsibleAutoriza).setSelectedAttribute(cto.getPrevisible() ? "1" : "0", true);
			} else if(periodoActividadJornada!=null){// periodo de actividad sin fecha cierta
				((HtmlSelect) periodoActividadJornada).setSelectedAttribute(cto.getNoCertainDate() ? "S" : "N", true);
			} 
		}

		DomNode certificadoProf = form.querySelector("select[name=certificadoProf]");
		if (certificadoProf != null) {
			((HtmlSelect) certificadoProf).setSelectedAttribute(cto.getCertificateProfessional() ? "S" : "N", true);
		}

		htmlPage = ((HtmlSubmitInput) form.querySelector("[name=aceptar]")).click();

		return htmlPage;
	}
	
	
	private static HtmlPage sepeReturnInitContractExtension(HtmlPage htmlPage, ContractExtension contractExtension)
			throws IOException, InterruptedException {
		Optional<String> sepeId = contractExtension.getSepeId();
		String ide  = sepeId.get();
		String ide1 = ide.substring(0, 2);
		String ide2 = ide.substring(2, 6);
		String ide3 = ide.substring(6);

		htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("#volver")).click();
		HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		
		DomNode convenio = form.querySelector("[name=\"conveniocolectivo\"]");
		if(convenio!=null) {
			((HtmlSelect) convenio).setSelectedAttribute(contractExtension.getConvenio() ? "S" : "N", true);
		}
		
		DomNode discontinuidad = form.querySelector("[name=\"discontinuidad\"]");
		if (discontinuidad != null && contractExtension.isDiscontinuo()) {
			((HtmlSelect) discontinuidad).setSelectedAttribute("S", true);
		}
		
		form.getInputByName("idprorroga").setValue(ide1 + "-" + ide2 + "-" + ide3);
		
		htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("[name=\"enviar\"]")).click();

		return htmlPage;
	}

	private static void handleSepeExceptions(HtmlPage htmlPage) throws SepeException {
		try {
			DomNode error = htmlPage.querySelector("#avisos > div > p:last-child");
			if (error != null && !error.getVisibleText().isEmpty()) {
				if(error.getVisibleText().contains("contrato escrito"))
					throw new SepeException(error.getVisibleText() + "Por favor comunique este contrato a traves de Contrata.");
				throw new SepeException(error.getVisibleText());
			} else {
				String body = htmlPage.asNormalizedText();
				if (body != null) {
					Pattern pattern = Pattern.compile("(?<certificate>certificado\\s*digital\\s*no\\s*v.lido)|(?<permission>no\\s*est.{1,3}\\s*autorizado)", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(body);
					if (matcher.find()) {
						if(matcher.group("certificate") != null) {
							throw new InvalidCertificateException("Certificado digital no v\u00e1lido");
						} else if(matcher.group("permission") != null)  {
							throw new SepeException("El usuario no est\u00e1 autorizado");
						}
					}
				}
			}
		} catch (NullPointerException e) {}
	}

	private static void handleSepeAlert(List<String> list) throws SepeException {
		if (!list.isEmpty()) {
			throw new SepeException(list.get(0));
		}
	}

	private static void validateCertImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SepeException, IOException {
		byte[] certByte = certificateInputStream.readAllBytes();
		try (WebClient webClient = HtmlUnitToolkit.getWebClientCert(new ByteArrayInputStream(certByte),
				certificatePassword, certificateType)) {
			
			webClient.getOptions().setUseInsecureSSL(true);
			
			validateCertExpired(new ByteArrayInputStream(certByte), certificatePassword);
			HtmlPage htmlPage = getFirstPageSepeContrata(webClient);
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click();
			handleSepeExceptions(htmlPage);
			DomNode fielset = htmlPage.querySelector("form > fieldset");
			if (fielset != null && !fielset.getVisibleText().isEmpty()
					&& fielset.getVisibleText().indexOf("errores") >= 0) {
				DomNode error = fielset.querySelector("p");
				if (error != null && !error.getVisibleText().isEmpty())
					throw new SepeException(error.getVisibleText());
			}

		} catch (Exception e) {
			throw new SepeException(e.getMessage());
		}
	}

	private static void validateCertExpired(InputStream certificateInputStream, String certificatePassword) throws Exception {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(certificateInputStream, certificatePassword.toCharArray());
		Enumeration<?> aliases = keystore.aliases();
		Date expiryDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		for (; aliases.hasMoreElements();) {
			String alias = (String) aliases.nextElement();
			expiryDate = ((X509Certificate) keystore.getCertificate(alias)).getNotAfter();
			if (expiryDate.compareTo(cal.getTime()) < 0)
				throw new Exception("El certificado ha expirado");
		}
	}
	
	private static void checkContractExist(HtmlPage htmlPage) throws SepeException {
		Optional<String> exist = htmlPage.querySelectorAll("form[name=\"datos\"] fieldset div[class*=titulo]")
		.stream()
		.filter(e -> !e.getTextContent().isEmpty() && e.getTextContent().trim().toLowerCase().contains("ya se ha comunicado"))
		.map(e -> e.getTextContent().trim())
		.findFirst();
	
		if(exist.isPresent()) {
			throw new SepeException(exist.get());
		}
	}
	
	private static String getCifType(String cif) {
		String cifTypeStr = Toolkit.getIdentityType(cif);
		if (cifTypeStr.equals("1")) {
			return "D"; //NIF
		} else if (cifTypeStr.equals("6")) {
			return "E"; //NIE
		}
		return " "; //CIF
	}
	
	// BUILD A FILE FROM ARRAY OF BYTES
		public static void buildFile(byte[] bytes, String docName) {
			File f = new File(docName);
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(bytes);
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("Archivo no encontrado");
			} catch (IOException e) {
				System.err.println("Error al escribir");
			}

		}
}
