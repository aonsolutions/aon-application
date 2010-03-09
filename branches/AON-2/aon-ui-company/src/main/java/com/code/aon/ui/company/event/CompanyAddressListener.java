package com.code.aon.ui.company.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the CompanyAddressController.
 */
public class CompanyAddressListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompanyAddressListener.class.getName());
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress address = null;
		if ( address == null && event.getController().getTo() instanceof RegistryAddress ) {
			address = (RegistryAddress)event.getController().getTo();
			address.setAddressType( (address.getAddressType() == null)? AddressType.DELEGATION: address.getAddressType() ); 
		}
	}

	/**
	 * Adds a workPlace related with the current RegistryAddress
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress address = null;
		if ( event.getController() instanceof ICompanyController ) {
			ICompanyController cc = (ICompanyController) event.getController();
			address = cc.getMainAddress();
		}
		if ( address == null && event.getController().getTo() instanceof RegistryAddress ) {
			address = (RegistryAddress)event.getController().getTo();
		}
		if( address.getAddressType().equals(AddressType.MAIN) || address.getAddressType().equals(AddressType.DELEGATION) ) {
			try {
				IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
				WorkPlace workPlace = new WorkPlace();
				workPlace.setDescription( address.getAddress() + (address.getAddress2() != null?" " + address.getAddress2():"" ) +  (address.getAddress3() != null?" " + address.getAddress3():"" ));
				workPlace.setAddress( address );
				workPlace.setActive( true );
				workPlaceBean.insert(workPlace);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding workPlace for rAddres with id= " + address.getId(), e);
			}
		}
	}

	/**
	 * Removes the workPlace related with the current RegistryAddress
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress address = null;
		if ( event.getController() instanceof ICompanyController ) {
			ICompanyController cc = (ICompanyController) event.getController();
			address = cc.getMainAddress();
		}
		if ( address == null && event.getController().getTo() instanceof RegistryAddress ) {
			address = (RegistryAddress)event.getController().getTo();
		}
		if( address.getAddressType().equals(AddressType.MAIN) || address.getAddressType().equals(AddressType.DELEGATION) ) {
			WorkPlace workPlace = obtainWorkPlace( address );
			try {
				IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
				if(workPlace != null){
					workPlace.setActive( false );
					workPlaceBean.insertOrUpdate( workPlace );
				}
			} catch (ManagerBeanException e) {
				//	TODO i18n
				String message = "No se puede borrar esta dirección: " + address.getId() 
								+ " ya que está asociada al Centro de Trabajo: " + workPlace.getDescription()
								+ ", desvincule 1º la relación de Empleados que trabajan en él.";
				throw new ControllerListenerException( message, e);
			}
		}
	}

	/**
	 * Obtains the workPlace related with the RegistryAddress
	 * 
	 * @param registryAddress the registry address
	 * 
	 * @return the work place
	 */
	private WorkPlace obtainWorkPlace(RegistryAddress registryAddress) {
		try {
			IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workPlaceBean.getFieldName(ICompanyAlias.WORK_PLACE_ADDRESS_ID), registryAddress.getId());
			Iterator iter = workPlaceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (WorkPlace)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining workPlace with address= " + registryAddress.getId(), e);
		}
		return null;
	}
}