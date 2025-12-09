package net.aonsolutions.aon.api.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ContractedPlansServlet", urlPatterns = "/ms/api/contracted_plans_servlet/*")
public class ContractedPlansServlet extends AonApiHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String ApiKey = "H94T4DE4CLCTTL8CSPHUP";

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
    
    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}
    
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			
			switch (api.getPath()) {
			case "/apps":
				response(req, resp, getContractedApps(api));
				break;	
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}	
	}


	private void post(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch(api.getPath()) {
				case "/callForm":
					response(req, resp, callFormService(api));
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

    private JSONObject callFormService(AonApiData api) throws IOException {
    	String url = "https://formsservice-pro.up.railway.app/api/form-request";
    	
        String email = api.getData().getString(IJsonNames.EMAIL);
        String name = api.getData().getString(IJsonNames.NAME);
        String surname = api.getData().getString(IJsonNames.SURNAME);
        String phone = api.getData().getString(IJsonNames.PHONE);
        String userFullName = name + " " + surname;
        String plan = api.getData().getString("plan");
        String domainurl = api.getData().getString("domainUrl");
        
        String chanel = api.getData().getString("chanel");
        chanel = AonStringUtils.isBlank(chanel) ? "Conect@ aon" : chanel;
        
        String serviceId = api.getData().getString("serviceId");
        Integer id = AonStringUtils.isBlank(serviceId) ? 19 : Integer.parseInt(serviceId);
        
        
        JSONObject company = api.getData().getJSONObject("companyData");
        String companyName = company.getString("name");
        String companyDocument = company.getString("document");
        String consulta = "El usuario : " + userFullName + " solicita el plan : " + plan + " Documento de la empresa : " + companyDocument;

        JSONObject formData = new JSONObject()
            .put("email", email)
            .put("name", companyName) 
            .put("phone", phone)
            .put("canal", chanel)
            .put("url", domainurl)
            .put("consulta", consulta) // plan solicitado + usuario que lo solicita
            .put("idEnterprise", id)
            .put("idAnalytics", "")
            .put("gclid", "")
            .put("source", "")
            .put("way", "")
            .put("campaign", "")
            ;
			       
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", ApiKey);
            post.setEntity(new StringEntity(formData.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return new JSONObject(responseBody);
            } 
        }
    }
    
    private JSONArray getContractedApps(AonApiData api) {
        JSONArray array = new JSONArray();

        DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
        dur.getDomainApps().stream().forEach(app -> array.put(app.name().toLowerCase()));
        return array;
    }
}
