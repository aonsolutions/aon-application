package net.aonsolutions.aon.api.servlet.registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.RegistryRelationshipJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import net.aonsolutions.aon.api.servlet.Utils;

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
		
		Stream<DomainCompany> domainSigLinks = CONSOLE.getCustomerDomains(registry);
		
		JSONArray arr = new JSONArray();
		
		domainSigLinks.forEach(domainCompany -> {
			JSONObject json = new JSONObject();
			json.put("registry", registry);
			json.put("domainGroup", "");
			json.put("domainId", domainCompany.getDomain().getId());
			json.put("domainName", domainCompany.getDomain().getName());
			json.put("schema", domainCompany.getSchema());
			json.put("domainType", domainCompany.getDomain().getDomainType().getName());
			
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
			
			// Check Comments
			if(AonStringUtils.isNotBlank(rrelationship.getComments()) && AonStringUtils.length(rrelationship.getComments()) >  64)
				throw new IllegalArgumentException("La URL de este dominio es demasiado larga. Por favor p\u00f3ngase en contacto con soporte.");
			
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
			
			// Check if user exists
			try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
				
				ctx.transaction(t -> {
					List<User> domainUsers = UserDAO.getStream(ctx, f -> f.getDomainProperty().eq(company.getDomain().getId()), new Options().setFull(true)).collect(Collectors.toList());
					
					if(domainUsers.size() == 0) {
						RegistryMedia emailRegistryMedia = AON.getRegistryMedia(new Domain().setName(api.getDomain().getName()).setId(api.getDomain().getId()), 
								new User().setLogin(api.getUser().getLogin()), 
								f -> f.getDomainProperty().eq(company.getDomain().getId()).and(f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))));
						
						if(null != emailRegistryMedia && null != emailRegistryMedia.getId()) {
							
							String email = emailRegistryMedia.getValue();
							
							if (Utils.isEmail(email)) {
								
								String login = ramdonLogin();
								String pass = null;
								
								Auth auth = AON_SOLUTIONS.getAuth(email);
								
								if (pass == null) pass = Utils.createPasswordHash(email, login);
								
								if (auth.getUuid() == null) {
									auth = new Auth()
											.setEmail(email)
											.setPassword(pass)
											.setName(company.get().getName())
											.setSurname(null)
											.setDocument(company.get().getDocument())
											;

									auth = AuthDAO.insertAuth(ctx, auth);
								} else {
									auth.setPassword(pass);
									auth = AuthDAO.updateAuth(ctx, auth);
								}

								User newUser = null;
								if (auth.getAuth() != null) {
									newUser = createUser(ctx, company.getDomain(), auth, login, company.get().getName());
									setUserAppRole(ctx, company.getDomain(), newUser);

									if (company.getDomain().isChild() || company.getDomain().isStandalone()) {
										createTaskHolder(ctx, company.getDomain(), auth, newUser);
									}
								}
								
							} else {
								System.err.println("El email (" + email + ") no tiene un formato correcto.");
							}
						}
					}
				});
				
			}
		}
		
		return AON_SOLUTIONS.saveRegistryRelationship( api.getDomain(), api.getUser(), rrelationship);
	}
	
	private static String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000 - 10000000 + 1) + 10000000;
		return i.toString();
	}

	private static User createUser(CloseableAONContext ctx, Domain newDomain, Auth auth, String login, String name) {
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId())).findFirst().get();

		User newUser = new User()
				.setAuth(auth)
				.setActive(true)
				.setDomain(newDomain.getId())
				.setLogin(newCompany.getDocument())
				.setName(AonStringUtils.isNotBlank(name) ? name : newCompany.getDocument())
				.setShared(false)
				.setEnterprise(newCompany.getId())
				.setToolbar(UserToolbar.GOOGLE);

		
		newUser = SecurityDAO.save(ctx, newUser);

		SecurityDAO.updateUserPassword(ctx, newUser.getId(), auth.getPassword());

		Scope scope = getScope(ctx, newDomain, newUser);

		if (scope != null) {
			SecurityDAO.insertUserScope(
					ctx, 
					new UserScope()
					.setDomain(newDomain.getId())
					.setScope(scope.getId())
					.setUserId(newUser.getId())
			);
		}

		ApplicationParameter appParam = AppParamDAO.fetchOne(ctx, AppParam.AON_PORTAL);
		
		if (appParam == null || appParam.getId() == null) {
			
			appParam = new ApplicationParameter()
					.setDomain(newDomain.getId())
					.setValue("288")
					.setName(AppParam.AON_PORTAL.getValue());
			
			AppParamDAO.insertApplicationParameter(ctx, appParam);
		}

		return newUser;
	}

	private static Scope getScope(CloseableAONContext ctx, Domain newDomain, User newUser) {

		Scope s = SecurityDAO.getScopeStream(
				ctx, 
				f -> f.getDomainProperty().eq(newDomain.getId())
					.and(f.getDescriptionProperty().eq("GENERAL"))
				)
				.findFirst().orElse(null);

		if (s == null && newDomain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(
					ctx, 
					f -> f.getDomainProperty().eq(newDomain.getParentId())
						.and(f.getDescriptionProperty().eq("GENERAL"))
					)
					.findFirst().orElse(null);
		}

		if (s == null) {
			s = SecurityDAO.getScopeStream(
					ctx, 
					f -> f.getDomainProperty().eq(newDomain.getId())
					)
					.findFirst().orElse(null);
		}

		if (s == null && newDomain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(
					ctx, 
					f -> f.getDomainProperty().eq(newDomain.getParentId())
					)
					.findFirst().orElse(null);
		}

		return s;
	}
	

	private static void setUserAppRole(CloseableAONContext ctx, Domain newDomain, User user) {
		Integer userId = user.getId();

		LinkedList<AonRole> aRoles = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole())
				.collect(Collectors.toCollection(LinkedList::new));

		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		tRoles.add(AonRole.ENTERPRISE);
		tRoles.add(AonRole.INVOICE_PORTAL);
		tRoles.add(AonRole.INVOICE);

		AonRole.stream().forEach(role -> {
			if (aRoles.contains(role) && !tRoles.contains(role)) {
				SecurityDAO.deleteUserAppRole(ctx, 
						f -> f.getDomainProperty().eq(newDomain.getId())
							.and(f.getUserIdProperty().eq(userId))
							.and(f.getRoleProperty().eq(role.value()))
				);
			}
			
			if (!aRoles.contains(role) && tRoles.contains(role)) {
				SecurityDAO.insertUserAppRole(ctx, 
						new UserAppRole()
						.setApp(null)
						.setDomain(newDomain.getId())
						.setRole(role)
						.setUser(userId)
				);
			}
		});
		
		SecurityDAO.insertUserApplicationAio(ctx, newDomain.getId(), userId);
	}
	
	private static void createTaskHolder(CloseableAONContext ctx, Domain newDomain, Auth auth, User newUser) {

		String alias = AonStringUtils.isBlank(auth.getName())
				? newUser.getName()
				: auth.getName().length() > 32 ? auth.getName().substring(0, 31) : auth.getName();
		
		Registry newRegistry = RegistryDAO.save(ctx, 
				new Registry()
				.setDocument(auth.getDocument())
				.setDocumentType(DocumentType.identify(auth.getDocument()))
				.setName(
						AonStringUtils.isBlank(auth.getName())
						? newUser.getName()
						: auth.getName() + (AonStringUtils.isBlank(auth.getSurname()) ? "" : (" " + auth.getSurname())) )
				.setAlias(alias)
				.setDomain(newDomain)
		);
		
		newUser.setRegistry(newRegistry);
		newUser = UserDAO.save(ctx, newUser);
		
		TaskHolder taskHolder = new TaskHolder()
				.copy(newRegistry)
				.setActive(true)
				.setUserId(newUser.getId());
		
		taskHolder = TaskHolderDAO.save(ctx, taskHolder);

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
		
		Optional<RegistryAddInfo> registryAddInfo = AON.getRegistryAddInfo(api.getDomain().getName(), 
				api.getDomain().getId(), 
				api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getRegistryProperty().eq(registry))
				.and(f.getAttributeProperty().like("AON_DOMAIN%_NAME"))
			);
		
		String attributeNum = getDomainGroup(registryAddInfo);
		
		AON.deleteRegistryAddInfo(
				api.getDomain().getName(), 
				api.getDomain().getId(), 
				api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getRegistryProperty().eq(registry))
					.and(
							f.getAttributeProperty().eq("AON_DOMAIN" + (AonStringUtils.isBlank(attributeNum) ? domainGroup : attributeNum) + "_SCHEMA").and(f.getValueProperty().eq(schema))
						.or(f.getAttributeProperty().eq("AON_DOMAIN" + (AonStringUtils.isBlank(attributeNum) ? domainGroup : attributeNum) + "_NAME").and(f.getValueProperty().eq(domainName)))
						.or(f.getAttributeProperty().eq("AON_DOMAIN" + (AonStringUtils.isBlank(attributeNum) ? domainGroup : attributeNum) + "_ID").and(f.getValueProperty().eq(domainId)))
						.or(f.getAttributeProperty().eq("AON_DOMAIN" + (AonStringUtils.isBlank(attributeNum) ? domainGroup : attributeNum) + "_TYPE").and(f.getValueProperty().eq(domainType)))
					)
				);
		
		return new JSONObject();
	}
	
	private static String getDomainGroup(Optional<RegistryAddInfo> registryAddInfo) {
		if(registryAddInfo.isPresent()) {
			try {
				String attribute = registryAddInfo.get().getAttribute();
				return attribute.split("AON_DOMAIN")[1].split("_NAME")[0];	
			} catch (Exception e) { }
		}
		
		return null;
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
