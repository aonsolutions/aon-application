package com.esferalia.aon.ui.payroll.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.company.controller.RegistryInfo;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.ui.payroll.controller.TrainingCenterController;

public class TrainingCenterControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		TrainingCenterController controller = (TrainingCenterController)event.getController(); 
		TrainingCenter trainingCenter = (TrainingCenter)event.getController().getTo(); 
		
		trainingCenter.setRegistry(new Registry());
		trainingCenter.getRegistry().setDocumentType(DocumentType.CIF);
		
		controller.setEmail( new RegistryMedia() );
		controller.setPhone( new RegistryMedia() );
		controller.setFax( new RegistryMedia() );
		controller.setWeb( new RegistryMedia() );
		
		
		controller.setMainAddress( new RegistryAddress() );
		controller.getMainAddress().setRegistry(new Registry());
		controller.getMainAddress().setAddressType(AddressType.MAIN);
		controller.getMainAddress().setStreetType(StreetType.CL);
		controller.getMainAddress().setGeozone(new GeoZone());
		
		controller.resetDirty();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			TrainingCenterController c = (TrainingCenterController) event.getController();
			TrainingCenter center = (TrainingCenter) c.getTo();
			
			Criteria criteriaMedia = new Criteria();
			IManagerBean beanMedia = BeanManager.getManagerBean(RegistryMedia.class);
			String registryIdFieldName = beanMedia.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteriaMedia.addEqualExpression(registryIdFieldName,center.getRegistry().getId());
			List<ITransferObject> mediaList = beanMedia.getList(criteriaMedia);
			
			RegistryMedia phone = new RegistryMedia();
			phone.setRegistry(center.getRegistry());
			phone.setMediaType(MediaType.FIXED_PHONE);
			RegistryMedia fax = new RegistryMedia();
			fax.setRegistry(center.getRegistry());
			fax.setMediaType(MediaType.FAX);
			RegistryMedia email = new RegistryMedia();
			email.setRegistry(center.getRegistry());
			email.setMediaType(MediaType.EMAIL);
			RegistryMedia web = new RegistryMedia();
			web.setRegistry(center.getRegistry());
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
				}
			}
			c.setPhone(phone);				
			c.setFax(fax);				
			c.setEmail(email);				
			c.setWeb(web);				
			
			c.resetDirty();
			
			loadMainAddress(event);
			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try{
			TrainingCenterController c = (TrainingCenterController)event.getController();

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
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			TrainingCenterController c = (TrainingCenterController)event.getController();
			TrainingCenter trainingCenter = (TrainingCenter) c.getTo();
			
			if (c.isPhoneDirty()){
				c.getPhone().setMediaType(MediaType.FIXED_PHONE);
				c.getPhone().setRegistry(trainingCenter.getRegistry());
				saveRegistryMedia(c.getPhone());
			}
			if (c.isFaxDirty()){
				c.getFax().setMediaType(MediaType.FAX);
				c.getFax().setRegistry(trainingCenter.getRegistry());
				saveRegistryMedia(c.getFax());
			}
			if (c.isEmailDirty()){
				c.getEmail().setMediaType(MediaType.EMAIL);
				c.getEmail().setRegistry(trainingCenter.getRegistry());
				saveRegistryMedia(c.getEmail());
			}
			if (c.isWebDirty()){
				c.getWeb().setMediaType(MediaType.WEB);
				c.getWeb().setRegistry(trainingCenter.getRegistry());
				saveRegistryMedia(c.getWeb());
			}
			
			if(c.isAddressDirty()){
				c.getMainAddress().setRegistry(trainingCenter.getRegistry());
				saveRegistryAddress(c.getMainAddress());
			}
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
	
	private void loadMainAddress(ControllerEvent event) throws ManagerBeanException {
		TrainingCenterController controller = (TrainingCenterController)event.getController();
		TrainingCenter trainingCenter = (TrainingCenter)controller.getTo();
		controller.setMainAddress( RegistryInfo.getMainAddress(trainingCenter.getRegistry()) );
		if (controller.getMainAddress() == null) {
			controller.setMainAddress( new RegistryAddress() );
			controller.getMainAddress().setRegistry(trainingCenter.getRegistry());
			controller.getMainAddress().setAddressType(AddressType.MAIN);			
			controller.getMainAddress().setStreetType(StreetType.CL);
			controller.getMainAddress().setGeozone(new GeoZone());
		} else if (controller.getMainAddress().getStreetType() == null) {
			controller.getMainAddress().setStreetType(StreetType.CL);
		}
	}
	
}
