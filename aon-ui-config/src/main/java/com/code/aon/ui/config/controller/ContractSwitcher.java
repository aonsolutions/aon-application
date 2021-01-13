package com.code.aon.ui.config.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.ContractData;
import com.code.aon.ui.config.DomainData;
import com.code.aon.ui.form.ITemplateController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.Person;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.util.AonDateUtils;

public class ContractSwitcher implements
		ITemplateController, Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;


	private final static Logger LOGGER = LoggerFactory
			.getLogger(ContractSwitcher.class);
	private DataModel model;
	private DataModel filteredModel;
	private String filter;
	private String modelFilter;
	private int page;
	private Integer pageLimit;
	private String beanName;

	public ContractSwitcher() {
		try {
			setPageLimit(15);
		} catch (Throwable th) {
		}
	}


	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}
	
	@Override
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
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


	public DataModel getModel() throws ManagerBeanException {
		if (model == null) {
			initializeModel();
		}
		if (StringUtils.isBlank(getFilter())) {
			return model;
		} else {
			if (!StringUtils.equals(modelFilter, filter)
					|| filteredModel == null) {
				setPage(1);
				List<ContractData> filteredList = new LinkedList<ContractData>();
				@SuppressWarnings("unchecked")
				List<ContractData> list = (List<ContractData>) model
						.getWrappedData();
				for (ContractData d : list) {
					if (StringUtils
							.containsIgnoreCase(
									d.getFullName(), getFilter())
							|| StringUtils.containsIgnoreCase(
									d.getSsNumber(), getFilter())
							|| StringUtils.containsIgnoreCase(
									d.getDocument(), getFilter())
	
							) {
						filteredList.add(d);
					}
				}
				setFilteredModel(new SerializableListDataModel(filteredList));
				modelFilter = filter;
			}
			return filteredModel;
		}
	}

	private List<Integer> getUserScopes() throws ManagerBeanException {
		List<Integer> scopes = null;
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		AONContext ctx = AONContext.getAONContext(getDomainNameURL(), getDomainId(), getCurrentUser());
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
	
	private Condition getDomainCondition() throws ManagerBeanException {
		return getDomainCondition(getParentDomainId() , getDomainId());		
	}

	public Condition getDomainCondition( Integer parentDomain, Integer  domain ) throws ManagerBeanException {
		Condition condition = DOMAIN.SCOPE.isNull();
		List<Integer> scopes = getUserScopes();
		if (scopes != null && !scopes.isEmpty()) {
			condition = condition.or(DOMAIN.SCOPE.in(scopes));
		}
		if ( parentDomain != null ) {
			condition = condition.and(DOMAIN.PARENT.eq(parentDomain));
		} else if ( domain != null ) {
			condition = condition.and(DOMAIN.ID.eq(parentDomain));
		}
		return condition;
	}


	private void initializeModel() throws ManagerBeanException {
		
		AONContext ctx = AONContext.getAONContext(getDomainNameURL(),getDomainId(),getCurrentUser());
		LinkedList<ContractData> contracts = new LinkedList<ContractData>();
		ctx
		.getDslContext()
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID))
		.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
		.where(getDomainCondition())
		.and(CONTRACT.END_DATE.isNull()
		.or(CONTRACT.END_DATE.ge(getStartDate())))
		.orderBy(REGISTRY.NAME)
		.fetchStream()
		.forEach( r -> {
			ContractData contractData = 
			new ContractData(
					r.get(CONTRACT.ID), 
					r.get(REGISTRY.DOCUMENT), 
					r.get(REGISTRY.NAME), 
					r.get(PERSON.SOCIAL_SECURITY_NUM), 
					r.get(CONTRACT.END_DATE), 
					r.get(CONTRACT.START_DATE),
					r.get(DOMAIN.ID),
					r.get(DOMAIN.DESCRIPTION));
			contracts.add(contractData);
		})				
		;

		ctx.finalize();
		setModel(new SerializableListDataModel(contracts));
	}

	public void setModel(DataModel model) {
		setPage(1);
		this.model = model;
	}


	public void onEditSearch(ActionEvent event) {
		setModel(null);
		setFilter(null);
		setFilteredModel(null);
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

	public String getSchema() {
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			return principal!=null?principal.getDatabaseName():null;
		} catch  (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date getStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, -1);
		return new java.sql.Date(calendar.getTimeInMillis());
	}

	public Integer getDomainId() throws ManagerBeanException {
		return ((DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER)).getDomainId();
	}

	public Integer getParentDomainId() throws ManagerBeanException {
		return ((DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER)).getParentDomain();
	}

	public String getDomainNameURL() throws ManagerBeanException {
		return ((DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER)).getDomainNameURL();
	}
	
}