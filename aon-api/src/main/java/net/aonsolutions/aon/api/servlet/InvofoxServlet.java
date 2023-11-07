package net.aonsolutions.aon.api.servlet;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.JSONB;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gson.JsonArray;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.invofox.OCRCompanyParams;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRLoginToken;
import net.aonsolutions.invofox.model.OCRSeverity;
import net.aonsolutions.invofox.model.OCRType;

@SuppressWarnings("serial")
@WebServlet(name = "InvofoxServlet", urlPatterns = {"/ms/api/invofox/*"})
public class InvofoxServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(InvofoxServlet.class.getName());
	
	public static final String DOCUMENTS = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			String token = OCRInvofox.getLoginToken().getLoginToken()
					.orElse(new OCRLoginToken()).getToken().orElse(null);
			Company cp = AON.getCompany(api.getDomain(), api.getUser(), f-> f.getDomainProperty().eq(api.getDomain().getId()));
			if(!AonStringUtils.isBlank(cp.getDocument())) {
				OCRCompaniesResponse companiesResponse = OCRInvofox.getCompanies(OCRCompanyParams.get().withTaxId(cp.getDocument()));
				List<OCRCompany> companies = companiesResponse.getCompanies().orElse(new LinkedList<>());
				if(!companies.isEmpty()) {
					OCRCompany company = companies.get(0);
					OCRDocumentsResponse response = OCRInvofox.getDocuments(
						OCRDocumentsParams.get().withType(OCRType.invoice)
//						.withPublicState(OCRSeverity.pendingCorrection)
						.withCompany(company.getId()));
					
					JSONArray array = new JSONArray();

					response.getDocuments().orElse(new LinkedList<>()).stream().forEach(r -> {
						JSONObject json = new JSONObject();
						json.put("id", r.getId().get());
						json.put("reference", r.getData().get().getDocumentNumber().get().getValue().orElse(""));
						json.put("name", r.getData().get().getIssuerName().get().getValue().orElse(""));
						json.put("date", r.getData().get().getIssueDate().get().getValue().orElse(""));
						json.put("total", r.getData().get().getTotalAmount().get().getValue().orElse(new BigDecimal(0)));
						json.put("token", token);
						json.put("status", r.getPublicState().get().name());
						array.put(json);
					});			
			
					response(req, resp, array);
				} else response(req, resp, new JsonArray());
			} else response(req, resp, new JsonArray());
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
}
