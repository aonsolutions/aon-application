package com.code.aon.ui.admin;

import static com.esferalia.aon.entity.IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN;
import static com.esferalia.aon.entity.IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID;

import java.io.Serializable;
import java.net.IDN;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.admin.event.DomainSearchListener;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainPrintInfo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Domain domain;
	
	private int childNumber;
	
	private int domainUserNumber;
	
	private String domainModuleList;
	
	private String payer;
	
	private String modifications;
	
	private boolean domainOne;
		
	private int domainUsedSpaceInMB;
	
	private String domainURL;

	public DomainPrintInfo(Domain domain) throws ManagerBeanException {
		this.domain = domain;
		init();
	}

	public Domain getDomain() {
		return domain;
	}
	
	private void init() throws ManagerBeanException {
		this.childNumber = calculateChildNumber();
		this.domainUserNumber = calculateDomainUserNumber();
		this.domainModuleList = calculateDomainModuleList();
		this.payer = calculatePayer();
		this.modifications = calculateModifications();
		this.domainOne = calculateDomainOne();
		this.domainURL = calculateDomainURL();
		this.domainUsedSpaceInMB = calculateDomainUsedSpaceInMB();
	}

	private int calculateChildNumber() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), domain.getId());
		return bean.getCount(criteria);		
	}
	
	private int calculateDomainUserNumber() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_DOMAIN), domain.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_ACTIVE), Boolean.TRUE);
		return bean.getCount(criteria);		
	}
	
	private String calculateDomainModuleList() throws ManagerBeanException {
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
					if ( dam.getModule() != Module.AON_ONE ) {
						String name = null;
						if ( domain.isDomainManagement() && (dam.getModule() == Module.PAYROLL_PORTAL) ) {
							name = AonUtil.getMessage(ICommonMessages.ADMIN_GLOBAL_PORTAL_ACCESS);
						} else {
							name = dam.getModule().getName(locale);
						}
						modules.add( name );							
					}
				}
				return StringUtils.join(modules, ", ");					
			}			
		}
		return null;
	}
	
	private String calculatePayer() throws ManagerBeanException {
		Integer value = AppParamUtil.getValueAsInteger(AppParam.AON_DOMAIN_PAYER, domain.getId());
		if ( value != null ) {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain payer = (Domain) bean.get(value);
			if ( payer != null ) {
				return payer.getDescription();
			}
		}
		return null;
	}
	
	private boolean calculateDomainOne() throws ManagerBeanException {
		if ( domain.getType() == DomainType.ENTERPRISE ) {
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			return AuditManager.hasModule(domain.getId(), appId, Module.AON_ONE);
		}
		return false;
	}
	
	private Criteria getDiffCriteria( Domain domain, IManagerBean bean ) throws ManagerBeanException {
		return getDiffCriteria(domain, bean, null);
	}

	private Criteria getDiffCriteria( Domain domain, IManagerBean bean, Date[] dateRange ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		Integer companyId = AdminUtil.getCompanyId(domain.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), companyId);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DOMAIN_BOOK_HISTORY);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_MIME_TYPE), MimeType.MIME_TXT);
		Date[] dates = dateRange;
		if ( ArrayUtils.isEmpty(dates) ) {
			DomainSearchListener dsl = (DomainSearchListener) AonUtil.getRegisteredBean(IAdminConstants.DOMAINS_SEARCH_CONTROLLER_NAME);
			dates = dsl.getModificationDate();
		}
		DomainSearchListener.addDateRange(criteria, bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ATTACH_DATE), dates );			
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), false);		
		return criteria;
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
		
	private String calculateModifications() throws ManagerBeanException {
		String diff = null;
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = getDiffCriteria(domain, bean);
		DomainInfo di1 = getDomainInfo(bean, criteria, 0);
		DomainSearchListener dsl = (DomainSearchListener) AonUtil.getRegisteredBean(IAdminConstants.DOMAINS_SEARCH_CONTROLLER_NAME);
		Date[] dates = dsl.getModificationDate();
		DomainInfo di2 = null;
		if ( (dates[0] != null) && (dates[1] == null) ) {
			Criteria previousCriteria = getDiffCriteria(domain, bean, new Date[]{null, dates[0]});
			di2 = getDomainInfo(bean, previousCriteria, 0);
		}
		if ( di2 == null ) {
			int count = bean.getCount(criteria);
			if ( count > 1 ) {
				di2 = getDomainInfo(bean, criteria, count-1);	
			}
		}
		if ( di2 != null ) {
			diff = di2.getDifferences(di1);
			if ( StringUtils.isEmpty(diff) ) {
				diff = AonUtil.getMessage(ICommonMessages.FINANCE_NONE);
			}				
		}
		return diff;
	}
	
	private int calculateDomainUsedSpaceInMB() throws ManagerBeanException {
		return DocumentManager.getUsedSpaceInMB(domain.getId());
	}	
	
	private String calculateDomainURL() throws ManagerBeanException {
		return IDN.toASCII(domain.getName());
	}	
	
	
	public int getChildNumber() {
		return childNumber;
	}

	public int getDomainUserNumber() {
		return domainUserNumber;
	}

	public String getDomainModuleList() {
		return domainModuleList;
	}
	
	public String getPayer() {
		return payer;
	}
	
	public String getModifications() {
		return modifications;
	}

	public boolean isDomainOne() {
		return domainOne;
	}

	public int getDomainUsedSpaceInMB() {
		return domainUsedSpaceInMB;
	}

	public String getDomainURL() {
		return domainURL;
	}
	
}
