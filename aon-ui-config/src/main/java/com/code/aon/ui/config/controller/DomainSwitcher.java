package com.code.aon.ui.config.controller;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Rawdoc.RAWDOC;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.io.Serializable;
import java.net.IDN;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.AbstractDomainSwitcher;
import com.code.aon.common.domain.DomainEvent;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomainChangeListener;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.DomainData;
import com.code.aon.ui.form.ITemplateController;
import com.code.aon.ui.resources.bean.CustomizeController;
import com.code.aon.ui.resources.bean.ResourceResolver;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DomainSwitcher extends AbstractDomainSwitcher implements
		ITemplateController, Serializable {

	private static final Integer COUNT_LIMIT = 500;

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String TOOLBAR_LOGO_DEFAULT =
	new ResourceResolver().getResolve().get( "/images/aon-icon/aon-icon-logo.svg");

	private final static Logger LOGGER = LoggerFactory
			.getLogger(DomainSwitcher.class);
	private List<IDomainChangeListener> listenerClasses;
	private DataModel model;
	private DataModel filteredModel;
	private Integer parentDomain;
	private String domainName;
	private String domainNameURL;
	private String domainDocument;
	private String filter;
	private String modelFilter;
	private String domainURL;
	private int page;
	private Integer pageLimit;
	private String beanName;

	// By domain itself state
	private boolean showActive;
	private boolean showInactive;
	private boolean showExpired;
	private boolean showOffice;
	private boolean showShared;
	
	// By it's invoices state
	private boolean showReject;
	private boolean showPending;
	private boolean showUnaccount;
	
	private Integer reject;
	private Integer pending;
	private Integer unaccount;

	public DomainSwitcher() {
		try {
			//setPageLimit(100);
			setShowActive(true);
			super.setDomainId(initializeDomain());
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error( "** Unknown exception " + th.getMessage());
			throw th;
		}
	}

	private Integer initializeDomain() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		Integer domain = principal.getDomainId();
		String sessionFactoryName = HibernateUtil
				.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.parent FROM domain d WHERE d.id = " + domain;
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName)
				.createSQLQuery(q);
		List<?> queryList = query.addScalar("parent", Hibernate.INTEGER).list();
		Iterator<?> iterator = queryList.iterator();
		if (iterator.hasNext()) {
			Integer pd = (Integer) iterator.next();
			setParentDomain(pd == null ? domain : null);
		} else {
			domain = null;
		}
		HibernateUtil.closeSession(sessionFactoryName, false);
		return domain;
	}

	public List<IDomainChangeListener> getListenerClasses() {
		return listenerClasses;
	}

	public void setListenerClasses(List<IDomainChangeListener> listenerClasses) {
		this.listenerClasses = listenerClasses;
		DomainEvent event = new DomainEvent(this, null, getDomainId());
		for (IDomainChangeListener listener : this.listenerClasses) {
			addDomainChangeListener(listener);
			listener.afterDomainChanged(event);
		}
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getDomainName() {
		if (domainName == null) {
			assignDomainName(getDomainId());
		}
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainNameURL() {
		if (domainNameURL == null) {
			assignDomainNameURL(getDomainId());
		}
		return domainNameURL;
	}

	public void setDomainDocument(String domainDocument) {
		this.domainDocument = domainDocument;
	}

	public String getDomainDocument() {
		if (domainDocument == null) {
			assignDomainDocument(getDomainId());
		}
		return domainDocument;
	}

	public String getCurrentDomainNameURL() {
		assignDomainNameURL(getDomainId());
		return domainNameURL;
	}

	public void setDomainNameURL(String domainNameURL) {
		this.domainNameURL = domainNameURL;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public DataModel getFilteredModel() {
		return filteredModel;
	}

	public void setFilteredModel(DataModel filteredModel) {
		this.filteredModel = filteredModel;
	}

	public Integer getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(Integer parentDomain) {
		this.parentDomain = parentDomain;
	}

	public boolean isChildDomain() {
		return !isParentDomain();
	}

	public boolean isParentDomainUserInChildDomain() {
		return DomainManager.isParentDomainUserInChildDomain();
	}

	public boolean isAonSolutionsOrg() {
		return AonStringUtils.endsWith(getDomainNameURL(),"aonsolutions.org");
	}

	public DataModel getModel() {

		if (model == null) {
			initializeModel();
		}
		if (StringUtils.isBlank(getFilter())) {
			return model;

		} else {
			if (!StringUtils.equals(modelFilter, filter)
					|| filteredModel == null) {
				setPage(1);
				List<DomainData> filteredList = new LinkedList<DomainData>();
				@SuppressWarnings("unchecked")
				List<DomainData> list = (List<DomainData>) model
						.getWrappedData();
				for (DomainData d : list) {
					if (AonStringUtils.containsMatching(d.getName(), getFilter())
							|| AonStringUtils.containsMatching(
									d.getDescription(), getFilter())
							|| StringUtils.containsIgnoreCase(
									d.getDocument(), getFilter())
							|| StringUtils.containsIgnoreCase(
									d.getCccs(), getFilter())
							) {
						filteredList.add(d);
						if ( filteredList.size() == pageLimit )
							break;
					}
				}
				
				
				setFilteredModel(new SerializableListDataModel(filteredList));
				modelFilter = filter;
			}
			return filteredModel;
		}
	}

	private List<Integer> getUserScopes() {
		List<Integer> scopes = null;
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		try (CloseableAONContext ctx = AONContext.getAONContext(getDomainNameURL(), domainId, getCurrentUser())) {
			scopes = ctx
					.getDslContext()
					.select(USER_SCOPE.SCOPE)
					.from(USER_SCOPE).where(USER_SCOPE.USER_ID.eq(principal.getUserId()))
					.fetch().into(Integer.class);
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		}						
		return scopes;
	}
	
	
	private <T extends SelectJoinStep<?>> T filterByInvoiceStatus(T selectJoinStep ) {
		if ( isShowPending() ) {
			selectJoinStep = filterByInvoiceStatus(selectJoinStep, RawdocStatus.INBOX);
		}
		if ( isShowReject() ) {
			selectJoinStep = filterByInvoiceStatus(selectJoinStep, RawdocStatus.REJECTED);
		}
		if ( isShowUnaccount() ) {
			selectJoinStep = filterByInvoiceStatus(selectJoinStep, InvoiceStatus.PENDING);
		}
		return selectJoinStep;		
	}

	private <T extends SelectJoinStep<?>> T filterByInvoiceStatus(T selectJoinStep, RawdocStatus rawdocStatus) {
		
		Table<?> subTable = 
		DSL
		.select(RAWDOC.DOMAIN
		,DSL.count(RAWDOC.ID).as(rawdocStatus.name()))
		.from(RAWDOC)
		.innerJoin(DOMAIN).on(RAWDOC.DOMAIN.eq(DOMAIN.ID))
		.where(RAWDOC.STATUS.eq((byte)rawdocStatus.ordinal()).and(getDomainCondition()))
		.groupBy(RAWDOC.DOMAIN)
		.asTable(rawdocStatus.name());
		
		return (T) selectJoinStep.innerJoin(subTable).on(subTable.field(0, Integer.class).eq(DOMAIN.ID));
	}

	private <T extends SelectJoinStep<?>> T filterByInvoiceStatus(T selectJoinStep, InvoiceStatus invoiceStatus) {

		Table<?> subTable = 
		DSL
		.select(
		INVOICE.DOMAIN
		,DSL.count(INVOICE.ID).as(invoiceStatus.name()))
		.from(INVOICE)
		.innerJoin(DOMAIN).on(INVOICE.DOMAIN.eq(DOMAIN.ID))
		.where(INVOICE.STATUS.eq((byte)InvoiceStatus.PENDING.ordinal()).and(getDomainCondition()))
		.groupBy(INVOICE.DOMAIN)
		.asTable(invoiceStatus.name());
		
		return (T) selectJoinStep.innerJoin(subTable).on(subTable.field(0, Integer.class).eq(DOMAIN.ID));
	}

	private Condition getDomainCondition() {
		return getDomainCondition(getParentDomain(), isShowInactive(), isShowExpired());		
	}

	public Condition getDomainCondition( Integer parentDomain, boolean showInactive, boolean showExpired ) {
		Condition condition = DOMAIN.PARENT.eq(parentDomain);
		if (! showExpired ) {
			Condition expirationCondition = DOMAIN.EXPIRATIONDATE.isNull().or(
					DOMAIN.EXPIRATIONDATE.gt(DSL.currentDate()));
			condition = condition.and(expirationCondition);
		}
		if (showInactive) {
			condition = condition.and(DOMAIN.ACTIVE.eq((byte) 0));
		} 
		
		if ( showActive ){
			condition = condition.and(DOMAIN.ACTIVE.eq((byte) 1));
		}
		
		if ( showOffice ) {
			condition = condition.and(DOMAIN.TYPE.eq((byte)DomainType.OFFICE.ordinal()));
		}
		
		if ( showShared ) {
			condition = condition.and( DSL.falseCondition());
		}

		if (!isAdminDomain()) {
			Condition scopeCondition = DOMAIN.SCOPE.isNull();
			List<Integer> scopes = getUserScopes();
			if (scopes != null && !scopes.isEmpty()) {
				scopeCondition = scopeCondition.or(DOMAIN.SCOPE.in(scopes));
			}
			condition = condition.and(scopeCondition);
		}
		return condition;
	}

	private void initializeModel() {
		
		if (getParentDomain() != null) {
			
			LinkedList<DomainData> domains = new LinkedList<DomainData>();
			try ( CloseableAONContext ctx = AONContext.getAONContext(getDomainNameURL(),domainId,getCurrentUser())) {
				SelectOnConditionStep<Record> domainsSelect = 
						ctx
						.getDslContext()
						.select()
						.from(DOMAIN)
						.leftOuterJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID))
						.leftOuterJoin(REGISTRY).on(ENTERPRISE.REGISTRY.eq(REGISTRY.ID))
						.leftOuterJoin(ENTERPRISE_CCC).on(ENTERPRISE_CCC.DOMAIN.eq(DOMAIN.ID));
						
				domainsSelect = filterByInvoiceStatus(domainsSelect);
				
				domainsSelect
				.where(getDomainCondition())
				.orderBy(DOMAIN.DESCRIPTION)
				.fetchStream()
				.forEach( r -> {
					DomainData last = domains.peekLast();
					if ( last != null && last.getId().equals(r.get(DOMAIN.ID)) ) {
						String ccc = r.get(ENTERPRISE_CCC.CCC);
						if ( StringUtils.isNotBlank(ccc) ) 
							last.addCCC(ccc);
						return;
					}
					DomainData domainData = 
							new DomainData(
							r.get(DOMAIN.ID), 
							r.get(DOMAIN.NAME), 
							r.get(DOMAIN.DESCRIPTION), 
							r.get(DOMAIN.EXPIRATIONDATE), 
							r.get(DOMAIN.ACTIVE) == 1, 
							r.get(DOMAIN.ENABLEHEREDITY) == 1,
							getSafeDomainType(r.get(DOMAIN.TYPE)));
					
					String ccc = r.get(ENTERPRISE_CCC.CCC);
					if ( StringUtils.isNotBlank(ccc) ) 
						domainData.addCCC(ccc);
					
					domainData.setDocument(r.get(REGISTRY.DOCUMENT));
					
					
					domainData.setLogo(TOOLBAR_LOGO_DEFAULT);

//							byte logo [] = r.get(RATTACH.DATA);
//							if ( logo == null || ArrayUtils.isEmpty(logo) ) {
//								domainData.setLogo(TOOLBAR_LOGO_DEFAULT);
//							}
//							else {
//								MimeType mimeType = AonEnumUtils.enumValue(r.get(RATTACH.MIMETYPE),
//										MimeType.MIME_PNG);
//								domainData.setLogo(String.format("data:%s;base64,%s", mimeType.getName(),
//										Base64.getEncoder().encodeToString(logo)));
//							}
					for ( RawdocStatus status: RawdocStatus.values() ) {
						Field<Integer> statusField = r.field(status.name(), Integer.class);
						if ( statusField != null ) {
							domainData.setRawdocCount(status, r.get(statusField));
						}
					}
					for ( InvoiceStatus status: InvoiceStatus.values() ) {
						Field<Integer> statusField = r.field(status.name(), Integer.class);
						if ( statusField != null ) {
							domainData.setInvoiceCount(status, r.get(status.name(), Integer.class));
						}
					}
					
					domains.add(domainData);
					
				})			
				;
			}
			setModel(new SerializableListDataModel(domains));
		} else {
			setModel(new SerializableListDataModel(Collections.emptyList()));
		}
	}

	public void setModel(DataModel model) {
		setPage(1);
		this.model = model;
	}

	public int getDomainCount() {
		if (getParentDomain() != null) {
			int count = 0;
			try ( CloseableAONContext ctx = AONContext.getAONContext(getDomainNameURL(),domainId,getCurrentUser())) {
				count = ctx.getDslContext().selectCount().from(DOMAIN)
						.where(getDomainCondition()).fetchOne(0, int.class);
			}
			return count;
		}
		return 0;
	}

	public void onEditSearch(ActionEvent event) {
		//setModel(null);
		//setFilter(null);
		//setFilteredModel(null);
	}

	public void onUpperDomain(ActionEvent event) {
		select(AonUtil.getAuthPrincipal().getDomainId(), null);
	}

	public void select(Integer id, String name) {
		super.setDomainId(id);
		setDomainName(name);
		LOGGER.info("Domain swicthed. New domain: '{}' - '{}'", id, name);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Map<String, Object> map = ec.getSessionMap();
		for (String key : map.keySet()) {
			String className = map.get(key).getClass().getName();
			if (isRemovable(key, className)) {
				map.remove(key);
				LOGGER.debug(
						"Element removed from session: [ key: {}, value class: {} ]",
						key, className);
			}
		}
		
		setModel(null);
		setFilter(null);
		setFilteredModel(null);
		
		this.domainURL = null;
		setShowInactive(false);
		setShowExpired(false);
		System.gc();
		
		this.domainNameURL = null;
		this.domainDocument = null;
	}

	private void assignDomainName(Integer domainId) {
		String sessionFactoryName = HibernateUtil
				.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.description FROM domain d" + " WHERE d.id = "
				+ domainId;
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName)
				.createSQLQuery(q);
		String name = (String) query.addScalar("description", Hibernate.STRING)
				.uniqueResult();
		HibernateUtil.closeSession(sessionFactoryName, false);
		setDomainName(name);
	}

	private void assignDomainNameURL(Integer domainId) {
		String sessionFactoryName = HibernateUtil
				.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.name FROM domain d" + " WHERE d.id = " + domainId;
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName)
				.createSQLQuery(q);
		String name = (String) query.addScalar("name", Hibernate.STRING)
				.uniqueResult();
		HibernateUtil.closeSession(sessionFactoryName, false);
		setDomainNameURL(name);
	}

	private void assignDomainDocument(Integer domainId) {
		String sessionFactoryName = HibernateUtil
				.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT r.document FROM company c INNER JOIN registry r ON ( c.registry = r.id )  " + " WHERE c.domain = " + domainId;
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName)
				.createSQLQuery(q);
		String document = (String) query.addScalar("document", Hibernate.STRING)
				.uniqueResult();
		HibernateUtil.closeSession(sessionFactoryName, false);
		setDomainDocument(document);
	}

	private boolean isRemovable(String key, String className) {
		return (StringUtils.startsWith(className, "com.code.aon")
				&& !StringUtils.startsWith(key, "com.code.aon.audit.")
				&& !StringUtils
				.startsWith(className,
						"com.code.aon.ui.config.controller.ContractSwitcher")
				&& !StringUtils
						.startsWith(className,
								"com.code.aon.ui.audit.controller.ApplicationOptionController")
				&& !StringUtils.startsWith(className,
						"com.code.aon.ui.resources.bean.ResourceResolver")
				&& !StringUtils.startsWith(className,
						"com.code.aon.ui.common.controller.LoggedUser") && !StringUtils
					.equals(className, this.getClass().getName()))
				|| StringUtils.startsWith(className, "com.esferalia.aon")
				|| StringUtils.endsWith(key, "OptionalListeners");
	}

	public DomainType getType() {
		return DomainType.values()[this.type];
	}
	
	public boolean isHotel(){
		return getType().equals(DomainType.HOTEL);
	}
	
	public String getDomainURL() throws ManagerBeanException {
		if (domainURL == null) {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(getDomainId());
			String path = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			domainURL = "https://" + domain.getName() + path;
		}
		return domainURL;
	}

	public boolean isEnabledGoToParent() {
		if (isParentDomainUserInChildDomain() || isAdminDomain()) {
			return !ObjectUtils.equals(getDomainId(), AonUtil
					.getAuthPrincipal().getDomainId());
		}
		return false;
	}

	public boolean isAdminDomain() {
		DomainType type = getDomainType(AonUtil.getAuthPrincipal()
				.getDomainId());
		return type == DomainType.ADMIN;
	}
	
	public boolean isOfficeDomain() {
		DomainType type = getDomainType(getDomainId());
		return type == DomainType.OFFICE;
	}
	
	public DomainUserRoles getDur() {
		com.esferalia.aon.occam.api.model.Domain domain = new com.esferalia.aon.occam.api.model.Domain();
		domain.setName(getCurrentDomainNameURL()).setId(getDomainId());
		User user = AON.getUser(getCurrentDomainNameURL(), getDomainId(), getCurrentUser());
		return SECURITY.getDomainUserRoles(domain, user.getLogin(), user.getId());
	}
	
	public boolean isComunica() {
		return getDur().isComunica();
	}
	
	public boolean isConsultancyDomain() {
		DomainType type = getDomainType(AonUtil.getAuthPrincipal()
				.getDomainId());
		return type == DomainType.CONSULTANCY;
	}
	
	public boolean isSnapshot() {
		return AonUtil.getDomainName().contains("aonsolutions.org");
	}
	
	public boolean isSnapshotVersion() {
		CustomizeController customize = (CustomizeController) AonUtil.getRegisteredBean(ICommonConstants.CUSTOMIZE_CONTROLLER_NAME);
		return customize.isSnapshotVersion();
	}

	public boolean isBetaEnabled() {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.AON_BETA_ENABLED);
		return (appParam!=null && new Boolean(appParam.getValue()));
	}
	
	public boolean isBetaUser() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.AON_BETA_ENABLED, principal.getUserDomainId());
		return (appParam!=null && new Boolean(appParam.getValue()));
	}
	
	public boolean isBetaDomain() {
		return isSnapshotVersion() || isBetaUser();
	}
	
	public boolean isAccountingConsolidationEnabled() {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.ACC_CONSOLIDATION);
		return  (appParam!=null && Boolean.valueOf(appParam.getValue()));
	}
	
	public boolean isAlphaEnabled() {
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.AON_ALPHA_ENABLED);
		return (appParam!=null && new Boolean(appParam.getValue()));
	}
	
	public boolean isAlphaUser() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.AON_ALPHA_ENABLED, principal.getUserDomainId());
		return (appParam!=null && new Boolean(appParam.getValue()));
	}
	
	public boolean isAlphaDomain() {
		return isSnapshotVersion() || isAlphaUser();
	}
	
	public boolean isUdapa() {
	    com.esferalia.aon.occam.api.model.ApplicationParameter ud = AON.getApplicationParameter(getDomainNameURL(), getDomainId(), "", com.esferalia.aon.occam.api.model.type.AppParam.AON_ADHOC_EXTENSION);
	    return  ud != null && ud.getValue() != null && ud.getValue().equalsIgnoreCase("udapa");
	}
	
	public boolean isSig() {
		return getDomainNameURL().equalsIgnoreCase("sig.aonsolutions.org");
	}
	
	public boolean isCuidadosInclusivos() {
		return getDomainNameURL().contains("cuidadosinclusivos.aonsolutions.net");
	}
	
	public boolean isPaturpat() {
	    com.esferalia.aon.occam.api.model.ApplicationParameter ud = AON.getApplicationParameter(getDomainNameURL(), getDomainId(), "", com.esferalia.aon.occam.api.model.type.AppParam.AON_ADHOC_EXTENSION);
	    return  ud  != null && ud.getValue() != null && ud.getValue().equalsIgnoreCase("paturpat");
	}

	public static DomainType getDomainType(Integer domainId) {
		String sfn = HibernateUtil.getSessionFactoryName();
		Query query = HibernateUtil.getSession(sfn).createQuery(
				"SELECT d.type FROM Domain d WHERE d.id = ?");
		query.setInteger(0, domainId);
		DomainType type = (DomainType) query.uniqueResult();
		HibernateUtil.closeSession(sfn, false);
		return type;
	}

	@Override
	public Integer getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
	}
	public String getCurrentUser() {
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			return principal!=null?principal.getShortName():null;
		} catch  (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCurrentDomainURL() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			DomainData domainData = (DomainData) getModel().getRowData();
			String name = IDN.toASCII(domainData.getName());
			return name;
		}
		return null;
	}

	public String getSchema() {
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			return principal!=null?principal.getDatabaseName():null;
		} catch  (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isShowOffice() {
		return showOffice;
	}
	
	public void setShowOffice(boolean showOffice) {
		this.showOffice = showOffice;
	}
	
	public boolean isShowInactive() {
		return showInactive;
	}

	public void setShowInactive(boolean showInactive) {
		this.showInactive = showInactive;
	}
	
	public boolean isShowExpired() {
		return showExpired;
	}

	public void setShowExpired(boolean showExpired) {
		this.showExpired = showExpired;
	}
	
	public void setShowShared(boolean showShared) {
		this.showShared = showShared;
	}
	
	public boolean isShowShared() {
		return showShared;
	}
	
	public void setShowActive(boolean showActive) {
		this.showActive = showActive;
	}

	public boolean isShowActive() {
		return showActive ; 
	}
	
	public void setShowReject(boolean showReject) {
		this.showReject = showReject;
	}
	
	public boolean isShowReject() {
		return showReject;
	}
	
	public void setShowPending(boolean showPending) {
		this.showPending = showPending;
	}
	
	public boolean isShowPending() {
		return showPending;
	}
	
	public void setShowUnaccount(boolean showUnaccount) {
		this.showUnaccount = showUnaccount;
	}
	
	public boolean isShowUnaccount() {
		return showUnaccount;
	}

	public void onChangeShowInactive(ActionEvent event) {
		setModel(null);
	}

	public void onChangeShowExpired(ActionEvent event) {
		setModel(null);
	}
	
	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}
	
	public String getToken() {
		Algorithm algorithm = Algorithm.HMAC256(AonSecret.getAonSecret());
		String token =   JWT.create()
				.withIssuer("auth0")
				.withIssuedAt(new Date())
				//.withExpiresAt(AonDateUtils.addDays(new Date(), 1))
				.withSubject(String.format("{'schema':'%s', 'schema_first_domain':'%s', 'uuid':'%s'}", getSchema(), getDomainNameURL(), getCurrentUser()))
				.sign(algorithm);
		System.out.println(token);
		return token;
	}
	
	
	public void showInactive(ActionEvent event) {
		setShowShared(false);
		setShowOffice(false);
		setShowActive(false);
		
		setShowPending(false);
		setShowReject(false);
		setShowUnaccount(false);

		setShowInactive(true);
		setModel(null);
	}
	
	public void showActive(ActionEvent event) {
		setShowShared(false);
		setShowOffice(false);
		setShowInactive(false);

		setShowPending(false);
		setShowReject(false);
		setShowUnaccount(false);

		setShowActive(true);
		setModel(null);
	}

	public void showOffice(ActionEvent event) {
		setShowShared(false);
		setShowInactive(false);
		setShowActive(false);

		setShowPending(false);
		setShowReject(false);
		setShowUnaccount(false);

		setShowOffice(true);
		setModel(null);
	}

	public void showShared(ActionEvent event) {
		setShowOffice(false);
		setShowInactive(false);
		setShowActive(false);

		setShowPending(false);
		setShowReject(false);
		setShowUnaccount(false);

		setShowShared(true);
		setModel(null);
	}
	
	public void showPending(ActionEvent event) {
		setShowReject(false);
		setShowUnaccount(false);

		setShowPending(true);
		setModel(null);
	}

	public void showReject(ActionEvent event) {
		setShowPending(false);
		setShowUnaccount(false);

		setShowReject(true);
		setModel(null);
	}
	
	public void showUnaccount(ActionEvent event) {
		setShowPending(false);
		setShowReject(false);

		setShowUnaccount(true);
		setModel(null);
	}
	
	public int getSummaryCount() {
		return AonNumberUtils.zeroIfNull(this.pending) 
				+ AonNumberUtils.zeroIfNull(this.reject) 
				+ AonNumberUtils.zeroIfNull(this.unaccount);
	}
	
	public int refreshSummaryCount() {
		try (CloseableAONContext ctx = AONContext.getAONContext(getDomainNameURL(),domainId,getCurrentUser())) {
			
			this.pending = 
					ctx.getDslContext().select(DOMAIN.ID)
					.from(DOMAIN)
					.innerJoin(RAWDOC).on(DOMAIN.ID.eq(RAWDOC.DOMAIN))
					.where(getCurrentDomainCondition().and(RAWDOC.STATUS.eq((byte)RawdocStatus.INBOX.ordinal()))).limit(COUNT_LIMIT+1).fetch(DOMAIN.ID).size();
			
			this.reject = 
					ctx.getDslContext().select(DOMAIN.ID)
					.from(DOMAIN)
					.innerJoin(RAWDOC).on(DOMAIN.ID.eq(RAWDOC.DOMAIN))
					.where(getCurrentDomainCondition().and(RAWDOC.STATUS.eq((byte)RawdocStatus.REJECTED.ordinal()))).limit(COUNT_LIMIT+1).fetch(DOMAIN.ID).size();

			this.unaccount = 
					ctx.getDslContext().select(DOMAIN.ID)
					.from(DOMAIN)
					.innerJoin(INVOICE).on(DOMAIN.ID.eq(INVOICE.DOMAIN))
					.where(getCurrentDomainCondition().and(INVOICE.STATUS.eq((byte)InvoiceStatus.PENDING.ordinal()))).limit(COUNT_LIMIT+1).fetch(DOMAIN.ID).size();
			return this.pending + this.reject  + this.unaccount;
		}
	}
	

	public String getPending() {
		if ( pending == null || pending == 0 )
			return "";
		return pending > COUNT_LIMIT ? "+" + COUNT_LIMIT.toString() : Integer.toString(pending);
	}

	public String getReject() {
		if ( reject == null || reject == 0 )
			return "";
		return reject > COUNT_LIMIT  ? "+" + COUNT_LIMIT.toString() : Integer.toString(reject);
	}
	
	public String getUnaccount() {
		if ( unaccount == null || unaccount == 0 )
			return "";
		return unaccount > COUNT_LIMIT  ? "+" + COUNT_LIMIT.toString() : Integer.toString(unaccount);
	}

	public String getTrace() {
		return String.valueOf(new Date(System.currentTimeMillis()));
	}
	
	// 
	
	public String getCurrentDomainDocument() {
		return getDomainDocument();
	}

	public String getCurrentDomainName() {
		return getDomainNameURL();
	}

	public String getCurrentDomainDescription() {
		return getDomainName();
	}
	

	private Condition getCurrentDomainCondition() {
		return Objects.equals(parentDomain, domainId) ? getDomainCondition() : DOMAIN.ID.eq(getDomainId());
	}

	private static DomainType getSafeDomainType( Byte b ) {
		if (b == null) return null;
		if (b < 0 || b >= DomainType.values().length) return null;
		return DomainType.values()[b];
	}
	
}