package solutions.aon.seg.social;

import static solutions.aon.seg.social.SistemaRED.PartType.ALTA;
import static solutions.aon.seg.social.SistemaRED.PartType.BAJA;
import static solutions.aon.seg.social.SistemaRED.PartType.CONFIRMACION;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getElConstains;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getWebClient;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.xml.XmlPage;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
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
	private static final String BASE_URI = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=IWXP0001";
	private static final String ARQ_SPM_OUT = "ARQ.SPM.OUT";

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
			throws SegSocialException {
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
	

	public static List<ITPart> getItsImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Optional<String> nss, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> dateBj)
			throws SegSocialException {
		SSLContext sslContext = null;
		
		try {
			
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray())
				.loadTrustMaterial(new TrustStrategy() {
	                @Override
	                 public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	                         return true;
	                 }
				})
				.build();
			
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, BASE_URI);
			Toolkit.checkProsaError(body);
			checkAuthorization(body);
			
			String link ="https://w2.seg-social.es" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_6"), "action");
			String ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");

			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair("SPM.CONTEXT", IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair(ARQ_SPM_OUT, "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("SPM.ACC.CONTINUAR_CONSULTA", "CONTINUAR_CONSULTA"));
			params.add(new BasicNameValuePair("regimenConsulta", regime));
			params.add(new BasicNameValuePair("cccConsulta", ccc));
			params.add(new BasicNameValuePair("nafConsulta", nss.isPresent() ? nss.get() : ""));
		
			if(dateBj.isPresent()) {
				Toolkit.formatDate(dateBj.get(), DATE_FORMAT).ifPresent(d-> params.add(new BasicNameValuePair("fechaBajaMedConsulta", d)));
			} else {
				params.add(new BasicNameValuePair("fechaBajaMedConsulta", ""));
			}
	
			if(startDate.isPresent()) {
				Toolkit.formatDate(startDate.get(), DATE_FORMAT).ifPresent(d-> params.add(new BasicNameValuePair("fechaDesdeConsulta", d)));
			} else {
				params.add(new BasicNameValuePair("fechaDesdeConsulta", ""));
			}
			
			if(endDate.isPresent()) {
				Toolkit.formatDate(endDate.get(), DATE_FORMAT).ifPresent(d-> params.add(new BasicNameValuePair("fechaHastaConsulta", d)));
			} else {
				params.add(new BasicNameValuePair("fechaHastaConsulta", ""));
			}
			
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
		
			try {			
			
				link = "https://w2.seg-social.es" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_6"), "action");
				ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");
				
				String xml = Toolkit.getBodyPOST(httpClient, httpPost);
				checkErrors(xml);
				
				return getParts(httpClient, xml, link, ticket, dateBj.isPresent());
			} catch (SAXException e) {
				e.printStackTrace();
				throw new SegSocialException(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}
	
	// REGISTER IT START
	private static byte[] registerItBajaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType,
			Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation, Optional<String> job,  Optional<String> jobDescription) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException, TransformerException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, startdate, contingency, situationEmployee, BAJA);

//			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
//			wait4(htmlPage, p -> p.querySelector("[name=\"fechaBaja\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
//			
//			form.getInputByName(ARQ_SPM_OUT).remove(); 
//			
//			Toolkit.formatDate(startdate, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBaja").setValue(d));

			if (job.isPresent()) {				
				((HtmlInput) htmlPage.querySelector("#puestoTrabajo")).setValue(job.get());
				((HtmlInput) htmlPage.querySelector("#puestoTrabajo")).setValueAttribute(job.get());
			}
			
			if (jobDescription.isPresent()) {				
				((HtmlTextArea) htmlPage.querySelector("#funcDesempe")).setText(jobDescription.get());
			}
			
			// Data Contract
			HtmlOption contractTypeOption = null;
			HtmlInput cotBaseInput = null;
			HtmlInput cotDaysInput = null;
			
			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL:
					contractTypeOption = htmlPage.querySelector("#tipoContrato option[value=\"1\"]");
					htmlPage = contractTypeOption.click();
					wait4(htmlPage, p -> p.getElementById("#sumaBaseCot")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
					cotBaseInput = htmlPage.querySelector("#sumaBaseCot");
					cotDaysInput = htmlPage.querySelector("#sumaDiasCot");
				break;
				case RESTO_Y_AUTONOMOS:
					contractTypeOption = htmlPage.querySelector("#tipoContrato option[value=\"2\"]");
					htmlPage = contractTypeOption.click();
					
					wait4(htmlPage, p -> p.getElementById("#BaseCot")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
					
					cotBaseInput = (HtmlInput) htmlPage.querySelector("#BaseCot");
					cotDaysInput = (HtmlInput) htmlPage.querySelector("#DiasCot");
				break;
			}
			
			if(fATEP.isPresent()) {
				DomNode inputATEP = htmlPage.querySelector("#fechaATEP");
				if(null != inputATEP) ((HtmlInput)inputATEP).setValue(Toolkit.formatDate(fATEP.get(), DATE_FORMAT).get());
			}
			
			String baseCotStr = Toolkit.parseDecimalToString(baseCot);
			if(cotBaseInput!=null && !baseCotStr.isEmpty()) {
				cotBaseInput.setValue(baseCotStr);
				cotBaseInput.setValueAttribute(baseCotStr);
			}

			if(cotDaysInput!=null && cotDays>0) {
				cotDaysInput.setValue(cotDays+"");
				cotDaysInput.setValueAttribute(cotDays+"");
			}
			
			if (occupation.isPresent()) {				
				((HtmlSelect) htmlPage.querySelector("#ocupacion")).setSelectedAttribute(occupation.get(), true);
			}
			
			Toolkit.buildFile(htmlPage.asXml().getBytes(), "/Users/svaldepenas/Desktop/it_2.html");
			
			HtmlButton validate = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Validar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			XmlPage xmlPage = validate.click();
			htmlPage = HtmlUnitToolkit.tranformXmlPage(xmlPage);
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML
			
			HtmlButton confim = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Confirmar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = confim.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			return getPdfProcess(htmlPage, "#ENVIO_10");
		}
	}

	// REGISTER IT END
	private static byte[] registerItAltaImpl(InputStream certificateInputStream, String certificatePassword,
				String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
				SistemaRED.SituationEmployee situationEmployee, Date fbaja, Date falta, Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, 
				SistemaRED.CauseType causeType, Optional<String> licenseNumber, Optional<String> cias)
				throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
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
			
			return getPdfProcess(htmlPage, "#ENVIO_10");
		}
	}

	// REGISTER IT CONFIRMATION
	private static byte[] registerItConfirmationImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Optional<String> licenseNumber, Optional<String> cias, Date fbaja,
			Date fconfirmation, Optional<String> npartConfimation) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
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
			throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = setUrlParseRemoveXml(htmlPage, (HtmlAnchor)htmlPage.getElementById("PEST_5"));
		
			wait4(htmlPage, p -> p.querySelector("[name=\"regimenEmision\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			form.getInputByName(ARQ_SPM_OUT).remove(); 

			form.getInputByName("regimenEmision").setValue(regime);
			form.getInputByName("cccEmision").setValue(ccc);
			form.getInputByName("nafEmision").setValue(nss);
			Toolkit.formatDate(dateBj, DATE_FORMAT).ifPresent(d-> form.getInputByName("fechaBajaMedEmision").setValue(d));
			
			HtmlButton continueIn = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[value=\"CONTINUAR_EMISION\"]")).orElseThrow();
			htmlPage = continueIn.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

			HtmlAnchor firstColumn = getOneAnchorPaginate2(htmlPage, partType, dateBj);
			
			if (firstColumn == null) {				
				throw new NoQueryData("Sin datos de consulta");
			}
			
			htmlPage = setUrlParseRemoveXml(htmlPage, firstColumn);
			
			return getPdfProcess2(htmlPage);
		}
	}
	
	private static HtmlPage fillGeneralData(HtmlPage htmlPage, String regime, String ccc, String naf, Date date,
			SistemaRED.Contingencies contingency, SistemaRED.SituationEmployee situationEmployee, SistemaRED.PartType type)
			throws IOException, InterruptedException, SegSocialException {
		HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
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

		HtmlButton accept = (HtmlButton) wait4(htmlPage, p ->p.getElementById("ENVIO_9")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
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
		
		HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		
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
	
	private static byte[] getPdfProcess2(HtmlPage htmlPage) throws InterruptedException, IOException, SegSocialException {
		
		HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		
		formTwo.getInputByName(ARQ_SPM_OUT).remove(); //PREVENT XML

		HtmlButton doc = (HtmlButton) wait4(htmlPage, p ->p.getElementByName("SPM.ACC.GENERAR_INFORME_EMISION")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		htmlPage = doc.click();
		
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
}
