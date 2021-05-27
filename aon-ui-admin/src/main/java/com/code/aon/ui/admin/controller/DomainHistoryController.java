package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.admin.DomainInfo;
import com.code.aon.ui.admin.DomainInfoType;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainHistoryController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	private List<SelectItem> domainInfoTypes;
	
	private DomainInfoType type;
	
	private String name;
	
	private Date[] date;
	
	private boolean showList;
	
	public void onInit( ActionEvent event ) {
		try {
			reset();
			initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}			
	}
	
	public void onClear(ActionEvent event) {
		reset();
	}
	
	public void onSearch(ActionEvent event) {
		try {
			initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}			
	}	
	
	private void reset() {
		setShowList(false);
		setName(null);
		setType(null);
		setDate(new Date[2]);
	}
	
	private Company getCompany() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		return companyController.obtainCompany();		
	}
	
	private void addDateRange( Criteria criteria, String alias, Date[] dates ) {
		if ( ArrayUtils.getLength(dates) == 2 ) {
			if ( dates[0] != null ) {
				criteria.addGreaterThanOrEqualExpression(alias, dates[0]);	
			}
			if ( dates[1] != null ) {
				criteria.addLessThanOrEqualExpression(alias, dates[1]);	
			}			
		}
	}	
	
	public List<SelectItem> getDomainInfoTypes() {
		if ( domainInfoTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			domainInfoTypes = new LinkedList<SelectItem>();
			for (DomainInfoType type : DomainInfoType.values()) {
				if ( type != DomainInfoType.MODIFICATION ) {
					String name = type.getName(locale);
					domainInfoTypes.add(new SelectItem(type, name));					
				}
			}		
		}
		return domainInfoTypes;
	}	
	
	private RegistryAttachmentType getType( DomainInfoType type ) {
		switch ( type ) {
			case INSERT:
				return RegistryAttachmentType.DOMAIN_INSERT_HISTORY;
			case MODIFICATION:
				return RegistryAttachmentType.DOMAIN_BOOK_HISTORY;
			case REMOVE:
				return RegistryAttachmentType.DOMAIN_REMOVE_HISTORY;
		}
		return null;
	}
	
	private void initializeModel() throws ManagerBeanException {
		List<DomainInfo> list = new LinkedList<DomainInfo>();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), getCompany().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_MIME_TYPE), MimeType.MIME_TXT);
		if (! StringUtils.isEmpty(getName()) ) {
			String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION);
			Expression expr = ExpressionUtilities.getLikeExpression(alias, "%" + getName() + "%");
			criteria.addExpression(expr);
		}		
		String typeAlias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		if ( type == null ) {
			Expression expr1 = ExpressionUtilities.getEqualExpression(typeAlias, getType(DomainInfoType.INSERT));
			Expression expr2 = ExpressionUtilities.getEqualExpression(typeAlias, getType(DomainInfoType.REMOVE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} else {
			criteria.addEqualExpression(typeAlias, getType(type) );	
		} 
		addDateRange(criteria, bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ATTACH_DATE), getDate());		
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_CREATION_DATE), false);
		for( ITransferObject to : bean.getList(criteria) ) {
			DomainInfo di = DomainInfo.getDomainInfo((RegistryAttachment) to);
			list.add(di);
		}
		setModel(new SerializableListDataModel(list));
		setShowList(true);
	}

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
	
	public DomainInfoType getType() {
		return type;
	}

	public void setType(DomainInfoType type) {
		this.type = type;
	}	

	public Date[] getDate() {
		return date;
	}

	public void setDate(Date[] date) {
		this.date = date;
	}
	
}
