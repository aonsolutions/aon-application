package net.aonsolutions.aon.api.servlet.registry;

import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
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
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryExpirationUtils;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonValidationUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomersServlet", urlPatterns = { "/ms/api/customers/*" })
public class CustomersServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(CustomersServlet.class.getName());

	public static final String CUSTOMERS = "/";
	public static final String CUSTOMER = "/:id";
	public static final String CUSTOMER_EMAILS = "/:id/emails";
	public static final String CUSTOMER_NOTE = "/note";
	public static final String CUSTOMER_DOMAIN_STATUS = "/domainStatus";

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

			Object object = new AonRouting(api)
					.addRoute(CUSTOMER_NOTE, CustomersServlet::saveCustomerNote)
					.addRoute(CUSTOMER_DOMAIN_STATUS, CustomersServlet::saveCustomerDomainStatus)
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
			} else {
			    JSONArray array = new JSONArray();

			    AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(),
			            api.getUser().getLogin(), f -> customerFilter(api, f), perPage * (page - 1), perPage)
			        .forEach(customer -> {
			            JSONObject object = CustomerJSON.toJSON(customer);
			            array.put(RegistryServlet.getRegistryAdditionalInfo(
			                    object, api, api.getData(), customer.getId(), null));
			        });

			    return array;
			}
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

		if (api.getData().opt(IJsonNames.SCOPE) != null && JsonUtils.getInt(api.getData(), IJsonNames.SCOPE) == -1) {
			filter = filter.and(f.getScopeProperty().isNull());
		} else if (api.getData().opt(IJsonNames.SCOPE) != null) {
			filter = filter.and(f.getScopeProperty().eq(JsonUtils.getInt(api.getData(), IJsonNames.SCOPE)));
		}

		int projectType = api.getData().optInt("projectType");
		if (projectType != 0) {
			filter = filter.and(f.getProjectTypeProperty().eq(projectType));
		}

		if (api.getData().opt(IJsonNames.STATUS) != null) {
			ArrayList<String> list = new ArrayList<>();
 
			api.getData().optJSONArray(IJsonNames.STATUS).forEach(str -> list.add(str.toString()));
 
			// Antes: f.getStatusProperty().in(status) sobre el estado almacenado
			Filter statusFilter = RegistryExpirationUtils.effectiveStatusFilter(f,
					RegistryStatus.safeValueOf(list));
 
			if (null != statusFilter)
				filter = filter.and(statusFilter);
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
					.or(f.getAliasProperty().like("%" + value + "%"))
					.or(f.getEmailProperty().like("%" + value + "%"))
					.or(f.getCelullarProperty().like("%" + value + "%"));
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

	    boolean isSig    = JsonUtils.getboolean(api.getData(), "isSig");
	    Integer customerId = JsonUtils.getInteger(api.getData(), "customerId");
	    String  tagName  = JsonUtils.getString(api.getData(), "tagName");
	    String  dateStr  = JsonUtils.getString(api.getData(), "date");

	    RegistryStatus newStatus = RegistryStatus.valueOf(JsonUtils.getString(api.getData(), "status"));

	    Date newExpirationDate = RegistryExpirationUtils.normalizeExpirationDate(
	            newStatus, AonDateUtils.simpleParse(dateStr));

	    Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(),
	            api.getUser().getLogin(), f -> f.getIdProperty().eq(customerId));

	    boolean statusChanged = !newStatus.equals(customer.getStatus());
	    boolean dateChanged   = !isSameDate(newExpirationDate, customer.getExpirationDate());

	    // Respuesta siempre con la misma forma: el front no tiene que distinguir casos
	    JSONObject result = new JSONObject()
	            .put("isSig", isSig)
	            .put("status", newStatus.name())
	            .put("expirationDate", null == newExpirationDate
	                    ? JSONObject.NULL : AonDateUtils.simpleFormat(newExpirationDate))
	            .put("domains", new JSONArray());

	    if (!statusChanged && !dateChanged)
	        return result;

	    saveStatusNote(api, customer, customerId, newStatus, newExpirationDate, tagName, statusChanged);

	    result.put("domains", syncLinkedDomains(api, customerId, isSig, newStatus, newExpirationDate));

	    if (statusChanged)
	        notifySellers(api, customer, newStatus, tagName, newExpirationDate);

	    return result;
	}
	
	/**
	 * Ejecuta en el dominio DESTINO. Actualiza estado/fecha del dominio vinculado.
	 * Sustituye a la propagacion cross-schema que hacia syncLinkedDomains para SIG.
	 */
	public static JSONObject saveCustomerDomainStatus(AonApiData api) {

	    Integer domainId   = JsonUtils.getInteger(api.getData(), "domainId");
	    String  domainName = JsonUtils.getString(api.getData(), "domainName");

	    RegistryStatus status = RegistryStatus.valueOf(JsonUtils.getString(api.getData(), "status"));

	    Date expirationDate = RegistryExpirationUtils.normalizeExpirationDate(
	            status, AonDateUtils.simpleParse(JsonUtils.getString(api.getData(), "expirationDate")));

	    if (null == domainId || AonStringUtils.isBlank(domainName))
	        throw new IllegalArgumentException("domainId y domainName son obligatorios");

	    Domain domain = AON.getDomain(domainName, domainId, api.getUser().getLogin(),
	            f -> f.getIdProperty().eq(domainId));

	    if (null == domain || null == domain.getId())
	        throw new IllegalStateException("Dominio no encontrado: " + domainName + " (" + domainId + ")");

	    domain.setExpirationDate(expirationDate);
	    domain.setActive(RegistryExpirationUtils.domainActive(status));

	    AON.updateDomainStatus(domainName, domainId, api.getUser().getLogin(), domain);

	    return new JSONObject().put("domainId", domainId).put("domainName", domainName);
	}

	// -----------------------------------------------------------------------
	// Nota de auditoria
	// -----------------------------------------------------------------------

	private static void saveStatusNote(AonApiData api, Customer customer, Integer customerId,
			RegistryStatus newStatus, Date newExpirationDate, String tagName, boolean statusChanged) {

		String comments = statusChanged
				? "Cambio estado de " + description(customer.getStatus()) + " a " + newStatus.getDescription() + ". "
				: "Cambio de fecha de expiracion en estado " + newStatus.getDescription() + ". ";

		comments += "\nMotivo: " + tagName;

		if (null != newExpirationDate)
			comments += "\nF. Expiracion: " + AonDateUtils.simpleFormat(newExpirationDate);

		RegistryNote note = new RegistryNote()
				.setDomain(api.getDomain().getId())
				.setRegistry(customerId)
				.setNoteDate(new Date())
				.setNoteType(NoteType.CUSTOMER_STATUS)
				.setConfidential(true)
				.setDescription(newStatus.getDescription())
				.setComments(comments);

		AON.saveRegistryNote(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), note);
	}

	// -----------------------------------------------------------------------
	// Sincronizacion con los dominios vinculados
	// -----------------------------------------------------------------------

	/**
	 * SIG: no propaga, devuelve los dominios vinculados para que el front haga
	 * el PUT contra cada domainName.
	 * Resto: comportamiento original (propaga via applyToDomain) y devuelve vacio.
	 */
	private static JSONArray syncLinkedDomains(AonApiData api, Integer customerId, boolean isSig,
	        RegistryStatus newStatus, Date newExpirationDate) {

	    if (isSig)
	        return getSigLinkedDomains(api, customerId);

	    Optional<RegistryRelationship> rrelationship = AON_SOLUTIONS.getRegistryRelationship(api.getDomain(),
	            api.getUser(), f -> f.getRegistryProperty().eq(customerId));

	    if (!rrelationship.isPresent())
	        return new JSONArray();

	    Enterprise enterprise = AON.getEnterprise(api.getDomain().getName(), api.getDomain().getId(),
	            api.getUser().getLogin(), rrelationship.get().getRelatedRegistry());

	    if (null == enterprise)
	        return new JSONArray();

	    Domain domainCustomer = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(),
	            api.getUser().getLogin(), f -> f.getIdProperty().eq(enterprise.getDomain()));

	    // OJO: contexto = dominio del llamante (comportamiento original)
	    applyToDomain(api, api.getDomain().getName(), api.getDomain().getId(),
	            domainCustomer, newStatus, newExpirationDate);

	    return new JSONArray();
	}
	
	/**
	 * Lee AON_DOMAINn_ID / AON_DOMAINn_NAME de raddinfo.
	 * Ya no se usa el SCHEMA: getDomainBySchema no sirve para el caso SIG.
	 * Fallback: si no hay raddinfo, el dominio unico de domain.aonCustomer.
	 */
	private static JSONArray getSigLinkedDomains(AonApiData api, Integer customerId) {

	    List<RegistryAddInfo> list = AON.getRegistryAddInfoStream(api.getDomain().getName(),
	            api.getDomain().getId(), api.getUser().getLogin(),
	            f -> f.getRegistryProperty().eq(customerId).and(f.getAttributeProperty().like("AON_DOMAIN%_ID")
	                    .or(f.getAttributeProperty().like("AON_DOMAIN%_NAME"))))
	            .collect(Collectors.toList());

	    JSONArray domains = new JSONArray();

	    if (list.isEmpty()) {
	        Domain domainCustomer = AON_SOLUTIONS.getDomainByAonCustomer(customerId);

	        if (null == domainCustomer || null == domainCustomer.getId()
	                || AonStringUtils.isBlank(domainCustomer.getName()))
	            throw new IllegalStateException(
	                    "SIG: el cliente " + customerId + " no tiene dominio vinculado resoluble");

	        return domains.put(new JSONObject()
	                .put("domainId", domainCustomer.getId())
	                .put("domainName", domainCustomer.getName()));
	    }

	    Map<String, Map<String, String>> grouped = list.stream().collect(
	            Collectors.groupingBy(rec -> rec.getAttribute().replaceAll("AON_DOMAIN(\\d+)_.*", "$1"),
	                    Collectors.toMap(rec -> rec.getAttribute().endsWith("_ID") ? "ID" : "NAME",
	                            RegistryAddInfo::getValue)));

	    grouped.forEach((index, entry) -> {
	        String id   = entry.get("ID");
	        String name = entry.get("NAME");

	        // NAME es obligatorio: sin el no se puede alcanzar el dominio destino
	        if (AonStringUtils.isBlank(id) || AonStringUtils.isBlank(name))
	            throw new IllegalStateException("SIG: raddinfo incompleto para AON_DOMAIN" + index
	                    + " del cliente " + customerId + " (ID=" + id + ", NAME=" + name + ")");

	        domains.put(new JSONObject()
	                .put("domainId", Integer.valueOf(id.trim()))
	                .put("domainName", name.trim()));
	    });

	    return domains;
	}

	/**
	 * Unico punto donde se traduce estado de cliente -> estado de dominio.
	 * Sustituye al antiguo setActive(!((INACTIVE||BLOCKED) && null==fecha)).
	 */
	private static void applyToDomain(AonApiData api, String ctxDomainName, Integer ctxDomainId,
			Domain domain, RegistryStatus newStatus, Date newExpirationDate) {

		if (null == domain || null == domain.getId()) {
			LOGGER.warning("Dominio vinculado no encontrado, no se sincroniza el estado");
			return;
		}

		domain.setExpirationDate(newExpirationDate);
		domain.setActive(RegistryExpirationUtils.domainActive(newStatus));

		AON.updateDomainStatus(ctxDomainName, ctxDomainId, api.getUser().getLogin(), domain);
	}

	// -----------------------------------------------------------------------
	// Aviso a los agentes
	// -----------------------------------------------------------------------

	private static void notifySellers(AonApiData api, Customer customer, RegistryStatus newStatus,
			String tagName, Date newExpirationDate) {

		List<RegistrySeller> rsellerList = AON.getRegistrySellerStream(
				api.getDomain(),
				api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getRegistryProperty().eq(customer.getId()))
					.and(f.getStatusProperty().eq(RegistrySellerStatus.ACTIVE.value()))
					.and(f.getStartDateProperty().le(AonDateUtils.toSql(new Date())))
				).collect(Collectors.toList());

		if (rsellerList.isEmpty())
			return;

		Set<Integer> sellerIds = rsellerList.stream()
				.map(RegistrySeller::getSeller)
				.filter(Objects::nonNull)
				.map(Seller::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		if (sellerIds.isEmpty())
			return;

		// Se calculaban dentro del bucle, una vez por agente
		String from = getFromMessage(api);
		String logoUrl = getLogoUrl(api);
		String expirationStr = AonDateUtils.simpleFormat(newExpirationDate);

		Domain useDomain = null == api.getDomain().getParentId()
				? api.getDomain()
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		sellerIds.forEach(sellerId -> {
			RegistryMedia emailMedia = AON.getRegistryMedia(api.getDomain(), api.getUser(),
					f -> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getRegistryProperty().eq(sellerId))
						.and(f.getMediaProperty().eq(MediaType.EMAIL.value())));

			if (null == emailMedia || AonStringUtils.isBlank(emailMedia.getValue())
					|| !AonValidationUtil.isValidEmail(emailMedia.getValue()))
				return;

			SESMessage msg = new SESMessage()
					.setFrom(from)
					.setAlias(useDomain.getDescription())
					.setTo(emailMedia.getValue())
					.setSubject("Cambio de estado del cliente " + customer.getName())
					.setBody(createCustomerStatusChangeTemplate(
							logoUrl,
							useDomain.getDescription(),
							customer.getName(),
							description(customer.getStatus()),
							newStatus.getDescription(),
							tagName,
							expirationStr));

			SES.sendEmail(msg);
		});
	}

	// -----------------------------------------------------------------------
	// Helpers
	// -----------------------------------------------------------------------

	private static boolean isSameDate(Date a, Date b) {
		if (null == a && null == b) return true;
		if (null == a || null == b) return false;
		return AonDateUtils.isSameDay(a, b);
	}

	private static String description(RegistryStatus status) {
		return null == status ? RegistryStatus.ACTIVE.getDescription() : status.getDescription();
	}
	
	private static String getLogoUrl(AonApiData api) {

		Domain useDomain = null == api.getDomain().getParentId()
				? api.getDomain()
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		Company company = AON.getCompany(api.getDomain(), api.getUser(), f -> f.getDomainProperty().eq(useDomain.getId()));	
			
		Attach attach = getLogoAttach(api, company.getId());

		String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
		String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		String logoUrl = null;

		Domain attachDomain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(attach.getDomain().getId()));
		
		logoUrl = "https://" + useDomain.getName() + "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/" + result;

		return logoUrl;
	}

	private static Attach getLogoAttach(AonApiData api, Integer enterpriseId) {
		
		Domain useDomain = null == api.getDomain().getParentId()
				? api.getDomain()
				: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));
		
		Optional<Attach> attach1 = AON.getAttachStream(
				useDomain.getName(), 
				useDomain.getId(), 
				api.getUser().getLogin(), 
				f -> f.getTypeProperty().eq(LOGO.value()).and(f.getDomainProperty().eq(useDomain.getId())).and(f.getAttachModuleProperty().eq(enterpriseId)), 
				AttachType.REGISTRY, 
				true
			).findFirst();
		
		return attach1
				.isPresent()
						? attach1.get()
						: AON.getAttachStream(
								useDomain.getName(), 
								useDomain.getId(), 
								api.getUser().getLogin(), 
								f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getDomainProperty().eq(useDomain.getId())).and(f.getAttachModuleProperty().eq(enterpriseId)), 
								AttachType.REGISTRY, 
								true
							).findFirst().get();
	}
	
	private static String getFromMessage(AonApiData api) {
		String from = null;
		
		Domain useDomain = null == api.getDomain().getParentId()
			? api.getDomain()
			: AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		Company company = AON.getCompany(useDomain, api.getUser(), f -> f.getDomainProperty().eq(useDomain.getId()));
		
		RegistryMedia emailMedia = AON.getRegistryMedia(useDomain, api.getUser(), f -> f.getDomainProperty().eq(useDomain.getId()).and(f.getRegistryProperty().eq(company.getId())).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
		
		if(AonStringUtils.isNotBlank(emailMedia.getValue()) && AonValidationUtil.isValidEmail(emailMedia.getValue()))
			from = emailMedia.getValue();

		return from;
	}
	
	private static String createCustomerStatusChangeTemplate(String logoUrl, String domainName, String customerName, String oldCustomerStatus, String newCustomerStatus, String reasonNewStatus, String expirationDate) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", domainName);
		context.put("customerName", customerName);
		context.put("oldCustomerStatus", oldCustomerStatus);
		context.put("newCustomerStatus", newCustomerStatus);
		context.put("reasonNewStatus", reasonNewStatus );
		context.put("expirationDateStatus", AonStringUtils.isBlank(expirationDate) ? "" : ("F. Expiraci\u00f3n: " + expirationDate) );
		
		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/customer_status_change.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

}
