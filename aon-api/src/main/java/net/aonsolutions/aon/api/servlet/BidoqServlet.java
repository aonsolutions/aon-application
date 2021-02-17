package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.IConstants;

@SuppressWarnings("serial")
@WebServlet(name = "BidoqServlet", urlPatterns = {"/ms/api/bidoq/*"})
public class BidoqServlet extends AonApiHttpServlet {
	
	private static final String BIDOQ_SESSION_ID = "AONd95770f269e711eb94390242ac130002";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			setToken((AonStringUtils.isEmpty(req.getHeader(IConstants.SESSION_ID)) 
					|| IConstants.NULL.equalsIgnoreCase(req.getHeader(IConstants.SESSION_ID))) 
				? IConstants.EMPTY : req.getHeader(IConstants.SESSION_ID));

			setData(getRequestJSON(req));
			
			if(BIDOQ_SESSION_ID.equals(getToken())) {
				String user = getData().optString("user");
				String company = getData().optString("company");
				String action = getData().optString("action");
				
				Auth auth = AON_SOLUTIONS.getAuthByDocument(user);
				if(auth.getUuid() == null) {
					throw new Exception("El usuario no existe");
				}
		   		String domain = AONContext.getSchemaFirstDomain(auth.getSchema());
				String token = AonToken.build(auth, AonDateUtils.addDays(new Date(), 1));
				Company cp = AON.getCompany(domain, 0, "", f -> f.getDocumentProperty().eq(company));
				if(cp.getId() == null) {
					List<String> schemas = AONContext.getSchemas();
					Integer index = 0;
					while(cp.getId() == null && index < schemas.size()) {
						String domainName = AONContext.getSchemaFirstDomain(schemas.get(index));
						if(!AonStringUtils.isBlank(domainName)) {
							cp = AON.getCompany(domainName, 0, "", f -> f.getDocumentProperty().eq(company));
					   	}
						index++;
					}
				}
				if(cp.getId() == null) {
					throw new Exception("La empresa no existe");
				}
				String url = "";
				if(AonStringUtils.isEmpty(action)) {
					url = "https://" +  cp.getDomain().getName() +"/login?token=" + token;
				} else {
					url = "https://" +  cp.getDomain().getName() +"/login?initAction=" + action + "&token=" + token;
				}

				JSONObject json = new JSONObject();
				json.put("url", url);
				response(req, resp, json);
			} else throw new Exception("El token es incorrecto.");
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
}
