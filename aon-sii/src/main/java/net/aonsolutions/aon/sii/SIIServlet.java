package net.aonsolutions.aon.sii;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@SuppressWarnings("serial")
@WebServlet(name = "SIIServlet22", urlPatterns = { "/sii22/*",
												 "/aon_gwt_aio/sii22/*"})
public class SIIServlet extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(SIIServlet.class.getName());

	public static SIIServlet getInstance() {
		return new SIIServlet();
	}
	
	public SIIServlet() {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SII Servlet - GET METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(pathInfo[pathInfo.length-1]);
		String domainName = parameters.get("domain");
		String login = parameters.get("login");	
		
		String[] idS = parameters.get("id").split(",");
		Integer[] ids = new Integer[idS.length];
		for(Integer i = 0 ; i < idS.length ; i++){
			ids[i] = Integer.parseInt(idS[i]);
		}
//		String accessToken = req.getParameter(MSG.ACCESS_TOKEN); TODO
//		String md5 = Utils.getMd5(login+domainName);
		if(true){//accessToken.equals(md5)){
			String action = parameters.get("action"); // consulta || suministro || anulacion
			String option = parameters.get("option"); 
			String terceros = parameters.get("terceros"); 
			Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));		
			Company company = AON.getCompany(domain.getName(), domain.getId(), login,f -> f.getDomainProperty().eq(domain.getId()));
			
			VatParams params = new VatParams();
			params.setDomain(domain.getId());
			params.setFromDate(AonDateUtils.addDays(new Date(), -7));
			params.setToDate(AonDateUtils.addDays(new Date(), 1));
			params.setInvoices(ids);//invoiceList.stream().map(i -> i.getId()).toArray(Integer[]::new));
			LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(domain.getName(), domain.getId(), login, params)
					.collect(Collectors.toCollection(LinkedList::new));
		
			// TODO dividir invoiceList en las demas listas.
			Integer cert = Integer.parseInt(parameters.get("cert"));
			String pass = parameters.get("pass");
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getIdProperty().eq(cert))
					.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())), AttachType.REGISTRY, true);
			if(attach.getData() == null){
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}
			try{
				Object object = new Object();
				if(option.equals("cobros")){
					LinkedList<Invoice> invoiceList = AON.getInvoiceList(domain.getName(), domain.getId(), login,f -> f.getIdProperty().in(ids));
					object = SIIPost.getInstance(attach.getData(), pass).suministroFacturasEmitidasCobros(domain, login, company, invoiceList, contextList, terceros);
				} else if(option.equals("pagos")){
					LinkedList<Invoice> invoiceList = AON.getInvoiceList(domain.getName(), domain.getId(), login,f -> f.getIdProperty().in(ids));
					object = SIIPost.getInstance(attach.getData(), pass).suministroFacturasRecibidasPagos(domain, login, company, invoiceList, contextList, terceros);
				} else if(option.equals("intracomunitaria")){
					String tipoOp = parameters.get("tipo_operacion");
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroOperacionesIntracomunitarias(domain, login,company, new LinkedList<>(Arrays.asList(ids)), contextList, tipoOp, terceros);
					}else if(action.equals("baja")){
						object= SIIPost.getInstance(attach.getData(), pass).bajaOperacionesIntracomunitarias(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				} else if(option.contains("fe_")){
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroFacturasEmitidas(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					} else if(action.equals("baja")){
						object = SIIPost.getInstance(attach.getData(), pass).bajaFacturasEmitidas(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				} else if(option.contains("fr_")){
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroFacturasRecibidas(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					} else if(action.equals("baja")){
						object = SIIPost.getInstance(attach.getData(), pass).bajaFacturasRecibidas(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				} else if("bienes".equalsIgnoreCase(option)){
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroBienesInversion(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);	
					} else if(action.equals("baja")){
						object = SIIPost.getInstance(attach.getData(), pass).bajaBienesInversion(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				} else if("metalico".equalsIgnoreCase(option)){
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroCobrosMetalico(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					} else if(action.equals("baja")){
						object = SIIPost.getInstance(attach.getData(), pass).bajaCobrosMetalico(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				} else if("seguros".equalsIgnoreCase(option)){
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroOperacionesSeguros(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					} else if(action.equals("baja")){
						object = SIIPost.getInstance(attach.getData(), pass).bajaOperacionesSeguros(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				} else if("agencias".equalsIgnoreCase(option)){
					if(action.equals("suministro")){
						object = SIIPost.getInstance(attach.getData(), pass).suministroAgenciasViajes(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					} else if(action.equals("baja")){
						object = SIIPost.getInstance(attach.getData(), pass).bajaAgenciasViajes(domain, login, company, new LinkedList<>(Arrays.asList(ids)), contextList, terceros);
					}
				}	
				giveBack(req, resp, object, new JSONObject());
			} catch (Exception e) {
				JSONArray array = new JSONArray();
				JSONObject json = new JSONObject();
				json.put("id", 1);
				json.put("name", e.getLocalizedMessage());
				array.put(json);
				giveBack(req, resp, array, new JSONObject());
				e.printStackTrace();
			}
		}
	}
	
	public static void giveBack(HttpServletRequest req, HttpServletResponse resp, Object object, JSONObject meta) {
		try {
			String js = req.getParameter("callback");
			if(js != null){
				resp.setContentType("application/javascript; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
				out.flush();
			} else {
				resp.setContentType("application/json");     
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SII Servlet - POST METHOD");
	}	
	
    public static Filter invoiceFilter(Domain domain, Map<String, String[]> filterMap, InvoiceProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
	
		if(filterMap.containsKey("id")){
			Integer id = Integer.parseInt(filterMap.get("id")[0]);
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		return filter;
    }
}
