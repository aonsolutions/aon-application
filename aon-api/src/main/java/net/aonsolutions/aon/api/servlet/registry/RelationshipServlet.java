package net.aonsolutions.aon.api.servlet.registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.RegistryRelationshipJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.DomainSigAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiRelationshipServlet", urlPatterns = {"/ms/api/relationship/*"})
public class RelationshipServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RelationshipServlet.class.getName());
	
	private static final String RELATIONS = "/";
	private static final String RELATION = "/:id";
	private static final String RELATION_RADDINFO = "/raddinfo/:id";
	private static final String RELATION_COMPANY = "/company";
	private static final String SIBLINGS_OFFICE = "/siblingsOffice";
	private static final String AON_CUSTOMER = "/aonCustomer";
	
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
				.addRoute(RELATION_COMPANY, RelationshipServlet::getRelationshipByCompany)
				.addRoute(SIBLINGS_OFFICE, RelationshipServlet::getSiblingsOffice)
				.addRoute(RELATION_RADDINFO, RelationshipServlet::getRelationshipRAddInfo)
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
				.addRoute(AON_CUSTOMER, RelationshipServlet::deleteAonCustomer)
				.addRoute(RELATION_RADDINFO, RelationshipServlet::deleteSigRelationship)
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
	
	private static JSONArray getRelationshipRAddInfo(AonApiData api) {
		JSONObject params = api.getData();
		
		Integer registry = params.optInt(IJsonNames.REGISTRY);
		
		List<DomainSigAddInfo> domainSigAddInfoList = AON.getDomainSigAddInfo(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), registry);
		
		JSONArray arr = new JSONArray();
		
		domainSigAddInfoList.forEach(domain -> {
			JSONObject json = new JSONObject();
			json.put("registry", domain.getRegistry());
			json.put("domainGroup", domain.getDomainGroup());
			json.put("domainId", domain.getDomainId());
			json.put("domainName", domain.getDomainName());
			json.put("schema", domain.getDomainSchema());
			json.put("domainType", domain.getDomainType());
			
			arr.put(json);
		});
		
		return arr;
	}
	
	private static JSONObject getRelationshipByCompany(AonApiData api) {
		JSONObject params = api.getData();
		JSONObject json = new JSONObject();
		
		String url = params.optString(IJsonNames.URL);
		Integer relatedRegistry = params.optInt(IJsonNames.RELATED_REGISTRY);
		
		AON_SOLUTIONS.getRegistryRelationship(api.getDomain(), api.getUser(), 
			f-> f.getRelatedRegistryProperty().eq(relatedRegistry)
			.and(f.getRelationshipProperty().eq(-1))
			.and(f.getCommentsProperty().eq(url))
		)
		.ifPresent(relation->
			json.put("rrelationship", RegistryRelationshipJSON.toJSON(relation))
		);
		
		return json;
	}
	
	private static JSONObject getSiblingsOffice(AonApiData api) {
		JSONObject params = api.getData();
		JSONObject json = new JSONObject();
		
		Integer domainId = params.optInt(IJsonNames.DOMAIN);
		
		Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(domainId));
		if(null != domain.getParentId()) {
			LinkedList<Domain> siblingsOffice = AON.getDomainList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getParentProperty().eq(domain.getParentId()).and(f.getTypeProperty().eq(DomainType.OFFICE.value())));
			if(!siblingsOffice.isEmpty()) {
				json.put("siblingsOffice", DomainJSON.toJSON(siblingsOffice));
			}
		}
		
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
					 String message = "";
					 boolean success = false;

					List<Company> companies = companyAll.stream().filter(c-> 
						c.getDocument().equals(customer.getDocument()) && 
						c.getDomain().getParentId().equals(customer.getDomain().getParentId())
					).collect(Collectors.toList());
					
					if(companies.isEmpty()) {
						 message = "No existe empresa.";
					} else if(companies.size()>1) {
						 message = "Existe mas de una empresa.";
						 json.put("companies", CompanyJSON.toJSON(companies));
					} else if(companies.size()==1) {
					
						 saveRelationship(api, 
						 	new RegistryRelationship()
							.setDomain(api.getDomain())
							.setRegistry(customer.getId())
							.setRelatedRegistry(companies.get(0).getId())
							.setComments(companies.get(0).getDomain().getName())
						 );
						 
						 success = true;
						 message = "Empresa vinculada.";
					 }
					
					 json.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(customer));
					 json.put(IJsonNames.MESSAGE, message);
					 json.put("success", success);
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
					 json.put(IJsonNames.MESSAGE, "Empresa desvinculada");
					 json.put("success", true);
					 arr.put(json); 
				});
			 }
		 }

		return arr;
	}

	private static RegistryRelationship saveRelationship(AonApiData api, RegistryRelationship rrelationship) {
		
		Company company = AON.getCompany(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(rrelationship.getRelatedRegistry()));
		Scope scope;
		
		if(null != company.getDomain()) {
			
			// Check Scope
			Integer domainScope = company.getDomain().getScope();
			
			if(null == domainScope) {
				Scope newScope = new Scope()
						.setDomain(null == company.getDomain().getParentId() ? company.getDomain().getId() : company.getDomain().getParentId())
						.setDescription(company.getDocument());
				newScope = AON.insertScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), newScope);
				AON.updateDomainScopeValue(company.getDomain().getName(), company.getDomain().getId(), api.getUser().getLogin(), newScope.getId());
				scope = newScope;
			} else {
				scope = AON.getScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), domainScope);
				if(!AonStringUtils.equalsIgnoreCase(scope.getDescription(), company.getDocument())) {
					Scope newScope = new Scope()
							.setDomain(null == company.getDomain().getParentId() ? company.getDomain().getId() : company.getDomain().getParentId())
							.setDescription(company.getDocument());
					newScope = AON.insertScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), newScope);
					AON.updateDomainScopeValue(company.getDomain().getName(), company.getDomain().getId(), api.getUser().getLogin(), newScope.getId());
					scope = newScope;
				}
			}
			
			// Check userScope
			User user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
			Domain userDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(user.getDomain().getId()));
			Stream<Scope> userScopes = AON.getUserScopeStream(userDomain.getName(), userDomain.getId(), api.getUser().getLogin(), user.getId(), f -> f.getDomainProperty().eq(userDomain.getId()));
			Integer scopeId = scope.getId();
			boolean existUserScope = userScopes.filter(s -> s.getId().equals(scopeId)).findAny().isPresent();
			if(!existUserScope) {
				AON.insertUserScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
						new UserScope()
						.setDomain(user.getDomain().getId())
						.setScope(scope.getId())
						.setUserId(user.getId()));
				
			}
		}
		
		return AON_SOLUTIONS.saveRegistryRelationship( api.getDomain(), api.getUser(), rrelationship);
	}
	
	private static JSONObject deleteRelationship(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		
		Optional<RegistryRelationship> rrelationship = AON_SOLUTIONS.getRegistryRelationship(
			api.getDomain(), 
			api.getUser(), 
			f-> f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getIdProperty().eq(id))
		);
		
		// Delete user scope & set scope null
		if(rrelationship.isPresent()) {
			Company company = AON.getCompany(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(rrelationship.get().getRelatedRegistry()));
			
			if(null != company.getDomain()) {
				
				// Check Scope
				Integer domainScope = company.getDomain().getScope();
				
				Scope scope = new Scope().setId(company.getDomain().getScope());
				
				if(null != domainScope)
					scope = AON.getScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), domainScope);
				
				// Check userScope
				User user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
				Domain userDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(user.getDomain().getId()));
				Stream<Scope> userScopes = AON.getUserScopeStream(userDomain.getName(), userDomain.getId(), api.getUser().getLogin(), user.getId(), f -> f.getDomainProperty().eq(userDomain.getId()));
				Integer scopeId = scope.getId();
				Optional<Scope> existUserScope = userScopes.filter(s -> s.getId().equals(scopeId)).findFirst();
				
				if(existUserScope.isPresent())
					AON.deleteUserScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getScopeProperty().eq(existUserScope.get().getId()));
				
				if(AonStringUtils.equalsIgnoreCase(scope.getDescription(), company.getDocument())) {
					AON.updateDomainScopeValue(company.getDomain().getName(), company.getDomain().getId(), api.getUser().getLogin(), null);
					AON.deleteScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), scope.getId());
				}
				
			}
		}
		
		AON_SOLUTIONS.deleteRegistryRelationship(api.getDomain(), api.getUser(), f-> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getIdProperty().eq(id))
		);
		
		return new JSONObject();
	}


	private static JSONObject deleteSigRelationship(AonApiData api) {
		Integer registry = api.getData().optInt("registry");
		String schema = api.getData().optString("schema");
		String domainName = api.getData().optString("domainName");
		String domainId = api.getData().optString("domainId");
		String domainType = api.getData().optString("domainType");
		String domainGroup = api.getData().optString("domainGroup");
		
		AON.deleteRegistryAddInfo(
				api.getDomain().getName(), 
				api.getDomain().getId(), 
				api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getRegistryProperty().eq(registry))
					.and(
							f.getAttributeProperty().eq("AON_DOMAIN" + domainGroup + "_SCHEMA").and(f.getValueProperty().eq(schema))
						.or(f.getAttributeProperty().eq("AON_DOMAIN" + domainGroup + "_NAME").and(f.getValueProperty().eq(domainName)))
						.or(f.getAttributeProperty().eq("AON_DOMAIN" + domainGroup + "_ID").and(f.getValueProperty().eq(domainId)))
						.or(f.getAttributeProperty().eq("AON_DOMAIN" + domainGroup + "_TYPE").and(f.getValueProperty().eq(domainType)))
					)
				);
		
		return new JSONObject();
	}
	
	private static JSONObject deleteAonCustomer(AonApiData api) {
		Integer registry = api.getData().optInt("registry");
		String domainName = api.getData().optString("domainName");
		
		Domain domain = AON_SOLUTIONS.getDomain(domainName);
		if(null != domain && null != domain.getId() && null != domain.getAonCustomer() && domain.getAonCustomer().equals(registry)) {
			AON.updateDomainCustomer(domain.getName(), domain.getId(), api.getUser().getLogin(), null);
		}
		
		return new JSONObject();
	}
	
}
