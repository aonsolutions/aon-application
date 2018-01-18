package com.code.aon.ui.config.controller;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.io.Serializable;
import java.net.IDN;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.code.aon.common.enumeration.MimeType;
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
import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class DomainSwitcher extends AbstractDomainSwitcher implements
		ITemplateController, Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String TOOLBAR_LOGO_DEFAULT = "/images/aon-icon/aon-icon-logo.png";

	private final static Logger LOGGER = LoggerFactory
			.getLogger(DomainSwitcher.class);
	private List<IDomainChangeListener> listenerClasses;
	private DataModel model;
	private DataModel filteredModel;
	private Integer parentDomain;
	private String domainName;
	private String domainNameURL;
	private String filter;
	private String modelFilter;
	private String domainURL;
	private int page;
	private Integer pageLimit;
	private boolean showInactive;
	private boolean showExpired;
	private String beanName;

	public DomainSwitcher() {
		try {
			setPageLimit(15);
			super.setDomainId(initializeDomain());
		} catch (Throwable th) {
			super.setDomainId(1);
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
					if (StringUtils
							.containsIgnoreCase(d.getName(), getFilter())
							|| StringUtils.containsIgnoreCase(
									d.getDescription(), getFilter())) {
						filteredList.add(d);
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
		AONContext ctx = AONContext.getAONContext(getDomainNameURL(), domainId, getCurrentUser());
		try {
			scopes = ctx
					.getDslContext()
					.select(USER_SCOPE.SCOPE)
					.from(USER_SCOPE).where(USER_SCOPE.USER_ID.eq(principal.getUserId()))
					.fetch().into(Integer.class);
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.finalize();	
		}						
		return scopes;
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
		if (!showInactive) {
			condition = condition.and(DOMAIN.ACTIVE.eq((byte) 1));
		}
		if (!isAdminDomain()) {
			Condition scopeCondition = DOMAIN.SCOPE.isNull();
			List<Integer> scopes = getUserScopes();
			if (!scopes.isEmpty()) {
				scopeCondition = scopeCondition.or(DOMAIN.SCOPE.in(scopes));
			}
			condition = condition.and(scopeCondition);
		}
		return condition;
	}

	private void fillDomainData(AONContext ctx, DomainData data) {
		Record2<byte[], Byte> logo = ctx
				.getDslContext()
				.select(RATTACH.DATA, RATTACH.MIMETYPE)
				.from(APP_PARAM)
				.leftOuterJoin(RATTACH)
				.on(DSL.cast(APP_PARAM.VALUE, Integer.class).eq(RATTACH.REGISTRY))
				.where(APP_PARAM.DOMAIN.eq(data.getId())
						.and(APP_PARAM.NAME.eq(AppParam.AON_CUSTOMIZE_ID
								.getValue())))
				.and(RATTACH.DESCRIPTION.eq(ICommonConstants.TOOLBAR_LOGO_NAME))
				.fetchOne();
		

		if (logo == null || AonArrayUtils.isEmpty(logo.value1())) {
			ResourceResolver resolver = new ResourceResolver();
			data.setLogo(resolver.getResolve().get(TOOLBAR_LOGO_DEFAULT));
		} else {
			MimeType mimeType = AonEnumUtils.enumValue(logo.value2(),
					MimeType.MIME_PNG);
			data.setLogo(String.format("data:%s;base64,%s", mimeType.getName(),
					Base64.getEncoder().encodeToString(logo.value1())));
		}

	}

	private void initializeModel() {
		List<DomainData> domains = Collections.emptyList();
		if (getParentDomain() != null) {
			AONContext ctx = AONContext.getAONContext(getDomainNameURL(),domainId,getCurrentUser());
			domains = ctx
					.getDslContext()
					.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION,
							DOMAIN.EXPIRATIONDATE, DOMAIN.ACTIVE, DOMAIN.ENABLEHEREDITY)
					.from(DOMAIN).where(getDomainCondition())
					.orderBy(DOMAIN.DESCRIPTION).fetch().into(DomainData.class);

			for (DomainData data : domains) {
				fillDomainData(ctx, data);
			}
			ctx.finalize();
		}
		setModel(new SerializableListDataModel(domains));
	}

	public void setModel(DataModel model) {
		setPage(1);
		this.model = model;
	}

	public int getDomainCount() {
		if (getParentDomain() != null) {
			AONContext ctx = AONContext.getAONContext(getDomainNameURL(),domainId,getCurrentUser());
			int count = ctx.getDslContext().selectCount().from(DOMAIN)
					.where(getDomainCondition()).fetchOne(0, int.class);
			ctx.finalize();
			return count;
		}
		return 0;
	}

	public void onEditSearch(ActionEvent event) {
		setModel(null);
		setFilter(null);
		setFilteredModel(null);
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
		this.onEditSearch(null);
		this.domainURL = null;
		setShowInactive(false);
		setShowExpired(false);
		System.gc();
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

	private boolean isRemovable(String key, String className) {
		return (StringUtils.startsWith(className, "com.code.aon")
				&& !StringUtils.startsWith(key, "com.code.aon.audit.")
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
	    com.esferalia.aon.occam.api.model.ApplicationParameter ud = AON.getApplicationParamenter(getDomainNameURL(), getDomainId(), "", com.esferalia.aon.occam.api.model.type.AppParam.AON_ADHOC_EXTENSION);
		return ud != null && ud.getValue() != null && ud.getValue().equalsIgnoreCase("udapa");
	}
	
	public boolean isPaturpat() {
	    com.esferalia.aon.occam.api.model.ApplicationParameter ud = AON.getApplicationParamenter(getDomainNameURL(), getDomainId(), "", com.esferalia.aon.occam.api.model.type.AppParam.AON_ADHOC_EXTENSION);
		return ud != null && ud.getValue() != null && ud.getValue().equalsIgnoreCase("paturpat");
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
	
}