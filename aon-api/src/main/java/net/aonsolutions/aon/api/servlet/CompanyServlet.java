package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMPANY SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		JSONArray jsArray = new JSONArray();
		AON_SOLUTIONS.getCompanyStream(token)
		//.filter(f -> f.isActive())
		.sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).forEach(
			domain -> jsArray.put(company2json(domain)));
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, jsArray, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMPANY SERVLET - POST METHOD");
	}
	
	private JSONObject company2json(Company company) {
		Domain domain = AON.getDomain(company.getDomainName(), company.getDomain(), "");
		Domain parentDomain = AON.getDomain(domain.getName(), domain.getParentId(), "");
		ApplicationParameter param= AON.getApplicationParameter(company.getDomainName(), company.getDomain(), "", AppParam.FS_DEFAULT_ADMINISTRATION);
		Administration administration = param.getValue() != null ? Administration.values()[Integer.parseInt(param.getValue())] : Administration.COMMON_TERRITORY;
		JSONObject json = new JSONObject()
				.put("id", company.getDomain())
				.put("domain", company.getDomainName())
				.put("name", company.getName())
				.put("document", company.getDocument())
				.put("active", company.isDomainActive())
				.put("administration", administration.name())
				.put("logo", "https://" + domain.getName() + "/aonDocuments/company.logo")
				.put("parent", domain.getParentId() == null);
		if(parentDomain != null) {
			json.put("parentLogo", "https://" + parentDomain.getName() + "/aonDocuments/company.logo");
		}
		return json;
	}
	
}
