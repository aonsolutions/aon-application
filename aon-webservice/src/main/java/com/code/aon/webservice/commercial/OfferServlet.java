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
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.OfferDetailStatus;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;

import es.translogia.tedi.ewok.TediCompany;
import es.translogia.tedi.json.TediCompanyJSON;

@SuppressWarnings("serial")
@WebServlet(name = "OfferServlet", urlPatterns = {"/offer/*",
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
		
		Domain domain = AON.getDomain(req.getServerName(), 1, "", f -> f.getNameProperty().eq(req.getServerName()));

		JSONObject object = new JSONObject();
		
		if(json.opt("evicertia") != null) {
			Offer offer = AON.getOffer(domain.getName(), domain.getId(), "", f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getSeriesProperty().eq("TEDI"))
					.and(f.getNumberProperty().eq(json.getInt("number"))));
			offer.setExternalReference(json.getJSONObject("evicertia").getString("uniqueId"));
			AON.updateOffer(domain.getName(), domain.getId(), "", offer);
		} else if(json.opt("company") != null) {
			TediCompany tc = TediCompanyJSON.fromJSON(json.getJSONObject("company"));
			Optional<Target> tOpt = AON.getTarget(domain.getName(), domain.getId(), "", f ->
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(tc.getDocument())));
			Target target = null;
			Scope scope = AON.getScopeStream(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId())).findFirst().get();;

			if(!tOpt.isPresent()) {
				Registry registry = AON.getRegistry(domain.getName(), domain.getId(), "", f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getDocumentProperty().eq(tc.getDocument())));
				
				if(registry.getId() == null) {
					registry = new Registry()
							.setDomain(domain.getId())
							.setName(tc.getName())
							.setDocument(tc.getDocument());
					registry = AON.insertRegistry(domain.getName(), domain.getId(), "", registry);
				}
				
				Target tar = new Target();
				tar.setDomain(domain.getId());
				tar.setAdvertising((short) 0);
				tar.setWithholding((short) 0);
				tar.setTransaction((short) 0);
				tar.setSurcharge((short) 0);	
				tar.setScope(scope.getId());
				tar.setStatus(TargetStatus.ACTIVE);
				tar.setId(registry.getId());
					
				target = AON.insertTarget(domain.getName(), domain.getId(), "", tar);
			} else {
				target = tOpt.get();
			}
			
			Offer o = AON.getOfferStream(domain.getName(), domain.getId(), "", f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getSeriesProperty().eq("TEDI")))
				.max((i, j) -> i.getNumber().compareTo(j.getNumber())).orElse(new Offer().setNumber(0));
			
			Workplace wp = AON.getWorkplace(domain.getName(), domain.getId(), "", f-> f.getDomainProperty().eq(domain.getId()));
			Offer offer = new Offer()
					.setDomain(domain.getId())
					.setIssueDate(new Date())
					.setTarget(target)
					.setType(OfferType.NORMAL)
					.setSeries("TEDI")
					.setStatus(OfferStatus.PENDING)
					.setScope(scope)
					.setNumber(o.getNumber() + 1)
					.setVersion(1)
					.setWorkPlace(wp)
					.setBankAccount(tc.getIban())
					.setBic(tc.getBic());
			
			offer = AON.insertOffer(domain.getName(), domain.getId(), "", offer);

			Boolean isAnual = tc.getPlan().getPeriod().equals("A");
			String plan = tc.getPlan().getPlan();
			Item item = AON.getItem(domain.getName(), domain.getId(), "", f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDetailProperty().eq("Plan " + plan))
				.and(f.getDetail2Property().eq(isAnual ? "Anual" : "Mensual")));
			Product p = AON.getProduct(domain.getName(), domain.getId(), "", f-> f.getIdProperty().eq(item.getProductId()));
			OfferDetail offerDetail = new OfferDetail()
					.setDescription(p.getName() + "[" + item.getDetail() + " " + item.getDetail2() + "]")
					.setOffer(offer)
					.setItem(item)
					.setDiscountExpression("0.0")
					.setDomain(domain.getId())
					.setLine((short) 1)
					.setPrice(item.getPrice())
					.setQuantity(1.0)
					.setStatus(OfferDetailStatus.PENDING);

			offerDetail = AON.insertOfferDetail(domain.getName(), domain.getId(), "", offerDetail);
			object.put("registry", offer.getTarget().getId());
			object.put("number", offer.getNumber());
		} else {
			System.out.println("¡¡EVICERTIA!!");
			System.out.println(json.toString());
			if("EviSignSigned".equals(json.getString("EventType")) || "EviSignFullySigned".equals(json.getString("EventType"))) {
				Offer offer = AON.getOffer(domain.getName(), domain.getId(), "", f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getSeriesProperty().eq("TEDI"))
					.and(f.getExternalReferenceProperty().eq(json.getString("EvidenceId"))));
				
				offer.setSigned(true);
				offer.setStatus(OfferStatus.APPROVED);
				AON.updateOffer(domain.getName(), domain.getId(), "", offer);
			} else if("EviSignRejected".equals(json.getString("EventType"))) {
				Offer offer = AON.getOffer(domain.getName(), domain.getId(), "", f -> 
				f.getDomainProperty().eq(domain.getId())
					.and(f.getSeriesProperty().eq("TEDI"))
					.and(f.getExternalReferenceProperty().eq(json.getString("EvidenceId"))));
				
				offer.setStatus(OfferStatus.REFUSED);
				AON.updateOffer(domain.getName(), domain.getId(), "", offer);
			}			
		}

		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println(object.toString());
		os.flush();
		os.close();
	}

}
