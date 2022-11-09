package net.aonsolutions.aon.api.servlet.registry;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.RegistryRelationshipJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiRelationshipServlet", urlPatterns = {"/ms/api/relationship/*"})
public class RelationshipServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RelationshipServlet.class.getName());
	
	private static final String RELATIONS = "/";
	private static final String RELATION = "/:id";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(RELATIONS, RelationshipServlet::getRelationships)
				.addRoute(RELATION, RelationshipServlet::getRelationship)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(RELATION, RelationshipServlet::saveRelationship)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(RELATION, RelationshipServlet::deleteRelationship)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getRelationships(AonApiData api) {
		return RegistryRelationshipJSON.toJSON( 
				AON_SOLUTIONS.getRegistryRelationshipStream(api.getDomain(), api.getUser(), f-> f.getDomainProperty().eq(api.getDomain().getId())) 
		);
	}
	
	private static JSONObject getRelationship(AonApiData api) {
		JSONObject params = api.getData();
		JSONObject json = new JSONObject();
		
		Integer registry = params.optInt(IJsonNames.REGISTRY);
		
		List<Company> companies = getCompanies(api);
		
		Integer[] registryCompanies = companies.stream().map(Company::getId).toArray(Integer[]::new);
		
		AON_SOLUTIONS.getRegistryRelationship(api.getDomain(), api.getUser(), 
			f-> f.getRegistryProperty().eq(registry)
			.and(f.getRelatedRegistryProperty().in(registryCompanies))
			.and(f.getRelationshipProperty().eq(-1))
		)
		.ifPresent(relation->
			json.put("rrelationship", RegistryRelationshipJSON.toJSON(relation))
		);
		
		json.put("companies", CompanyJSON.toJSON(companies));
		
		return json;
	}
	
	private static List<Company> getCompanies(AonApiData api) {
		JSONObject params = api.getData();
		return AON.getCompanyStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f-> f.getDomainParentProperty().eq(params.optInt(IJsonNames.PARENT_ID))
			.and(f.getDocumentProperty().eq(params.optString(IJsonNames.DOCUMENT)))
		).collect(Collectors.toList());
	}
	
	
	public static JSONObject saveRelationship(AonApiData api) {
		 JSONObject params = api.getData();
		 
		 RegistryRelationship rrelationship = RegistryRelationshipJSON.fromJSON(api.getData());
		 
		 return RegistryRelationshipJSON.toJSON( 
			 AON_SOLUTIONS.saveRegistryRelationship( api.getDomain(), api.getUser(), rrelationship)
		 );
				 
// ------------- SAVE IN COMPANY
//		 RegistryRelationship rrelationshipCompany = new RegistryRelationship()
//		 .setDomain(company.getDomain())
//		 .setRegistry(0)
//		 .setRelatedRegistry(company.getId())
//		 .setComments(api.getDomain().getName())
//		 ;
//		 AON_SOLUTIONS.saveRegistryRelationship(company.getDomain(), api.getUser(), rrelationshipCompany);
//				
//		throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
	}

	private static JSONObject deleteRelationship(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		AON_SOLUTIONS.deleteRegistryRelationship(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
}
