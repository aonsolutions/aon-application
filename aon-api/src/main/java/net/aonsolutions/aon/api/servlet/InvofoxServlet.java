package net.aonsolutions.aon.api.servlet;
import java.math.BigDecimal;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.JSONB;
import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
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
			String token = OCRInvofox.getLoginToken().getLoginToken().get().getToken().orElse(null);
			OCRDocumentsResponse response = OCRInvofox.getDocuments(
					OCRDocumentsParams.get().withType(OCRType.invoice)
					.withPublicState(OCRSeverity.pendingCorrection)
					.withCompany("6480556355f159000abb18eb"));
			
			JSONArray array = new JSONArray();

			response.getDocuments().get().stream().forEach(r -> {
				JSONObject json = new JSONObject();
				json.put("id", r.getId().get());
				json.put("reference", r.getData().get().getDocumentNumber().get().getValue().orElse(""));
				json.put("name", r.getData().get().getIssuerName().get().getValue().orElse(""));
				json.put("date", r.getData().get().getIssueDate().get().getValue().orElse(""));
				json.put("total", r.getData().get().getTotalAmount().get().getValue().orElse(new BigDecimal(0)));
				json.put("token", token);
				array.put(json);
			});			
			
			response(req, resp, array);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
}
