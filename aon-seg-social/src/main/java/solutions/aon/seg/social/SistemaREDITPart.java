package solutions.aon.seg.social;

import static solutions.aon.seg.social.SistemaRED.PartType.BAJA;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.getElConstains;
import static solutions.aon.seg.social.toolkit.HtmlUnitToolkit.wait4;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.NoQueryData;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.It.ItBuilder;
import solutions.aon.seg.social.object.ItPartId;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDITPart extends ServicioREDPartRegeXML {
	
	//Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/test.html");
	private static final String MESSAGE_ERROR  = "Error no aceptada la comunicaci\u00f3n";
	private static final String TRY_AGAIN  = "Intente nuevamente!";
	private static final String FORMAT_DATE = "dd/MM/yyyy";
	private static final String BASE_URI = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=IWXP0001";

	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to, Optional<String> nss)
			throws SegSocialException {
		String link = "";
		String ticket = "";

		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, BASE_URI);
			Toolkit.checkProsaError(body);
			checkAuthorization(body);
			
			link ="https://w2.seg-social.es" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_6"), "action");
			ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");

			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair("SPM.CONTEXT", IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("SPM.ACC.CONTINUAR_CONSULTA", "CONTINUAR_CONSULTA"));
			params.add(new BasicNameValuePair("nafConsulta", ""));
			params.add(new BasicNameValuePair("fechaBajaMedConsulta", ""));
			params.add(new BasicNameValuePair("regimenConsulta", regime));
			params.add(new BasicNameValuePair("cccConsulta", ccc));
			
			nss.ifPresent(n-> params.add(new BasicNameValuePair("nafConsulta", n)));
	
			//DATES
			Toolkit.formatDate(from, FORMAT_DATE).ifPresent(d-> params.add(new BasicNameValuePair("fechaDesdeConsulta", d)));
			Toolkit.formatDate(to, FORMAT_DATE).ifPresent(d-> params.add(new BasicNameValuePair("fechaHastaConsulta", d)));
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
		
			try {			
			
				link = "https://w2.seg-social.es" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_6"), "action");
				ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");
				
				String xml = Toolkit.getBodyPOST(httpClient, httpPost);
				checkErrors(xml);
				
				return getParts(httpClient, xml, link, ticket);
			} catch (SAXException e) {
				e.printStackTrace();
				throw new SegSocialException(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}
	
	// REGISTER IT START HANDLE EXCEPTIONS
	public static byte[] registerItBaja(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType, Optional<String> licenseNumber, Optional<String> cias,
			Optional<String> occupation) throws SegSocialException {

		Toolkit.verifyData(new Object[] { regime, ccc, naf, contingency, situationEmployee, licenseNumber, cias,
				startdate, contractType, baseCot, cotDays });

		try {
			return registerItBajaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					contingency, situationEmployee,startdate, contractType, baseCot,
					cotDays, fATEP, accidentType, licenseNumber, cias, occupation);
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
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} 
	}

	// REGISTER IT START
	private static byte[] registerItBajaImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.Contingencies contingency,
			SistemaRED.SituationEmployee situationEmployee, Date startdate, SistemaRED.ContractType contractType, float baseCot, int cotDays,
			Optional<Date> fATEP, Optional<SistemaRED.AccidentType> accidentType,
			Optional<String> licenseNumber, Optional<String> cias, Optional<String> occupation) throws FailingHttpStatusCodeException, IOException, InterruptedException, SegSocialException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			webClient.getOptions().setUseInsecureSSL(true);
			
			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			htmlPage = fillGeneralData(htmlPage, regime, ccc, naf, contingency, situationEmployee, BAJA);

			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			wait4(htmlPage, p -> p.querySelector("[name=\"fechaBaja\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			form.getInputByName("ARQ.SPM.OUT").remove(); 
			
			Toolkit.formatDate(startdate, FORMAT_DATE).ifPresent(d-> form.getInputByName("fechaBaja").setValueAttribute(d));

			// Data Contract
			HtmlOption contractTypeOption = null;
			HtmlInput cotBaseInput = null;
			HtmlInput cotDaysInput = null;
	
			switch (contractType) {
				case FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL:
					contractTypeOption = form.querySelector("#tipoContrato option[value=\"1\"]");
					cotBaseInput = form.getInputByName("sumaBaseCot");
					cotDaysInput = form.getInputByName("sumaDiasCot");
				break;
				case RESTO_Y_AUTONOMOS:
					contractTypeOption = form.querySelector("#tipoContrato option[value=\"2\"]");
					cotBaseInput = form.getInputByName("BaseCot");
					cotDaysInput = form.getInputByName("DiasCot");	
				break;
			}
			
			if(null!=contractTypeOption) contractTypeOption.click();

			String baseCotStr = Toolkit.parseDecimalToString(baseCot);
			if(cotBaseInput!=null && !baseCotStr.isEmpty()) {
				cotBaseInput.setValueAttribute(baseCotStr);
			}

			if(cotDaysInput!=null && cotDays>0) {
				cotDaysInput.setValueAttribute(cotDays+"");
			}
			
			if (occupation.isPresent()) {				
				((HtmlSelect) form.querySelector("#ocupacion")).setSelectedAttribute(occupation.get(), true);
			}
			
			licenseNumber.ifPresent(d-> form.getInputByName("numcolegiado").setValueAttribute(d));
			cias.ifPresent(c-> form.getInputByName("cias").setValueAttribute(c));

			if (fATEP.isPresent()) {
				Toolkit.formatDate(fATEP.get(), FORMAT_DATE).ifPresent(d-> form.getInputByName("fechaATEP").setValueAttribute(d));
			}

			if (accidentType.isPresent()) {
				HtmlOption typeAccidentOption = null;
				switch (accidentType.get()) {
				case LEVE:
					typeAccidentOption = form.querySelector("#tipoAccidente option[value=\"1\"]");
					break;
				case GRAVE:
					typeAccidentOption = form.querySelector("#tipoAccidente option[value=\"2\"]");
					break;
				case MUY_GRAVE:
					typeAccidentOption = form.querySelector("#tipoAccidente option[value=\"3\"]");
					break;
				}
				if(null!=typeAccidentOption) typeAccidentOption.click();
			}
			
			HtmlButton validate = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Validar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = validate.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			formTwo.getInputByName("ARQ.SPM.OUT").remove(); //PREVENT XML
			
			HtmlButton confim = (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[type=\"submit\"][title=\"Confirmar\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			htmlPage = confim.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			return getPdfProcess(htmlPage);
		}
	}
	
	// remove ITImpl
	private static void removeItImpl(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String naf, SistemaRED.PartType partType, Date dateBj, Date dateProcess ) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = webClient.getPage(BASE_URI);
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			htmlPage = setUrlParseRemoveXml(htmlPage, (HtmlAnchor)htmlPage.getElementById("PEST_4"));
		
			wait4(htmlPage, p -> p.querySelector("[name=\"regimenAnulacion\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			
			HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			form.getInputByName("ARQ.SPM.OUT").remove(); 

			form.getInputByName("regimenAnulacion").setValueAttribute(regime);
			form.getInputByName("cccAnulacion").setValueAttribute(ccc);
			form.getInputByName("nafAnulacion").setValueAttribute(naf);
			Toolkit.formatDate(dateBj, FORMAT_DATE).ifPresent(d-> form.getInputByName("fechaBajaMedAnulacion").setValueAttribute(d) );
			
			HtmlButton continueIn= (HtmlButton) wait4(htmlPage, p ->p.querySelector("button[value=\"CONTINUAR_ANULACION\"]")).orElseThrow();
			htmlPage = continueIn.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);

			HtmlAnchor firstColumn = getOneAnchorPaginate(htmlPage, partType, dateProcess);
			
			if (firstColumn == null) {				
				throw new NoQueryData("Sin datos de consulta");
			}
			
			htmlPage = setUrlParseRemoveXml(htmlPage, firstColumn);
			
			HtmlForm formCancel = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			formCancel.getInputByName("ARQ.SPM.OUT").remove(); 

			HtmlButton anular = (HtmlButton) formCancel.querySelector("button[value=\"ACEPTAR_ANULACION\"]");
			htmlPage = anular.click();
			HtmlUnitToolkit.handleNewSegSocialExceptions(htmlPage);
			
			HtmlForm formConfirm = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
			formConfirm.getInputByName("ARQ.SPM.OUT").remove(); 

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

	private static HtmlPage fillGeneralData(HtmlPage htmlPage, String regime, String ccc, String naf,
			SistemaRED.Contingencies contingency, SistemaRED.SituationEmployee situationEmployee, SistemaRED.PartType type)
			throws IOException, InterruptedException, SegSocialException {
		HtmlForm form = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		wait4(htmlPage, p ->p.querySelector("[name=\"regimen\"]")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));

		form.getInputByName("regimen").setValueAttribute(regime); 
		form.getInputByName("ccc").setValueAttribute(ccc); 
		form.getInputByName("naf").setValueAttribute(naf); 
		
		form.getInputByName("ARQ.SPM.OUT").remove(); 
		
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

	
	private static Collection<It> getParts(CloseableHttpClient httpClient, String xml, String link, String ticket) throws ParserConfigurationException, IOException, SegSocialException {
		List<ITPart> parts = new ArrayList<>();
		List<ITPart> last = new ArrayList<>();
		String error = "";
		boolean next;
	
		do {
			next = false;
			try {
				List<ITPart> aux = new ArrayList<>();
				Map<Integer, ITPart> map = getInfoPartsByXml(xml);
				for (Map.Entry<Integer, ITPart> entry : map.entrySet()) {
					Integer position = entry.getKey();
					ITPart part = entry.getValue();
					
					String newLink = link+"?SPM.CONTEXT=internet&ARQ.SPM.OUT=XML_STYLESHEET&ES_FW4=1&SPM.HAYJS=1&ARQ.SPM.TICKET="+ticket+"&SPM.ISPOPUP=0&SPM.ACC.DETALLES_PARTE_CONSULTA=DETALLES_PARTE_CONSULTA&position="+position;
					
					String newXml = Toolkit.getBodyGET(httpClient, newLink);
					setInfoPart(newXml, part);
				
					if (!parts.contains(part)) {										
						aux.add(part);
					}
					
					next = position >=10;
				}
		
				if(!last.containsAll(aux)) {
					parts.addAll(aux);
					last = aux;
					if(next) {
						xml = Toolkit.getBodyGET(httpClient, link+"?SPM.CONTEXT=internet&ARQ.SPM.OUT=XML_STYLESHEET&ES_FW4=1&SPM.HAYJS=1&ARQ.SPM.TICKET="+ticket+"&SPM.ISPOPUP=0&SPM.ACC.PAG_SIG_CONSULTA=PAG_SIG_CONSULTA");
					}
				} else {
					next = false;
				}
		    } catch (SAXException e) {
				e.printStackTrace();
				error = e.getMessage();
				next = false;
			}
		} while(next);
		
		if(parts.isEmpty() && !error.isEmpty()) {
			throw new SegSocialException(error);
		} else {
			return orderByIT(parts);
		}
	}
	
	private static List<It> orderByIT(List<ITPart> itParts) {
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
		
		ArrayList<It> its = new ArrayList<>();
		Set<ItPartId> partIds = orderedItParts.keySet();
		ItBuilder builder = new ItBuilder();
		
		for (ItPartId id : partIds) {
	
			itParts =  (List<ITPart>) orderedItParts.get(id);
			ITPart end = null;
			ITPart start = null;
			ArrayList<ITPart> confirmations = new ArrayList<>();

			for (ITPart itp : itParts) {
				String partStr = itp.getPartType().toLowerCase();
				if (partStr.indexOf("baja")>-1 || partStr.indexOf("PB")>-1) {					
					start = itp;
				} else if ((partStr.indexOf("confirmaci\u00F3n")>-1 || partStr.indexOf("PC")>-1)  && !confirmations.contains(itp)) {					
					confirmations.add(itp);
				} else if (partStr.indexOf("alta")>-1 || partStr.indexOf("PA")>-1) {					
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
	
	private static byte[] getPdfProcess(HtmlPage htmlPage) throws InterruptedException, IOException, SegSocialException {
		
		HtmlForm formTwo = (HtmlForm) wait4(htmlPage, p -> p.getElementById("FORMULARIO_6")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
		
		formTwo.getInputByName("ARQ.SPM.OUT").remove(); //PREVENT XML

		HtmlButton continueIn = (HtmlButton) wait4(htmlPage, p ->p.querySelector("#ENVIO_10")).orElseThrow(()-> new SegSocialException(TRY_AGAIN));
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
	
	private static HtmlAnchor getOneAnchorPaginate(HtmlPage htmlPage, SistemaRED.PartType partType, Date date)
			throws IOException {
		
		String dateStr = Toolkit.formatDate(date, FORMAT_DATE).get();
		
		HtmlTable table = (HtmlTable) htmlPage.querySelector("#FORMULARIO_6 table");
		HtmlAnchor next = null;		
		HtmlAnchor firstColumn = null;
		boolean last = false;
		int numberCell = 0;
		String anulado = "No";
		
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
					if (fCell.getVisibleText().equalsIgnoreCase(dateStr)
							&& typeCell.getVisibleText().equalsIgnoreCase(partType.getDescription())
							&& anulCell.getVisibleText().equalsIgnoreCase(anulado)) {
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
	
	private static HtmlPage setUrlParseRemoveXml(HtmlPage htmlPage, HtmlAnchor link) throws IOException {
		link = HtmlUnitToolkit.setUrlParse(htmlPage, link);
		link.setAttribute("href", link.getHrefAttribute().replace("&ARQ.SPM.OUT=XML_STYLESHEET", ""));
		htmlPage = link.click();
		return htmlPage;
	}
}
