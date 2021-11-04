package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.in.payroll.tgss.report.CCCLaboralLife;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
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
import net.aonsolutions.aon.api.notification.NotificationRequest;
import net.aonsolutions.aon.api.utils.ComunicaUtils;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;
import solutions.aon.seg.social.ServicioREDEmployee;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.toolkit.Toolkit;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	private static final String APP_PARAMS_NAME = "APP_COMUNICA_SINCRONIZED"; 
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMUNICA SERVLET");
		try {		
			AonApiData api = initialize(req, resp);
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
				case "/ipfxnaf":
					LOGGER.info("IPFXNAF SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.ipfxnaf(api));
					break;
				case "/nafxipf":
					LOGGER.info("NAFXIPF SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.nafxipf(api));
				break;
				case "/rlce":
					LOGGER.info("RLCE SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getRlce(api));
				break;
				case "/contract-type":
					LOGGER.info("CONTRACT-TYPE SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getContractType(api));
				break;
				case "/occupation":
					LOGGER.info("OCCUPATION SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getOccupation(api));
				break;
				case "/quote-group":
					LOGGER.info("QUOTE-GROUP SERVLET - GET METHOD");
					jsonInString = gjson.toJson(getQuoteGroup(api));
				break;
				case "/update-contracts":
					LOGGER.info("UPDATE-CONTRACTS SERVLET - GET METHOD");
					jsonInString = gjson.toJson(updateContracts(api));
				break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
			
		} catch (Exception e) {
			error(req, resp, e);
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON COMUNICA SERVLET POST");
		try {		
			AonApiData api = initialize(req, resp);
		    Gson gjson = new GsonBuilder().setDateFormat(FORMAT_DATE).create();
		    String jsonInString = null;
			switch (api.getPath()) {
				case "/alta-directa":
					LOGGER.info("ALTA-DIRECTA SERVLET - POST METHOD");
					jsonInString = gjson.toJson(sendAlta(api));
					response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
					break;
				case "/baja":
					LOGGER.info("BAJA SERVLET - POST METHOD");
					jsonInString = gjson.toJson(sendBaja(api));
					response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
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
	
	private Collection<Employee> getMovements(AonApiData api) throws Exception {
		
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		ArrayList<Employee> employees = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		PAYROLL.getCCCStream(domain.getName(), domain.getId(), api.getUser().getLogin())
		.filter(ComunicaUtils.distinctByKey(ci -> ci.getCccAccount()))
		.forEach(ccc -> {
            try{            	
                employees.addAll(
                	 SistemaRED.getTotalEmployees(
	                	 new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), 
	                	 ccc.getCccRegimeCode(), ccc.getCccAccount()
                	)
                );
            } catch(InvalidCertificateException e) {
                e.printStackTrace();
                errors.add(e.getClass().getSimpleName());
            } catch(Exception e) {
                e.printStackTrace();
            }
        });	
        
        if(!errors.isEmpty()) throw new Exception(errors.get(0));
        
		return employees;
	}
	
	private ArrayList<com.esferalia.aon.in.payroll.tgss.report.Employee> getMovementsCcc(AonApiData api) throws Exception {
		ArrayList<com.esferalia.aon.in.payroll.tgss.report.Employee> employees = new ArrayList<>();
		Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		List<String> errors = new ArrayList<>();
	
		Date startDate = !api.getParams().optString("startDate").isEmpty() ?Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd") : new Date();
		Date endDate = !api.getParams().optString("endDate").isEmpty() ?Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd") : new Date();
		
		PAYROLL.getCCCStream(domain.getName(), domain.getId(), "").forEach(ccc -> {
		  try {
			  byte[] pdf = ServicioREDEmployee.getCccLaboralLifePOST(
					new ByteArrayInputStream(certificate.getCertificate()), 
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
		  } catch(Exception e) {}
		});	

		if(!errors.isEmpty()) throw new Exception(errors.get(0));
		
		return employees;
	}
	
	private Map<String, String> getRlce(AonApiData api) throws Exception {
		return RLCE.getRLCE();
	}
	
	private Map<Integer, ContractTypeRecord> getContractType(AonApiData api) throws Exception {
		 return new ContractType().getContractTypes();
	}
	
	private  Map<String, String> getOccupation(AonApiData api) throws Exception {
		Map<String, String> map = new HashMap<>();
		for (Entry<String, String> v : Occupation.getOccupation().entrySet()) 
			map.put(v.getValue(), v.getKey());
		return map;
	}
	
	private Map<String, String> getQuoteGroup(AonApiData api) throws Exception {
		Map<String, String> map = new HashMap<>();
		for (Entry<String, String> v : QuoteGroup.getQuoteGroup().entrySet()) 
			map.put(v.getValue(), v.getKey());

		return map;
	}
	
	private Collection<Employee> ipfxnaf(AonApiData api) throws Exception {
			Domain domain = api.getDomain();
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
			final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
			
			String nss = api.getParams().optString("nss");
			if(nss.isEmpty()) 
				throw new Exception("nss requerido");
			
		    ArrayList<String> nssList = new ArrayList<>();
		    nssList.add(nss);	
		    
		    return SistemaRED.ipfxnaf(certificateInputStream, certificate.getPassword(), certificate.getType(), nssList);
	}
	
	private Employee sendAlta(AonApiData api) throws Exception{
		ComunicaUtils.validateAlta(api);
		
	    Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		//first screen
		String regime = api.getData().optString("regime");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		
		String ipf = api.getData().optString("ipf");
		
		//second screen
		Date fecha = Toolkit.parseDate(api.getData().optString("fecha"), FORMAT_DATE);

		String gc = api.getData().optString("gc");
		String contract = api.getData().optString("contract");	
		String name = api.getData().optString("name");
		String ocup = api.getData().has("ocup")  && !api.getData().isNull("ocup") ? api.getData().optString("ocup") : null;
		String coef = api.getData().has("coef")  && !api.getData().isNull("coef") ? api.getData().optString("coef") : null;
		String convenio =  api.getData().has("convenio")  && !api.getData().isNull("convenio") ? api.getData().optString("convenio") : "60888888888888";
		String rlce = api.getData().has("rlce")  && !api.getData().isNull("rlce") ? api.getData().optString("rlce") : null; //para regime agrario
		String md_ctz = api.getData().has("md_ctz")  && !api.getData().isNull("md_ctz") ? api.getData().optString("md_ctz") : null; //para regime agrario
//        Ctz mensual = 1
//        Jornadas reales = 2
		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder
		.setRegime(regime)
		.setName(name)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fecha)
		.setOcup(ocup)
		.setCoef(coef)
		.setColec(convenio)
		.setGc(gc)
		.setContract(contract)
		.setMdctz(md_ctz)
		.setRlce(rlce)
		.build();
		employee = SistemaRED.sendAlta(new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), employee);
		if(employee.getName().isPresent()) 
			sendMovEmailNotification(api, employee, fecha, "alta");
		return employee;
	}
	
	private Employee sendBaja(AonApiData api) throws Exception{
		
	    Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		//first screen
		String regime = api.getData().optString("regime");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		
		String ipf = api.getData().optString("ipf");
		String name = api.getData().optString("name");
		String situation = api.getData().optString("situation");
		Date fecha = Toolkit.parseDate(api.getData().optString("fechaBaja"), FORMAT_DATE);

		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder
		.setRegime(regime)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fecha)
		.setName(name)
		.setSituation(situation)
		.build();
		employee = SistemaRED.sendBaja(new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), employee);
		if(employee.getName().isPresent()) 
			sendMovEmailNotification(api, employee, fecha, "baja");
		return employee;
	}
	
	private JSONObject movDelete(AonApiData api) throws Exception {
		
		 Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		
		//first screen
		String situation = api.getData().getString("situation");
		String regime = api.getData().getString("regime");
		String ctaCti = api.getData().getString("ctaCti");
		String nss = api.getData().getString("nss");
		String ipf = api.getData().getString("ipf");
		Boolean prev = api.getData().getBoolean("prev"); //true prev, false consolidado
		String name =  api.getData().getString("name");
		Date fecha = Toolkit.parseDate(api.getData().getString("fra"), FORMAT_DATE);
		
		if(!api.getData().isNull("frb")) 
			fecha = Toolkit.parseDate(api.getData().getString("frb"), FORMAT_DATE);

		if(Boolean.TRUE.equals(prev)) 
			SistemaRED.movPrevDelete(new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), situation, regime, ctaCti, nss, fecha);
		else 
			SistemaRED.removeMovConsolidated(new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), 
					SituationType.safeValueOf(situation), regime, ctaCti, nss, ipf, fecha);
		
		if(!api.getData().isNull("name")) {
			String situationStr = situation.equalsIgnoreCase("AL") ? "Alta"  : "Baja";
			String body = "Te informamos que se ha realizado una <b>Eliminación de "+situationStr+"</b> en la Seguridad Social de <b>"
			+name+"</b> en la Cuenta de Cotización <b>"+regime+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
			sendNotification(api, body);
		}
		return new JSONObject();
	}
	
	private Employee nafxipf(AonApiData api) throws Exception{
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		String ipf = api.getParams().optString("ipf");
		String apellido1 =  api.getParams().optString("apellido1");
		String apellido2 =  api.getParams().optString("apellido2");
		return SistemaRED.nafxipf(new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), ipf, apellido1,  apellido2);
	}
	
	private Employee getEmployee(AonApiData api) throws Exception{
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());

		String regime = api.getParams().optString("regime");
		String ccc =  api.getParams().optString("ctaCti");
		String nss =  api.getParams().optString("nss");
		return SistemaRED.getEmployee(new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), regime, ccc, nss);	
	}
	
	private JSONObject updateContract(AonApiData api) throws Exception {
		
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		
		JSONObject json = new JSONObject();
		JSONArray errors = new JSONArray();
		if(api.getData().isNull("fecha")) 
			throw new Exception("Fecha requerida");

		json.put("contract_edit", false);
		
		updateOccupation(api, new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), json, errors);
	
		updateGrupCtz(api, new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), json, errors);

		updateFactor(api, new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), json, errors);
		
		json.put("errors", errors);
		
		return json;
	}
	
	private void updateOccupation(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json, JSONArray errors){
		JSONObject data = api.getData();
		String ocup = data.optString("ocup");
		String regime = data.optString("regime");
		String ctaCti = data.optString("ctaCti");
		String nss = data.optString("nss");
		String ipf = data.optString("ipf");
		String name = data.optString("name");
		Date fecha = Toolkit.parseDate(data.optString("fecha"), FORMAT_DATE);
		
		if(!ocup.isEmpty() && !data.optString("ocup_edit").isEmpty() && data.getBoolean("ocup_edit")) {
            try {
            	SistemaRED.cambioOcupacion( certificateInputStream, certificatePassword, certificateType, ipf, regime, ctaCti, nss, ocup, fecha);
            	json.put("ocup_edit", true);
            	json.put("contract_edit", true);
            	if(!data.isNull("name")) {
        			String body = "Te informamos que se ha realizado un Cambio de ocupación a "
        					+ "(<b>"+ocup.toUpperCase()+"</b>) en la Seguridad Social de <b>"+ name+"</b> en la Cuenta de Cotización <b>"
        					+ regime+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
        			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
        			sendNotification(api, body);
        		}
            } catch(Exception e) {
            	e.printStackTrace();
            	errors.put(e.getMessage());
            }	
		}
	}
	
	private void updateGrupCtz(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json, JSONArray errors){
		JSONObject data = api.getData();
		String regime = data.optString("regime");
		String ctaCti = data.optString("ctaCti");
		String nss = data.optString("nss");
		String ipf = data.optString("ipf");
		String name = data.optString("name");
		String gc = data.optString("gc");
		Date fecha = Toolkit.parseDate(data.optString("fecha"), FORMAT_DATE);
		if(!gc.isEmpty() && !data.optString("gc_edit").isEmpty() && data.getBoolean("gc_edit")) {
			 try{
				 SistemaRED.cambioGrupCtz( certificateInputStream, certificatePassword, certificateType, ipf, regime, ctaCti, nss, gc, fecha);
				 json.put("gc_edit", true);
				 json.put("contract_edit", true);
            	 if(!data.isNull("name")) {
        			String body = "Te informamos que se ha realizado un Cambio de Grupo de cotización a (<b>"+gc+"</b>) en la Seguridad Social de <b>"
            	 + name+"</b> en la Cuenta de Cotización <b>"+ regime+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
        			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
        			sendNotification(api, body);
        		 }
			} catch(Exception e) {
				e.printStackTrace();
				errors.put(e.getMessage());
			}	
		}
	}
	
	private void updateFactor(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json, JSONArray errors){
		JSONObject data = api.getData();
		String regime = data.optString("regime");
		String ctaCti = data.optString("ctaCti");
		String nss = data.optString("nss");
		String ipf = data.optString("ipf");
		String coef = data.optString("coef");
		String contract =  data.optString("contract");
		Date fecha = Toolkit.parseDate(data.optString("fecha"), FORMAT_DATE);
	
		if(!coef.isEmpty() && !data.optString("coef_edit").isEmpty() && data.getBoolean("coef_edit") ||
				!contract.isEmpty() && !data.optString("contract_edit").isEmpty() && data.getBoolean("contract_edit")) {
			 try{
				 SistemaRED.cambioContratoCoef(certificateInputStream, certificatePassword, certificateType, 
						 ipf, regime, ctaCti, nss, fecha, Optional.ofNullable(contract), coef
				);
				 json.put("coef_edit", true);
				 json.put("contract_edit", true);
			} catch(Exception e) {
				e.printStackTrace();
				errors.put(e.getMessage());
			}	
		}
	}
	
	private void sendMovEmailNotification(AonApiData api, Employee employee, Date date, String mov){
		Thread newThread = new Thread(() -> {
			try {
				String pre = mov.equalsIgnoreCase("alta") ? "el" : "la";
				Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
				String subject = "TGSS | "+mov.toUpperCase()+" de "+employee.getName().get();
				String body = "La Tesorería General de la Seguridad Social ha procedido a reconocer "+pre+" <b>"+mov+"</b> "
						+ "en el Régimen General de D./Dña. <b>"+employee.getName().get()+"</b>, "
						+ "con número de afiliación <b>"+employee.getNss()+"</b> y DNI/NIE <b>"+employee.getIpf()+"</b>, con fecha <b>"+Toolkit.formatDate(date, "dd-MM-yyyy").get()+"</b>, "
						+ "como trabajador de <b>"+company.getName()+"</b> "
						+ "con código de cuenta de cotización <b>"+employee.getRegime()+" "+ employee.getCtaCti().get()+"</b>.";
			
				sendNotification(api, body); //SEND NOTIFICATION

				//SEND EMAIL
				Domain domain = api.getDomain();
				User user = AON_SOLUTIONS.getUser(domain, api.getToken());
				Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
				final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
				String regime = employee.getRegime();
				String ccc = employee.getCtaCti().get();
				String nss = employee.getNss();
				Date fecha = employee.getFra();

				LinkedList<File> files = new LinkedList<>();
				
				try {
					byte[] fileByte = SistemaRED.getTA(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc, nss, fecha);
					File file = File.createTempFile("duplicado", ".pdf");
					FileOutputStream os = new FileOutputStream(file);
		            os.write(fileByte);
		            os.close();
					files.add(file);
				} catch (Exception e) {
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
	    Domain domain = api.getDomain();
	    List<String> toList = new LinkedList<>();
	    String alternative = JsonUtils.optString(api.getData(), "alternative");
	    if(alternative!=null && !alternative.isEmpty()) toList.add(alternative);
		User newUser = user; //.isPresent() ? user.get() : AON_SOLUTIONS.getUser(domain, api.getToken()) ;
		Auth auth = AON_SOLUTIONS.getAuth(newUser.getAuth().getAuth());
		toList.add(auth.getEmail());
		AON.getDomainUserStream(domain.getName(), domain.getId(), newUser.getLogin(), f -> f.getAuthProperty().isNotNull().and(f.getIdProperty().ne(newUser.getId()))).forEach(usr -> {
    		DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, newUser.getLogin(), usr.getId());
    		if(Boolean.TRUE.equals(dur.isComunicaManager())) {
    			toList.add(AON_SOLUTIONS.getAuth(usr.getAuth().getAuth()).getEmail());
    		}
    	});
		return toList;
	}
	
	private void sendNotification(AonApiData api, String body) {
		Thread newThread = new Thread(() -> {
			
			Domain domain = api.getDomain();
			User user = AON_SOLUTIONS.getUser(domain, api.getToken());
			LinkedList<Auth> auths = new LinkedList<>();
			
			AON.getDomainUserStream(domain.getName(), domain.getId(), api.getUser().getLogin(), f -> f.getIdProperty().ne(user.getId())).forEach(usr -> {
				DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, user.getLogin(), usr.getId());
				if(Boolean.TRUE.equals(dur.isComunicaManager())) {
					Auth auth = new Auth().setAuth(usr.getAuth().getAuth());
					if(auth.getAuth()!=null) auths.add(auth);
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
		});
		newThread.start();
	}
	
	private void sendEmail(AonApiData api, String subject, String body, LinkedList<File> files) {
		Thread newThread = new Thread(() -> {
			try {
				User user = AON_SOLUTIONS.getUser(api.getDomain(), api.getToken());
				SESMessage msg = new SESMessage()
						.setAlias("AON | COMUNIC@")
						.setSubject(subject)
						.setBody(body)
						.setTo(getEmails(api, user));
				
				if(!files.isEmpty()) 
					msg.setFiles(files);
				
				SES.sendEmail(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	private static JSONObject updateContracts(AonApiData api){
		Domain domain = api.getDomain();
    	try {
    	    Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
			ArrayList<Employee> employees = new ArrayList<>();
			List<CCCInfo> cccs = getCcs(api);
			
			ApplicationParameter appParams = new ApplicationParameter();
			
			//------------MOVEMENTS OLD
			if(!api.getParams().optString("startDate").isEmpty()) {
				Date startIni = Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
				appParams = appParamsExists(api);
				if(appParams.getId()!=null && appParams.getValue()!=null) {
					//DEDUCT 15 DAYS
					startIni = AonDateUtils.addDays(new Date( Long.parseLong( appParams.getValue() ) ), -15) ;
				} 
				employees.addAll(ComunicaUtils.getEmployeesOld(startIni, certificate, cccs));
			}

			//------------MOVEMENTS REAL(ACTUAL)
			employees.addAll(ComunicaUtils.getEmployeesReal(certificate, cccs));
			
			//------------CONTRACT
			employees.forEach(data ->{
				try {
				  
				    String nss = data.getNss();
					//----GET PERSON
					java.sql.Date fraSql = new java.sql.Date(data.getFra().getTime());      
					Optional<Date> frb = data.getFrb();
					Optional<Contract> contract = Optional.empty();
					Person person = AON.getPerson(domain, "", f->f.getDomainProperty().eq(domain.getId()).and(f.getSocialSecurityNumProperty().eq(nss)));
					
					////--GET CONTRACT ACTIVE
					if(null!= person.getDocument()) {
						contract = PAYROLL.getContract(domain.getName(), domain.getId(), "",
							f->f.getDomainProperty().eq(domain.getId())
							.and(f.getPersonProperty().eq(person.getId()))
							.and( 
									frb.isEmpty() ?
									f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(fraSql)) :
									f.getStartDateProperty().eq(fraSql).and(f.getEndDateProperty().eq( new java.sql.Date(frb.get().getTime()) ) ) )
							);
					}
					// CONTRACT NO EXIST
					if(contract.isEmpty()) { 
						if(data.getGc().isEmpty()) {
							  data = SistemaRED.getEmployee(
										new ByteArrayInputStream(certificate.getCertificate()), certificate.getPassword(), certificate.getType(), 
										data.getRegime(), data.getCtaCti().get(), nss
								  );
						}
						
						if( nss!=null) {
							System.out.println("-------------CREANDO CONTRATO-------------");
							System.out.println(data.toString());
							PAYROLL.addEmployee(domain.getName(), domain.getId(), "", ComunicaUtils.employeeParse(data));
						}
					} else {
						System.out.println("--------YA EXISTE EL CONTRATO-------------");
						System.out.println(data.toString());
					}
		
				} catch (SegSocialException e) {e.printStackTrace();}	
			});
			if(!employees.isEmpty()) {
				saveAppParams(api, appParams); //SAVE APP PARAMS
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	/*
	 * SAVE OR UPDATE APP PARAMS
	 */
	private static void saveAppParams(AonApiData api, ApplicationParameter exists) {
		Domain domain = api.getDomain();
		ApplicationParameter appParams = new ApplicationParameter()
				.setDomain(domain.getId())
				.setValue(new Date().getTime()+"")
				.setName(APP_PARAMS_NAME)
				;
		if(exists.getId()!=null) {
			System.out.println("--------UPDATE APP PARAMS-------------");
			AON.updateApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), appParams, 
					f->f.getDomainProperty().eq(appParams.getDomain()).and(f.getNameProperty().eq(appParams.getName()))
				);
		} else {
			System.out.println("--------SAVE APP PARAMS-------------");
			AON.insertApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), appParams);
		}
	}
	
	private static ApplicationParameter appParamsExists(AonApiData api) {
		return AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), APP_PARAMS_NAME);
	}
	
	private static List<CCCInfo> getCcs(AonApiData api) {
		return PAYROLL.getCCCStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())
		.filter(ComunicaUtils.distinctByKey(CCCInfo::getCccAccount)).collect(Collectors.toList());
	}
}
