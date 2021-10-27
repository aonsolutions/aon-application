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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.occam.api.model.type.Occupation;
import com.esferalia.aon.occam.api.model.type.QuoteGroup;
import com.esferalia.aon.occam.api.model.type.RLCE;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 

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
					jsonInString = gjson.toJson(this.getQuoteGroup(api));
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
		    Domain domain = api.getDomain();
			User user = AON_SOLUTIONS.getUser(domain, api.getToken());
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
			final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
			switch (api.getPath()) {
				case "/alta-directa":
					LOGGER.info("ALTA-DIRECTA SERVLET - POST METHOD");
					jsonInString = gjson.toJson(sendAlta(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/baja":
					LOGGER.info("BAJA SERVLET - POST METHOD");
					jsonInString = gjson.toJson(sendBaja(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/delete-mov":
					LOGGER.info("DELETE-MOV SERVLET - POST METHOD");
					jsonInString = gjson.toJson(movDelete(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/update-contrato":
					LOGGER.info("UPDATE-CONTRATO SERVLET - POST METHOD");
					jsonInString = gjson.toJson(updateContrato(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Collection<Employee> getMovements(AonApiData api) throws Exception {
		
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		ArrayList<Employee> employees = new ArrayList<>();
	
		byte[] cert = certificateInputStream.readAllBytes();
		List<String> errors = new ArrayList<>();

		PAYROLL.getCCCStream(domain.getName(), domain.getId(), api.getUser().getLogin())
		.filter(distinctByKey(ci -> ci.getCccAccount()))
		.forEach(ccc -> {
            String cti = ccc.getCccAccount();
            String regimen = ccc.getCccRegimeCode();
            try{            	
                employees.addAll(SistemaRED.getTotalEmployees(new ByteArrayInputStream(cert), certificate.getPassword(), certificate.getType(), regimen, cti));
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
	
	private Employee sendAlta(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws Exception{
		validateAlta(api);
		//first screen
		String regimen = api.getData().optString("regimen");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		
		String ipf = api.getData().optString("ipf");
		
		//second screen
		Date fecha = Toolkit.parseDate(api.getData().optString("fecha"), FORMAT_DATE);

		String grup_ctz = api.getData().optString("grup_ctz");
		String type_cto = api.getData().optString("type_cto");	
		String name = api.getData().optString("name");
		String ocupacion = api.getData().has("ocupacion")  && !api.getData().isNull("ocupacion") ? api.getData().optString("ocupacion") : null;
		String coefparcial = api.getData().has("coefparcial")  && !api.getData().isNull("coefparcial") ? api.getData().optString("coefparcial") : null;
		String convenio =  api.getData().has("convenio")  && !api.getData().isNull("convenio") ? api.getData().optString("convenio") : "60888888888888";
		String rlce = api.getData().has("rlce")  && !api.getData().isNull("rlce") ? api.getData().optString("rlce") : null; //para regimen agrario
		String md_ctz = api.getData().has("md_ctz")  && !api.getData().isNull("md_ctz") ? api.getData().optString("md_ctz") : null; //para regimen agrario
//        Ctz mensual = 1
//        Jornadas reales = 2
		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder
		.setRegime(regimen)
		.setName(name)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fecha)
		.setOcup(ocupacion)
		.setCoef(coefparcial)
		.setColec(convenio)
		.setGc(grup_ctz)
		.setContract(type_cto)
		.setMdctz(md_ctz)
		.setRlce(rlce)
		.build();
		employee = SistemaRED.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
		if(employee.getName().isPresent()) 
			sendMovEmailNotification(api, employee, fecha, "alta");
		return employee;
	}
	
	private Employee sendBaja(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws Exception{
		//first screen
		String regimen = api.getData().optString("regime");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		
		String ipf = api.getData().optString("ipf");
		String name = api.getData().optString("name");
		String situation = api.getData().optString("situation");
		Date fecha = Toolkit.parseDate(api.getData().optString("fechaBaja"), FORMAT_DATE);

		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder
		.setRegime(regimen)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fecha)
		.setName(name)
		.setSituation(situation)
		.build();
		employee = SistemaRED.sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
		if(employee.getName().isPresent()) 
			sendMovEmailNotification(api, employee, fecha, "baja");
		return employee;
	}
	
	private Boolean movDelete(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws Exception {

		//first screen
		String situation = api.getData().getString("situation");
		String regimen = api.getData().getString("regime");
		String ctaCti = api.getData().getString("ctaCti");
		String nss = api.getData().getString("nss");
		Boolean prev = api.getData().getBoolean("prev"); //true prev, false consolidado
		String name =  api.getData().getString("name");
		Date fecha = Toolkit.parseDate(api.getData().getString("fra"), FORMAT_DATE);
		
		if(!api.getData().isNull("frb")) 
			fecha = Toolkit.parseDate(api.getData().getString("frb"), FORMAT_DATE);

		if(prev) 
			SistemaRED.movPrevDelete(certificateInputStream, certificatePassword, certificateType,  situation, regimen, ctaCti, nss, fecha);
		else 
			SistemaRED.altaConsolidadaDelete(certificateInputStream, certificatePassword, certificateType, situation, regimen, ctaCti, nss);
		
		if(!api.getData().isNull("name")) {
			String situationStr = situation.equalsIgnoreCase("AL") ? "Alta"  : "Baja";
			String body = "Te informamos que se ha realizado una <b>Eliminación de "+situationStr+"</b> en la Seguridad Social de <b>"
			+name+"</b> en la Cuenta de Cotización <b>"+regimen+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
			sendNotification(api, body);
		}
		return true;
	}
	
	private Employee nafxipf(AonApiData api) throws Exception{
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String ipf = api.getParams().optString("ipf");
		String apellido1 =  api.getParams().optString("apellido1");
		String apellido2 =  api.getParams().optString("apellido2");
		return SistemaRED.nafxipf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, apellido1,  apellido2);
	}
	
	private Employee getEmployee(AonApiData api) throws Exception{
		Domain domain = api.getDomain();
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		
		String regimen = api.getParams().optString("regime");
		String ccc =  api.getParams().optString("ctaCti");
		String nss =  api.getParams().optString("nss");
		return SistemaRED.getEmployee(certificateInputStream, certificate.getPassword(), certificate.getType(), regimen, ccc, nss);	
	}
	
	private Map<String, Object> updateContrato(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws Exception {
		Map<String, Object> map = new HashMap<>();
		List<String> errors = new ArrayList<String>();
		map.put("grup_ctz_edit", false);
		map.put("ocupacion_edit", false);
		if(api.getData().isNull("fecha")) 
			throw new Exception("Fecha requerida");

		updateOccupation(api, new ByteArrayInputStream(certificateInputStream.readAllBytes()), certificatePassword, certificateType, map, errors);
		
		updateGrupCtz(api, new ByteArrayInputStream(certificateInputStream.readAllBytes()), certificatePassword, certificateType, map, errors);
		
		return map;
	}
	
	private void updateOccupation(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, Map<String, Object> map, List<String> errors){
		JSONObject data = api.getData();
		String ocup = data.optString("ocupacion");
		String regimen = data.optString("regimen");
		String ctaCti = data.optString("ctaCti");
		String nss = data.optString("nss");
		String ipf = data.optString("ipf");
		String name = data.optString("name");
		Date fecha = Toolkit.parseDate(data.optString("fecha"), FORMAT_DATE);
		
		if(!ocup.isEmpty() && !data.optString("ocupacion_edit").isEmpty() && data.getBoolean("ocupacion_edit")) {
            try {
            	SistemaRED.cambioOcupacion( new ByteArrayInputStream(certificateInputStream.readAllBytes()), certificatePassword, certificateType, ipf, regimen, ctaCti, nss, ocup, fecha);
            	map.put("ocupacion_edit", true);
            	if(!data.isNull("name")) {
        			String body = "Te informamos que se ha realizado un Cambio de ocupación a "
        					+ "(<b>"+ocup.toUpperCase()+"</b>) en la Seguridad Social de <b>"+ name+"</b> en la Cuenta de Cotización <b>"
        					+ regimen+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
        			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
        			sendNotification(api, body);
        		}
            } catch(Exception e) {
            	e.printStackTrace();
            	errors.add(e.getMessage());
            }	
		}
	}
	
	private void updateGrupCtz(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, Map<String, Object> map, List<String> errors){
		JSONObject data = api.getData();
		String regimen = data.optString("regimen");
		String ctaCti = data.optString("ctaCti");
		String nss = data.optString("nss");
		String ipf = data.optString("ipf");
		String name = data.optString("name");
		String grup_ctz = data.optString("grup_ctz");
		Date fecha = Toolkit.parseDate(data.optString("fecha"), FORMAT_DATE);
		if(!grup_ctz.isEmpty() && !data.optString("grup_ctz_edit").isEmpty() && data.getBoolean("grup_ctz_edit")) {
			 try{
				 SistemaRED.cambioGrupCtz( new ByteArrayInputStream(certificateInputStream.readAllBytes()), certificatePassword, certificateType, ipf, regimen, ctaCti, nss, grup_ctz, fecha);
				 map.put("grup_ctz_edit", true);
            	 if(!data.isNull("name")) {
        			String body = "Te informamos que se ha realizado un Cambio de Grupo de cotización a (<b>"+grup_ctz+"</b>) en la Seguridad Social de <b>"
            	 + name+"</b> en la Cuenta de Cotización <b>"+ regimen+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
        			sendEmail(api, "COMUNIC@ | AON SOLUTIONS", body, new LinkedList<>());
        			sendNotification(api, body);
        		 }
			} catch(Exception e) {
				e.printStackTrace();
				errors.add(e.getMessage());
			}	
		}
	}
	
	private void validateAlta(AonApiData api) throws Exception {
		if(api.getData().isNull("ctaCti")) 
			throw new Exception("Cuenta de cotización requerida");
		else if(api.getData().isNull("nss")) 
			throw new Exception("Número de afiliación requerido");
	    else if(api.getData().isNull("ipf")) 
			throw new Exception("DNI/NIE requerido");
		else if(api.getData().isNull("fecha")) 
			throw new Exception("Fecha requerida");
		else if(api.getData().isNull("grup_ctz")) 
			throw new Exception("Grupo de cotización requerido");
		else if(api.getData().isNull("type_cto")) 
			throw new Exception("Tipo de contrato requerido");
		else if( "501".equals(api.getData().getString("type_cto")) || "502".equals(api.getData().getString("type_cto")) ) 
			if(api.getData().optString("coefparcial") == null || "".equals(api.getData().optString("coefparcial")) ) {
				throw new Exception("Coeficiente parcial requerido");
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
				String regimen = employee.getRegime();
				String ccc = employee.getCtaCti().get();
				String nss = employee.getNss();
				Date fecha = employee.getFra();

				LinkedList<File> files = new LinkedList<>();
				
				try {
					byte[] fileByte = SistemaRED.getTA(certificateInputStream, certificate.getPassword(), certificate.getType(), regimen, ccc, nss, fecha);
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
    		if(dur.isComunicaManager()) 
				toList.add(AON_SOLUTIONS.getAuth(usr.getAuth().getAuth()).getEmail());
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
	    		if(dur.isComunicaManager()) {
					Auth auth = new Auth().setAuth(usr.getAuth().getAuth());
					if(auth.getAuth()!=null) auths.add(auth);
	    		}
			});
		
			if(auths.size()>0) {
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
				
				if(files.size()>0) 
					msg.setFiles(files);
				
				SES.sendEmail(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	// predicate to filter the duplicates by the given key extractor.
	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
