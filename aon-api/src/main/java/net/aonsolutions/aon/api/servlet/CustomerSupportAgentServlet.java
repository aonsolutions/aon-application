package net.aonsolutions.aon.api.servlet;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.type.MediaType;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "CustomerSupportAgentServlet", urlPatterns = {"/ms/api/customers-support-agent/*"})
public class CustomerSupportAgentServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomerSupportAgentServlet.class.getName());
	
	public static final String SYNC_CUSTOMERS_SUPPORT_AGENTS = "/";
	public static final String SYNC_CUSTOMER_SUPPORT_AGENTS = "/:customer";
	public static final String CUSTOMER_SYNC_DOMAINS = "/check-customer-sync-domains/";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(CUSTOMER_SYNC_DOMAINS, CustomerSupportAgentServlet::checkCustomerSyncDomains)
				.addRoute(SYNC_CUSTOMER_SUPPORT_AGENTS, CustomerSupportAgentServlet::checkCustomerSyncDomain)
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
				.addRoute(SYNC_CUSTOMERS_SUPPORT_AGENTS, CustomerSupportAgentServlet::syncCustomersSupportAgent)
				.addRoute(SYNC_CUSTOMER_SUPPORT_AGENTS, CustomerSupportAgentServlet::syncCustomerSupportAgent)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject checkCustomerSyncDomain(AonApiData api) {
		JSONObject logJson = new JSONObject();
		
		try {
			
			JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
	  		Integer customerId = vars.getInt(IJsonNames.CUSTOMER);
			
			// Si tiene dominio asociado se obvia
			Stream<DomainCompany> domainCustomer = CONSOLE.getDomains(f -> f.getAonCustomerProperty().eq(customerId));
			Optional<DomainCompany> domain = domainCustomer.findFirst();
			
			if(domain.isPresent()) {
				logJson.put("customer", customerId.toString());
				logJson.put("schema", domain.get().getSchema());
				logJson.put("domainName", domain.get().getDomain().getName());
				logJson.put("domainId", domain.get().getDomain().getId().toString());
			}
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new  AonApiException(e.getMessage());
		}
		
		return logJson;
	}
	
	private static JSONObject checkCustomerSyncDomains(AonApiData api) {
		JSONObject logJson = new JSONObject();
		
		JSONArray customersLog = new JSONArray();
		logJson.put("customers", customersLog);	
		
		JSONArray customersDomainLog = new JSONArray();
		logJson.put("customersDomain", customersDomainLog);	
		
		TreeMap<String, String> customerNames = new TreeMap<String, String>();
		
		try {
			
			Stream<Customer> customerStream = AON.getCustomerStream(
					api.getDomain().getName(), 
					api.getDomain().getId(), 
					api.getUser().getLogin(), 
					f -> f.getStatusProperty().eq((byte) 0)
						.and(f.getDomainProperty().eq(api.getDomain().getId()))
			);
			
			for(Customer customer : customerStream.toList()) {
				
				// Si no tiene cuota se obvia
				Stream<Fee> feeStream = AON.getFeeStream(api.getDomain().getName(), 
						api.getDomain().getId(), 
						api.getUser().getLogin(), 
						f -> f.getCustomerProperty().eq(customer.getId())
							.and(f.getDomainProperty().eq(api.getDomain().getId()))
				);
				
				if(feeStream.toList().isEmpty()) continue;
				
				// Si tiene dominio asociado se obvia
				Stream<DomainCompany> domainCustomer = CONSOLE.getDomains(f -> f.getAonCustomerProperty().eq(customer.getId()));
				Optional<DomainCompany> domain = domainCustomer.findFirst();
				
				if(!domain.isPresent()) customerNames.put(customer.getName(), customer.getId().toString());
				else {
					JSONObject customerJson = new JSONObject();
					customerJson.put("name", customer.getName());
					customerJson.put("id", customer.getId().toString());
					customerJson.put("domain", domain.get().getDomain());
					customersDomainLog.put(customerJson);
				}
				
			}
					
		} catch (Exception e) {
			e.printStackTrace();
			throw new  AonApiException(e.getMessage());
		}
		
		customerNames.entrySet().forEach(entry -> {
			String customerName = entry.getKey();
			String customerId = entry.getValue();
			
			JSONObject customerJson = new JSONObject();
			customerJson.put("name", customerName);
			customerJson.put("id", customerId);
			customersLog.put(customerJson);
		});
		
		return logJson;
	}
	
	private static JSONObject syncCustomerSupportAgent(AonApiData api) {
		JSONObject respJson = new JSONObject();
		respJson.put("message", "Sincronizacion finalizada");	
		
		try {			
			
			String schema = api.getData().get("schema").toString();
			String domainName = api.getData().get("domainName").toString();
			Integer domainId = Integer.parseInt(api.getData().get("domainId").toString());
			String owner =  api.getData().get("owner").toString();
	
			AON.updateDomainOwner(schema, domainName, domainId, owner);
	
		} catch (Exception e) {
			e.printStackTrace();
			throw new  AonApiException(e.getMessage());
		}
		
		return respJson;

	}
	
	private static JSONObject syncCustomersSupportAgent(AonApiData api) {
		JSONObject logJson = new JSONObject();
		
		JSONArray successLog = new JSONArray();
		logJson.put("success", successLog);	
		
		JSONArray errorLog = new JSONArray();
		logJson.put("error", errorLog);		
		
		try {
			
			List<RegistrySeller> rsellerList = AON.getRegistrySellerStream(
					api.getDomain(), 
					api.getUser().getLogin(), 
					f -> f.getTypeProperty().eq((byte)1)
						.and(f.getStartDateProperty().le(new Date(new java.util.Date().getTime())))
						.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new Date(new java.util.Date().getTime()))))
						.and(f.getStatusProperty().eq((byte)0))
						.and(f.getDomainProperty().eq(api.getDomain().getId()))
			).toList();
			
			for(RegistrySeller rseller : rsellerList) {
				
				// Domain  Customer
				Stream<DomainCompany> domainCustomer = CONSOLE.getDomains(f -> f.getAonCustomerProperty().eq(rseller.getRegistry()));
				Optional<DomainCompany> domain = domainCustomer.findFirst();
				
				Registry customerRegistry = AON.getRegistry( api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(rseller.getRegistry()).and(f.getDomainProperty().eq(api.getDomain().getId())));
				
				// Customer - Domain not linked
				if(null == domainCustomer || domain.isEmpty()) {
					JSONObject errorJson = new JSONObject();
					errorJson.put("error", "El cliente <b>" + customerRegistry.getName() + "</b> (" + customerRegistry.getAlias() + ") no tiene dominio asociado. (Agente de soporte: <b>" + rseller.getSeller().getName() + "</b>)");
					errorLog.put(errorJson);
					
					continue;
				}
				
				// Seller email
				Stream<RegistryMedia> rmediaStream = AON.getRegistryMediaStream(api.getDomain(), api.getUser(), f -> f.getRegistryProperty().eq(rseller.getSeller().getId()).and(f.getDomainProperty().eq(api.getDomain().getId())));
				Optional<RegistryMedia> emailMedia = rmediaStream.filter(rmedia -> rmedia.getMedia().equals(MediaType.EMAIL)).findFirst();
				
				if(emailMedia.isEmpty()) {
					JSONObject errorJson = new JSONObject();
					errorJson.put("error", "El agente de soporte <b>" + rseller.getSeller().getName() + "</b> no tiene email registrado. (Cliente: <b>" + customerRegistry.getName() + "</b>)");
					errorLog.put(errorJson);
					
					continue;
				}
				
				// Add seller email to domain owner column
				
				AON.updateDomainOwner(domain.get().getSchema(), domain.get().getDomain().getName(), domain.get().getDomain().getId(), emailMedia.get().getValue());
				
				JSONObject successJson = new JSONObject();
				successJson.put("success", "Se ha asignado el agente de soporte <b>" + rseller.getSeller().getName() + "</b> al cliente <b>" + domain.get().getDomain().getDescription() + "</b> (" + domain.get().getDomain().getName() + ")");
				successLog.put(successJson);
					
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new  AonApiException(e.getMessage());
		}
		
		return logJson;

	}
	
}
