package com.code.aon.ui.company.event;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			ICompanyController c = (ICompanyController)event.getController();
			Company company = (Company) c.getTo();

			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
			company.setName( domain.getDescription() );
			company.setAlias( StringUtils.upperCase( StringUtils.substringBefore(domain.getName(), ".")) );
			initDomainValues(c);
		} catch (ManagerBeanException e) {
			// Nada, no se inicializan los datos.
			
		} 	
	}
	
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			ICompanyController c = (ICompanyController)event.getController();
			Company company = (Company) c.getTo();
			
			Criteria criteriaMedia = new Criteria();
			IManagerBean beanMedia = BeanManager.getManagerBean(RegistryMedia.class);
			String registryIdFieldName = beanMedia.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteriaMedia.addEqualExpression(registryIdFieldName,company.getId());
			List<ITransferObject> mediaList = beanMedia.getList(criteriaMedia);
			
			RegistryMedia phone = new RegistryMedia();
			phone.setRegistry(company);
			phone.setMediaType(MediaType.FIXED_PHONE);
			RegistryMedia fax = new RegistryMedia();
			fax.setRegistry(company);
			fax.setMediaType(MediaType.FAX);
			RegistryMedia email = new RegistryMedia();
			email.setRegistry(company);
			email.setMediaType(MediaType.EMAIL);
			RegistryMedia web = new RegistryMedia();
			web.setRegistry(company);
			web.setMediaType(MediaType.WEB);
			
			Iterator<ITransferObject> mediaIter = mediaList.iterator();
			while (mediaIter.hasNext()){
				RegistryMedia rmedia = (RegistryMedia)mediaIter.next();
				switch (rmedia.getMediaType()) {
					case FIXED_PHONE:
						phone = rmedia;
						break;
					case FAX:
						fax = rmedia;
						break;
					case EMAIL:
						email = rmedia;
						break;
					case WEB:
						web = rmedia;
						break;
					default:
						break;
				}
			}
			c.setPhone(phone);				
			c.setFax(fax);				
			c.setEmail(email);				
			c.setWeb(web);
			
			if(((CompanyController)c).isEdiSupportEnabled()){
				ApplicationParameter param = AppParamUtil.getParameter(AppParam.EDI_SUPPORT);
				((CompanyController)event.getController()).setEdiSupport(param!=null?param.getValue():null);
				((CompanyController)event.getController()).setEdiCompanyCode(AppParamUtil.getParameter(AppParam.EDI_COMPANY_CODE).getValue());
			}
			
			initDomainValues(c);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try{
			ICompanyController c = (ICompanyController)event.getController();

			if (c.isPhoneDirty()){
				saveRegistryMedia(c.getPhone());
			}
			if (c.isFaxDirty()){
				saveRegistryMedia(c.getFax());
			}
			if (c.isEmailDirty()){
				saveRegistryMedia(c.getEmail());
			}
			if (c.isWebDirty()){
				saveRegistryMedia(c.getWeb());
			}
			
			if(c.isAddressDirty()){
				saveRegistryAddress(c.getMainAddress());
			}
			
			if(AonUtil.getRoleManager().isSysAdmin()){
				AppParamUtil.insertParameter(AppParam.EDI_SUPPORT.name(), ((CompanyController)c).getEdiSupport());
			}
			
			if(((CompanyController)c).isEdiSupportEnabled()){
				AppParamUtil.insertParameter(AppParam.EDI_COMPANY_CODE.name(), ((CompanyController)c).getEdiCompanyCode());
			}
			
			updateDomainValues(c);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			ICompanyController c = (ICompanyController)event.getController();
			Company company = (Company) c.getTo();

			Scope scope = CompanyController.obtainScope(company.getId());
			Enterprise enterprise = CompanyController.addEnterprise(company, scope);

			if (c.isPhoneDirty()){
				c.getPhone().setMediaType(MediaType.FIXED_PHONE);
				c.getPhone().setRegistry(company);
				saveRegistryMedia(c.getPhone());
			}
			if (c.isFaxDirty()){
				c.getFax().setMediaType(MediaType.FAX);
				c.getFax().setRegistry(company);
				saveRegistryMedia(c.getFax());
			}
			if (c.isEmailDirty()){
				c.getEmail().setMediaType(MediaType.EMAIL);
				c.getEmail().setRegistry(company);
				saveRegistryMedia(c.getEmail());
			}
			if (c.isWebDirty()){
				c.getWeb().setMediaType(MediaType.WEB);
				c.getWeb().setRegistry(company);
				saveRegistryMedia(c.getWeb());
			}
			
			if(c.isAddressDirty()){
				c.getMainAddress().setRegistry(company);
				saveRegistryAddress(c.getMainAddress());
				CompanyController.insertWorkPlace(c.getMainAddress(), enterprise);
			}
			
			updateDomainValues(c);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	private void saveRegistryMedia(RegistryMedia rmedia) throws ManagerBeanException{
		IManagerBean beanMedia = BeanManager.getManagerBean(RegistryMedia.class);
		beanMedia.insertOrUpdate(rmedia);
	}

	private void saveRegistryAddress(RegistryAddress mainAddress) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		rAddressBean.insertOrUpdate(mainAddress);
	}
	
	private void initDomainValues( ICompanyController controller ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
		if ( domain != null ) {
			controller.setScope(domain.getScope());
			controller.setActive(domain.isActive());
		}
	}

	private void updateDomainValues( ICompanyController controller ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
		if ( domain != null ) {
			domain.setScope(controller.getScope());
			domain.setActive(controller.isActive());
			bean.update(domain);
		}
	}
	
}
