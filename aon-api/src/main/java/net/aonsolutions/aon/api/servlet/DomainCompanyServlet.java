package net.aonsolutions.aon.api.servlet;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.json.BookingJSON;
import com.esferalia.aon.occam.api.json.DomainCompanyJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.DomainTypeInfo;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "DomainServlet", urlPatterns = {"/ms/api/domain/*"})
public class DomainCompanyServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DomainCompanyServlet.class.getName());
	
	public static final String DOMAINS = "/";
	public static final String CUSTOMER_DOMAINS = "/:customer"; //buscar entre todos los schemas los que tengan ese aonCustomer
	public static final String DOMAIN_LINKED = "/link/";
	public static final String BOOKING = "/booking/";
	public static final String REMOTE = "/remote/";
	
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

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
					.addRoute(DOMAINS, DomainCompanyServlet::getDomains)
					.addRoute(CUSTOMER_DOMAINS, DomainCompanyServlet::getCustomerDomains)
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
				.addRoute(DOMAINS, DomainCompanyServlet::updateCustomerDomains)
				.addRoute(DOMAIN_LINKED, DomainCompanyServlet::saveDomainLinked)
				.addRoute(BOOKING, DomainCompanyServlet::updateBookingRitems)
				.addRoute(REMOTE, DomainCompanyServlet::remoteDomain)
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
//				.addRoute(CUSTOMER_DOMAINS, DomainCompanyServlet::deleteAction)
				.addRoute(DOMAIN_LINKED, DomainCompanyServlet::deleteDomainLinked)
				.addRoute(BOOKING, DomainCompanyServlet::deleteBookingRitems)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getDomains(AonApiData api) {
		JSONArray domains = new JSONArray();
		CONSOLE.getDomains(f -> domainFilter(api, f)).map(DomainCompanyJSON::toJSON).forEach(domains::put);
		return domains;
	}
	
	private static Filter domainFilter(AonApiData api, DomainProperties f) {
		Filter filter = f.getIdProperty().gt(0);
		if (api.getData().opt(IJsonNames.LINKED) != null) {
			boolean linked = api.getData().optBoolean(IJsonNames.LINKED);
			filter = filter.and(linked ? f.getAonCustomerProperty().isNotNull() : f.getAonCustomerProperty().isNull());
		}
		if (api.getData().opt(IJsonNames.AON_CUSTOMER) != null) {
			filter = filter.and(f.getAonCustomerProperty().eq(api.getData().optInt(IJsonNames.AON_CUSTOMER)));
		}
		if (api.getData().opt(IJsonNames.DOMAIN_ID) != null) {
			filter = filter.and(f.getIdProperty().eq(api.getData().optInt(IJsonNames.DOMAIN_ID)));
		}
		return filter;
	}
	
	private static JSONArray getCustomerDomains(AonApiData api) {
		JSONArray domains = new JSONArray();
  		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
  		Integer customerId = vars.getInt(IJsonNames.CUSTOMER);
  		CONSOLE.getCustomerDomains(customerId).map(DomainCompanyJSON::toJSON).forEach(domains::put);
		return domains;
	}
	
	private static JSONArray updateCustomerDomains(AonApiData api) {
		JSONArray domainsJson = api.getData().optJSONArray(IJsonNames.DOMAINS);
		Integer customer = api.getData().optInt(IJsonNames.CUSTOMER) > 0 ? api.getData().optInt(IJsonNames.CUSTOMER) : null;
		List<DomainCompany> domains = DomainCompanyJSON.fromJSON(domainsJson);
		List<Domain> updatedDomain = CONSOLE.updateDomainCustomerData(domains, customer);
		return DomainJSON.toJSON(updatedDomain);
	}
	
	private static JSONObject saveDomainLinked(AonApiData api) {
		Integer customer = api.getData().optInt(IJsonNames.CUSTOMER) > 0 ? api.getData().optInt(IJsonNames.CUSTOMER) : null;
		JSONObject domainJson = api.getData().optJSONObject(IJsonNames.DOMAIN);
		DomainCompany domainCompany = DomainCompanyJSON.fromJSON(domainJson);
		if (customer != null) {
			Domain domain = domainCompany.getDomain();
			String schema = domainCompany.getSchema();
			DomainLinked domainLinked = new DomainLinked()
					.setId(domain.getId())
					.setName(domain.getName())
					.setRegistry(customer)
					.setSchema(schema)
					.setType(domain.getDomainType().getName());
			CONSOLE.saveDomainLink(api.getDomain(), api.getUser(), domainLinked);
		}
		return new JSONObject();
	}
	
	private static JSONObject remoteDomain(AonApiData api) {
		CONSOLE.remoteAccess(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		return new JSONObject();
	}
	
	private static JSONObject deleteDomainLinked(AonApiData api) {
		Domain apiDomain = api.getDomain();
		User apiUser = api.getUser();
		Integer customer = api.getData().optInt(IJsonNames.CUSTOMER) > 0 ? api.getData().optInt(IJsonNames.CUSTOMER) : null;
		JSONObject domainJson = api.getData().optJSONObject(IJsonNames.DOMAIN);
		DomainCompany domainCompany = DomainCompanyJSON.fromJSON(domainJson);
		if (customer != null) {
			Domain domain = domainCompany.getDomain();
			List<DomainLinked> domainLinkeds = AON.getDomainLinkedList(apiDomain.getName(), apiDomain.getId(), apiUser.getLogin(), customer);
			domainLinkeds
			.stream()
			.filter(dl -> AonStringUtils.equals(dl.getName(), domain.getName())
					&& AonNumberUtils.equals(dl.getId(), domain.getId()))
			.forEach(dl -> {
				CONSOLE.deleteDomainLink(apiDomain, apiUser, dl);				
			});
			
		}
		return new JSONObject();
	}
	
	private static JSONObject updateBookingRitems(AonApiData api) {
		JSONObject errJson = new JSONObject();
		JSONObject domainJson = api.getData().optJSONObject(IJsonNames.DOMAIN);
		JSONObject bookingJson = api.getData().optJSONObject(IJsonNames.BOOKING);

		JSONArray errors = new JSONArray();
		errJson.put(IJsonNames.DOMAIN, domainJson);
		
		errJson.put(IJsonNames.ERRORS, errors);		
		
		if (domainJson != null && bookingJson != null) {
			DomainCompany domainCompany = DomainCompanyJSON.fromJSON(domainJson);
			Booking booking = BookingJSON.fromJSON(bookingJson);
			Domain domain = domainCompany.getDomain();
			if (domain != null) {
				List<AonApp> apps = booking.getApps();
				DomainType domainType = domain.getDomainType();
				Integer aonCustomer = domain.getAonCustomer();
				for (AonApp app : apps) {
					String barCode = getBarCode(domainType, app);
					Item item = AON.getItem(api.getDomain(), api.getUser().getLogin(), f -> f.getBarcodeProperty().like("%" + barCode + "%"));
//					if (item == null || item.getId() == null) {
//						item = createItem(api, domain, app);
//					}
					Integer itemId = item != null ? item.getId() : null;
					if (item != null && itemId != null) {
						RegistryItem ritem = AON.getRItem(
								api.getDomain().getName(),
								api.getDomain().getId(),
								api.getUser().getLogin(),
								f -> f.getRegistryProperty().eq(aonCustomer).and(f.getItemProperty().eq(itemId))
						);
						if (ritem == null || ritem.getId() == null) {
							RegistryItem newRitem = new RegistryItem()
									.setDomain(api.getDomain().getId())
									.setRegistry(aonCustomer)
									.setItem(item)
									.setType(RegistryMode.BOOKING)
									.setStatus(RegistryItemStatus.ACTIVE)
									.setPriority(Priority.NONE)
									.setCode("CONSOLE");
							try {
								AON.saveRItem(api.getDomain(), api.getUser(), newRitem);								
							} catch (Exception e) {
								errors.put(createError(barCode, domainType, app, "No se pudo guardar [" + e.getMessage() + "]"));
							}
						}
					} else {
						errors.put(createError(barCode, domainType, app, "Item no encontrado"));
					}
				}
				
				// Create RItem for users - xx.USER - Quantity = numUsers
				updateUserBookingRItem(domainCompany, booking, aonCustomer, errors, api);
				
				// Create RItem for @Conectas | xx.yy.zz | xx=01 Asesoria | yy=Empresa/Despacho | zz=24 Basica, 25 Estandar, 26 Profesional 
				updateConectaBookingRItem(domainCompany, booking, aonCustomer, errors, api);
				
			}
		} else if (bookingJson == null || bookingJson.isEmpty()) {
			errors.put(createError(null, null, null, "Contratación no encontrada"));
		}
		if (!errors.isEmpty()) {			
			return errJson;
		} else {
			return new JSONObject();
		}
	}

	private static void updateUserBookingRItem(DomainCompany domainCompany, Booking booking, Integer aonCustomer, JSONArray errors, AonApiData api) {
		String userBarCode = getUserBarCode(domainCompany.getDomain().getDomainType());
		updateRItem(domainCompany, booking, aonCustomer, errors, api, userBarCode, booking.getNumberOfUsers().toString());
	}

	private static String getUserBarCode(DomainType domainType) {
		StringBuilder sb = new StringBuilder();
		if (domainType != null) {
			int domainTypeOrdinal = domainType.ordinal();
			sb.append(AonStringUtils.leftPad(AonNumberUtils.toString(domainTypeOrdinal), 2, '0'));
			sb.append(".USR");
		}
		return sb.toString();
	}
	
	private static void updateConectaBookingRItem(DomainCompany domainCompany, Booking booking, Integer aonCustomer, JSONArray errors, AonApiData api) {
		DomainType domainType = domainCompany.getDomain().getDomainType();
		if(domainType.equals(DomainType.CONSULTANCY) && null != booking.getResume()) {
			// Empresas
			DomainTypeInfo enterpriseChildBooking = booking.getResume().getDomainTypes().get(DomainType.ENTERPRISE);
			
			if(null != enterpriseChildBooking) {
				
				// Empresas - Conecta Basico
				Long quantityConnectaBasic = enterpriseChildBooking.getChildApps().get(AonApp.BASIC_MANAGEMENT);
				if(null != quantityConnectaBasic) {
					String connectaBasicBarCode = getConnectarBarCode(domainType, DomainType.ENTERPRISE, AonApp.BASIC_MANAGEMENT); 
					updateRItem(domainCompany, booking, aonCustomer, errors, api, connectaBasicBarCode, quantityConnectaBasic.toString());
				}
				
				// Empresas - Conecta Standard
				Long quantityConenctaStandard = enterpriseChildBooking.getChildApps().get(AonApp.STANDAR_MANAGEMENT);
				if(null != quantityConenctaStandard) {
					String connectaStandardBarCode = getConnectarBarCode(domainType, DomainType.ENTERPRISE, AonApp.STANDAR_MANAGEMENT); 
					updateRItem(domainCompany, booking, aonCustomer, errors, api, connectaStandardBarCode, quantityConenctaStandard.toString());
				}
				
				// Empresas - Conecta Professional
				Long quantityConnectaProfessional = enterpriseChildBooking.getChildApps().get(AonApp.PROFESSIONAL_MANAGEMENT);
				if(null != quantityConnectaProfessional) {
					String conenctaProfessionalBarCode = getConnectarBarCode(domainType, DomainType.ENTERPRISE, AonApp.PROFESSIONAL_MANAGEMENT); 
					updateRItem(domainCompany, booking, aonCustomer, errors, api, conenctaProfessionalBarCode, quantityConnectaProfessional.toString());
				}
			}
			
			// Despacho
			DomainTypeInfo officeChildBooking = booking.getResume().getDomainTypes().get(DomainType.OFFICE);
			
			if(null != officeChildBooking) {
				// Despacho - Conecta Basico
				Long quantityConnectaBasic = officeChildBooking.getChildApps().get(AonApp.BASIC_MANAGEMENT);
				if(null != quantityConnectaBasic) {
					String connectaBasicBarCode = getConnectarBarCode(domainType, DomainType.ENTERPRISE, AonApp.BASIC_MANAGEMENT); 
					updateRItem(domainCompany, booking, aonCustomer, errors, api, connectaBasicBarCode, quantityConnectaBasic.toString());
				}
				
				// Despacho - Conecta Standard
				Long quantityConenctaStandard = officeChildBooking.getChildApps().get(AonApp.STANDAR_MANAGEMENT);
				if(null != quantityConenctaStandard) {
					String connectaStandardBarCode = getConnectarBarCode(domainType, DomainType.ENTERPRISE, AonApp.STANDAR_MANAGEMENT); 
					updateRItem(domainCompany, booking, aonCustomer, errors, api, connectaStandardBarCode, quantityConenctaStandard.toString());
				}
				
				// Despacho - Conecta Professional
				Long quantityConnectaProfessional = officeChildBooking.getChildApps().get(AonApp.PROFESSIONAL_MANAGEMENT);
				if(null != quantityConnectaProfessional) {
					String conenctaProfessionalBarCode = getConnectarBarCode(domainType, DomainType.ENTERPRISE, AonApp.PROFESSIONAL_MANAGEMENT); 
					updateRItem(domainCompany, booking, aonCustomer, errors, api, conenctaProfessionalBarCode, quantityConnectaProfessional.toString());
				}
			}
			
		}
		
	}
	
	private static String getConnectarBarCode(DomainType domainType, DomainType childDomainType, AonApp aonApp) {
		StringBuilder sb = new StringBuilder();
		if (domainType != null) {
			int domainTypeOrdinal = domainType.ordinal();
			int childDomainTypeOrdinal = childDomainType.ordinal();
			int aonAppOrdinal = aonApp.ordinal();
			sb.append(AonStringUtils.leftPad(AonNumberUtils.toString(domainTypeOrdinal), 2, '0'));
			sb.append(".");
			sb.append(AonStringUtils.leftPad(AonNumberUtils.toString(childDomainTypeOrdinal), 2, '0'));
			sb.append(".");
			sb.append(AonStringUtils.leftPad(AonNumberUtils.toString(aonAppOrdinal), 2, '0'));
		}
		return sb.toString();
	}
	
	

	private static JSONObject deleteBookingRitems(AonApiData api) {
		int customer = api.getData().optInt(IJsonNames.CUSTOMER);
		boolean removeAll = api.getData().optBoolean("all");
		if (!removeAll && customer > 0) {
			AON.deleteRItem(api.getDomain(), api.getUser(), f -> f.getRegistryProperty().eq(customer).and(f.getTypeProperty().eq(RegistryMode.BOOKING.value())));
		} else if (removeAll){
			AON.deleteRItem(api.getDomain(), api.getUser(), f -> f.getTypeProperty().eq(RegistryMode.BOOKING.value()));
		}
		return new JSONObject();
	}
	
	private static void updateRItem(DomainCompany domainCompany, Booking booking, Integer aonCustomer, JSONArray errors, AonApiData api, String barCode, String quantity) {
		Item item = AON.getItem(api.getDomain(), api.getUser().getLogin(), f -> f.getBarcodeProperty().like("%" + barCode + "%"));
		
		Integer itemId = item != null ? item.getId() : null;
		if (item != null && itemId != null) {
			RegistryItem ritem = AON.getRItem(
					api.getDomain().getName(),
					api.getDomain().getId(),
					api.getUser().getLogin(),
					f -> f.getRegistryProperty().eq(aonCustomer).and(f.getItemProperty().eq(itemId))
			);
			if (ritem == null || ritem.getId() == null) {
				RegistryItem newRitem = new RegistryItem()
						.setDomain(api.getDomain().getId())
						.setRegistry(aonCustomer)
						.setItem(item)
						.setType(RegistryMode.BOOKING)
						.setStatus(RegistryItemStatus.ACTIVE)
						.setPriority(Priority.NONE)
						.setCode("CONSOLE")
						.setQuantity(quantity);
				try {
					AON.saveRItem(api.getDomain(), api.getUser(), newRitem);								
				} catch (Exception e) {
					errors.put(createError(barCode, domainCompany.getDomain().getDomainType(), null, "No se pudo guardar [" + e.getMessage() + "]"));
				}
			}
		} else {
			errors.put(createError(barCode, domainCompany.getDomain().getDomainType(), null, "Item no encontrado"));
		}
	}
	
	
	private static JSONObject createError(String barCode, DomainType domainType, AonApp app, String error) {
		JSONObject itemErr = new JSONObject();
		if (AonStringUtils.isNotBlank(barCode)) {
			itemErr.put(IJsonNames.BARCODE, barCode);
		}
		if (domainType != null) {
			itemErr.put(IJsonNames.DOMAIN_TYPE, domainType.name());			
		}
		if (app != null) {
			itemErr.put(IJsonNames.APP, app.name());
		}
		if (AonStringUtils.isNotBlank(error)) {
			itemErr.put(IJsonNames.ERROR, error);
		}
		
		return itemErr.isEmpty() ? null : itemErr;
	}
	
	private static Product createProduct(AonApiData api, Domain domain, AonApp app) {
		if (domain == null || app == null) {
			return null;
		}
		Product product = new Product().setName(app.name()).setCode(""/*no lo sé*/).setKind(ProductKind.SALE);
		return AON.saveProduct(api.getDomain(), api.getUser().getLogin(), product);
	}
	
	private static Item createItem(AonApiData api, Domain domain, AonApp app) {
		if (domain == null || app == null) {
			return null;
		}
		Product product = AON.getProduct(api.getDomain(), api.getUser().getLogin(), f -> f.getNameProperty().eq(app.name()));
		if (product == null || product.getId() == null) {
			product = createProduct(api, domain, app);
		}
		Item item = new Item().setBarcode(getBarCode(domain.getDomainType(), app)).setProduct(product);
		return AON.saveItem(api.getDomain(), api.getUser().getLogin(), item);
	}
	
	private static String getBarCode(DomainType domainType, AonApp app) {
		StringBuilder sb = new StringBuilder();
		if (domainType != null && app != null) {
			int domainTypeOrdinal = domainType.ordinal();
			int appOrdinal = app.ordinal();
			sb.append(AonStringUtils.leftPad(AonNumberUtils.toString(domainTypeOrdinal), 2, '0'));
			sb.append(".");
			sb.append(AonStringUtils.leftPad(AonNumberUtils.toString(appOrdinal), 2, '0'));
		}
		return sb.toString();
	}
	
}
