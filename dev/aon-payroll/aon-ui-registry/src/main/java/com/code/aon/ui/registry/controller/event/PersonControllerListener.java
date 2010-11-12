package com.code.aon.ui.registry.controller.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.PersonController;

/**
 * Listener added to the CompanyController
 * 
 */
public class PersonControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PersonController c = (PersonController)event.getController();
		c.setEmail( new RegistryMedia());
		c.setPhone( new RegistryMedia());
		c.setFax(new RegistryMedia());
		c.setWeb(new RegistryMedia());
		c.setRegistryBank(new RegistryBank());
		c.getRegistryBank().setBank( new Bank() );
		c.getRegistryBank().setBankAccount( new BankAccount() );		
		c.setRegistryPayMethod(new RegistryPayMethod());
		c.getRegistryPayMethod().setPayment(new PayMethod());
		c.getRegistryPayMethod().setRegistryBank( new RegistryBank() );
		c.getRegistryPayMethod().getRegistryBank().setBank( new Bank() );
		c.getRegistryPayMethod().getRegistryBank().setBankAccount( new BankAccount() );		
		c.setMainAddress (new RegistryAddress());
		c.getMainAddress().setGeozone(new GeoZone());
		c.getMainAddress().setAddressType(AddressType.MAIN);
		c.resetDirty();		
	}
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			PersonController c = (PersonController)event.getController();
			Person person = (Person) c.getTo();
			
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), person.getId());
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
			Iterator iter = rAddressBean.getList(criteria).iterator();
			if(iter.hasNext()){
				c.setMainAddress((RegistryAddress)iter.next());
			}else{
				c.setMainAddress (new RegistryAddress());
				c.getMainAddress().setRegistry(person.getRegistry());
				c.getMainAddress().setGeozone(new GeoZone());
				c.getMainAddress().setAddressType(AddressType.MAIN);
				c.getMainAddress().setStreetType(StreetType.CL);
			}
			
			
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteriaBank = new Criteria();
			criteriaBank.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), person.getId());
			iter = rBankBean.getList(criteriaBank).iterator();
			if(iter.hasNext()){
				c.setRegistryBank((RegistryBank)iter.next());
			}else{
				c.setRegistryBank(new RegistryBank());
				c.getRegistryBank().setRegistry(person.getRegistry());
				c.getRegistryBank().setBank( new Bank() );
				c.getRegistryBank().setBankAccount( new BankAccount() );		
			}

			IManagerBean rPayBean = BeanManager.getManagerBean(RegistryPayMethod.class);
			Criteria rPayBeanCriteria = new Criteria();
			rPayBeanCriteria.addEqualExpression(rPayBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), person.getId());
			iter = rPayBean.getList(rPayBeanCriteria).iterator();
			if(iter.hasNext()){
				c.setRegistryPayMethod((RegistryPayMethod)iter.next());
			}else{
				c.setRegistryPayMethod(new RegistryPayMethod());
				c.getRegistryPayMethod().setRegistry(person.getRegistry());
				c.getRegistryPayMethod().setPayment(new PayMethod());
				c.getRegistryPayMethod().setRegistryBank( new RegistryBank() );
				c.getRegistryPayMethod().getRegistryBank().setBank( new Bank() );
				c.getRegistryPayMethod().getRegistryBank().setBankAccount( new BankAccount() );		
			}

			Criteria criteriaMedia = new Criteria();
			IManagerBean beanMedia = BeanManager.getManagerBean( RegistryMedia.class);
			String registryIdFieldName = beanMedia.getFieldName( IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteriaMedia.addEqualExpression(registryIdFieldName,person.getId() );
			List mediaList = beanMedia.getList(  criteriaMedia );
			
			RegistryMedia phone = new RegistryMedia();
			phone.setRegistry(person.getRegistry());
			phone.setMediaType(MediaType.FIXED_PHONE);
			RegistryMedia fax = new RegistryMedia();
			fax.setRegistry(person.getRegistry());
			fax.setMediaType(MediaType.FAX);
			RegistryMedia email = new RegistryMedia();
			email.setRegistry(person.getRegistry());
			email.setMediaType(MediaType.EMAIL);
			RegistryMedia web = new RegistryMedia();
			web.setRegistry(person.getRegistry());
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

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try{
			PersonController c = (PersonController)event.getController();

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
			if(c.isRegistryBankDirty()){
				saveRegistryBank(c.getRegistryBank());
			}
			if(c.isRegistryPayMethodDirty()){
				saveRegistryPayMethod(c.getRegistryPayMethod());
			}
			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			PersonController c = (PersonController)event.getController();
			Person person = (Person) c.getTo();
			
			if (c.isPhoneDirty()){
				c.getPhone().setMediaType(MediaType.FIXED_PHONE);
				c.getPhone().setRegistry(person.getRegistry());
				saveRegistryMedia(c.getPhone());
			}
			if (c.isFaxDirty()){
				c.getFax().setMediaType(MediaType.FAX);
				c.getFax().setRegistry(person.getRegistry());
				saveRegistryMedia(c.getFax());
			}
			if (c.isEmailDirty()){
				c.getEmail().setMediaType(MediaType.EMAIL);
				c.getEmail().setRegistry(person.getRegistry());
				saveRegistryMedia(c.getEmail());
			}
			if (c.isWebDirty()){
				c.getWeb().setMediaType(MediaType.WEB);
				c.getWeb().setRegistry(person.getRegistry());
				saveRegistryMedia(c.getWeb());
			}
			
			if(c.isAddressDirty()){
				c.getMainAddress().setRegistry(person.getRegistry());
				saveRegistryAddress(c.getMainAddress());
			}
			if(c.isRegistryBankDirty()){
				c.getRegistryBank().setRegistry(person.getRegistry());
				saveRegistryBank(c.getRegistryBank());
			}
			if(c.isRegistryPayMethodDirty()){
				c.getRegistryPayMethod().setRegistry(person.getRegistry());
				saveRegistryPayMethod(c.getRegistryPayMethod());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	private void saveRegistryMedia(RegistryMedia rmedia) throws ManagerBeanException{
		IManagerBean beanMedia = BeanManager.getManagerBean( RegistryMedia.class);
		beanMedia.insertOrUpdate(rmedia);
	}

	private void saveRegistryAddress(RegistryAddress mainAddress) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		rAddressBean.insertOrUpdate(mainAddress);
	}

	private void saveRegistryBank(RegistryBank registryBank) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryBank.class);
		if (registryBank != null && registryBank.getBank() != null && registryBank.getBank().getId() != null) {
			rAddressBean.insertOrUpdate(registryBank);	
		}
	}

	private void saveRegistryPayMethod(RegistryPayMethod registryPayMethod) throws ManagerBeanException {
		if (registryPayMethod != null && registryPayMethod.getPayment() != null && registryPayMethod.getPayment().getId() != null) {
			if (registryPayMethod.getRegistryBank() == null || registryPayMethod.getRegistryBank().getId() == null) {
				registryPayMethod.setRegistryBank(null);
			}
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryPayMethod.class);
			rAddressBean.insertOrUpdate(registryPayMethod);
		}
	}
}
