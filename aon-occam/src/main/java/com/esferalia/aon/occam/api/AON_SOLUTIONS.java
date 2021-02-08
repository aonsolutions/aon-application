package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainAppFilter;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.Filter.UserAppRoleFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.ApiImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
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
		return insertDomain(domainName, 0, domain,  registry);
	}

	public static Domain insertDomain(String domainName, Integer domainId, Domain domain, Registry registry) throws Exception{ 
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

	public static DomainApp saveDomainApp(String domainName, Integer domainId, String login, DomainApp domainApp) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().saveDomainApp(ctx, domainApp);
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
}
