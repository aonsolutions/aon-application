package net.aonsolutions.aon.api.servlet.project;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.ProjectJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiProjectServlet", urlPatterns = {"/ms/api/project/*"})
public class ProjectServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ProjectServlet.class.getName());
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[GET /ms/api/project] AON API PROJECT SERVLET");
		try {		
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					break;
				case "/office":
					response(req, resp, getOfficeProjects(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private JSONArray getOfficeProjects(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		JSONArray arr = new JSONArray();
		AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
			AON.getProjectStream(domain, "", f -> f.getRegistryProperty().eq(customer.getId()).and(f.getProjectTypeProperty().isNotNull()))
				.forEach(project -> {
					ProjectHolder holder = AON.getProjectHolder(project.getDomain(), "", f -> f.getProjectProperty().eq(project.getId()).and(f.getEndDateProperty().isNull()));
					project.setProjectHolder(holder);
					arr.put(ProjectJSON.toJSON(project));	
				});
		});
		return arr;
	}
}
