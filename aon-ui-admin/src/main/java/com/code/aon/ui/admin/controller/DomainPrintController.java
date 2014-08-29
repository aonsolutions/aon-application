package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAINS_CONTROLLER_NAME;
import static com.esferalia.aon.entity.IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN;
import static com.esferalia.aon.entity.IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID;

import java.net.IDN;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.admin.DomainInfo;
import com.code.aon.ui.admin.event.DomainSearchListener;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainPrintController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainPrintController.class);

	private IControllerListener domainFilter;
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			clearCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		super.onSearch(event);		
	}

	public int getCurrentDomainChildNumber() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(getFieldName(IEntityAlias.DOMAIN_PARENT_ID), domain.getId());
			return getManagerBean().getCount(criteria);
		}
		return 0;
	}

	public int getCurrentDomainUserNumber() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(User.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_DOMAIN), domain.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_ACTIVE), Boolean.TRUE);
			return bean.getCount(criteria);
		}
		return 0;
	}

	public String getCurrentDomainModuleList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			Integer domainApp = AdminUtil.getDomainApplication(domain.getId(), appId);
			if ( domainApp != null ) {
				IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
				Criteria criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression(bean.getFieldName(DOMAIN_APPLICATION_MODULE_DOMAIN), domain.getId());
				criteria.addEqualExpression(bean.getFieldName(DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID), domainApp);
				List<ITransferObject> list = bean.getList(criteria);
				if (! list.isEmpty() ) {
					Set<String> modules = new TreeSet<String>();
					Locale locale = AonUtil.getCurrentLocale();
					for( ITransferObject to : list ) {
						DomainApplicationModule dam = (DomainApplicationModule) to;
						modules.add( dam.getModule().getName(locale) );
					}
					return StringUtils.join(modules, ", ");					
				}			
			}
		}
		return null;
	}
	
	public int getCurrentDomainUsedSpaceInMB() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			return DocumentManager.getUsedSpaceInMB(domain.getId());
		}
		return 0;
	}
	
	public IControllerListener getDomainFilter() {
		if ( this.domainFilter == null ) {
			this.domainFilter = new DomainFilter();
		}
		return this.domainFilter;
	}

	@Override
	public void onSelect(ActionEvent event) {
		DomainsController controller = (DomainsController) AonUtil.getRegisteredBean(DOMAINS_CONTROLLER_NAME);
		controller.selectDomain( (Domain) getSelectedTO() );
	}
	

	private Criteria getDiffCriteria( Domain domain, IManagerBean bean ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		Integer companyId = AdminUtil.getCompanyId(domain.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), companyId);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DOMAIN_BOOK_HISTORY);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_MIME_TYPE), MimeType.MIME_TXT);
		DomainSearchListener dsl = (DomainSearchListener) AonUtil.getRegisteredBean(IAdminConstants.DOMAINS_SEARCH_CONTROLLER_NAME);
		dsl.addModificationDateRange(criteria, bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ATTACH_DATE) );
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), false);		
		return criteria;
	}
	
	public boolean isCurrentHasModifications() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = getDiffCriteria(domain, bean);
			return bean.getCount(criteria) > 1;
		}
		return false;				
	}
	
	private DomainInfo getDomainInfo( IManagerBean bean, Criteria criteria, int offset ) throws ManagerBeanException {
		DomainInfo di = null;
		List<ITransferObject> list = bean.getList(criteria, offset, 1);
		if (! list.isEmpty() ) {
			RegistryAttachment ra = (RegistryAttachment) list.get(0);
			di = DomainInfo.getDomainInfo(ra);
		}
		return di;
	}
	
	public String getCurrentModifications() throws ManagerBeanException {
		String diff = null;
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = getDiffCriteria(domain, bean);
			int count = bean.getCount(criteria);
			DomainInfo di1 = getDomainInfo(bean, criteria, 0);
			DomainInfo di2 = getDomainInfo(bean, criteria, count-1);
			diff = di2.getDifferences(di1);
			if ( StringUtils.isEmpty(diff) ) {
				diff = AonUtil.getMessage(ICommonMessages.FINANCE_NONE);
			}
		}
		return diff;
	}
	
	public String getCurrentDomainURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			String name = IDN.toASCII(domain.getName());
			return name;
		}
		return null;
	}	

	private static class DomainFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				Criteria criteria = controller.getCriteria();
				criteria.setSkipDomainFilter(true);
				criteria.addNotEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_TYPE), DomainType.ADMIN);
				criteria.addEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT), Boolean.TRUE);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering domain", e);
			}
		}
		
	}
	
}