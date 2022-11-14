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
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.RegistryRelationshipJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
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
			.and(f.getRelationshipProperty().eq(-1))
			.and(
				companies.isEmpty() ? 
				f.getDomainProperty().eq(api.getDomain().getId()) :
				f.getRelatedRegistryProperty().in(registryCompanies)
			)
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
	
	
	public static Object saveRelationship(AonApiData api) {

		 boolean isCustomers = !api.getData().isNull("customers");
		 if(isCustomers) {
			 return saveRelationshipAll(api);
		 } else {
			 return RegistryRelationshipJSON.toJSON( 
					 saveRelationship(api, RegistryRelationshipJSON.fromJSON(api.getData())) 
			);
		 }
	}
	
	private static Object saveRelationshipAll(AonApiData api) {
		 JSONObject params = api.getData();
		 
		 JSONArray arr = new JSONArray();
		 
		 boolean isAdd = params.optBoolean("add");
		 
		 List<Customer> customers = CustomerJSON.fromJSON(params.optJSONArray("customers"));
		 
		 if(!customers.isEmpty()) {
			
			 Integer[] registryId = customers.stream().map(Customer::getId).toArray(Integer[]::new);
			 
			 if(isAdd) {
				 Integer[] parentIds = customers.stream().map(c-> c.getDomain().getParentId()).toArray(Integer[]::new);
				 String[] documents  = customers.stream().map(Customer::getDocument).toArray(String[]::new);
				 
				 List<Company> companyAll = AON.getCompanyStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
						f-> f.getDomainParentProperty().in(parentIds)
						.and(f.getDocumentProperty().in(documents))
				).collect(Collectors.toList());
				 
				 for(Customer customer : customers) {
					 JSONObject json = new JSONObject();
					 json.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(customer));
					 json.put("success", false);
						 
						List<Company> companies = companyAll.stream().filter(c-> 
							c.getDocument().equals(customer.getDocument()) && 
							c.getDomain().getParentId().equals(customer.getDomain().getParentId())
						).collect(Collectors.toList());
						
						if(companies.isEmpty()) {
							 json.put(IJsonNames.MESSAGE, "No existe empresa");
						} else if(companies.size()>1) {
							 json.put(IJsonNames.MESSAGE, "Existe mas de una empresa");
							 json.put("companies", CompanyJSON.toJSON(companies));
						} else if(companies.size()==1) {
							
							RegistryRelationship rrelationship = new RegistryRelationship()
							.setDomain(api.getDomain())
							.setRegistry(customer.getId())
							.setRelatedRegistry(companies.get(0).getId())
							.setComments(companies.get(0).getDomain().getName());
							
							 saveRelationship(api, rrelationship);
							 
							 json.put(IJsonNames.MESSAGE, "Empresa vinculada");
							 json.put("success", true);
						}
				
					 arr.put(json); 
				 } 
			 } else {
				AON_SOLUTIONS.deleteRegistryRelationship(api.getDomain(), api.getUser(), f-> 
					f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getRegistryProperty().in(registryId))
					.and(f.getRelationshipProperty().eq(-1))
				);
				customers.forEach(customer->{
					 JSONObject json = new JSONObject();
					 json.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(customer));
					 json.put("success", true);
					 arr.put(json); 
				});
			 }
		 }

		return arr;
	}

	private static RegistryRelationship saveRelationship(AonApiData api, RegistryRelationship rrelationship) {
		return AON_SOLUTIONS.saveRegistryRelationship( api.getDomain(), api.getUser(), rrelationship);
	}
	
	private static JSONObject deleteRelationship(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		AON_SOLUTIONS.deleteRegistryRelationship(api.getDomain(), api.getUser(), f-> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getIdProperty().eq(id))
		);
		return new JSONObject();
	}
}
