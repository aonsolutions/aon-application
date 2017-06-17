package com.code.aon.webservice.sii;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "SIIServlet", urlPatterns = { "/sii/*",
												 "/aon_gwt_aio/sii/*"})
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
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String md5 = Utils.getMd5(login+domainName);
		if(accessToken.equals(md5)){
			String action = parameters.get(MSG.ACTION); // consulta || suministro || anulacion
			Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));		
			Company company = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
			LinkedList<Invoice> invoiceList = AON.getInvoiceList(domain.getName(), domain.getId(), login, f -> invoiceFilter(domain, req.getParameterMap(), f));
			VatParams params = new VatParams();
			params.setFromDate(AonDateUtils.addDays(new Date(), -7));
			params.setToDate(AonDateUtils.addDays(new Date(), 1));
			params.setInvoices(invoiceList.stream().map(i -> i.getId()).toArray(Integer[]::new));
			LinkedList<VatContext> contextList = FISCAL.getVatContext(domain.getName(), domain.getId(), login, params)
					.collect(Collectors.toCollection(LinkedList::new));
			
			LinkedList<Integer> emitidasList =  invoiceList.stream().filter(f -> f.getType().equals(InvoiceType.SALES) && !f.getTransaction().equals(InvoiceTransactionType.INTRACOMMUNITY))
					.map(f -> f.getId()).collect(Collectors.toCollection(LinkedList::new));
			LinkedList<Integer> recibidasList = invoiceList.stream().filter(f -> f.getType().equals(InvoiceType.PURCHASE) && !f.getTransaction().equals(InvoiceTransactionType.INTRACOMMUNITY) && !f.isInvestment()).map(f -> f.getId()).collect(Collectors.toCollection(LinkedList::new));
			LinkedList<Integer> bienesList =invoiceList.stream().filter(f -> f.getType().equals(InvoiceType.PURCHASE) && !f.getTransaction().equals(InvoiceTransactionType.INTRACOMMUNITY) && f.isInvestment()).map(f -> f.getId()).collect(Collectors.toCollection(LinkedList::new));;
			LinkedList<Integer> intracomunitariasList =  invoiceList.stream().filter(f -> f.getType().equals(InvoiceType.PURCHASE) && f.getTransaction().equals(InvoiceTransactionType.INTRACOMMUNITY)).map(f -> f.getId()).collect(Collectors.toCollection(LinkedList::new));
			LinkedList<Integer> metalicoList = new LinkedList<>();
			LinkedList<Integer> segurosList = new LinkedList<>();
			LinkedList<Integer> agenciasList = new LinkedList<>();
		
			// TODO dividir invoiceList en las demas listas.
		
			if(emitidasList.size() > 0){
				SIIPost.getInstance().suministroFacturasEmitidas(domain, login, company, emitidasList, contextList);
			}
		
			if(recibidasList.size() > 0){
				SIIPost.getInstance().suministroFacturasRecibidas(domain, login, company, recibidasList, contextList);
			}
		
			if(bienesList.size() > 0){
				SIIPost.getInstance().suministroBienesInversion(domain, login, company, bienesList, contextList);
			}
		
			if(intracomunitariasList.size() > 0){
				SIIPost.getInstance().suministroOperacionesIntracomunitarias(domain, login,company, intracomunitariasList, contextList);
			}
		
			if(metalicoList.size() > 0){
				SIIPost.getInstance().suministroOperacionesMetalico(domain, login, company, metalicoList, contextList);
			}
		
			if(segurosList.size() > 0){
				SIIPost.getInstance().suministroOperacionesSeguros(domain, login, company, segurosList, contextList);
			}
		
			if(agenciasList.size() > 0){
				SIIPost.getInstance().suministroAgenciasViajes(domain, login, company, agenciasList, contextList);
			}
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
