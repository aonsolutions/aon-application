package com.code.aon.ui.admin.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.controller.DomainPrintController;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainSearchListener.class);
	
	private String name;
	private String description;
	private Domain parent;
	private Domain payer;
	private List<DomainType> types;
	private Boolean active;
	private Boolean enableHeredity;
	private Boolean domainManagement;
	private Date[] creationDate;
	private Date[] modificationDate;	
	private Date[] expirationDate;
	private Date[] lastAccessDate;
	private Module module;
	private boolean showList;

	public boolean isShowList() {
		return showList;
	}

	public void setShowList(boolean showList) {
		this.showList = showList;
	}
	
	public String getShowOpened() {
		return Boolean.toString(!this.showList);
	}

	public void setShowOpened(String showOpened) {
		this.showList = ! Boolean.parseBoolean(showOpened);
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DomainType> getTypes() {
		if (types == null) {
			types = new LinkedList<DomainType>();
			types.add(null);
		}
		return types;
	}
	
	public void setTypes(List<DomainType> types) {
		this.types = types;
	}

	public int getTypesSize() {
		return types.size();
	}	
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Boolean getEnableHeredity() {
		return enableHeredity;
	}

	public void setEnableHeredity(Boolean enableHeredity) {
		this.enableHeredity = enableHeredity;
	}

	public Boolean getDomainManagement() {
		return domainManagement;
	}

	public void setDomainManagement(Boolean domainManagement) {
		this.domainManagement = domainManagement;
	}

	public Date[] getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date[] creationDate) {
		this.creationDate = creationDate;
	}

	public Date[] getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date[] modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	public Date[] getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date[] expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public Date[] getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date[] lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	public Domain getParent() {
		return parent;
	}

	public void setParent(Domain parent) {
		this.parent = parent;
	}

	public Domain getPayer() {
		return payer;
	}

	public void setPayer(Domain payer) {
		this.payer = payer;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public void onClear(ActionEvent event) {
		reset();
	}
	
	public void reset() {
		setName(null);
		setDescription(null);
		setActive(null);
		setEnableHeredity(null);
		setDomainManagement(null);
		setTypes(null);
		setCreationDate(new Date[2]);
		setModificationDate(new Date[2]);
		setExpirationDate(new Date[2]);
		setLastAccessDate(new Date[2]);
		setModule(null);
		setShowList(false);
		try {
			IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
			setParent((Domain) domainBean.createNewTo());
			setPayer((Domain) domainBean.createNewTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		reset();
	}

	@Override
	public void completeCriteria( Criteria criteria ) throws ManagerBeanException {
		DomainPrintController dpc = (DomainPrintController) getController();
		if (! StringUtils.isEmpty(getName()) ) {
			dpc.addExpression(criteria, IEntityAlias.DOMAIN_NAME, getName());
		}
		if (! StringUtils.isEmpty(getDescription()) ) {
			dpc.addExpression(criteria, IEntityAlias.DOMAIN_DESCRIPTION, getDescription());
		}
		if ( getActive() != null ) {
			criteria.addEqualExpression( getFieldName(IEntityAlias.DOMAIN_ACTIVE), getActive());
		}
		if ( getEnableHeredity() != null ) {
			criteria.addEqualExpression( getFieldName(IEntityAlias.DOMAIN_ENABLE_HEREDITY), getEnableHeredity());
		}
		if ( getDomainManagement() != null ) {
			criteria.addEqualExpression( getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT), getDomainManagement());
		}
		addEnumToCriteria(criteria, getFieldName(IEntityAlias.DOMAIN_TYPE), getTypes().toArray());
		if ( getParent() != null && getParent().getId() != null) {
			criteria.addEqualExpression( getFieldName(IEntityAlias.DOMAIN_PARENT_ID), getParent().getId());			
		}
		if ( getPayer() != null && getPayer().getId() != null) {
			addPayerSubQuery( module, criteria );			
		}
		addDateRange(criteria, getFieldName(IEntityAlias.DOMAIN_CREATION_DATE), getCreationDate());
		addDateRange(criteria, getFieldName(IEntityAlias.DOMAIN_EXPIRATION_DATE), getExpirationDate());
		addDateRange(criteria, getFieldName(IEntityAlias.DOMAIN_LAST_ACCESS_DATE), getLastAccessDate());
		addDateRange(criteria, getFieldName(IEntityAlias.DOMAIN_MODIFICATION_DATE), getModificationDate());
		if ( module != null ) {
			addModuleSubQuery( module, criteria );
		}
		setShowList(true);
	}	

	public void onAddType(ActionEvent event) {
		this.types.add(null);
	}
	
	public void onRemoveType(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.types.remove(index);
		if (this.types.isEmpty()) {
			this.types.add(null);
		}
	}	
	
	public static void addDateRange( Criteria criteria, String alias, Date[] dates ) {
		if ( ArrayUtils.getLength(dates) == 2 ) {
			if ( dates[0] != null ) {
				criteria.addGreaterThanOrEqualExpression(alias, dates[0]);	
			}
			if ( dates[1] != null ) {
				criteria.addLessThanOrEqualExpression(alias, dates[1]);	
			}			
		}
	}
	
	public void addModuleSubQuery(Module module, Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_MODULE), module);
		String idAlias = bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(DomainApplicationModule.class, subCriteria, pl);
		criteria.addInExpression(getFieldName(IEntityAlias.DOMAIN_ID), exp);			
	}

	public void addPayerSubQuery(Module module, Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AppParam.AON_DOMAIN_PAYER.getValue());
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_VALUE), getPayer().getId().toString());
		String idAlias = bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(ApplicationParameter.class, subCriteria, pl);
		criteria.addInExpression(getFieldName(IEntityAlias.DOMAIN_ID), exp);			
	}
	
}