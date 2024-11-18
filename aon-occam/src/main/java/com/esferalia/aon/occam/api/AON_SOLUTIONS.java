package com.esferalia.aon.occam.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.ProductJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.LogData;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Filter.AuthAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.AuthFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
import com.esferalia.aon.occam.api.model.Filter.DailyTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainAppFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.JobTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.LocationFilter;
import com.esferalia.aon.occam.api.model.Filter.LogDataFilter;
import com.esferalia.aon.occam.api.model.Filter.NewsFilter;
import com.esferalia.aon.occam.api.model.Filter.NoteFilter;
import com.esferalia.aon.occam.api.model.Filter.NotificationFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RRelationshipFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskWorkflowFilter;
import com.esferalia.aon.occam.api.model.Filter.TimeControlFilter;
import com.esferalia.aon.occam.api.model.Filter.UserAppRoleFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthAttach;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.DailyTracking;
import com.esferalia.aon.occam.api.model.task.JobType;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskCounts;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.impl.jooq.ApiImpl;
import com.esferalia.aon.occam.impl.jooq.AttachmentImpl;
import com.esferalia.aon.occam.impl.jooq.CategoryImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
import com.esferalia.aon.occam.impl.jooq.LogDataImpl;
import com.esferalia.aon.occam.impl.jooq.NewsImpl;
import com.esferalia.aon.occam.impl.jooq.NoteImpl;
import com.esferalia.aon.occam.impl.jooq.NotificationImpl;
import com.esferalia.aon.occam.impl.jooq.Product2Impl;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.RelationshipImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;
import com.esferalia.aon.occam.impl.jooq.Task2Impl;
import com.esferalia.aon.occam.impl.jooq.TaskImpl;
import com.esferalia.aon.occam.impl.jooq.TimeControlImpl;
import com.esferalia.aon.occam.impl.jooq.dao.invoiceduplicatefix.InvoiceDuplicateFixDAO;
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
	
	private static ITask2 getTask2() {
		return new Task2Impl();
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
	
	private static IAttachment getAttachment() {
		return new AttachmentImpl();
	}
	
	private static INote getNote() {
		return new NoteImpl();
	}
	
	private static ICategory getCategory() {
		return new CategoryImpl();
	}
	
	private static INews getNews() {
		return new NewsImpl();
	}
	
	private static IRelationship getRelationship() {
		return new RelationshipImpl();
	}
	
	private static ILogData getILogData() {
		return new LogDataImpl();
	}

	public static AuthAttach getAuthAttach(Auth auth, AuthAttachFilter filter) { 
		String domainName = AONContext.getSchemaFirstDomain(auth.getSchema());
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, 0, "")){
			return getAttachment().getAuthAttach(ctx, filter, true);
		}
	}
	
	public static AuthAttach saveAuthAttach(Auth auth, AuthAttach attach) { 
		String domainName = AONContext.getSchemaFirstDomain(auth.getSchema());
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, 0, "")){
			return getAttachment().saveAuthAttach(ctx, attach);
		}
	}
	
	public static Auth getAuth(String domainName, Integer domainId, String email) { 
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){
			return getSecurity().getAuth(ctx, email);
		}
	}
	
	public static Auth getAuth(String domainName, Integer domainId, byte[] auth) { 
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, "");
			return getSecurity().getAuthByDocument(ctx, document);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static List<Auth> getAuths(AuthFilter filter) {
		LinkedList<Auth> auth = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				auth.addAll(getAuthStream(domain, 0, filter).collect(Collectors.toCollection(LinkedList::new)));
		   	}	    		
		}
		return auth;
	}
	
	public static Stream<Auth> getAuthStream(String domainName, Integer domainId, AuthFilter filter) { 
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){
			return getSecurity().getAuthStream(ctx, filter);
		}
	}
	
	public static List<Auth> getAuthsWithDevices(AuthFilter filter) {
		LinkedList<Auth> auth = new LinkedList<>();
		List<String> schemas = AONContext.getSchemas();
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				auth.addAll(getAuthStreamWithDevices(domain, 0, filter).collect(Collectors.toCollection(LinkedList::new)));
		   	}	    		
		}
		return auth;
	}
	
	public static Stream<Auth> getAuthStreamWithDevices(String domainName, Integer domainId, AuthFilter filter) { 
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){
			return getSecurity().getAuthStream(ctx, filter).map(auth -> {
				try (CloseableAONContext ctx2 = AONContext.getAONContext(domainName, domainId, "")){
					List<AuthDevice> devices = getSecurity().getAuthDevices(ctx2, f -> f.getAuthProperty().eq(auth.getAuth())); 
					AuthAttach attach = getAttachment().getAuthAttach(ctx2, f -> f.getAuthProperty().eq(auth.getAuth()), true);
					return auth.setDevices(devices).setAttach(attach);
				}
			});
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getSecurity().updateAuth(ctx, auth);
		}
	}
	
	public static Auth updateAuthPassword(Auth auth) {
		String domain = AONContext.getSchemaFirstDomain(auth.getSchema());
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")){		
			return getSecurity().updateAuthPassword(ctx, auth);
		}
	}
	
	public static Auth insertAuth(String domainName, Integer domainId, Auth auth) { 
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getCommon().insertDomain(ctx, domain, registry);
		}
	}
	
	public static void assignAuthToUser(String domainName, Integer domainId, User user, String uuid) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			byte[] auth = getSecurity().unHexUuid(ctx, uuid);
			getSecurity().assignAuthToUser(ctx, user, auth);
		}
	}
	
	public static void assignAuthToUser(String domainName, Integer domainId, User user, byte[] auth) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			getSecurity().assignAuthToUser(ctx, user, auth);
		}
	}
	
	public static Stream<DomainApp> getDomainApp(String domainName, Integer domainId, String login, DomainAppFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().getDomainAppStream(ctx, filter);
		}
	}
	
	public static boolean isOCRActive(String domainName, Integer domainId, String login) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().isOCRActive(ctx,domainId);
		}
	}

	public static DomainApp saveDomainApp(String domainName, Integer domainId, String login, DomainApp domainApp, boolean old) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().saveDomainApp(ctx, domainApp, old);
		}
	}

	public static Stream<UserAppRole> getUserAppRole(String domainName, Integer domainId, String login, UserAppRoleFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().getUserAppRoleStream(ctx, filter);
		}
	}
	
	public static UserAppRole insertUserAppRole(String domainName, Integer domainId, String login, UserAppRole userAppRole) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().insertUserAppRole(ctx, userAppRole);
		}
	}
	
	public static UserAppRole updateUserAppRole(String domainName, Integer domainId, String login, UserAppRole userAppRole) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().updateUserAppRole(ctx, userAppRole);
		}
	}
	
	public static UserAppRole deleteUserAppRole(String domainName, Integer domainId, String login, UserAppRoleFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){		
			return getSecurity().deleteUserAppRole(ctx, filter);
		}
	}
	
	public static User getUser(Domain domain, String token) {
		AonToken aonToken = SECURITY.getAonToken(token);
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "")){		
			return getSecurity().getUser(ctx, f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
					.and(f.getAuthProperty().eq(aonToken.getAuth()).or(f.getLoginProperty().eq(aonToken.getUuid()))));
		}
	}
	
	public static LinkedList<User> getUsersByEmail(String domainName, Integer domainId, String email) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){		
			return getSecurity().getUsersByEmail(ctx, email);
		}
	}
	
	public static String getUserPassword(String domainName, Integer domainId, Integer user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, "")){
			return getSecurity().getUserPassword(ctx, user);
		}
	}
	
	public static User getUserUuid(Domain domain, String uuid) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "")){		
			byte[] auth = getSecurity().unHexUuid(ctx, uuid);
			return getSecurity().getUser(ctx, f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))).and(f.getAuthProperty().eq(auth)));
		}
	}
	
	public static Stream<AonCompany> getCompanyStream(String token, String schema, Integer page, Integer perPage) {	
		AonToken aonToken = SECURITY.getAonToken(token);
		Stream<AonCompany> stream = new LinkedList<AonCompany>().stream();
		String domain = AONContext.getSchemaFirstDomain(schema);
		if(!AonStringUtils.isBlank(domain)) {
			try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")) {
				Stream<AonCompany> s = getRegistry().getCompanyStream(ctx, aonToken.getAuth(), page, perPage);
				stream = Stream.concat(stream, s);
			}
		} 
		return stream;
	}
	
	public static List<AonCompany> getCompanyBySchemaStream(String token, CompanyFilter filter, Integer page, Integer perPage) {	
		AonToken aonToken = SECURITY.getAonToken(token);
		List<AonCompany> list = new ArrayList<>();

		for(String schema: AONContext.getSchemas()) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")) {
					getRegistry().getCompanyStream(ctx, aonToken.getAuth(), filter, page, perPage)
					.forEach(c-> list.add(c.setSchema(schema)));
				}
			} 
		}
		
		return list;
	}
	
	public static Domain getDomain(String token, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(token)){
			return getCommon().getDomain(ctx, domainId);
		}
	}
	
	public static void saveDomainMaxDefinedUser(String domainName, Integer domainId, String login, Integer maxDefinedUser) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			getSecurity().saveDomainMaxDefinedUser(ctx, maxDefinedUser);
		}
	}
	
	// INVOICE
	
	public static Stream<Invoice> getInvoices(String domainName, Integer domainId, String login, InvoiceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getInvoices(ctx, filter);
		} 
	}
	
	public static Integer getInvoicesCount(String domainName, Integer domainId, String login, InvoiceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getInvoicesCount(ctx, filter);
		} 
	}
	
	public static Stream<InvoiceNewPortal> getInvoiceNewPortal(String domainName, Integer domainId, String login, InvoiceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getInvoiceNewPortal(ctx, filter);
		} 
	}
	
	public static Date getInvoiceExpDate(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getInvoiceExpDate(ctx, id);
		} 
	}

	public static TaskHolder getTaskHolder(AonToken aonToken) {
		TaskHolder taskHolder = null;
		try (CloseableAONContext ctx = AONContext.getAONContext(aonToken.getSchemaFirstDomain(),0, "")){
			taskHolder = getTask().getTaskHolderStream(ctx, aonToken.getAuth()).findFirst().orElse(null);
		}
		if(taskHolder == null) {
			List<String> schemas = AONContext.getSchemas();
			for(String schema: schemas) {
				if(taskHolder == null) {
					String domain = AONContext.getSchemaFirstDomain(schema);
					try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")){
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
				try (CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")){
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlStream(ctx, startDate, endDate);
		}
	}
	
	public static Stream<TimeControlDetail> getTimeControlHistoric(Domain domain, String login, TimeControlFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlHistoric(ctx, filter);
		}
	}
	
	public static Stream<TimeControl> getTaskHolderTimeControlStream(Domain domain, String login, Integer taskHolderId, Date startDate, Date endDate, TimeControlGroup group) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTaskHolderTimeControlStream(ctx, taskHolderId, startDate, endDate, group);
		}
	}
	
	public static TimeControl getTaskHolderTimeControl(Domain domain, String login, Integer taskHolderId, Date startDate, Date endDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTaskHolderTimeControl(ctx, taskHolderId, startDate, endDate);
		}
	}
	
	public static Stream<TimeControlDetail> getTimeControlDetailStream(Domain domain, String login, TimeControlFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlDetailStream(ctx, filter);
		}
	}
	
	public static LinkedList<TimeControlDetail> getTimeControlDetailList(Domain domain, String login, TimeControlFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getTimeControlDetailList(ctx, filter);
		}
	}
	
	public static TimeControlDetail saveTimeControlDetail(Domain domain, String login, TimeControlDetail tcd) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().saveTimeControlDetail(ctx, tcd);
		}
	}
	public static void deleteTimeControlDetail(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getTimeControl().deleteTimeControlDetail(ctx, id);
		}
	}
	
	public static Location saveLocation(Domain domain, String login, Location lc) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().saveLocation(ctx, lc);
		}
	}
	
	public static Location getLocation(Domain domain, String login, LocationFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getLocation(ctx, filter);
		}
	}
	public static Location getLocationByCoordinates(Domain domain, String login, Coordinates c) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getLocationByCoordinates(ctx, c);
		}
	}
	
	public static Stream<Location> getLocationStream(Domain domain, String login, LocationFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getTimeControl().getLocationStream(ctx, filter);
		}
	}
	
	public static void deleteLocation(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getTimeControl().deleteLocation(ctx, id);
		}
	}
	
	
	//----------------NOTE
	
	public static Note getNote(Domain domain, String login, NoteFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNote().getNote(ctx, filter);
		}
	}
	
	public static Stream<Note> getNoteStream(Domain domain, String login, NoteFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNote().getNoteStream(ctx, filter);
		}
	}
	
	public static Note saveNote(Domain domain, String login, Note note) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNote().saveNote(ctx, note);
		}
	}
	
	public static void deleteNote(Domain domain, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getNote().deleteNote(ctx, id);
		}
	}
	
	public static void deleteNoteTag(Domain domain, String login, NoteFilter filter){
		try(CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)) {
			getNote().deleteNoteTag(ctx, filter);
		}
	}
	
	public static void updateNoteTag(Domain domain, String login, String noteTag, NoteFilter filter){
		try(CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)) {
			getNote().updateNoteTag(ctx, noteTag, filter);
		}
	}
	
	public static HashMap<String, Integer> getNoteCountForDate(Domain domain, String login, NoteFilter filter, Date date, Integer userId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNote().getNoteCountForDate(ctx, filter, date, userId);
		}
	}
	
	//----- END NOTE
	
	//----------------CATEGORY
	public static Category getCategory(Domain domain, User user, CategoryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getCategory().getCategory(ctx, filter);
		}
	}
	
	public static Stream<Category> getCategoryStream(Domain domain, User user, CategoryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getCategory().getCategoryStream(ctx, filter);
		}
	}
	
	public static Stream<Category> getCategoryStream(Domain domain, User user, CategoryFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getCategory().getCategoryStream(ctx, filter, page, perPage);
		}
	}

	public static Category saveCategory(Domain domain, User user, Category category) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getCategory().saveCategory(ctx, category);
		}
	}
	
	public static void deleteCategory(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getCategory().deleteCategory(ctx, id);
		}
	}
	// ---END CATEGORY
	
	//----------------NEWS
	public static News getNews(Domain domain, User user, NewsFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getNews().getNews(ctx, filter);
		}
	}
	
	public static Stream<News> getNewsStream(Domain domain, User user, NewsFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getNews().getNewsStream(ctx, filter);
		}
	}
	
	public static Stream<News> getNewsStream(Domain domain, User user, NewsFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getNews().getNewsStream(ctx, filter, page, perPage);
		}
	}

	public static News saveNews(Domain domain, User user, News news) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getNews().saveNews(ctx, news);
		}
	}
	
	public static void deleteNews(Domain domain, User user, News news) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getNews().deleteNews(ctx, news);
		}
	}
	// ---END NEWS
	
	@Deprecated
	public static void saveUserFinancePortal(Domain domain, String login, Integer userId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getSecurity().saveUserFinancePortal(ctx, userId);
		}
	}
	
	public static Stream<Registry> getSuggestionRegistries(Domain domain, String login, LinkedList<RegistryType> list, RegistryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getRegistry().getSuggestionRegistries(ctx, list, filter);
		}
	}
	
	public static Stream<Registry> getGlobalSuggestionRegistries(Domain domain, String login, RegistryFilter filter) {
		return getRegistry().getGlobalSuggestionRegistries(filter);	
	}
	
	public static Notification getNotification(Domain domain, String login, NotificationFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNotification().getNotification(ctx, filter);
		}
	}
	
	
//	public static Stream<Notification> getNotificationStream(Auth auth) {
//		return getNotificationStream(f->f.getAuthProperty().eq(auth.getAuth()).or(f.getSenderProperty().eq(auth.getAuth())), 1, 10);
//	}
	
	public static Stream<Notification> getNotificationStream(NotificationFilter filter, Integer page, Integer perPage) {
		List<String> schemas = AONContext.getSchemas();
		LinkedList<Notification> list = new LinkedList<>();
	
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				try {
					getNotificationStream(domain, 0, "", filter, page, perPage)
					.forEach(list::add);
					
					if(list.size() == perPage) {
						break;
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			
		}
		return list.stream();
	}
	
	public static Stream<Notification> getNotificationStream(String domainName, Integer domainId, String login, NotificationFilter filter, Integer page, Integer peerPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getNotification().getNotificationStream(ctx, filter, page, peerPage);
		}
	}
	
	public static Stream<Notification> getNotificationStream(Integer domainId, String domainName, String login, NotificationFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getNotification().getNotificationStream(ctx, filter);
		}
	}
	
	public static Notification saveNotification(Domain domain, String login, Notification nt) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getNotification().saveNotification(ctx, nt);
		}
	}
	
	public static void deleteNotification(Domain domain, String login, Notification nt) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getNotification().deleteNotification(ctx, nt);
		}
	}
	
	public static void markReadNotification(Domain domain, String login, NotificationFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getNotification().markReadNotification(ctx, filter);
		}
	}
	
	public static Integer getTotalNotification(NotificationFilter filter) {
		List<String> schemas = AONContext.getSchemas();
		Integer total = 0;
		for(String schema: schemas) {
			String domain = AONContext.getSchemaFirstDomain(schema);
			if(!AonStringUtils.isBlank(domain)) {
				try(CloseableAONContext ctx = AONContext.getAONContext(domain, 0, "")) {
					total += getNotification().getTotalNotification(ctx, filter);
				} 
			}
		}
		return total;
	}
	
	
	// ----- INVOICE - ACCEPT INVOICE
	
	public static JSONObject getInvoiceJSON(Domain domain, User user, Integer id) {
		return getInvoiceJSON(domain.getName(), domain.getId(), user.getLogin(), id);
	}
	
	public static JSONObject getInvoiceJSON(Domain domain, String login, Integer id) {
		return getInvoiceJSON(domain.getName(), domain.getId(), login, id);		
	}

	public static JSONObject getInvoiceJSON(String domainName, Integer domainId, String login, Integer id) {
		Invoice invoice = getInvoice(domainName, domainId, login, id);
		return InvoiceJSON.toJSON(invoice);
	}
	
	public static Invoice getInvoice(String domainName, Integer domainId, String login, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			Invoice invoice = getFinance().getFullInvoice(ctx, id);
			return invoice;
		}
	}
	
	public static JSONObject acceptInvoice(Domain domain, User user, JSONObject json) {
		return acceptInvoice(domain.getName(), domain.getId(), user.getLogin(), json);
	}
	
	public static JSONObject acceptInvoice(Domain domain, String login, JSONObject json) {
		return acceptInvoice(domain.getName(), domain.getId(), login, json);
	}
	
	public static JSONObject acceptInvoice(String domainName, Integer domainId, String login, JSONObject json) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().acceptInvoice(ctx, invoice, invoice.getId());
		}
	}
	
	public static Invoice validateInvoice(Domain domain, User user, Invoice invoice) {
		return validateInvoice(domain.getName(), domain.getId(), user.getLogin(), invoice);
	}
	
	public static Invoice validateInvoice(Domain domain, String login, Invoice invoice) {
		return validateInvoice(domain.getName(), domain.getId(), login, invoice);
	}
	
	public static Invoice validateInvoice(String domainName, Integer domainId, String login, Invoice invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().validateInvoice(ctx, invoice, invoice.getId());
		}
	}
	
	// ----- PRODUCT - GET PRODUCT

	public static JSONArray getProducts(Domain domain, User user, ProductFilter filter) {
		return getProducts(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static JSONArray getProducts(Domain domain, String login, ProductFilter filter) {
		return getProducts(domain.getName(), domain.getId(), login, filter);
	}
	
	public static Stream<Product> getProducts(Domain domain, User user, ProductFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())){
			return getProduct().getProductStream(ctx, filter, page, perPage);
		}
	}
	
	public static JSONArray getProducts(String domainName, Integer domainId, String login, ProductFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return ItemJSON.toJSON(getProduct().getItemStream(ctx, filter));
		}
	}
	
	public static Stream<Item> getItems(Domain domain, User user, ItemFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProduct().getItemStream(ctx, filter, page, perPage);
		}
	}
	
	public static Stream<Item> getRItemStream(Domain domain, User user, ItemFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getProduct().getRItemStream(ctx, filter);
		}
	}
	
	public static PrintInvoiceConfiguration getPrintInvoiceConfiguration(Domain domain, User user, Boolean withData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getFinance().getPrintInvoiceConfiguration(ctx, withData);
		}
	}
	
	public static PrintInvoiceConfiguration getPrintInvoiceConfiguration(String domainName, Integer domainId, String login, Boolean withData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getFinance().getPrintInvoiceConfiguration(ctx, withData);
		}
	}
	
	public static PrintInvoiceConfiguration savePrintInvoiceConfiguration(Domain domain, User user, PrintInvoiceConfiguration pic) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getFinance().savePrintInvoiceConfiguration(ctx, pic);
		}
	}

	// TASK
	public static Task getTask(Domain domain, User user, TaskFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTask(ctx, filter);
		}
	}
	
	public static Task getTaskAndChilds(Domain domain, User user, TaskFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskAndChilds(ctx, filter);
		}
	}
	
	public static Map<String, Integer> getTaskCount(Domain domain, User user, TaskFilter sender, TaskFilter receiver) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskCount(ctx, sender, receiver);
		}
	}
	
	public static TaskCounts getTaskGeneralCount(Domain domain, User user, Optional<TaskFilter> status, Optional<TaskFilter> workgroup,  Optional<TaskFilter> tags) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskGeneralCount(ctx, status, workgroup, tags);
		}
	}
	
	public static Integer getTaskCountFilter(Domain domain, User user, TaskFilter taskFilter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskCountFilter(ctx, taskFilter);
		}
	}
	
	public static Stream<Task> getTaskStream(Domain domain, User user, TaskFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskStream(ctx, filter);
		}
	}
	
	public static Stream<Task> getTaskStream(Domain domain, User user, TaskFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskStream(ctx, filter, page, perPage);
		}
	}
	
	public static Stream<Task> getTaskParentOrChildStream(Domain domain, User user, TaskFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskParentOrChildStream(ctx, filter, page, perPage);
		}
	}
	
	public static Stream<Task> getTaskParentOrChildStream(Domain domain, User user, TaskFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskParentOrChildStream(ctx, filter);
		}
	}
	
	public static Stream<Task> getTaskAndChildsStream(Domain domain, User user, TaskFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskAndChildsStream(ctx, filter);
		}
	}
	
	public static Stream<Task> getTaskAndChildsStream(Domain domain, User user, TaskFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskAndChildsStream(ctx, filter, page, perPage);
		}
	}
	
	public static Task saveTask(Domain domain, User user, Task task) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().saveTask(ctx, task);
		}
	}
	
	public static void deleteTask(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getTask2().deleteTask(ctx, id);
		}
	}
	
	//----------------------- TASKWORKFLOW
	public static TaskWorkflow getTaskWorkflow(Domain domain, User user, TaskWorkflowFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskWorkflow(ctx, filter);
		}
	}
	
	public static Stream<TaskWorkflow> getTaskWorkflowStream(Domain domain, User user, TaskWorkflowFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskWorkflowStream(ctx, filter);
		}
	}
	
	public static void updateTaskWorkflowBetween(Domain domain, User user, TaskWorkflowFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			getTask2().updateTaskWorkflowBetween(ctx, filter);
		}
	}

	public static List<TaskWorkflow> getTaskWorkflowList(Domain domain, User user, TaskWorkflowFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskWorkflowList(ctx, filter);
		}
	}
	
	public static List<TaskWorkflow> getTaskWorkflowList(Domain domain, User user, TaskWorkflowFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskWorkflowList(ctx, filter, page, perPage);
		}
	}
	
	public static TaskWorkflow saveTaskWorkflow(Domain domain, User user, TaskWorkflow workflow) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().saveTaskWorkflow(ctx, workflow);
		}
	}
	
	public static void deleteTaskWorkflow(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){		
			getTask2().deleteTaskWorkflow(ctx, id);
		}
	}
	
	//-------------------- TASKATTACH
	public static TaskAttach getTaskAttach(Domain domain, User user, TaskAttachFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskAttach(ctx, filter);
		}
	}
	
	public static Stream<TaskAttach> getTaskAttachStream(Domain domain, User user, TaskAttachFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskAttachStream(ctx, filter);
		}
	}

	public static LinkedList<TaskAttach> getTaskAttachList(Domain domain, User user, TaskAttachFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getTaskAttachList(ctx, filter);
		}
	}
	
	public static TaskAttach saveTaskAttach(Domain domain, User user, TaskAttach task) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().saveTaskAttach(ctx, task);
		}
	}
	
	public static void deleteTaskAttach(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){		
			getTask2().deleteTaskAttach(ctx, id);
		}
	}
	
	//------ DAILY_TRACKING-------
	public static DailyTracking getDailyTracking(Domain domain, User user, DailyTrackingFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getDailyTracking(ctx, filter);
		}
	}
	
	public static Stream<DailyTracking> getDailyTrackingStream(Domain domain, User user, DailyTrackingFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getDailyTrackingStream(ctx, filter);
		}
	}
	
	public static Stream<DailyTracking> getDailyTrackingStream(Domain domain, User user, DailyTrackingFilter filter, Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getDailyTrackingStream(ctx, filter, page, perPage);
		}
	}

	public static DailyTracking saveDailyTracking(Domain domain, User user, DailyTracking dailyTracking) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().saveDailyTracking(ctx, dailyTracking);
		}
	}
	
	public static void deleteDailyTracking(Domain domain, User user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){		
			getTask2().deleteDailyTracking(ctx, id);
		}
	}
	
	//----JOB_TYPE
	
	public static Stream<JobType> getJobTypeStream(Domain domain, User user, JobTypeFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getTask2().getJobTypeStream(ctx, filter);
		}
	}
	
	//------ RELATIONSHIP -------
	public static Optional<RegistryRelationship> getRegistryRelationship(Domain domain, User user, RRelationshipFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getRelationship().getRegistryRelationship(ctx, filter);
		}
	}
	
	public static Stream<RegistryRelationship> getRegistryRelationshipStream(Domain domain, User user, RRelationshipFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getRelationship().getRegistryRelationshipStream(ctx, filter);
		}
	}

	public static RegistryRelationship saveRegistryRelationship(Domain domain, User user, RegistryRelationship rrelationship) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			return getRelationship().saveRegistryRelationship(ctx, rrelationship);
		}
	}
	
	public static void deleteRegistryRelationship(Domain domain, User user, RRelationshipFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){		
			getRelationship().deletetRegistryRelationship(ctx, filter);
		}
	}
	
	// COBROS Y PAGOS CARD

	public static Double getFinanceGroupStatus(Domain domain, User user, FinanceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){		
			return getFinance().getFinanceGroupStatus(ctx, filter);
		}
	}
	
	public static void invoiceDuplicateFix(Domain domain, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){		
			InvoiceDuplicateFixDAO.invoiceDuplicateFix(ctx);
			InvoiceDuplicateFixDAO.invoiceIrpfDuplicateFix(ctx);
		}
	}
	
	public static void insertLogData(String domainName,Integer domainId, String login, LogData data) {
		try {
			CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login);
			getILogData().insertLogData(ctx, data);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void insertLogData(Occam occam, LogData data) {
		try {
			CloseableAONContext ctx = AONContext.getAONContext(occam.getDomainName(),occam.getDomain(), occam.getUser());
			getILogData().insertLogData(ctx, data);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static LogData getLogData(String domainName,Integer domainId, String login, LogDataFilter filter) {
		try {
			CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login);
			return getILogData().getLogData(ctx, filter);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void deleteLogData(String domainName,Integer domainId, String login, LogDataFilter filter) {
		try {
			CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login);
			getILogData().deleteLogData(ctx, filter);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static Stream<LogData> getLogsStream(String domainName, Integer domainId, String login){
		try {
			CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login);
			return getILogData().getLogsTream(ctx);
		} catch (Exception e) {
			throw e;
		}
	}
}
