package com.code.aon.ui.company.event;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.config.Scope;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the CompanyController
 * 
 */
public class CompanyControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyControllerListener.class.getName());
	
	/**
	 * Initializes controller fields and loads the addresses and medias of the company
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			ICompanyController c = (ICompanyController)event.getController();
			Company company = (Company) c.getTo();
			
			Criteria criteriaMedia = new Criteria();
			IManagerBean beanMedia = BeanManager.getManagerBean( RegistryMedia.class);
			String registryIdFieldName = beanMedia.getFieldName( IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteriaMedia.addEqualExpression(registryIdFieldName,company.getId() );
			List mediaList = beanMedia.getList(  criteriaMedia );
			
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
			
			Iterator mediaIter = mediaList.iterator();
			while (mediaIter.hasNext()){
				RegistryMedia rmedia = (RegistryMedia)mediaIter.next();
				switch ( rmedia.getMediaType() ) {
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
				}
			}
			c.setPhone(phone);				
			c.setFax(fax);				
			c.setEmail(email);				
			c.setWeb(web);				
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	/**
	 * Updates the dirty fields of the controller
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
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
				Enterprise enterprise = obtainEnterprise(c.getMainAddress());
				updateWorkPlace(c.getMainAddress(), enterprise);
			}
			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	/**
	 * Adds the dirty fields of the controller
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			ICompanyController c = (ICompanyController)event.getController();
			Company company = (Company) c.getTo();
			
			Enterprise enterprise = addEnterprise(company);

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
				updateWorkPlace(c.getMainAddress(), enterprise);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	/**
	 * Adds or updates a RegistryMedia
	 * 
	 * @param rmedia the rmedia
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	private void saveRegistryMedia(RegistryMedia rmedia) throws ManagerBeanException{
		IManagerBean beanMedia = BeanManager.getManagerBean( RegistryMedia.class);
		beanMedia.insertOrUpdate(rmedia);
	}

	private void saveRegistryAddress(RegistryAddress mainAddress) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		rAddressBean.insertOrUpdate(mainAddress);
	}
	
	private Enterprise addEnterprise( Company company ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
		Enterprise enterprise = new Enterprise();
		enterprise.setRegistry(company);
		enterprise.setScope(obtainScope());
		return (Enterprise) bean.insert(enterprise);
	}

	private Scope obtainScope() throws ManagerBeanException {
		IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(scopeBean.getFieldName(IConfigAlias.SCOPE_ID));
		return (Scope)scopeBean.getList(criteria).get(0);
	}

	private Enterprise obtainEnterprise(RegistryAddress registryAddress) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_REGISTRY_ID), registryAddress.getRegistry().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (Enterprise) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining enterprise with address= " + registryAddress.getId(), e);
		}
		return null;
	}		

	private void updateWorkPlace(RegistryAddress address, Enterprise enterprise) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		WorkPlace workPlace = null;
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ADDRESS_ID), address.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			workPlace = (WorkPlace) list.get(0);
		} else {
			workPlace = new WorkPlace();
			workPlace.setEnterprise( enterprise );
			workPlace.setAddress( address );
			workPlace.setActive( true );
		}
		workPlace.setDescription( address.getFullAddress() );
		bean.insertOrUpdate(workPlace);
	}
	
}
