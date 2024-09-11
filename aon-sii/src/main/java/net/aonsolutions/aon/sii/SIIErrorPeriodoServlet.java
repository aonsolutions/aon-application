package net.aonsolutions.aon.sii;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.InvoicePropertiesOLD;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@SuppressWarnings("serial")
@WebServlet(name = "SIIServletErrorPeriodo", urlPatterns = { "/siiErrorPeriodo/*",
												 "/aon_gwt_aio/ms/siiErrorPeriodo/*"})
public class SIIErrorPeriodoServlet extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(SIIErrorPeriodoServlet.class.getName());

	public static SIIErrorPeriodoServlet getInstance() {
		return new SIIErrorPeriodoServlet();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SII Servlet - GET METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(pathInfo[pathInfo.length-1]);
		String domainName = parameters.get("domain");
		String login = parameters.get("login");	

		if(true){
			String option = parameters.get("option"); 
			String terceros = parameters.get("terceros"); 

			Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));		
			Company company = AON.getCompany(domain.getName(), domain.getId(), login,f -> f.getDomainProperty().eq(domain.getId()));
			LinkedList<Integer> invIds = getInvoiceRecibidasList(domain, login);
			
			Integer cert = Integer.parseInt(parameters.get("cert"));
			String pass = parameters.get("pass");
			Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(cert)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())), AttachType.REGISTRY, true);
			
			if(attach.getData() == null){
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}

			SiiConfiguration siiConfiguration = AON.getSiiConfiguration(domain, login);
			siiConfiguration.setCertificate(new Certificate()
					.setData(attach.getData())
					.setPassword(pass)
					.setType(CertificateType.AEAT.name()));
			try{
				SIIManager manager = SIIManager.getInstance(siiConfiguration);
				invIds.stream().forEach(id -> {
					Integer[] ids = new Integer[1];
					ids[0] = id;
					AccountingReportParams params = new AccountingReportParams();
					params.setDomain(domain.getId());
					params.setInvoices(ids);

					LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(domain.getName(), domain.getId(), login, params, option)
						.collect(Collectors.toCollection(LinkedList::new));
					try {
						manager.suministroFacturasRecibidas(domain, login, company, ids[0], contextList, terceros, true);
					} catch (JAXBException | ParserConfigurationException | SOAPException | IOException e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
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
	
    public static Filter invoiceFilter(Domain domain, Map<String, String[]> filterMap, InvoicePropertiesOLD f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
	
		if(filterMap.containsKey("id")){
			Integer id = Integer.parseInt(filterMap.get("id")[0]);
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		return filter;
    }
    
    private LinkedList<Integer> getInvoiceRecibidasList(Domain domain, String login){
    	Date from = AonDateUtils.getDate(2021, 8, 22);
    	Date to = AonDateUtils.getDate(2021, 9, 27);
    	return AON.getSiiInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> iFilterRecibidas(domain, login, f, from, to)
    			,false, true, false, false, false, "")
    		.map(r -> r.getId()).collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static Filter iFilterRecibidas(Domain domain, String login, InvoicePropertiesOLD f, Date from, Date to) {
    	ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());
    	Filter filter =  f.getDomainProperty().eq(domain.getId())
    			.and(isRegistro 
    				? f.getCreationDateProperty().ge(new Timestamp(from.getTime()))
    				: f.getTaxDateProperty().ge(from));
    	if(to != null) {
    		filter = filter.and(isRegistro 
				? f.getCreationDateProperty().le(new Timestamp(to.getTime()))
				: f.getTaxDateProperty().le(to));
    	}
    	

		filter = filter.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()) 
					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())));
    	return filter;
    }
}
