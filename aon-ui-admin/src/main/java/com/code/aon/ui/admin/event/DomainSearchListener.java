package com.code.aon.ui.admin.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.controller.DomainPrintController;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainSearchListener extends ControllerSearchListenerEx {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainSearchListener.class);
	
	private String name;
	private String description;
	private Domain parent;
	private List<DomainType> types;
	private Boolean active;
	private Boolean enableHeredity;
	private Boolean domainManagement;
	private Date[] creationDate;
	private Date[] modificationDate;	
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

	public Domain getParent() {
		return parent;
	}

	public void setParent(Domain parent) {
		this.parent = parent;
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
		setShowList(false);
		try {
			IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
			setParent((Domain) domainBean.createNewTo());
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
		addDateRange(criteria, getFieldName(IEntityAlias.DOMAIN_CREATION_DATE), getCreationDate());
		addModificationDateRange(criteria, getFieldName(IEntityAlias.DOMAIN_MODIFICATION_DATE));
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
	
	public void addModificationDateRange( Criteria criteria, String alias ) throws ManagerBeanException {
		addDateRange(criteria, alias, getModificationDate());		
	}
	
	private void addDateRange( Criteria criteria, String alias, Date[] dates ) {
		if ( dates[0] != null ) {
			criteria.addGreaterThanOrEqualExpression(alias, dates[0]);	
		}
		if ( dates[1] != null ) {
			criteria.addLessThanOrEqualExpression(alias, dates[1]);	
		}
	}
	
}