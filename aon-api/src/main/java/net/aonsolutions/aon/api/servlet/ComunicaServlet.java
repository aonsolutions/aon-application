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
import java.util.Optional;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import solutions.aon.aws.SES;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMUNICA SERVLET");
		try {		
			AonApiData api = initialize(req, resp);
		    Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		    String jsonInString = null;
		    Domain domain = api.getDomain();
			User user = AON_SOLUTIONS.getUser(domain, api.getToken());
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
			final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
			switch (api.getPath()) {
				case "/get-employee":
					LOGGER.info("GET-EMPLOYEE SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getEmployee(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/movements":
					LOGGER.info("MOVEMENTS SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getMovements(domain, "",  certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/ipfxnaf":
					LOGGER.info("IPFXNAF SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.ipfxnaf(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/nafxipf":
					LOGGER.info("NAFXIPF SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.nafxipf(api, certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
			response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
			
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON COMUNICA SERVLET POST");
		try {		
			AonApiData api = initialize(req, resp);
		    Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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
					throw new Exception("La ruta introducida es incorrecta.");
			}
			response(req, resp, jsonInString!=null ? new JsonParser().parse(jsonInString) : new JSONObject());
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Collection<Employee> getMovements(Domain domain, String login, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {
		ArrayList<Employee> employees = new ArrayList<>();
	
		byte[] cert = certificateInputStream.readAllBytes();
		List<String> errors = new ArrayList<String>();
        PAYROLL.getCCCStream(domain.getName(), domain.getId(), login).forEach(ccc -> {
            String cti = ccc.getCccAccount();
            String regimen = ccc.getCccRegimeCode();
            try{
                employees.addAll(SistemaRED.getTotalEmployees(new ByteArrayInputStream(cert), certificatePassword, certificateType, regimen, cti));
            } catch(InvalidCertificateException e) {
                e.printStackTrace();
                errors.add(e.getClass().getSimpleName());
            } catch(Exception e) {
                e.printStackTrace();
            }
        });	
        
        if(errors.size() > 0) throw new Exception(errors.get(0));
        
		return employees;
	}
	
	private Collection<Employee> ipfxnaf(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {
			String nss = api.getParams().optString("nss");
			if(nss.isEmpty()) {
				throw new Exception("nss requerido");
			};
		    ArrayList<String> nssList = new ArrayList<>();
		    nssList.add(nss);	
		    
		    return SistemaRED.ipfxnaf(certificateInputStream, certificatePassword, certificateType, nssList);
	}
	
	private Employee sendAlta(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		validateAlta(api);
		//first screen
		String regimen = api.getData().optString("regimen");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		
		String ipf = api.getData().optString("ipf");
		
		//second screen
		Date fecha = Toolkit.parseDate(api.getData().optString("fecha"), "yyyy-MM-dd");

		String grup_ctz = api.getData().optString("grup_ctz");
		String type_cto = api.getData().optString("type_cto");	
		String name = api.getData().optString("nombre");
		String ocupacion = api.getData().has("ocupacion")  && !api.getData().isNull("ocupacion") ? api.getData().optString("ocupacion") : null;
		String coefparcial = api.getData().has("coefparcial")  && !api.getData().isNull("coefparcial") ? api.getData().optString("coefparcial") : null;
		String convenio =  api.getData().has("convenio")  && !api.getData().isNull("convenio") ? api.getData().optString("convenio") : "60888888888888";
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
		.build();
		employee = SistemaRED.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
		if(employee.getName().isPresent()) {
			String body = "Te informamos que se ha realizado un Alta en la Seguridad Social de <b>"+ employee.getName().get()+"</b> en la Cuenta de Cotización <b>"
		+ employee.getRegime()+"-"+employee.getCtaCti().get()+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
			sendEmailAlta(api, employee, body); // send mov mail
			sendNotification(api, body);
		}
		return employee;
	}
	
	private Employee sendBaja(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		//first screen
		String regimen = api.getData().optString("regime");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		
		String ipf = api.getData().optString("ipf");
		String name = api.getData().optString("nombre");
		Date fecha = Toolkit.parseDate(api.getData().optString("fechaBaja"), "yyyy-MM-dd");

		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder
		.setRegime(regimen)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fecha)
		.setName(name)
		.build();
		employee =  SistemaRED.sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
		if(employee.getName().isPresent()) {
			String body = "Te informamos que se ha realizado una Baja en la Seguridad Social de <b>"+ employee.getName().get()+"</b> en la Cuenta de Cotización <b>"+ 
		employee.getRegime()+"-"+employee.getCtaCti().get()+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
			sendEmail(api, body); // send mov mail
			sendNotification(api, body);
		}
		return employee;
	}
	
	private Boolean movDelete(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {

		//first screen
		String situation = api.getData().getString("situation");
		String regimen = api.getData().getString("regime");
		String ctaCti = api.getData().getString("ctaCti");
		String nss = api.getData().getString("nss");
		Boolean prev = api.getData().getBoolean("prev"); //true prev, false consolidado
		String name =  api.getData().getString("nombre");
		Date fecha = Toolkit.parseDate(api.getData().getString("fra"), "yyyy-MM-dd");
		
		if(!api.getData().isNull("frb")) {
			fecha = Toolkit.parseDate(api.getData().getString("frb"), "yyyy-MM-dd");
		}
		
		if(prev) {
			SistemaRED.movPrevDelete(certificateInputStream, certificatePassword, certificateType,  situation, regimen, ctaCti, nss, fecha);
		} else {
			SistemaRED.altaConsolidadaDelete(certificateInputStream, certificatePassword, certificateType, situation, regimen, ctaCti, nss);
		}
		
		if(!api.getData().isNull("nombre")) {
			String situationStr = situation.equalsIgnoreCase("AL") ? "Alta"  : "Baja";
			String body = "Te informamos que se ha realizado una Eliminación de <b>"+situationStr+"</b> en la Seguridad Social de <b>"
			+name+"</b> en la Cuenta de Cotización <b>"+regimen+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
			sendEmail(api, body); // send mov mail
			sendNotification(api, body);
		}
		return true;
	}
	
	private Employee nafxipf(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		String ipf = api.getParams().optString("ipf");
		String apellido1 =  api.getParams().optString("apellido1");
		String apellido2 =  api.getParams().optString("apellido2");
		return SistemaRED.nafxipf(certificateInputStream, certificatePassword, certificateType, ipf, apellido1,  apellido2);
	}
	
	private Employee getEmployee(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		String regimen = api.getParams().optString("regime");
		String ccc =  api.getParams().optString("ctaCti");
		String nss =  api.getParams().optString("nss");
		Employee employee =  SistemaRED.getEmployee(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss);	
		return employee;
	}
	
	private Map<String, Object> updateContrato(AonApiData api, final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {
		Map<String, Object> map = new HashMap<>();
		List<String> errors = new ArrayList<String>();
		map.put("grup_ctz_edit", false);
		map.put("ocupacion_edit", false);
		if(api.getData().isNull("fecha")) {
			throw new Exception("fecha requerida");
		}
		String regimen = api.getData().optString("regimen");
		String ctaCti = api.getData().optString("ctaCti");
		String nss = api.getData().optString("nss");
		String ipf = api.getData().optString("ipf");
		String name = api.getData().optString("nombre");
		Date fecha = Toolkit.parseDate(api.getData().optString("fecha"), "yyyy-MM-dd");
		
		String ocup = api.getData().optString("ocupacion");
		if(!ocup.isEmpty() && !api.getData().optString("ocupacion_edit").isEmpty() && api.getData().getBoolean("ocupacion_edit")) {
            try {
            	SistemaRED.cambioOcupacion(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, ocup, fecha);
            	map.put("ocupacion_edit", true);
            	if(!api.getData().isNull("nombre")) {
        			String body = "Te informamos que se ha realizado un Cambio de ocupación a "
        					+ "(<b>"+ocup.toUpperCase()+"</b>) en la Seguridad Social de <b>"+ name+"</b> en la Cuenta de Cotización <b>"
        					+ regimen+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
        			sendEmail(api, body); // send mov mail
        			sendNotification(api, body);
        		}
            } catch(Exception e) {
            	e.printStackTrace();
            	errors.add(e.getMessage());
            }	
		}
		
		String grup_ctz = api.getData().optString("grup_ctz");
		if(!grup_ctz.isEmpty() && !api.getData().optString("grup_ctz_edit").isEmpty() && api.getData().getBoolean("grup_ctz_edit")) {
			 try{
				 SistemaRED.cambioGrupCtz(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, grup_ctz, fecha);
				 map.put("grup_ctz_edit", true);
            	 if(!api.getData().isNull("nombre")) {
        			String body = "Te informamos que se ha realizado un Cambio de Grupo de cotización a (<b>"+grup_ctz+"</b>) en la Seguridad Social de <b>"
            	 + name+"</b> en la Cuenta de Cotización <b>"+ regimen+"-"+ctaCti+"</b> con fecha <b>"+Toolkit.formatDate(fecha, "dd-MM-yyyy").get()+"</b>";
        			sendEmail(api, body); // send mov mail
        			sendNotification(api, body);
        		 }
			} catch(Exception e) {
				e.printStackTrace();
				errors.add(e.getMessage());
			}	
		}
		if(errors.size() > 0) map.put("errors",errors);
		
		return map;
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
	
	private  LinkedList<String> getEmails(AonApiData api, User user) {
	    Domain domain = api.getDomain();
		LinkedList<String> toList = new LinkedList<>();
		User newUser = user; //.isPresent() ? user.get() : AON_SOLUTIONS.getUser(domain, api.getToken()) ;
		Auth auth = AON_SOLUTIONS.getAuth(newUser.getAuth());
		toList.add(auth.getEmail());
		AON.getDomainUserStream(domain.getName(), domain.getId(), newUser.getLogin(), f -> f.getAuthProperty().isNotNull().and(f.getIdProperty().ne(newUser.getId()))).forEach(usr -> {
    		DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, newUser.getLogin(), usr.getId());
    		if(dur.isComunicaManager()) {
				toList.add(AON_SOLUTIONS.getAuth(usr.getAuth()).getEmail());
    		}
    	});
		return toList;
	}
	
	private void sendNotification(AonApiData api, String body) {
		Thread newThread = new Thread(() -> {
			
			Domain domain = api.getDomain();
			User user = AON_SOLUTIONS.getUser(domain, api.getToken());
			LinkedList<Auth> auths = new LinkedList<Auth>();
			
			AON.getDomainUserStream(domain.getName(), domain.getId(), api.getUser().getLogin(), f -> f.getIdProperty().ne(user.getId())).forEach(usr -> {
				DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, user.getLogin(), usr.getId());
	    		if(dur.isComunicaManager()) {
					Auth auth = new Auth().setAuth(usr.getAuth());
					if(auth.getAuth()!=null) auths.add(auth);
	    		}
			});
		
			if(auths.size()>0) {
				String title = "Comunic@";
		    	NotificationRequest notification = new NotificationRequest();
		    	notification.setTitle(title);
		    	notification.setBody(body);
		    	notification.setSender(user.getAuth());
		    	notification.setDomain(api.getDomain());
		    	notification.setUser(api.getUser());
		    	notification.setAuths(auths);
		    	notification.send();
			}
		});
		newThread.start();
	}
	
	private void sendEmailAlta(AonApiData api, Employee employee, String body) {
		Thread newThread = new Thread(() -> {
			try {
				Domain domain = api.getDomain();
				User user = AON_SOLUTIONS.getUser(domain, api.getToken());
				Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
				final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
				String regimen = employee.getRegime();
				String ccc = employee.getCtaCti().get();
				String nss = employee.getNss();
				Date fecha = employee.getFra();
				String from = "no-reply@aon.solutions"; 
				String subject = "COMUNIC@ | AON SOLUTIONS"; 
				LinkedList<File> files = new LinkedList<File>();
				byte[] fileByte = SistemaRED.getTA(certificateInputStream, certificate.getPassword(), certificate.getType(), regimen, ccc, nss, fecha);
				File file = File.createTempFile("duplicado_alta", ".pdf");
				FileOutputStream os = new FileOutputStream(file);
	            os.write(fileByte);
	            os.close();
				files.add(file);
				SES.sendEmailWithAttachment(from, getEmails(api, user), subject, body, files);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	private void sendEmail(AonApiData api, String body) {
		Thread newThread = new Thread(() -> {
			try {
				Domain domain = api.getDomain();
				User user = AON_SOLUTIONS.getUser(domain, api.getToken());
				String from = "no-reply@aon.solutions"; 
				String subject = "COMUNIC@ | AON SOLUTIONS"; 
				SES.sendEmailToList(from, getEmails(api, user), subject, body);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
}
