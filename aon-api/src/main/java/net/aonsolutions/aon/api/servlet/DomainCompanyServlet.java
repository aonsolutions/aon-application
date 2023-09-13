package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.LinkedList;
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
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "DomainServlet", urlPatterns = {"/ms/api/domain/*"})
public class DomainCompanyServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DomainCompanyServlet.class.getName());
	
	public static final String DOMAINS = "/";
	public static final String CUSTOMER_DOMAINS = "/:customer"; //buscar entre todos los schemas los que tengan ese aonCustomer
	public static final String DOMAIN_LINKED = "/link/";
	public static final String BOOKING = "/booking/";
	public static final String BOOKING2 = "/booking2/";
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
				.addRoute(BOOKING2, DomainCompanyServlet::updateBookingRitems2)
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
	
	private static JSONObject updateBookingRitems2(AonApiData api) {
		try {
			
			Integer customerId = JsonUtils.getInteger(api.getData(), IJsonNames.CUSTOMER);
			
			if(customerId != null) {
				LinkedList<Booking> customerBookings = getBookings(customerId);
				
				for(Booking booking : customerBookings) {
					
					if (booking.getDomain() != null && booking.getDomain().getId() != null) {
						for (AonApp app : booking.getApps()) {
							String barCode = getBarCode(booking.getType(), app);
							Item item = AON.getItem(api.getDomain(), api.getUser().getLogin(), f -> f.getBarcodeProperty().like("%" + barCode + "%"));
		
							Integer itemId = item != null ? item.getId() : null;
							if (item != null && itemId != null) {
								RegistryItem ritem = AON.getRItem(
										api.getDomain().getName(),
										api.getDomain().getId(),
										api.getUser().getLogin(),
										f -> f.getRegistryProperty().eq(customerId).and(f.getItemProperty().eq(itemId))
								);
								if (ritem == null || ritem.getId() == null) {
									RegistryItem newRitem = new RegistryItem()
											.setDomain(api.getDomain().getId())
											.setRegistry(customerId)
											.setItem(item)
											.setType(RegistryMode.BOOKING)
											.setStatus(RegistryItemStatus.ACTIVE)
											.setPriority(Priority.NONE)
											.setCode("CONSOLE")
											.setQuantity("1");
									try {
										AON.saveRItem(api.getDomain(), api.getUser(), newRitem);								
									} catch (Exception e) {
										throw new AonApiException("No se pudo guardar [" + e.getMessage() + "]");
									}
								}
							} else {
								throw new AonApiException("Item no encontrado");
							}
						}
						
						// Create RItem for users - xx.USER - Quantity = numUsers
						updateUserBookingRItem(api, booking, customerId);
						
						// Create RItem for @Conectas | xx.yy.zz | xx=01 Asesoria | yy=Empresa/Despacho | zz=24 Basica, 25 Estandar, 26 Profesional 
						updateConectaBookingRItem(api, booking, customerId);
						
					} else  {
						throw new AonApiException("Contratación no encontrada");
					}
				}
			
			}
		} catch (Exception e) {
			throw new  AonApiException(e.getMessage());
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
									.setCode("CONSOLE")
									.setQuantity("1");
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

			} else {
				JSONObject itemErr = new JSONObject();
				itemErr.put("error", "Error parseando los objetos contratacion y dominio");
				errors.put(itemErr);
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
	
	public static LinkedList<Booking> getBookings(Integer customerId) throws IOException, InterruptedException {
//		String url = "http://localhost:8080/ms/api/booking/customer?customer=" + customerId;
		String url = "https://aon.solutions/ms/api/booking/customer?customer=" + customerId;
		HttpRequest request = HttpRequest.newBuilder()
				.uri( URI.create(url) )
				.header("session_id","AONd95770f269e711eb94390242ac130002")
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.GET( )
				.build();
		HttpResponse<String> response = HttpClient.newBuilder()
				.build()
				.send(request, BodyHandlers.ofString());
		
		JSONArray responseJson = new JSONArray(response.body());
		
		return BookingJSON.fromJSON(responseJson);		
	}

	private static void updateUserBookingRItem(AonApiData api, Booking booking, Integer aonCustomer) {
		String userBarCode = getUserBarCode(booking.getType());
		updateRItem(api, booking, aonCustomer, userBarCode, booking.getNumberOfUsers().toString());
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
	
	@Deprecated
	private static void updateConectaBookingRItem(DomainCompany domainCompany, Booking booking, Integer aonCustomer, JSONArray errors, AonApiData api) {
		DomainType domainType = domainCompany.getDomain().getDomainType();

		List<AonApp> checkAonApps = new ArrayList<>();
		checkAonApps.add(AonApp.BASIC_MANAGEMENT);
		checkAonApps.add(AonApp.STANDAR_MANAGEMENT);
		checkAonApps.add(AonApp.PROFESSIONAL_MANAGEMENT);

		if(domainType.equals(DomainType.CONSULTANCY) && null != booking.getResume()) {

			if(null == booking.getResume().getDomainTypes() || booking.getResume().getDomainTypes().size() == 0) {
				JSONObject itemErr = new JSONObject();
				itemErr.put("error", "No existe resumen para esta contratacion");
				errors.put(itemErr);
			}

			booking.getResume().getDomainTypes().entrySet().forEach(entry -> {

				DomainType domainChildType = entry.getKey();
				DomainTypeInfo domainChildInfo = entry.getValue();

				if(null != domainChildInfo) {

					checkAonApps.forEach(checkAonApp -> {

						Long checkAonAppCount = domainChildInfo.getChildApps().get(checkAonApp);

						if(null != checkAonAppCount && checkAonAppCount > 0) {
							String barCode = getConnectarBarCode(domainType, domainChildType, checkAonApp); 
							updateRItem(domainCompany, booking, aonCustomer, errors, api, barCode, checkAonAppCount.toString());
							errors.put(createError(barCode, domainType, checkAonApp, "Se procede a guardar " + barCode));
						}

					});

				}
			});
		}

	}
	
	@Deprecated
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
	
	@Deprecated
	private static void updateUserBookingRItem(DomainCompany domainCompany, Booking booking, Integer aonCustomer, JSONArray errors, AonApiData api) {
		String userBarCode = getUserBarCode(domainCompany.getDomain().getDomainType());
		updateRItem(domainCompany, booking, aonCustomer, errors, api, userBarCode, booking.getNumberOfUsers().toString());
	}
	
	private static void updateConectaBookingRItem(AonApiData api, Booking booking, Integer aonCustomer) {
		List<AonApp> checkAonApps = new ArrayList<>();
		checkAonApps.add(AonApp.BASIC_MANAGEMENT);
		checkAonApps.add(AonApp.STANDAR_MANAGEMENT);
		checkAonApps.add(AonApp.PROFESSIONAL_MANAGEMENT);
		
		if(DomainType.CONSULTANCY.equals(booking.getType()) && null != booking.getResume()) {
			
			booking.getResume().getDomainTypes().entrySet().forEach(entry -> {
				
				DomainType domainChildType = entry.getKey();
				DomainTypeInfo domainChildInfo = entry.getValue();
				
				if(null != domainChildInfo) {
					
					checkAonApps.forEach(checkAonApp -> {
						
						Long checkAonAppCount = domainChildInfo.getChildApps().get(checkAonApp);
						
						if(null != checkAonAppCount && checkAonAppCount > 0) {
							String barCode = getConnectarBarCode(booking.getType(), domainChildType, checkAonApp); 
							updateRItem(api, booking, aonCustomer, barCode, checkAonAppCount.toString());
						}
						
					});
					
				}
			});
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
	
	private static void updateRItem( AonApiData api, Booking booking, Integer aonCustomer, String barCode, String quantity) {
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
					throw new AonApiException("No se pudo guardar [" + e.getMessage() + "]");
				}
			}
		} else {
			throw new AonApiException("Item no encontrado");
		}
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
	
}
