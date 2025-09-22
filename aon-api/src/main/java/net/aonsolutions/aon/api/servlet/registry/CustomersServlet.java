package net.aonsolutions.aon.api.servlet.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TargetJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.TargetProperties;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomersServlet", urlPatterns = { "/ms/api/customers/*" })
public class CustomersServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(CustomersServlet.class.getName());

	public static final String CUSTOMERS = "/";
	public static final String CUSTOMER = "/:id";
	public static final String CUSTOMER_EMAILS = "/:id/emails";
	public static final String CUSTOMER_NOTE = "/note";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
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

			Object object = new AonRouting(api).addRoute(CUSTOMERS, CustomersServlet::getCustomers)
					.addRoute(CUSTOMER, CustomersServlet::getCustomer)
					.addRoute(CUSTOMER_EMAILS, CustomersServlet::getCustomerEmails).apply();

			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);

			Object object = new AonRouting(api).addRoute(CUSTOMER_NOTE, CustomersServlet::saveCustomerNote)
					.addRoute(CUSTOMERS, CustomersServlet::saveCustomer)
					.addRoute(CUSTOMER, CustomersServlet::saveCustomer).apply();

			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private static JSONObject getCustomer(AonApiData api) {
		Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> customerFilter(api, f));
		JSONObject object = CustomerJSON.toJSON(customer);

		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), customer.getId(), null);
	}

	private static JSONArray getCustomerEmails(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer customerId = JsonUtils.getInteger(vars, IJsonNames.ID);
		ArrayList<String> list = AON.getRegistryMediaStream(api.getDomain(), api.getUser(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getRegistryProperty().eq(customerId))
						.and(f.getMediaProperty().eq(MediaType.EMAIL.value())))
				.map(r -> r.getValue()).collect(Collectors.toCollection(ArrayList::new));
		return new JSONArray(list);
	}

	private static JSONArray getCustomers(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null ? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null ? api.getData().optInt(IJsonNames.PER_PAGE)
				: 50;

		if (isTarget(api)) {
			return TargetJSON.toJSON(AON.getTargetStream(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> targetFilter(api, f), perPage * (page - 1), perPage));
		} else {
			
			boolean isSig = api.getData().optBoolean("isSig");
			
			if(isSig && api.getData().opt(IJsonNames.RRELATIONSHIP) != null) {
				
				boolean rrelationship = api.getData().optBoolean(IJsonNames.RRELATIONSHIP);
				
				return rrelationship 
						? CustomerJSON.toJSON(AON.getSigCustomerStream(api.getDomain().getName(), api.getDomain().getId(),
								api.getUser().getLogin(), f -> customerFilter(api, f), perPage * (page - 1), perPage))
						: CustomerJSON.toJSON(AON.getSigCustomerNotLinkedStream(api.getDomain().getName(), api.getDomain().getId(),
									api.getUser().getLogin(), f -> customerFilter(api, f), perPage * (page - 1), perPage));
			} else
				return CustomerJSON.toJSON(AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(),
						api.getUser().getLogin(), f -> customerFilter(api, f), perPage * (page - 1), perPage));
		}
			
	}

	private static boolean isTarget(AonApiData api) {
		return api.getData().opt("type") != null && !api.getData().optBoolean("type");
	}

	private static Filter customerFilter(AonApiData api, CustomerProperties f) {
		Filter filter;
		if (api.getData().opt(IJsonNames.REGISTRY) != null) {
			filter = f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY));
		} else if (api.getData().opt(IJsonNames.ID) != null) {
			filter = f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.ID));
		} else if (api.getData().optBoolean(IJsonNames.RELATED_REGISTRY, false)) {
			Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin());
			filter = f.getRelatedRegistryProperty().eq(company.getId());
		} else {
			filter = f.getDomainProperty().eq(api.getDomain().getId());
			// .and(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value()));
		}

		if (api.getData().opt(IJsonNames.DOCUMENT) != null) {
			filter = filter.and(f.getDocumentProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.DOCUMENT)));
		}

		if (api.getData().opt(IJsonNames.SCOPE) != null) {
			filter = filter.and(f.getScopeProperty().eq(JsonUtils.getInt(api.getData(), IJsonNames.SCOPE)));
		}

		int projectType = api.getData().optInt("projectType");
		if (projectType != 0) {
			filter = filter.and(f.getProjectTypeProperty().eq(projectType));
		}

		if (api.getData().opt(IJsonNames.STATUS) != null) {
			ArrayList<String> list = new ArrayList<>();

			api.getData().optJSONArray(IJsonNames.STATUS).forEach(str -> list.add(str.toString()));

			Byte[] status = RegistryStatus.safeValueOf(list).stream().map(RegistryStatus::value).toArray(Byte[]::new);

			filter = filter.and(f.getStatusProperty().in(status));
		}

		if (api.getData().opt(IJsonNames.RRELATIONSHIP) != null) {
			boolean rrelationship = api.getData().optBoolean(IJsonNames.RRELATIONSHIP);
			boolean isSig = api.getData().optBoolean("isSig");
			
			if(!isSig)
				filter = filter.and(rrelationship ? f.getRegistryRelationProperty().isNotNull()
						: f.getRegistryRelationProperty().isNull());
		}

		if (api.getData().opt(IJsonNames.VALUE) != null) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"))
					.or(f.getAliasProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}

		if (api.getData().opt(IJsonNames.EMAIL) != null) {
			filter = filter.and(f.getEmailProperty().like(JsonUtils.getString(api.getData(), IJsonNames.EMAIL)));
		}
		return filter;
	}

	private static Filter targetFilter(AonApiData api, TargetProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		// .and(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value()));

		if (api.getData().opt(IJsonNames.REGISTRY) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY)));
		} else if (api.getData().opt(IJsonNames.ID) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.ID)));
		}

		if (api.getData().opt(IJsonNames.DOCUMENT) != null) {
			filter = filter.and(f.getDocumentProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.DOCUMENT)));
		}

		if (api.getData().opt(IJsonNames.SCOPE) != null) {
			filter = filter.and(f.getScopeProperty().eq(JsonUtils.getInt(api.getData(), IJsonNames.SCOPE)));
		}

		if (api.getData().opt(IJsonNames.STATUS) != null) {
			ArrayList<String> list = new ArrayList<>();

			api.getData().optJSONArray(IJsonNames.STATUS).forEach(str -> list.add(str.toString()));

			Byte[] status = RegistryStatus.safeValueOf(list).stream().map(RegistryStatus::value).toArray(Byte[]::new);

			filter = filter.and(f.getStatusProperty().in(status));
		}

		if (api.getData().opt(IJsonNames.VALUE) != null) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"))
					.or(f.getAliasProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}

	public static JSONObject saveCustomer(AonApiData api) {
		Customer customer = CustomerJSON.fromJSON(api.getData());
		customer = AON.saveCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				customer);
		RegistryServlet.saveRegistryAdditionalInfo(api, customer.getId(), customer.getDomain().getId());
		return CustomerJSON.toJSON(customer);
	}

	public static JSONObject saveCustomerNote(AonApiData api) {

		System.out.println(api.getData());

		boolean isSig = JsonUtils.getboolean(api.getData(), "isSig");

		Integer customerId = JsonUtils.getInteger(api.getData(), "customerId");

		String newStatusStr = JsonUtils.getString(api.getData(), "status");
		RegistryStatus newStatus = RegistryStatus.valueOf(newStatusStr);

		String tagName = JsonUtils.getString(api.getData(), "tagName");

		Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getIdProperty().eq(customerId));

		if (!newStatus.equals(customer.getStatus())) {

			String comments = "Cambio estado de " + customer.getStatus().getDescription() + " a "
					+ newStatus.getDescription() + ". ";
			comments += "\nMotivo: " + tagName;

			String dateStr = JsonUtils.getString(api.getData(), "date");
			if (AonStringUtils.isNotBlank(dateStr))
				comments += "\nF. Expiracion: " + dateStr;

			RegistryNote note = new RegistryNote().setDomain(api.getDomain().getId()).setRegistry(customerId)
					.setNoteDate(new Date()).setNoteType(NoteType.CUSTOMER_STATUS).setConfidential(true)
					.setDescription(newStatus.getDescription()).setComments(comments);

			AON.saveRegistryNote(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), note);

			// Update domain (work for all except SIG)
			Optional<RegistryRelationship> rrelationship = AON_SOLUTIONS.getRegistryRelationship(api.getDomain(),
					api.getUser(), f -> f.getRegistryProperty().eq(customerId));
			if (rrelationship.isPresent()) {
				Enterprise enterprise = AON.getEnterprise(api.getDomain().getName(), api.getDomain().getId(),
						api.getUser().getLogin(), rrelationship.get().getRelatedRegistry());
				if (null != enterprise) {
					Domain domainCustomer = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
							api.getUser().getLogin(), f -> f.getIdProperty().eq(enterprise.getDomain()));

					Date expirationDate = AonDateUtils.simpleParse(dateStr);

					domainCustomer.setExpirationDate(newStatus.equals(RegistryStatus.ACTIVE) ? null : expirationDate);

					AON.updateDomainStatus(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
							domainCustomer);

				}
			} else if (isSig) {
				Stream<RegistryAddInfo> registryAddInfoStream = AON.getRegistryAddInfoStream(api.getDomain().getName(),
						api.getDomain().getId(), api.getUser().getLogin(),
						f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_ID")
								.or(f.getAttributeProperty().like("AON_DOMAIN%_SCHEMA"))));

				if(registryAddInfoStream.count() > 0) {
					Map<Integer, String> domainMap = new HashMap<>();

					Map<String, Map<String, String>> grouped = registryAddInfoStream.collect(
							Collectors.groupingBy(rec -> rec.getAttribute().replaceAll("AON_DOMAIN(\\d+)_.*", "$1"),
									Collectors.toMap(rec -> rec.getAttribute().endsWith("_ID") ? "ID" : "SCHEMA",
											RegistryAddInfo::getValue)));

					grouped.values().forEach(entry -> {
						Integer id = Integer.parseInt( entry.get("ID") );
						String schema = entry.get("SCHEMA");
						if (id != null && schema != null) {
							domainMap.put(id, schema);
						}
					});
					
					domainMap.entrySet().forEach(entry -> {
						Integer domainId = entry.getKey();
						String schema = entry.getValue();
						
						Domain domainCustomer = AON_SOLUTIONS.getDomainBySchema(schema, domainId);
						
						Date expirationDate = AonDateUtils.simpleParse(dateStr);

						domainCustomer.setExpirationDate(newStatus.equals(RegistryStatus.ACTIVE) ? null : expirationDate);

						AON.updateDomainStatus(domainCustomer.getName(), domainCustomer.getId(), api.getUser().getLogin(),
								domainCustomer);
					});
				} else {
					Domain domainCustomer = AON_SOLUTIONS.getDomainByAonCustomer(customerId);
					
					Date expirationDate = AonDateUtils.simpleParse(dateStr);

					domainCustomer.setExpirationDate(newStatus.equals(RegistryStatus.ACTIVE) ? null : expirationDate);

					AON.updateDomainStatus(domainCustomer.getName(), domainCustomer.getId(), api.getUser().getLogin(),
							domainCustomer);
				}

			}

		}

		return new JSONObject();
	}

}
