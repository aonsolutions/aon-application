package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMPANY SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		String sch = req.getHeader("schema");
		String rsch = "";
		Integer page = AonNumberUtils.toInteger(req.getHeader("page"));
		Integer perPage = AonNumberUtils.toInteger(req.getHeader("per_page"));
		JSONArray jsArray = new JSONArray();
		Boolean bool = sch == null;
		Boolean next = false;
		List<String> schemas = AONContext.getSchemas();
		for(String schema : schemas) {
			if(next) {
				rsch = schema;
				page = 1;
				next = false;
			}
			if(bool || (sch != null && (sch.equalsIgnoreCase(schema) || sch.equalsIgnoreCase("first")))) {
				AON_SOLUTIONS.getCompanyStream(token, schema, page, perPage)
					//.filter(f -> f.isActive())
					.sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).forEach(
							domain -> jsArray.put(company2json(domain)));
				next = jsArray.length() >= 0 && jsArray.length() < perPage;
				page = page + 1;
				rsch = schema;
			}
		}
		JSONObject json = new JSONObject();
		json.put("companies", jsArray);
		json.put("page", page);
		json.put("per_page", perPage);
		json.put("schema", rsch);
		json.put("end", next);
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMPANY SERVLET - POST METHOD");
	}
	
	private JSONObject company2json(Company company) {
		Domain domain = AON.getDomain(company.getDomainName(), company.getDomain(), "");
		Domain parentDomain = domain.getParentId() != null ? AON.getDomain(domain.getName(), domain.getParentId(), "") : null;
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
