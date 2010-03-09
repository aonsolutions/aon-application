package com.code.aon.ui.company.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the CompanyController
 * 
 */
public class CompanyControllerListener extends ControllerAdapter {

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
				if (MediaType.FIXED_PHONE == rmedia.getMediaType()){
					phone = rmedia;
				}else if (MediaType.FAX == rmedia.getMediaType()){
					fax = rmedia;
				}else if (MediaType.EMAIL == rmedia.getMediaType()){
					email = rmedia;
				}else if (MediaType.WEB == rmedia.getMediaType()){
					web = rmedia;
				}
			}
			c.setPhone(phone);				
			c.setFax(fax);				
			c.setEmail(email);				
			c.setWeb(web);				
			
			c.loadAddresses();
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

			if (c.isPhoneDirty()){
				c.getPhone().setMediaType(MediaType.FIXED_PHONE);
				c.getPhone().setRegistry((Company)c.getTo());
				saveRegistryMedia(c.getPhone());
			}
			if (c.isFaxDirty()){
				c.getFax().setMediaType(MediaType.FAX);
				c.getFax().setRegistry((Company)c.getTo());
				saveRegistryMedia(c.getFax());
			}
			if (c.isEmailDirty()){
				c.getEmail().setMediaType(MediaType.EMAIL);
				c.getEmail().setRegistry((Company)c.getTo());
				saveRegistryMedia(c.getEmail());
			}
			if (c.isWebDirty()){
				c.getWeb().setMediaType(MediaType.WEB);
				c.getWeb().setRegistry((Company)c.getTo());
				saveRegistryMedia(c.getWeb());
			}
			
			if(c.isAddressDirty()){
				c.getMainAddress().setRegistry((Company)c.getTo());
				saveRegistryAddress(c.getMainAddress());
			}
			
			c.loadAddresses();
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
		if (rmedia.getId()==null){
			beanMedia.insert(rmedia);				
		}else{
			beanMedia.update(rmedia);				
		}		
	}

	private void saveRegistryAddress(RegistryAddress mainAddress) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		if(mainAddress.getId() == null){
			rAddressBean.insert(mainAddress);
		}else{
			rAddressBean.update(mainAddress);
		}
	}
}
