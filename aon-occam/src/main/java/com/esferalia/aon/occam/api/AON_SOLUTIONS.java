package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.ProductJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainAppFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.Filter.NotificationFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.Filter.UserAppRoleFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.ApiImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
import com.esferalia.aon.occam.impl.jooq.NotificationImpl;
import com.esferalia.aon.occam.impl.jooq.Product2Impl;
import com.esferalia.aon.occam.impl.jooq.ProductImpl;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;
import com.esferalia.aon.occam.impl.jooq.TaskImpl;
import com.esferalia.aon.occam.impl.jooq.TimeControlImpl;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AON_SOLUTIONS {
	
	private static IApi getApi() {
		return new ApiImpl();
	}
	
	private static ICommon getCommon() {
		return new CommonImpl();
	}
	
	private static IRegistry getRegistry() {
		return new RegistryImpl();
	}
	
	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}
	
	private static ITask getTask() {
		return new TaskImpl();
	}
	
	private static ITimeControl getTimeControl() {
		return new TimeControlImpl();
	}
	
	private static INotification getNotification() {
		return new NotificationImpl();
	}
	
	private static IFinance getFinance() {
		return new FinanceImpl();
	}

	private static IProduct2 getProduct() {
		return new Product2Impl();
	}
	
	public static Auth getAuth(String domainName, Integer domainId, String email) { 
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, "");
			return getSecurity().getAuth(ctx, email);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static Auth getAuth(String domainName, Integer domainId, byte[] auth) { 
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, "");
			return getSecurity().getAuth(ctx, auth);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static Auth getAuth(String schema, String email) { 
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(schema);
			return getSecurity().getAuth(ctx, email);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static Auth getAuth(String email) {
		Auth auth = new Auth();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
				auth = getAuth(domain, 0, email);
				auth.setSchema(schema);
		   	}	    		
		}
		return auth;
	}
	
	public static Auth getAuthByDocument(String domainName, Integer domainId, String document) { 
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, "");
			return getSecurity().getAuthByDocument(ctx, document);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static Auth getAuthByDocument(String document) {
		Auth auth = new Auth();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
				auth = getAuthByDocument(domain, 0, document);
				auth.setSchema(schema);
		   	}	    		
		}
		return auth;
	}
	
	public static Auth getAuth(byte[] auth) {
		Auth auth0 = new Auth();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(auth0.getUuid() == null && !AonStringUtils.isBlank(domain)) {
				auth0 = getAuth(domain, 0, auth);
				auth0.setSchema(schema);
		   	}	    		
		}
		return auth0;
	}
	
	public static Auth updateAuth(Auth auth) { 
		String domain = AONContext.getSchemaFirstDomain(auth.getSchema());
		return updateAuth(domain, 0, auth);
	}
	
	public static Auth updateAuth(String domainName, Integer domainId, Auth auth) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getSecurity().updateAuth(ctx, auth);
		}
	}
	
	public static Auth updateAuthPassword(Auth auth) {
		String domain = AONContext.getSchemaFirstDomain(auth.getSchema());
		try (AONContext ctx = AONContext.getAONContext(domain, 0, "")){		
			return getSecurity().updateAuthPassword(ctx, auth);
		}
	}
	
	public static Auth insertAuth(String domainName, Integer domainId, Auth auth) { 
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getSecurity().insertAuth(ctx, auth);
		}
	}
	
	public static Auth insertAuth(Auth auth) { 
		List<String> schemas = AONContext.getSchemas();
		Boolean inserted = false;
		Integer index = 0;
		Boolean pro = schemas.contains("pro-aonsolutions-net");

		while(!inserted && index < schemas.size()) {
			if(pro) {
				if(schemas.get(index).equalsIgnoreCase("pro-aonsolutions-net")) {
					String domain = AONContext.getSchemaFirstDomain(schemas.get(index));
					auth = insertAuth(domain, 0, auth);			
					auth.setSchema(schemas.get(index));
					inserted = true;
				}
			} else if(!schemas.get(index).contains("global")) {
				String domain = AONContext.getSchemaFirstDomain(schemas.get(index));
				auth = insertAuth(domain, 0, auth);
				auth.setSchema(schemas.get(index));
				inserted = true;
			}
			index++;
		}
		
		return auth;
	}
	
	public static Domain insertDomain(String schema, Domain domain, Registry registry) throws Exception { 
		String domainName = AONContext.getSchemaFirstDomain(schema);
		return insertDomain(domainName, 0, "", domain, registry);
	}

	public static Domain insertDomain(Domain domain, User user, Domain newDomain, Registry registry) throws Exception { 
		return insertDomain(domain.getName(), domain.getId(), user.getLogin(), newDomain, registry);
	}
	
	public static Domain insertDomain(String domainName, Integer domainId, String login, Domain domain, Registry registry) throws Exception{ 
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getCommon().insertDomain(ctx, domain, registry);
		}
	}
	
	public static void assignAuthToUser(String domainName, Integer domainId, User user, String uuid) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			byte[] auth = getSecurity().unHexUuid(ctx, uuid);
			getSecurity().assignAuthToUser(ctx, user, auth);
		}
	}
	
	public static void assignAuthToUser(String domainName, Integer domainId, User user, byte[] auth) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			getSecurity().assignAuthToUser(ctx, user, auth);
		}
	}
	
	public static Stream<DomainApp> getDomainApp(String domainName, Integer domainId, String login, DomainAppFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().getDomainAppStream(ctx, filter);
		}
	}
	
	public static boolean isOCRActive(String domainName, Integer domainId, String login) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().isOCRActive(ctx,domainId);
		}
	}

	public static DomainApp saveDomainApp(String domainName, Integer domainId, String login, DomainApp domainApp, boolean old) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().saveDomainApp(ctx, domainApp, old);
		}
	}

	public static Stream<UserAppRole> getUserAppRole(String domainName, Integer domainId, String login, UserAppRoleFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().getUserAppRoleStream(ctx, filter);
		}
	}
	
	public static UserAppRole insertUserAppRole(String domainName, Integer domainId, String login, UserAppRole userAppRole) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().insertUserAppRole(ctx, userAppRole);
		}
	}
	
	public static UserAppRole updateUserAppRole(String domainName, Integer domainId, String login, UserAppRole userAppRole) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().updateUserAppRole(ctx, userAppRole);
		}
	}
	
	public static UserAppRole deleteUserAppRole(String domainName, Integer domainId, String login, UserAppRoleFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().deleteUserAppRole(ctx, filter);
		}
	}
	
	public static User getUser(Domain domain, String token) {
		AonToken aonToken = SECURITY.getAonToken(token);
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "")){		
			return getSecurity().getUser(ctx, f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
					.and(f.getAuthProperty().eq(aonToken.getAuth()).or(f.getLoginProperty().eq(aonToken.getUuid()))));
		}
	}
	
	public static LinkedList<User> getUsersByEmail(String domainName, Integer domainId, String email) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getSecurity().getUsersByEmail(ctx, email);
		}
	}
	
	public static String getUserPassword(String domainName, Integer domainId, Integer user) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, "")){
			return getSecurity().getUserPassword(ctx, user);
		}
	}
	
	public static User getUserUuid(Domain domain, String uuid) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "")){		
			byte[] auth = getSecurity().unHexUuid(ctx, uuid);
			return getSecurity().getUser(ctx, f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))).and(f.getAuthProperty().eq(auth)));
		}
	}
	
	public static Stream<AonCompany> getCompanyStream(String token, String schema, Integer page, Integer perPage) {	
		AonToken aonToken = SECURITY.getAonToken(token);
		Stream<AonCompany> stream = new LinkedList<AonCompany>().stream();
		String domain = AONContext.getSchemaFirstDomain(schema);
		if(!AonStringUtils.isBlank(domain)) {
			try (AONContext ctx = AONContext.getAONContext(domain, 0, "")) {
				Stream<AonCompany> s = getRegistry().getCompanyStream(ctx, aonToken.getAuth(), page, perPage);
				stream = Stream.concat(stream, s);
			}
		} 
		return stream;
	}
	
	public static Domain getDomain(String token, Integer domainId) {
		try (AONContext ctx = AONContext.getAONContext(token)){
			return getCommon().getDomain(ctx, domainId);
		}
	}
	
	// INVOICE
	
	public static Stream<JSONObject> getDataResponseInvoices(String domainName, Integer domainId, String login, DataResponseFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getDataResponseInvoices(ctx, filter);
		} 
	}
	
	public static Stream<Invoice> getInvoices(String domainName, Integer domainId, String login, InvoiceFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getInvoices(ctx, filter);
		} 
	}
	
	public static Invoice insertInvoices(String domainName, Integer domainId, String login, Invoice invoice) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().insertInvoice(ctx, invoice);
		} 
	}
	
	public static void deleteInvoices(String domainName, Integer domainId, String login, LinkedList<Integer> ids) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			for (Integer id : ids) {
				getApi().deleteInvoice(ctx, id);
			}
		} 
	}

	public static TaskHolder getTaskHolder(AonToken aonToken) {
		TaskHolder taskHolder = null;
		try (AONContext ctx = AONContext.getAONContext(aonToken.getSchemaFirstDomain(),0, "")){
			taskHolder = getTask().getTaskHolderStream(ctx, aonToken.getAuth()).findFirst().orElse(null);
		}
		if(taskHolder == null) {
			List<String> schemas = AONContext.getSchemas();
			for(String schema: schemas) {
				if(taskHolder == null) {
					String domain = AONContext.getSchemaFirstDomain(schema);
					try (AONContext ctx = AONContext.getAONContext(domain, 0, "")){
						taskHolder = getTask().getTaskHolderStream(ctx, aonToken.getAuth()).findFirst().orElse(null);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return taskHolder;
	}
	
	public static LinkedList<TaskHolder> getTaskHolders(AonToken aonToken) {
		LinkedList<TaskHolder> list = new LinkedList<TaskHolder>();;
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				try (AONContext ctx = AONContext.getAONContext(domain, 0, "")){
					getTask().getTaskHolderStream(ctx, aonToken.getAuth())
					.forEach(r -> {
						list.add(r);
					});
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public static Stream<TimeControl> getTimeControlStream(Domain domain, String login, Date startDate, Date endDate) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlStream(ctx, startDate, endDate);
		}
	}
	
	public static Stream<TimeControl> getTaskHolderTimeControlStream(Domain domain, String login, Integer taskHolderId, Date startDate, Date endDate, TimeControlGroup group) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTaskHolderTimeControlStream(ctx, taskHolderId, startDate, endDate, group);
		}
	}
	
	public static TimeControl getTaskHolderTimeControl(Domain domain, String login, Integer taskHolderId, Date startDate, Date endDate) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTaskHolderTimeControl(ctx, taskHolderId, startDate, endDate);
		}
	}
	
	public static Stream<TimeControlDetail> getTimeControlDetailStream(Domain domain, String login, TimeControlFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlDetailStream(ctx, filter);
		}
	}
	
	public static LinkedList<TimeControlDetail> getTimeControlDetailList(Domain domain, String login, TimeControlFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlDetailList(ctx, filter);
		}
	}
	
	public static TimeControlDetail saveTimeControlDetail(Domain domain, String login, TimeControlDetail tcd) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().saveTimeControlDetail(ctx, tcd);
		}
	}
	public static void deleteTimeControlDetail(Domain domain, String login, TimeControlFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getTimeControl().deleteTimeControlDetail(ctx, filter);
		}
	}
	
	public static Location saveLocation(Domain domain, String login, Location lc) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().saveLocation(ctx, lc);
		}
	}
	
	public static Location getLocation(Domain domain, String login, LocationFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getLocation(ctx, filter);
		}
	}
	public static Location getLocation(Domain domain, String login, Coordinates c) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getLocation(ctx, c);
		}
	}
	
	public static Stream<Location> getLocationStream(Domain domain, String login, LocationFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getLocationStream(ctx, filter);
		}
	}
	
	public static void deleteLocation(Domain domain, String login, Location lc) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getTimeControl().deleteLocation(ctx, lc);
		}
	}
	
	@Deprecated
	public static void saveUserFinancePortal(Domain domain, String login, Integer userId) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getSecurity().saveUserFinancePortal(ctx, userId);
		}
	}
	
	public static Stream<Registry> getSuggestionRegistries(Domain domain, String login, LinkedList<RegistryType> list, RegistryFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getRegistry().getSuggestionRegistries(ctx, list, filter);
		}
	}
	
	public static Notification getNotification(Domain domain, String login, NotificationFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNotification().getNotification(ctx, filter);
		}
	}
	
	
//	public static Stream<Notification> getNotificationStream(Auth auth) {
//		return getNotificationStream(f->f.getAuthProperty().eq(auth.getAuth()).or(f.getSenderProperty().eq(auth.getAuth())), 1, 10);
//	}
	
	public static Stream<Notification> getNotificationStream(NotificationFilter filter, Integer page, Integer peerPage) {
		List<String> schemas = AONContext.getSchemas();
		Stream<Notification> stream = new LinkedList<Notification>().stream();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				try {
					Stream <Notification> s = getNotificationStream(domain, 0, "", filter, page, peerPage); 
					stream = Stream.concat(stream, s);
				} catch (Exception e) {}
			}
			
		}
		return stream;
	}
	
	public static Stream<Notification> getNotificationStream(String domainName, Integer domainId, String login, NotificationFilter filter, Integer page, Integer peerPage) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getNotification().getNotificationStream(ctx, filter, page, peerPage);
		}
	}
	
	public static Notification saveNotification(Domain domain, String login, Notification nt) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNotification().saveNotification(ctx, nt);
		}
	}
	
	public static void deleteNotification(Domain domain, String login, Notification nt) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getNotification().deleteNotification(ctx, nt);
		}
	}
	
	public static void markReadNotification(Domain domain, String login, Integer id) {
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getNotification().markReadNotification(ctx, id);
		}
	}
	
	public static Integer getTotalNotification(NotificationFilter filter) {
		List<String> schemas = AONContext.getSchemas();
		Integer total = 0;
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				try {
					AONContext ctx = AONContext.getAONContext(domain, 0, "");
					total =  total + getNotification().getTotalNotification(ctx, filter);
				} catch (Exception e) {}
			}
			
		}
		return total;
	}
	
	
	// ----- INVOICE - ACCEPT INVOICE
	
	public static JSONObject getInvoice(Domain domain, User user, Integer id) {
		return getInvoice(domain.getName(), domain.getId(), user.getLogin(), id);
	}
	
	public static JSONObject getInvoice(Domain domain, String login, Integer id) {
		return getInvoice(domain.getName(), domain.getId(), login, id);		
	}

	public static JSONObject getInvoice(String domainName, Integer domainId, String login, Integer id) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			Invoice invoice = getFinance().getFullInvoice(ctx, id);
			return InvoiceJSON.toJSON(invoice);
		}
	}
	
	
	public static JSONObject acceptInvoice(Domain domain, User user, JSONObject json) {
		return acceptInvoice(domain.getName(), domain.getId(), user.getLogin(), json);
	}
	
	public static JSONObject acceptInvoice(Domain domain, String login, JSONObject json) {
		return acceptInvoice(domain.getName(), domain.getId(), login, json);
	}
	
	public static JSONObject acceptInvoice(String domainName, Integer domainId, String login, JSONObject json) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			Invoice invoice = InvoiceJSON.fromJSON(json);
			invoice = getFinance().acceptInvoice(ctx, invoice, invoice.getId());
			return InvoiceJSON.toJSON(invoice);
		}
	}
	
	public static Invoice acceptInvoice(Domain domain, User user, Invoice invoice) {
		return acceptInvoice(domain.getName(), domain.getId(), user.getLogin(), invoice);
	}
	
	public static Invoice acceptInvoice(Domain domain, String login, Invoice invoice) {
		return acceptInvoice(domain.getName(), domain.getId(), login, invoice);
	}
	
	public static Invoice acceptInvoice(String domainName, Integer domainId, String login, Invoice invoice) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().acceptInvoice(ctx, invoice, null);
		}
	}
	
	// ----- PRODUCT - GET PRODUCT

	public static JSONArray getProducts(Domain domain, User user, ProductFilter filter) {
		return getProducts(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static JSONArray getProducts(Domain domain, String login, ProductFilter filter) {
		return getProducts(domain.getName(), domain.getId(), login, filter);
	}
	
	public static JSONArray getProducts(String domainName, Integer domainId, String login, ProductFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			Stream<Product> products = getProduct().getProductStream(ctx, filter);
			return ProductJSON.toJSON(products);
		}
	}
	
	// ----- PRODUCT - GET PRODUCT

	public static JSONArray getItems(Domain domain, User user, ItemFilter filter) {
		return getItems(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static JSONArray getItems(Domain domain, String login, ItemFilter filter) {
		return getItems(domain.getName(), domain.getId(), login, filter);
	}
	
	public static JSONArray getItems(String domainName, Integer domainId, String login, ItemFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			Stream<Item> items = getProduct().getItemStream(ctx, filter);
			return ItemJSON.toJSON(items);
		}
	}
}
