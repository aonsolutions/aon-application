package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaREDMov;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMUNICA SERVLET");
		super.doGet(req, resp);
		try {		
		    Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		    String jsonInString = null;
		    Domain domain = getDomain();
			User user = AON_SOLUTIONS.getUser(domain, getToken());
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
			final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
			switch (getPath()) {
				case "/get-employee":
					LOGGER.info("GET-EMPLOYEE SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getEmployee(certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/movements":
					LOGGER.info("MOVEMENTS SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.getMovements(domain, "",  certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/ipfxnaf":
					LOGGER.info("IPFXNAF SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.ipfxnaf(certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/nafxipf":
					LOGGER.info("NAFXIPF SERVLET - GET METHOD");
					jsonInString = gjson.toJson(this.nafxipf(certificateInputStream, certificate.getPassword(), certificate.getType()));
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
		super.doPost(req, resp);
		try {		
		    Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		    String jsonInString = null;
		    Domain domain = getDomain();
			User user = AON_SOLUTIONS.getUser(domain, getToken());
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
			final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
			switch (getPath()) {
				case "/alta-directa":
					LOGGER.info("ALTA-DIRECTA SERVLET - POST METHOD");
					jsonInString = gjson.toJson(sendAlta(certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/delete-mov":
					LOGGER.info("DELETE-MOV SERVLET - POST METHOD");
					jsonInString = gjson.toJson(movDelete(certificateInputStream, certificate.getPassword(), certificate.getType()));
					break;
				case "/update-contrato":
					LOGGER.info("UPDATE-CONTRATO SERVLET - POST METHOD");
					jsonInString = gjson.toJson(updateContrato(certificateInputStream, certificate.getPassword(), certificate.getType()));
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
	
	private Collection<Employee> ipfxnaf(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {
			String nss = getParams().optString("nss");
			if(nss.isEmpty()) {
				throw new Exception("nss requerido");
			};
		    ArrayList<String> nssList = new ArrayList<>();
		    nssList.add(nss);	
		    
		    return SistemaRED.ipfxnaf(certificateInputStream, certificatePassword, certificateType, nssList);
	}
	
	private Employee sendAlta(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		validateSendMov();
		//first screen
		String regimen = getData().optString("regimen");
		String ctaCti = getData().optString("ctaCti");
		String nss = getData().optString("nss");
		
		String ipf = getData().optString("ipf");
		
		//second screen
		Date fecha = Toolkit.parseDate(getData().optString("fecha"), "yyyy-MM-dd");

		String grup_ctz = getData().optString("grup_ctz");
		String type_cto = getData().optString("type_cto");	
		String ocupacion = getData().has("ocupacion")  && !getData().isNull("ocupacion") ? getData().optString("ocupacion") : null;
		String coefparcial = getData().has("coefparcial")  && !getData().isNull("coefparcial") ? getData().optString("coefparcial") : null;
		String convenio =  getData().has("convenio")  && !getData().isNull("convenio") ? getData().optString("convenio") : "60888888888888";
		String md_ctz = getData().has("md_ctz")  && !getData().isNull("md_ctz") ? getData().optString("md_ctz") : null; //para regimen agrario
//        Ctz mensual = 1
//        Jornadas reales = 2
		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder
		.setRegime(regimen)
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
		return SistemaREDMov.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
	}
	
	private Boolean movDelete(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {

		//first screen
		String situation = getData().getString("situation");
		String regimen = getData().getString("regime");
		String ctaCti = getData().getString("ctaCti");
		String nss = getData().getString("nss");
		Boolean prev = getData().getBoolean("prev"); //true prev, false consolidado

		//second screen
		Date fecha = Toolkit.parseDate(getData().getString("fra"), "yyyy-MM-dd");

		if(prev) {
			SistemaRED.movPrevDelete(certificateInputStream, certificatePassword, certificateType,  situation, regimen, ctaCti, nss, fecha);
		} else {
			SistemaRED.altaConsolidadaDelete(certificateInputStream, certificatePassword, certificateType, situation, regimen, ctaCti, nss);
		}
		return true;
	}
	
	private Employee nafxipf(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		String ipf = getParams().optString("ipf");
		String apellido1 =  getParams().optString("apellido1");
		String apellido2 =  getParams().optString("apellido2");
		return SistemaRED.nafxipf(certificateInputStream, certificatePassword, certificateType, ipf, apellido1,  apellido2);
	}
	
	private Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception{
		String regimen = getParams().optString("regime");
		String ccc =  getParams().optString("ctaCti");
		String nss =  getParams().optString("nss");
		return SistemaRED.getEmployee(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss);
	}
	
	private Map<String, Object> updateContrato(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException, Exception {
		Map<String, Object> map = new HashMap<>();
		List<String> errors = new ArrayList<String>();
		map.put("grup_ctz_edit", false);
		map.put("ocupacion_edit", false);
		if(getData().isNull("fecha")) {
			throw new Exception("fecha requerida");
		}
		String regimen = getData().optString("regimen");
		String ctaCti = getData().optString("ctaCti");
		String nss = getData().optString("nss");
		String ipf = getData().optString("ipf");
		Date fecha = Toolkit.parseDate(getData().optString("fecha"), "yyyy-MM-dd");
		
		String ocup = getData().optString("ocupacion");
		if(!ocup.isEmpty() && !getData().optString("ocupacion_edit").isEmpty() && getData().getBoolean("ocupacion_edit")) {
            try {
            	map.put("ocupacion_edit", true);
            	SistemaRED.cambioOcupacion(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, ocup, fecha);
            } catch(Exception e) {
            	e.printStackTrace();
            	errors.add(e.getMessage());
            }	
		}
		
		String grup_ctz = getData().optString("grup_ctz");
		if(!grup_ctz.isEmpty() && !getData().optString("grup_ctz_edit").isEmpty() && getData().getBoolean("grup_ctz_edit")) {
			 try{
				 map.put("grup_ctz_edit", true);
				 SistemaRED.cambioGrupCtz(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, grup_ctz, fecha);
			} catch(Exception e) {
				e.printStackTrace();
				errors.add(e.getMessage());
			}	
		}
		if(errors.size() > 0) map.put("errors",errors);
		
		return map;
	}
	
	private void validateSendMov() throws Exception {
		if(getData().isNull("ctaCti")) 
			throw new Exception("Cuenta de cotización requerida");
		else if(getData().isNull("nss")) 
			throw new Exception("Número de afiliación requerido");
	    else if(getData().isNull("ipf")) 
			throw new Exception("DNI/NIE requerido");
		else if(getData().isNull("fecha")) 
			throw new Exception("Fecha requerida");
		else if(getData().isNull("grup_ctz")) 
			throw new Exception("Grupo de cotización requerido");
		else if(getData().isNull("type_cto")) 
			throw new Exception("Tipo de contrato requerido");
		else if( "501".equals(getData().getString("type_cto")) || "502".equals(getData().getString("type_cto")) ) 
			if(getData().optString("coefparcial") == null || "".equals(getData().optString("coefparcial")) ) {
				throw new Exception("Coeficiente parcial requerido");
			}
	}
	
}
