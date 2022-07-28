package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.in.payroll.AonComunica;
import com.esferalia.aon.in.payroll.tgss.report.CCCLaboralLife;
import com.esferalia.aon.in.payroll.utils.EmployeeParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Occupation;
import com.esferalia.aon.occam.api.model.type.QuoteGroup;
import com.esferalia.aon.occam.api.model.type.RLCE;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import net.aonsolutions.aon.api.utils.ComunicaUtils;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;
import solutions.aon.seg.social.ServicioRED;
import solutions.aon.seg.social.ServicioREDEmployee;
import solutions.aon.seg.social.ServicioREDMov;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.sepe.Sepe;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	private static final String APP_PARAMS_NAME = "APP_COMUNICA_SINCRONIZED"; 
	private static final String APP_COMUNICA_EMAILS = "APP_COMUNICA_EMAILS"; 
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMUNICA SERVLET");
		try {		
			String path = req.getPathInfo()!= null || IConstants.EMPTY.equalsIgnoreCase(req.getPathInfo()) 
					? req.getPathInfo() : IConstants.ROOT_BAR;
			switch (path) {
				case "/app-param":
					LOGGER.info("APP-PARAM SERVLET - GET METHOD");
					response(req, resp,	getAppParam(initialize(req)));
				break;
				case "/rlce":
					LOGGER.info("RLCE SERVLET - GET METHOD");
					response(req, resp,	getRlce());
				break;
				case "/occupation":
					LOGGER.info("OCCUPATION SERVLET - GET METHOD");
					response(req, resp,	getOccupation());
				break;
				case "/quote-group":
					LOGGER.info("QUOTE-GROUP SERVLET - GET METHOD");
					response(req, resp,	getQuoteGroup());
				break;
				case "/contract-type":
					LOGGER.info("CONTRACT-TYPE SERVLET - GET METHOD");
					response(req, resp,	getContractType());
				break;
				case "/ipfxnaf":
					LOGGER.info("IPFXNAF SERVLET - GET METHOD");
					response(req, resp, getIpfxNaf(initialize(req)));
				break;
				case "/nafxipf":
					LOGGER.info("NAFXIPF SERVLET - GET METHOD");
					response(req, resp, getNafxIpf(initialize(req)));
				break;
				case "/update-contracts":
					LOGGER.info("UPDATE-CONTRACTS SERVLET - GET METHOD");
					response(req, resp, updateContracts(initialize(req)));
				break;
				case "/get-ta":
					LOGGER.info("GET-TA");
					responseFile(resp, "TA", getTA(initialize(req)), MimeType.PDF);
				break;
				case "/get-idc":
					LOGGER.info("GET-IDC");
					responseFile(resp, "IDC", getIDC(initialize(req)), MimeType.PDF);
					break;
				case "/cert-corriente":
					LOGGER.info("CERT-CORRIENTE");
					responseFile(resp, "CERT_CORRIENTE", getCertCorriente(initialize(req)), MimeType.PDF);
					break;
				case "/get-idc-ccc":
					LOGGER.info("GET-IDC-CCC");
					responseFile(resp, "IDC_CCC", getIdcCcc(initialize(req)), MimeType.PDF);
					break;
				case "/get-report-affiliate-in-alta":
					LOGGER.info("GET-REPORT-AFFILIATE-IN-ALTA");
					responseFile(resp, "AFILIATE_IN_ALTA", getReportAffiliateInAlta(initialize(req)), MimeType.PDF);
					break;
				case "/get-report-affiliate-in-mov-prev":
					LOGGER.info("GET-REPORT-AFFILIATE-IN-MOV-PREV");
					responseFile(resp, "AFILIADO_MOV_PREV", getReportAffiliateInMovPrev(initialize(req)), MimeType.PDF);
					break;
				case "/get-contract-sepe":
					LOGGER.info("GET-CONTRACT-SEPE");
					responseFile(resp, "CONTRACT", getContractSepe(initialize(req)), MimeType.PDF);
					break;
				case "/get-copy-basic":
					LOGGER.info("GET-COPY-BASIC");
					responseFile(resp, "COPY_BASIC", getCopyBasicSepe(initialize(req)), MimeType.PDF);
					break;
				default:
					doGetGson(req, resp);
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON COMUNICA SERVLET POST");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/alta-directa":
					LOGGER.info("ALTA-DIRECTA SERVLET - POST METHOD");
					response(req, resp, sendAlta(api));
				break;
				case "/baja":
					LOGGER.info("BAJA SERVLET - POST METHOD");
					response(req, resp, sendBaja(api));
				break;
				case "/delete-mov":
					LOGGER.info("DELETE-MOV SERVLET - POST METHOD");
					response(req, resp, movDelete(api));
				break;
				case "/update-contrato":
					LOGGER.info("UPDATE-CONTRATO SERVLET - POST METHOD");
					response(req, resp, updateContract(api));
				break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void doGetGson(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
		    Gson gjson = new GsonBuilder().setDateFormat(FORMAT_DATE).create();
		    String jsonInString = null;
			switch (api.getPath()) {
				case "/get-employee":
					LOGGER.info("GET-EMPLOYEE SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getEmployee(api));
					break;
				case "/movements":
					LOGGER.info("MOVEMENTS SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getMovements(api));
					break;
				case "/movements-cccs":
					LOGGER.info("MOVEMENTS CCCS SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getMovementsCcc(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
		} catch (Exception e) {
			error(req, resp, e);
		}
	
	}
	
	private Collection<Employee> getMovements(AonApiData api) throws SegSocialException {
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");

		Map<String, Set<String>> map = new LinkedHashMap<>();

		PAYROLL.getCCCStream(domain.getName(), domain.getId(), api.getUser().getLogin())
		.filter(ComunicaUtils.distinctByKey(CCCInfo::getCccAccount))
		.forEach(ccc -> {
			String regime = ccc.getCccRegimeCode();
			String ctaCti = ccc.getCccAccount();
		
			Set<String> defaultSet = map.getOrDefault(regime, new LinkedHashSet<>());
			defaultSet.add(ctaCti);
			
			map.put(regime, defaultSet);
        });	
			    
	    return ServicioREDEmployee.getTotalEmployees(certificate.getData(), certificate.getPassword(), certificate.getType(), map);
	}
	
	private ArrayList<com.esferalia.aon.in.payroll.tgss.report.Employee> getMovementsCcc(AonApiData api) {
		ArrayList<com.esferalia.aon.in.payroll.tgss.report.Employee> employees = new ArrayList<>();
		Domain domain = api.getDomain();
		JSONObject params = api.getData();
		User user = api.getUser();

		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), "TGSS");
		List<String> errors = new ArrayList<>();
		
		Date startDate = !params.optString("startDate").isEmpty() ? AonDateUtils.parse(params.optString("startDate"), FORMAT_DATE) : new Date();
		Date endDate = !params.optString("endDate").isEmpty() ? AonDateUtils.parse(params.optString("endDate"), FORMAT_DATE) : new Date();
		
		PAYROLL.getCCCStream(domain.getName(), domain.getId(), user.getLogin()).forEach(ccc -> {
		  try {
			  byte[] pdf = ServicioREDEmployee.getCccLaboralLifePOST(
					new ByteArrayInputStream(certificate.getData()), 
					certificate.getPassword(), 
					certificate.getType(), 
					ccc.getCccRegimeCode(), 
					ccc.getCccAccount(), 
					startDate, 
					endDate
			  );
		      employees.addAll(CCCLaboralLife.parse(new ByteArrayInputStream(pdf), new com.esferalia.aon.in.payroll.tgss.report.Employee.EmployeeBuilder()));
		  } catch(InvalidCertificateException e) {
		      e.printStackTrace();
		      errors.add(e.getClass().getSimpleName());
		  } catch(Exception e) {
			  e.printStackTrace();
		  }
		});	

		if(!errors.isEmpty()) {
			throw new AonApiException(errors.get(0));
		}
		
		return employees;
	}
	
	private JSONArray getRlce() {
		JSONArray arr = new JSONArray();
		for( Entry<String, String> rlce : RLCE.getRLCE().entrySet()) 
			arr.put(new JSONObject().put(IJsonNames.VALUE, rlce.getKey()).put(IJsonNames.NAME, rlce.getValue()));
		return arr;
	}
	
	private JSONArray getContractType() {
		 JSONArray arr = new JSONArray();
		for( Entry<Integer, ContractTypeRecord> contractType : new ContractType().getContractTypes().entrySet()) 
			arr.put(new JSONObject().put(IJsonNames.VALUE, contractType.getKey()).put(IJsonNames.NAME, contractType.getValue()));
		return arr;
	}
	
	private JSONArray getOccupation() {
		JSONArray arr = new JSONArray();
		for (Entry<String, String> v : Occupation.getOccupation().entrySet()) {
			String value = 	v.getValue();
			if(v.getValue()!=null && v.getValue().equals("-1")) value = " ";
			arr.put(new JSONObject().put(IJsonNames.NAME,  v.getKey()).put(IJsonNames.VALUE,value));		
		}
	
		return arr;
	}
	
	private JSONArray getQuoteGroup() {
		JSONArray arr = new JSONArray();
		List<String> quotes = Arrays.asList("08", "09", "10", "11");
		
		for (Entry<String, String> v : QuoteGroup.getQuoteGroup().entrySet()) {
			arr.put(
					new JSONObject()
					.put(IJsonNames.VALUE, v.getValue())
					.put(IJsonNames.NAME,  v.getKey())
					.put("quoteMonth", quotes.contains(v.getValue()) )

			);	
		}
		return arr;
	}

	private byte[] getTA(AonApiData api) throws Exception {
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		JSONObject params = api.getData();
		SituationType situationType = SituationType.ALTA;
		String regime = params.optString(IJsonNames.REGIME);
		String ccc = params.optString("ctaCti");
		String nss = params.optString("nss");
		Date date = AonDateUtils.parse(params.optString("fra"), FORMAT_DATE);
		String frb = params.optString("frb");
		if(!frb.isEmpty()) {
			date = AonDateUtils.parse(frb, FORMAT_DATE);
			situationType = SituationType.BAJA;
		}

	    return ServicioRED.getTADuplicatePOST(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ccc, regime, situationType, nss, date);		
	}
	
	private byte[] getIDC(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String regime     = params.optString(IJsonNames.REGIME);
		String ccc        = params.optString("ctaCti");
		String nss        = params.optString("nss");
		Date date         = AonDateUtils.parse(params.optString("fra"), FORMAT_DATE);
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		
		return ServicioRED.getIDCPOST(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), nss, regime, ccc, date);
	}
	
	private byte[] getCertCorriente(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String regime = params.optString(IJsonNames.REGIME);
		String ccc    = params.optString("ccc");
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		
	    return SistemaRED.getUp2DateSS(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regime, ccc);	
	}
	
	private byte[] getIdcCcc(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String regime = params.optString(IJsonNames.REGIME);
		String ccc    = params.optString("ccc");
		Date fecha    = AonDateUtils.parse(params.optString("fecha"), FORMAT_DATE); 
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
	
	    return SistemaRED.getIDCCCC(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regime, ccc, fecha);	
	}
	
	private byte[] getReportAffiliateInAlta(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String regime = params.optString(IJsonNames.REGIME);
		String ccc    = params.optString("ccc");
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");

	    return SistemaRED.getReportAffiliateInAlta(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regime, ccc);	
	}
	
	private byte[] getReportAffiliateInMovPrev(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String regime = params.optString(IJsonNames.REGIME);
		String ccc    = params.optString("ccc");
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");

	    return SistemaRED.getReportAffiliateInMovPrev(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regime, ccc);	
	}
	
	private byte[] getContractSepe(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String ipf = params.optString("ipf");
		Date date = AonDateUtils.parse(params.optString("startDate"), FORMAT_DATE); 
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "SEPE");
		
		return Sepe.getContratoPdf(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ipf, date, date);
	}
	
	private byte[] getCopyBasicSepe(AonApiData api) throws Exception {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		
		String ipf = params.optString("ipf");
		Date fecha = AonDateUtils.parse(params.optString("fecha"), FORMAT_DATE); 
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "SEPE");

		return Sepe.getCopyBasicPdf(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ipf, fecha, fecha);	
	}	
	
	private JSONArray getIpfxNaf(AonApiData api) throws SegSocialException, IOException, ParserConfigurationException {
			Domain domain     = api.getDomain();
			JSONObject params = api.getData();
			
			String nss = params.optString("nss");
			if(nss.isEmpty()) {				
				throw new AonApiException("nss requerido");
			}
			
			
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");

			JSONArray arr = new JSONArray();			
			final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

		    ArrayList<String> nssList = new ArrayList<>();
		    nssList.add(nss);	
		    
		    List<Employee> list = ServicioREDMov.ipfxnaf(certificateInputStream, certificate.getPassword(), certificate.getType(), nssList);
		    for (Employee employee : list) {
				JSONObject json = new JSONObject();
				json.put("ipf", employee.getIpf());
				json.put("nss", nss);
				employee.getName().ifPresent(name-> json.put(IJsonNames.NAME,name) );
				arr.put(json);
			}
		    return arr;
	}
	
	private JSONObject getNafxIpf(AonApiData api) throws SegSocialException {
		Domain domain     = api.getDomain();//
        JSONObject params = api.getData(); 
		
		String ipf       =  params.optString("ipf");
		String apellido1 =  params.optString("apellido1");
		String apellido2 =  params.optString("apellido2");
		
		JSONObject json = new JSONObject();
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		
		Employee employee = SistemaRED.nafxipf(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ipf, apellido1, apellido2);
		json.put("ident", employee.getIdent());
		json.put("ipf", employee.getIpf());
		json.put("nss", employee.getNss());
		employee.getName().ifPresent(name-> json.put(IJsonNames.NAME,name) );
		
		return json;
	}
	
	private JSONObject sendAlta(AonApiData api) throws Exception{
		JSONObject params = api.getData(); 
		ComunicaUtils.validateAlta(params);
		
	    Domain domain = api.getDomain();
		User user = api.getUser();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), "TGSS");

		String regime      = params.optString(IJsonNames.REGIME);
		String name        = params.optString(IJsonNames.NAME);
		String ctaCti      = params.optString("ctaCti");
		String nss         = params.optString("nss");
		String ipf         = params.optString("ipf");
		String gc          = params.optString("gc");
		String contract    = params.optString("contract");	
		String ocup        = params.has("ocup") && !params.isNull("ocup") ? params.optString("ocup") : null;
		String coef        = params.has("coef") && !params.isNull("coef") ? params.optString("coef") : null;
		String convenio    = params.has("convenio") && !params.isNull("convenio") ? params.optString("convenio") : "60888888888888";
		String rlce        = params.has("rlce") && !params.isNull("rlce") ? params.optString("rlce") : null; 
		String collective  = params.has("collective") && !params.isNull("collective") ? params.optString("collective") : null;
		String modCtz      = params.has("md_ctz") && !params.isNull("md_ctz") ? params.optString("md_ctz") : null; //para regime agrario
		Boolean quoteMonth = params.optBoolean("quoteMonth");
		Date   fra         = AonDateUtils.parse(params.optString("fecha"), FORMAT_DATE);

		EmployeeBuilder builder = new EmployeeBuilder()
		.setRegime(regime)
		.setName(name)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fra)
		.setOcup(ocup)
		.setColec(convenio)
		.setGc(gc)
		.setContract(contract)
		.setCollective(collective)
		.setRlce(rlce)
		.setMdctz(modCtz)
		.setQuoteMonth(quoteMonth);
		
		if(coef!=null) {
			Integer fact = Integer.parseInt(coef);
			builder.setFactor( Double.valueOf(fact) / 1000 );
		}
	
		Employee employee = builder.build();
		
		AonComunica.communicateAlta(employee, certificate);
		
		try {AonComunica.addContract(domain, EmployeeParse.toEmployeeOccam(employee), certificate);} 
		catch (Exception e) {}

		if(employee.getName().isPresent()) {			
			sendMovEmailNotification(api, employee, fra, SituationType.ALTA, certificate);
		}
		
		return new JSONObject();
	}
	
	private JSONObject sendBaja(AonApiData api) throws Exception{
		JSONObject params = api.getData(); 
	    Domain domain     = api.getDomain();
		User user         = api.getUser();
		
		String regime    = params.optString(IJsonNames.REGIME);
		String ctaCti    = params.optString("ctaCti");
		String nss       = params.optString("nss");
		String ipf       = params.optString("ipf");
		String name      = params.optString(IJsonNames.NAME);
		String situation = params.optString("situation");
		Date frb         = AonDateUtils.parse(params.optString("fechaBaja"), FORMAT_DATE);
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), "TGSS");

		EmployeeBuilder builder = new EmployeeBuilder();
		
		builder
		.setRegime(regime)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(frb)
		.setFrb(frb)
		.setName(name)
		.setSituation(situation);
		
		if(!params.optString("frv").isEmpty()) {
			builder.setFrv(AonDateUtils.parse(params.optString("frv"), FORMAT_DATE));
			if(!params.optString("asociativeSA").isEmpty()) {
				builder.setAsociativeSA(params.optString("asociativeSA"));
			}
		}
		
		Employee employee = builder.build();
		
		SistemaRED.sendBaja(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), employee);
		
		if(employee.getName().isPresent()) {
			sendMovEmailNotification(api, employee, frb, SituationType.BAJA, certificate);
		}
		
		return new JSONObject();
	}
	
	private JSONObject movDelete(AonApiData api) throws Exception {
		JSONObject params = api.getData(); 
		Domain domain     = api.getDomain();
		User user         = api.getUser();
		
		//first screen
		String regime = params.optString(IJsonNames.REGIME);
		String name   = params.optString(IJsonNames.NAME);
		String frb    = params.optString("frb");
		String ctaCti = params.optString("ctaCti");
		String nss    = params.optString("nss");
		String ipf    = params.optString("ipf");
		Date date     = AonDateUtils.parse(params.optString("fra"), FORMAT_DATE);
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), "TGSS");

		com.esferalia.aon.occam.api.model.payroll.Employee employee = new com.esferalia.aon.occam.api.model.payroll.Employee()
		.setStartDate(date)
		.setRegime(regime)
		.setCcc(ctaCti)
		.setNaf(nss)
		.setDni(ipf)
		;
		
		if(!frb.isEmpty()) {
			Date endDate = AonDateUtils.parse(frb, FORMAT_DATE);
			employee.setEndDate(endDate);
			date = endDate;
		}
		
		AonComunica.deleteContract(certificate.getData(), certificate.getPassword(), certificate.getType(), domain, employee , true);
		
		if(!name.isEmpty()) {
			SituationType situationType = employee.getEndDate().isEmpty() ? SituationType.ALTA : SituationType.BAJA;
			String body = "Te informamos que se ha realizado una <b>Eliminación de "+situationType.getName()+"</b> en la Seguridad Social de <b>"
			+name+"</b> en la Cuenta de Cotización <b>"+regime+"-"+ctaCti+"</b> con fecha <b>"+AonDateUtils.simpleFormat(date)+"</b>";
			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
			sendNotification(api, body);
		}

		return new JSONObject();
	}
	
	private Employee getEmployee(AonApiData api) throws Exception{
        JSONObject params = api.getData(); 
		Domain domain     = api.getDomain();

		String regime     = params.optString(IJsonNames.REGIME);
		String ccc        = params.optString("ctaCti");
		String nss        = params.optString("nss");
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		
		return SistemaRED.getEmployee(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regime, ccc, nss);	
	}
	
	private JSONObject updateContract(AonApiData api) throws Exception {
		
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
		
		JSONObject json = new JSONObject();
		JSONArray errors = new JSONArray();
		if(api.getData().isNull("fecha")) {
			throw new Exception("Fecha requerida");
		}
	
		json.put("contract_edit", false);
		
		updateOccupation(api, certificate, json, errors);
	
		updateGrupCtz(api, certificate, json, errors);

		updateFactor(api, certificate, json, errors);
		
		json.put("errors", errors);
		
		return json;
	}
	
	private void updateOccupation(AonApiData api, Certificate certificate, JSONObject json, JSONArray errors){
		JSONObject params = api.getData();
		String ocup       = params.optString("ocup");
		String regime     = params.optString(IJsonNames.REGIME);
		String ctaCti     = params.optString("ctaCti");
		String nss        = params.optString("nss");
		String ipf        = params.optString("ipf");
		String name       = params.optString(IJsonNames.NAME);
		Date date         = AonDateUtils.parse(params.optString("fecha"), FORMAT_DATE);
		
		if(!ocup.isEmpty() && params.optBoolean("ocup_edit")) {
            try {
            	SistemaRED.cambioOcupacion( new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ipf, regime, ctaCti, nss, ocup, date);
            	
            	json.put("ocup_edit", true);
            	json.put("contract_edit", true);
            	
            	if(!name.isEmpty()) {
        			String body = "Te informamos que se ha realizado un Cambio de ocupación a "
        					+ "(<b>"+ocup.toUpperCase()+"</b>) en la Seguridad Social de <b>"+ name+"</b> en la Cuenta de Cotización <b>"
        					+ regime+"-"+ctaCti+"</b> con fecha <b>"+AonDateUtils.simpleFormat(date)+"</b>";
        			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
        			sendNotification(api, body);
        		}
            } catch(Exception e) {
            	e.printStackTrace();
            	errors.put(e.getMessage());
            }	
		}
	}
	
	private void updateGrupCtz(AonApiData api, Certificate certificate, JSONObject json, JSONArray errors){
		JSONObject params = api.getData();
		String regime     = params.optString(IJsonNames.REGIME);
		String name       = params.optString(IJsonNames.NAME);
		String ctaCti     = params.optString("ctaCti");
		String nss        = params.optString("nss");
		String ipf        = params.optString("ipf");
		String gc         = params.optString("gc");
		Date date         = AonDateUtils.parse(params.optString("fecha"), FORMAT_DATE);
		if(!gc.isEmpty() && params.optBoolean("gc_edit")) {
			 try{
				 SistemaRED.cambioGrupCtz(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ipf, regime, ctaCti, nss, gc, date);
				 json.put("gc_edit", true);
				 json.put("contract_edit", true);
            	 if(!name.isEmpty()) {
        			String body = "Te informamos que se ha realizado un Cambio de Grupo de cotización a (<b>"+gc+"</b>) en la Seguridad Social de <b>"
            	 + name+"</b> en la Cuenta de Cotización <b>"+ regime+"-"+ctaCti+"</b> con fecha <b>"+AonDateUtils.simpleFormat(date)+"</b>";
        			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
        			sendNotification(api, body);
        		 }
			} catch(Exception e) {
				e.printStackTrace();
				errors.put(e.getMessage());
			}	
		}
	}
	
	private void updateFactor(AonApiData api, Certificate certificate, JSONObject json, JSONArray errors){
		JSONObject params = api.getData();
		
		String regime   = params.optString(IJsonNames.REGIME);
		String ctaCti   = params.optString("ctaCti");
		String nss      = params.optString("nss");
		String ipf      = params.optString("ipf");
		String coef     = params.optString("coef");
		String contract = params.optString("contract");
		Date date       = AonDateUtils.parse(params.optString("fecha"), FORMAT_DATE);
		
		if(params.optBoolean("coef_edit") || params.optBoolean("contract_edit")) {
			 try{
				 SistemaRED.cambioContratoCoef(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
						 ipf, regime, ctaCti, nss, date, Optional.ofNullable(contract), coef
				);
				 json.put("coef_edit", true);
				 json.put("contract_edit", true);
			} catch(Exception e) {
				e.printStackTrace();
				errors.put(e.getMessage());
			}	
		}
	}
	
	private void sendMovEmailNotification(AonApiData api, Employee employee, Date date, SituationType situation, Certificate certificate){
		Thread newThread = new Thread(() -> {
			try {
				User user = api.getUser();
				String pre =  situation.equals(SituationType.ALTA) ? "el" : "la";
				Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), user.getLogin());
				String subject = "TGSS | "+situation.getName()+" de "+employee.getName().get();
				String body = "La Tesorería General de la Seguridad Social ha procedido a reconocer "+pre+" <b>"+situation.getName()+"</b> "
						+ "en el Régimen General de D./Dña. <b>"+employee.getName().get()+"</b>, "
						+ "con número de afiliación <b>"+employee.getNss()+"</b> y DNI/NIE <b>"+employee.getIpf()+"</b>, con fecha <b>"+AonDateUtils.simpleFormat(date)+"</b>, "
						+ "como trabajador de <b>"+company.getName()+"</b> "
						+ "con código de cuenta de cotización <b>"+employee.getRegime()+" "+ employee.getCtaCti().get()+"</b>.";
			
				//---------------------------SEND NOTIFICATION
				sendNotification(api, body); 

				//--------------------SEND EMAIL
				String regime = employee.getRegime();
				String ccc = employee.getCtaCti().get();
				String nss = employee.getNss();

				LinkedList<File> files = new LinkedList<>();
				
				try {
					byte[] fileByte = ServicioRED.getTADuplicatePOST(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), ccc, regime, situation, nss, date);
					File file = File.createTempFile("duplicateTA", ".pdf");
					FileOutputStream os = new FileOutputStream(file);
		            os.write(fileByte);
		            os.close();
					files.add(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Date now = new Date();
					Date newDate = date.compareTo(now) > 0 ? now : date;
					byte[] fileByte = ServicioRED.getIDCPOST(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), nss, regime, ccc, newDate);
					File file = File.createTempFile("duplicadoIDC", ".pdf");
					FileOutputStream os = new FileOutputStream(file);
		            os.write(fileByte);
		            os.close();
					files.add(file);
				}catch (Exception e) {
					e.printStackTrace();
				}
				sendEmail(api, subject, body, files);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	private List<String> getEmails(AonApiData api, User user) {

	    List<String> toList = new LinkedList<>();

	    String alternative = JsonUtils.optString(api.getData(), "alternative");
	 
	    if(alternative!=null && !alternative.isEmpty()) {
	    	toList.add(alternative);
	    }
	 
	    // ------------------------ MY USER -------------------
		User newUser = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), user.getLogin(), f -> f.getIdProperty().eq(user.getId()));
		Auth auth = AON_SOLUTIONS.getAuth(newUser.getAuth().getAuth());
		
		if(auth.getEmail()!=null) {
			toList.add(auth.getEmail());
		}

		//---------------USER CONFIG--------------
		List<String> list = getEmailsAppParams(api);
		if(!list.isEmpty()) {
			toList.addAll(list);
		}
    
		return toList.stream().distinct().collect(Collectors.toList());
	}
	
	private void sendNotification(AonApiData api, String body) {
		Thread newThread = new Thread(() -> {
			try {
				Domain domain = api.getDomain();
				User user = api.getUser();
				LinkedList<Auth> auths = new LinkedList<>();
				
				AON.getDomainUserStream(domain.getName(), domain.getId(), api.getUser().getLogin(), f -> f.getIdProperty().ne(user.getId()))
				.forEach(usr -> {
					DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, user.getLogin(), usr.getId());
					if(Boolean.TRUE.equals(dur.isComunicaManager())) {
						Auth auth = new Auth().setAuth(usr.getAuth().getAuth());
						if(auth.getAuth()!=null) {
							auths.add(auth);
						}
		    		}
				});
			
				if(!auths.isEmpty()) {
					String title = "AON | COMUNIC@";
			    	NotificationRequest notification = new NotificationRequest();
			    	notification.setTitle(title);
			    	notification.setBody(body);
			    	notification.setSender(user.getAuth().getAuth());
			    	notification.setDomain(api.getDomain());
			    	notification.setUser(api.getUser());
			    	notification.setAuths(auths);
			    	notification.send();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
		newThread.start();
	}
	
	private void sendEmail(AonApiData api, String subject, String body, LinkedList<File> files) {
		Thread newThread = new Thread(() -> {
			try {
				User user = api.getUser();
				List<String> emails = getEmails(api, user);
				if(!emails.isEmpty()) {
					SESMessage msg = new SESMessage()
							.setAlias("AON | COMUNIC@")
							.setSubject(subject)
							.setBody(body)
							.setTo(emails);
					
					if(!files.isEmpty()) {
						msg.setFiles(files);
					}
					SES.sendEmail(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	private static JSONObject updateContracts(AonApiData api){
		Domain domain = api.getDomain();
		User user     = api.getUser();
    	try {
    		JSONObject params = api.getData();
    	    Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId(), "TGSS");
			HashSet<Employee> employees = new HashSet<>();
			List<CCCInfo> cccs = getCcs(api);
			
			ApplicationParameter appParams = appParamsExists(api);
			
			if(params.optBoolean("employeesOld")) {//------------MOVEMENTS OLD
				
				Date startIni = AonDateUtils.getYearFirstDay( AonDateUtils.addMonths(new Date(), -6) ); 
				
				if(!params.optString("startDate").isEmpty()) {
					startIni = AonDateUtils.parse(params.optString("startDate"), FORMAT_DATE);
				} else if(appParams.getId()!=null && appParams.getValue()!=null) {
					startIni = AonDateUtils.addDays(new Date( Long.parseLong( appParams.getValue() ) ), -15) ;
				}

				employees.addAll(ComunicaUtils.getEmployeesOld(startIni, certificate, cccs));
			}
			
			if(params.optBoolean("employeesPrev")) {		//------------MOVEMENTS PREV
				employees.addAll(ComunicaUtils.getEmployeesPrev(certificate, cccs));
			}
			
			//------------CONTRACT
			employees.forEach(data ->{
				try {
					java.sql.Date fraSql = new java.sql.Date(data.getFra().getTime());      
				    String nss = data.getNss();
					Optional<Date> frb = data.getFrb();
					Optional<Contract> contract = Optional.empty();
					//----GET PERSON
					Person person = AON.getPerson(domain, user.getLogin(), f->f.getDomainProperty().eq(domain.getId()).and(f.getSocialSecurityNumProperty().eq(nss)));
					
					////--GET CONTRACT ACTIVE
					if(null!= person.getDocument()) {
						contract = PAYROLL.getContract(domain.getName(), domain.getId(), user.getLogin(),
							f->f.getDomainProperty().eq(domain.getId())
							.and(f.getPersonProperty().eq(person.getId()))
							.and( 
								frb.isPresent() ?
								f.getStartDateProperty().eq(fraSql).and(f.getEndDateProperty().eq( new java.sql.Date(frb.get().getTime()) ) )  :
								f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(fraSql)) 
							)
						);
					}
					// CONTRACT NO EXIST
					if(contract.isEmpty()) { 
						if(data.getGc().isEmpty()) {
							data = SistemaRED.getEmployee(
								new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
								data.getRegime(), data.getCtaCti().get(), nss
							);
						}
						
						if( nss!=null) {
							LOGGER.info("-------------CREANDO CONTRATO-------------");
							LOGGER.info(data.toString());			
							EmployeeParse.toEmployeeOccam(data);
							PAYROLL.addEmployee(domain.getName(), domain.getId(), user.getLogin(), EmployeeParse.toEmployeeOccam(data));
							LOGGER.info("------------------------------------");
						}
					} else {
						LOGGER.info("--------YA EXISTE EL CONTRATO-------------");
						LOGGER.info(data.toString());
						LOGGER.info("------------------------------------");
					}
		
				} catch (SegSocialException e) {e.printStackTrace();}	
			});
			
			if(!employees.isEmpty()) {
				saveAppParams(domain, api.getUser(), appParams); //SAVE APP PARAMS
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	/*
	 * SAVE OR UPDATE APP PARAMS
	 */
	private static void saveAppParams(Domain domain, User user, ApplicationParameter exists) {
		ApplicationParameter appParams = new ApplicationParameter()
				.setDomain(domain.getId())
				.setValue(new Date().getTime()+"")
				.setName(APP_PARAMS_NAME)
				;
		if(exists.getId()!=null) {
			LOGGER.info("--------UPDATE APP PARAMS-------------");
			AON.updateApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), appParams, 
					f->f.getDomainProperty().eq(appParams.getDomain()).and(f.getNameProperty().eq(appParams.getName()))
				);
		} else {
			LOGGER.info("--------SAVE APP PARAMS-------------");
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), appParams);
		}
	}
	
	private static JSONObject getAppParam(AonApiData api) {
		JSONObject json = new JSONObject();
		ApplicationParameter appParams = appParamsExists(api);
		if(appParams.getId()!=null && appParams.getValue()!=null) {
			json.put(IJsonNames.NAME, appParams.getName());
			json.put(IJsonNames.VALUE, appParams.getValue());
		}
		return json;
	}
	
	private static ApplicationParameter appParamsExists(AonApiData api) {
		return AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), APP_PARAMS_NAME);
	}
	
	private static List<String> getEmailsAppParams(AonApiData api) {
		Domain domain = api.getDomain();
		List<String> list = new ArrayList<>();

		try {
			ApplicationParameter child = AON.getApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), APP_COMUNICA_EMAILS);
			
			if(child!=null && child.getValue()!=null && !child.getValue().isEmpty()) {
				list.addAll( Arrays.asList(child.getValue().split(",")) );
			}  
			
			if(domain.isChild()) {
				ApplicationParameter parent = AON.getApplicationParameter(domain.getName(), domain.getParentId(), api.getUser().getLogin(), APP_COMUNICA_EMAILS);
				if(parent!=null && parent.getValue()!=null && !parent.getValue().isEmpty()) {
					list.addAll( Arrays.asList(parent.getValue().split(",")) );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	private static List<CCCInfo> getCcs(AonApiData api) {
		return PAYROLL.getCCCStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())
		.filter(ComunicaUtils.distinctByKey(CCCInfo::getCccAccount)).collect(Collectors.toList());
	}
}
