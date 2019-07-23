package com.code.aon.webservice.commercial;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.OfferType;

import es.translogia.tedi.TediCompany;

@SuppressWarnings("serial")
@WebServlet(name = "CommissionServlet", urlPatterns = {"/offer/*",
												   "/aon_gwt_aio/offer/*",
												   "/aon_gwt_commercial/offer/*"})
public class OfferServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(OfferServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Offer Servlet - GET METHOD");
		System.out.println(req.getServerName());
	
		Object object = new Object();
		JSONObject meta = new JSONObject();
		
		Utils.giveBack(req, resp, object, meta);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Offer Servlet - POST METHOD");
		
		JSONObject json = Utils.getRequestJSON(req);
		
		System.out.println(req.getServerName());
		
		Domain domain = AON.getDomain(req.getServerName(), 1, "", f -> f.getNameProperty().eq(req.getServerName()));
		
		Object object = new Object();
		
	
		TediCompany tc = new TediCompany(json);
		Optional<Target> tOpt = AON.getTarget(domain.getName(), domain.getId(), "", f ->
			f.getDomainProperty().eq(domain.getId())
			.and(f.getDocumentProperty().eq(tc.getDocument())));
		Target target = null;
		if(!tOpt.isPresent()) {
			Registry registry = AON.getRegistry(domain.getName(), domain.getId(), "", f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(tc.getDocument())));
			
			if(registry.getId() == null) {
				registry = new Registry()
						.setName(tc.getName())
						.setDocument(tc.getDocument());
				registry = AON.insertRegistry(domain.getName(), domain.getId(), "", registry);
			}
			target = AON.insertTarget(domain.getName(), domain.getId(), "", (Target) registry);
		} else {
			target = tOpt.get();
		}

		Offer o = AON.getOffer(domain.getName(), domain.getId(), "", f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSeriesProperty().eq("")));
		Workplace wp = AON.getWorkplace(domain.getName(), domain.getId(), "", f-> f.getDomainProperty().eq(domain.getId()));
		Offer offer = new Offer()
				.setDomain(domain.getId())
				.setIssueDate(new Date())
				.setTarget(target)
				.setType(OfferType.NORMAL)
				.setNumber(o.getNumber() + 1)
				.setVersion(1)
				.setWorkPlace(wp)
				;
		
		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println(object.toString());
		os.flush();
		os.close();
	}
}
